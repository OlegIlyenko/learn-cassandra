package util

import language.postfixOps

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object FutureUtil {
  implicit class FutureResult[T](f: Future[T]) {
    def await = Await.result(f, 10 seconds)
  }
}
