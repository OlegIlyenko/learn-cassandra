package util

object MeasureUtil {
  def time[T](label: String)(fn: ⇒ T) = {
    val start = System.currentTimeMillis()
    val res = fn
    val end = System.currentTimeMillis()

    println(s"Time ($label): ${end - start} ms")

    res
  }
}
