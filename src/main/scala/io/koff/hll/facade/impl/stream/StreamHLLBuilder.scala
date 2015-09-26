package io.koff.hll.facade.impl.stream

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus
import io.koff.hll.facade.{HLL, HLLBuilder}

/**
 * Builder implementation for stream
 * @author coffius@gmail.com
 */
class StreamHLLBuilder extends HLLBuilder {
  override def create: HLL = new StreamHLLWrapper(new HyperLogLogPlus(16, 25))

  override def create(bytes: Array[Byte]): HLL = {
    val newHLL = new HyperLogLogPlus(16, 25)
    newHLL.offer(bytes)
    new StreamHLLWrapper(newHLL)
  }

}

/**
 * Wrapper for algebird HLLs
 * @param impl internal implementation
 * @author coffius@gmail.com
 */
class StreamHLLWrapper(private val impl: HyperLogLogPlus) extends HLL {
  override def count: Long = impl.cardinality()

  override def merge(hll: HLL): HLL = {
    if(!hll.isInstanceOf[StreamHLLWrapper]){
      val thisClassName = this.getClass.getSimpleName
      val argClassName = hll.getClass.getSimpleName
      throw new IllegalArgumentException(s"can`t merge different implementations of HLL: this: $thisClassName, arg: $argClassName")
    } else {
      val argHLL = hll.asInstanceOf[StreamHLLWrapper].impl
      val newHLL = new HyperLogLogPlus(16, 25)
      newHLL.merge(impl, argHLL)
      new StreamHLLWrapper(newHLL)
    }
  }
}
