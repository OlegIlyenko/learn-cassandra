import java.net.InetAddress
import java.util.UUID

import com.datastax.driver.core.Session
import com.typesafe.config.ConfigFactory
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.connectors.{Connector, ContactPoints, KeySpace}
import com.websudos.phantom.database.DatabaseImpl
import com.websudos.phantom.dsl._

import scala.collection.JavaConverters._
import scala.concurrent.Future
import util.FutureUtil._
import util.MeasureUtil._

object Test1 extends App {

  val config = ConfigFactory.load()

  val hosts = config.getStringList("cassandra.host").asScala
  val inets = hosts.map(InetAddress.getByName)
  val keyspace: String = config.getString("cassandra.keyspace")

  val connector = ContactPoints(hosts)

  val db = new MyDatabase(connector.keySpace("initial", autoinit = false))

  (1 to 1000) foreach { _ ⇒
    time("subjects") {
      val serviceFoo = new SubjectService(db.subjects)(KeySpace("foo"))

      serviceFoo.list().await

      val serviceBar = new SubjectService(db.subjects)(KeySpace("bar"))

      serviceBar.list().await
    }

//    fromDb.foreach(println)
  }

//  val res = db.subjects("bar").add(fromDb(0)).await

//  println(res.wasApplied() + " " + res.one())

  db.shutdown()
}

case class Subject(id: UUID, version: Int, info: String)

abstract class SubjectModel extends CassandraTable[SubjectModel, Subject] with RootConnector {
  override def tableName: String = "complex_key"

  object id extends UUIDColumn(this) with PrimaryKey[UUID]
  object version extends IntColumn(this) with PrimaryKey[Int]
  object info extends StringColumn(this)

  override def fromRow(r: Row): Subject =
    Subject(id(r), version(r), info(r))
}

class SubjectService(model: SubjectModel)(implicit keySpace: KeySpace) {
  import model.{session, insert, select}

  def list(): Future[List[Subject]] =
    select.all().fetch()

  def add(subj: Subject) =
    insert()
      .value(_.id, subj.id)
      .value(_.version, subj.version)
      .value(_.info, subj.info)
      .ifNotExists()
      .future()
}

class MyDatabase(override val connector: KeySpaceDef) extends DatabaseImpl(connector) {
  object subjects extends SubjectModel with connector.Connector
}