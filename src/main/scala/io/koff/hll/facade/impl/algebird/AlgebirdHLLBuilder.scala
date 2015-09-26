package io.koff.hll.facade.impl.algebird

import com.twitter.algebird.{HLL => AlgeBirdHLL, HyperLogLogMonoid}
import io.koff.hll.facade.{HLL, HLLBuilder}

/**
 * Builder implementation for algebird
 * @author coffius@gmail.com
 */
class AlgebirdHLLBuilder extends HLLBuilder {
  private val monoid = new HyperLogLogMonoid(bits = 10)

  override def create: HLL = new AlgebirdHLLWrapper(monoid.zero)

  override def create(bytes: Array[Byte]): HLL = new AlgebirdHLLWrapper(monoid.create(bytes))
}

/**
 * Wrapper for algebird HLLs
 * @param impl internal implementation
 * @author coffius@gmail.com
 */
class AlgebirdHLLWrapper(private val impl: AlgeBirdHLL) extends HLL {
  override def count: Long = impl.approximateSize.estimate

  override def merge(hll: HLL): HLL = {
    if(!hll.isInstanceOf[AlgebirdHLLWrapper]){
      val thisClassName = this.getClass.getSimpleName
      val argClassName = hll.getClass.getSimpleName
      throw new IllegalArgumentException(s"can`t merge different implementations of HLL: this: $thisClassName, arg: $argClassName")
    } else {
      new AlgebirdHLLWrapper(impl + hll.asInstanceOf[AlgebirdHLLWrapper].impl)
    }
  }
}