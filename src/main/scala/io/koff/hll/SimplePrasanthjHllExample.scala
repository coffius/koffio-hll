package io.koff.hll

/**
 * Simple example of using HLL from Prasanthj
 */
object SimplePrasanthjHllExample {
  import hyperloglog.HyperLogLog.HyperLogLogBuilder
  def main(args: Array[String]) {
    //define test data
    val data = Seq("aaa", "bbb", "ccc")

    //create a builder for HLL.
    val hllBuilder = new HyperLogLogBuilder()
    // You can set different parameters for it using
    // hllBuilder.setEncoding(...)
    // hllBuilder.setNumHashBits(...)
    // hllBuilder.setNumRegisterIndexBits(...)
    // hllBuilder.enableBitPacking(...)
    // hllBuilder.enableNoBias(...)

    //create hll object in which we will merge our data
    val mergedHll = hllBuilder.build()

    //merge data
    data.foreach { elem =>
      //explicitly set using encoding
      val bytes = elem.getBytes("utf-8")
      mergedHll.addBytes(bytes)
    }

    //print the estimation of count
    println("estimate count: " + mergedHll.count())
  }
}
