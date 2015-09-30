package io.koff.hll.facade.impl.agkn

import com.google.common.hash.Hashing
import io.koff.hll.facade.{HLL, HLLBuilder}

import net.agkn.hll.{HLL => AgknHLL}

/**
 * Builder implementation for agkn
 * @author coffius@gmail.com
 */
class AgknHLLBuilder extends HLLBuilder {
  private val seed = 123456
  private val hash = Hashing.murmur3_128(seed)

  override def create: HLL = new AgknHLLWrapper(new AgknHLL(16, 5))

  override def create(bytes: Array[Byte]): HLL = {
    val hasher = hash.newHasher()
    hasher.putBytes(bytes)
    val firstValue = hasher.hash().asLong()

    val newHll = new AgknHLL(16, 5)
    newHll.addRaw(firstValue)
    new AgknHLLWrapper(newHll)
  }
}

class AgknHLLWrapper(private val impl: AgknHLL) extends HLL {
  override def count: Long = impl.cardinality()

  override def merge(hll: HLL): HLL = {
    if(!hll.isInstanceOf[AgknHLLWrapper]){
      val thisClassName = this.getClass.getSimpleName
      val argClassName = hll.getClass.getSimpleName
      throw new IllegalArgumentException(s"can`t merge different implementations of HLL: this: $thisClassName, arg: $argClassName")
    } else {
      val newHll = new AgknHLL(16, 5)
      newHll.union(impl)
      newHll.union(hll.asInstanceOf[AgknHLLWrapper].impl)
      new AgknHLLWrapper(newHll)
    }
  }
}