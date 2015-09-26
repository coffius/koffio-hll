package io.koff.hll

/**
 * A common algorithm to find intersection of several HLL counters
 */
object Intersection {
  import io.koff.hll.facade._
  import io.koff.hll.facade.impl.algebird._
  def main(args: Array[String]) {
    //"444", "555" and "666" are common elements
    def set1 = Seq("111", "222", "333", "444", "555", "666").toHLL
    def set2 = Seq("222", "333", "444", "555", "666", "777").toHLL
    def set3 = Seq("333", "444", "555", "666", "777", "888").toHLL
    def set4 = Seq("444", "555", "666", "777", "888", "999").toHLL

    val intersectionCount = HLLUtils.intersection(set1, set2, set3, set4)
    println("intersection: " + intersectionCount) //will print "intersection: 3"
  }
}
