package io.koff.hll

import com.twitter.algebird.HyperLogLogMonoid

/**
 * Simple example of using HLL from algebird
 */
object SimpleAlgebirdExample {
  def main(args: Array[String]) {
    //define test data
    val data = Seq("aaa", "bbb", "ccc")
    //create algebird HLL
    val hll = new HyperLogLogMonoid(bits = 10)
    //convert data elements to a seq of hlls
    val hlls = data.map { str =>
      val bytes = str.getBytes("utf-8")
      hll.create(bytes)
    }

    //merge seq of hlls in one hll object
    val merged = hll.sum(hlls)

    //WARN: don`t use merged.size - it is a different thing
    //get estimate count from merged hll
    println("estimate count: " + hll.sizeOf(merged).estimate)
  }
}
