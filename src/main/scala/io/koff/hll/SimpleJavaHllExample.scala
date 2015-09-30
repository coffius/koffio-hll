package io.koff.hll

import com.google.common.hash.{Hashing, Murmur3_128HashFunction}

/**
 * Simple example of using https://github.com/aggregateknowledge/java-hll
 */
object SimpleJavaHllExample {
  import net.agkn.hll.HLL

  private val seed = 123456
  /** use murmur3 hash function from `com.google.common.hash`*/
  private val hash = Hashing.murmur3_128(seed)

  def main(args: Array[String]) {
    //define test data
    val data = Seq("aaa", "bbb", "ccc")

    //create hll object in which we will merge our data with default values of params
    val hll = new HLL(13, 5)

    //add data to the hll counter
    data.foreach(str => hll.addRaw(toHash(str)))

    println("estimate count: " + hll.cardinality())
  }

  def toHash(str: String): Long = {
    val hasher = hash.newHasher()
    //As always we set encoding explicitly
    hasher.putBytes(str.getBytes("utf-8"))
    hasher.hash().asLong()
  }
}
