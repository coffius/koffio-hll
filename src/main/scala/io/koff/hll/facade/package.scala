package io.koff.hll

import scala.UnsupportedOperationException

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

  trait HLLConverter[A]{
    def toHLL(value: A)(implicit hllBuilder: HLLBuilder): HLL
  }

  implicit val stringConverter = new HLLConverter[String] {
    override def toHLL(value: String)(implicit hllBuilder: HLLBuilder): HLL = {
      val strBytes = value.getBytes("utf-8")
      hllBuilder.create(strBytes)
    }
  }
  
  /**
   * Enriches string by `.toHLL` operation
   */
  implicit class HLLString(value: String) {

    /**
     * Convert string value to HLL counter with one element
     * @param hllBuilder builder which will be used to create HLL object
     * @return new hll object
     */
    def toHLL(implicit hllBuilder: HLLBuilder): HLL = stringConverter.toHLL(value)
  }

  /**
   * Enriches traversables by `.toHLL`
   */
  implicit class HLLTraversable[A](traversable: Traversable[A]){
    /**
     * Returns counter which contains all the elements from traversable
     */
    def toHLL(implicit hllBuilder: HLLBuilder, hllConverter: HLLConverter[A]): HLL = {
      traversable.foldLeft(hllBuilder.create)(_ + hllConverter.toHLL(_))
    }
  }
}
