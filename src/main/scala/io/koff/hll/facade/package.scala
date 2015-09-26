package io.koff.hll

/**
 * Facade for different implementations of HLL
 */
package object facade {

  /**
   * A common interface for hll counter
   */
  trait HLL {
    /**
     * Returns estimate count of unique values in this counter
     * @return estimate count of unique values
     */
    def count: Long

    /**
     * Merge two counters together
     * @param hll another counter to merge
     * @return new hll counter
     */
    def merge(hll: HLL): HLL

    /**
     * Just alias for `merge(...)` method
     */
    def +(hll: HLL) : HLL = merge(hll)
  }

  /**
   * A commong interface for hll builder
   */
  trait HLLBuilder {
    /**
     * Create an empty hll counter
     * @return a new empty hll counter
     */
    def create: HLL

    /**
     * Create a counter with one element
     * @param bytes bytes to add in a new counter
     * @return a new hll counter with one element
     */
    def create(bytes: Array[Byte]): HLL
  }

  /**
   * Enriches string by `.toHLL` operation
   */
  implicit class HLLString(str: String) {

    /**
     * Convert string value to HLL counter with one element
     * @param hllBuilder builder which will be used to create HLL object
     * @return new hll object
     */
    def toHLL(implicit hllBuilder: HLLBuilder): HLL = {
      val strBytes = str.getBytes("utf-8")
      hllBuilder.create(strBytes)
    }
  }
}
