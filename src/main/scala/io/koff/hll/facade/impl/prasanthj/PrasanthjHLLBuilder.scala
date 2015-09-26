package io.koff.hll.facade.impl.prasanthj

import hyperloglog.HyperLogLog
import hyperloglog.HyperLogLog.HyperLogLogBuilder
import io.koff.hll.facade.{HLL, HLLBuilder}

/**
 * Implementation for prasanthj/hyperloglog hll counters
 * @author coffius@gmail.com
 */
class PrasanthjHLLBuilder extends HLLBuilder{
  private val hllBuilder = new HyperLogLogBuilder()

  override def create: HLL = new PrasanthjHLLWrapper(hllBuilder.build(), hllBuilder)

  override def create(bytes: Array[Byte]): HLL = {
    val newHLL = hllBuilder.build()
    newHLL.addBytes(bytes)
    new PrasanthjHLLWrapper(newHLL, hllBuilder)
  }
}

/**
 * Wrapper for Prasanthj HLLs
 * @param impl internal implementation
 * @author coffius@gmail.com
 */
class PrasanthjHLLWrapper(private val impl: HyperLogLog, private val builder: HyperLogLogBuilder) extends HLL {

  override def count: Long = impl.count()

  override def merge(hll: HLL): HLL = {
    if(!hll.isInstanceOf[PrasanthjHLLWrapper]){
      val thisClassName = this.getClass.getSimpleName
      val argClassName = hll.getClass.getSimpleName
      throw new IllegalArgumentException(s"can`t merge different implementations of HLL: this: $thisClassName, arg: $argClassName")
    } else {
      val newHLL = builder.build()
      newHLL.merge(impl)
      newHLL.merge(hll.asInstanceOf[PrasanthjHLLWrapper].impl)
      new PrasanthjHLLWrapper(newHLL, builder)
    }
  }
}
