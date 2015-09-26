package io.koff.hll.facade

/**
 * Utility class for intersection
 * @author coffius@gmail.com
 */
object HLLUtils {
  /**
   * Calculate count of common elements in hlls in the seq.<br/>
   * Adaptation of com.twitter.algebird.HyperLogLogMonoid#intersectionSize.
   *
   * @param hlls hlls for calcs
   * @return count of common elements
   */
  def intersection(hlls: HLL*): Long = {
    hlls.headOption.map{ head =>
      val tail = hlls.tail

      head.count + intersection(tail:_*) - intersection(tail.map(_ + head):_*)
    }.getOrElse(0L)
  }
}
