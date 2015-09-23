package io.koff.hll

import hyperloglog.HyperLogLog.HyperLogLogBuilder

/**
 * Simple example of using HLL from Prasanthj
 */
object SimplePrasanthjHllExample {
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
    data.foreach(mergedHll.addString)

    //print estimation of count
    println("estimate count: " + mergedHll.count())
  }
}
