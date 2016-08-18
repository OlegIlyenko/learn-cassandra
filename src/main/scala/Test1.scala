import java.net.InetAddress

import com.typesafe.config.ConfigFactory
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.connectors.{Connector, ContactPoint, ContactPoints}
import com.websudos.phantom.database.DatabaseImpl
import com.websudos.phantom.dsl._

import scala.collection.JavaConverters._
import scala.concurrent.Future

import util.FutureUtil._

object Test1 extends App {

  val config = ConfigFactory.load()

  val hosts = config.getStringList("cassandra.host").asScala
  val inets = hosts.map(InetAddress.getByName)
  val keyspace: String = config.getString("cassandra.keyspace")

  lazy val connector = ContactPoints(hosts).keySpace(keyspace)

  val db = new MyDatabase(connector)

  db.subjects.list().await.foreach(println)
}

case class Subject(id: UUID, version: Long, info: String)

abstract class SubjectModel extends CassandraTable[SubjectModel, Subject] with RootConnector {
  override def tableName: String = "complex_key"

  object id extends UUIDColumn(this) with PrimaryKey[UUID]
  object version extends IntColumn(this) with PrimaryKey[Int]
  object info extends StringColumn(this)

  override def fromRow(r: Row): Subject =
    Subject(id(r), version(r), info(r))

  def list(): Future[List[Subject]] =
    select.all().fetch()
}

class MyDatabase(override val connector: KeySpaceDef) extends DatabaseImpl(connector) {
  object subjects extends SubjectModel with connector.Connector
}