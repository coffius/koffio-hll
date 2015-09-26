package io.koff.hll

/**
 * Simple example of using HLL from stream
 */
object SimpleStreamExample {
  import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus
  def main(args: Array[String]) {
    //define test data
    val data = Seq("aaa", "bbb", "ccc")

    //create HLL object in which we will add our data.
    // You can set parameters here in a constructor
    val merged = new HyperLogLogPlus(5, 25)

    //adding data in hll
    data.foreach{ elem =>
      //in order to control string encoding during string conversion to bytes we explicitly set using encoding
      val bytes = elem.getBytes("utf-8")
      merged.offer(bytes)
    }

    //print the estimation
    println("estimate count: " + merged.cardinality())
  }
}
