package io.koff.hll

import java.io.ByteArrayOutputStream
import java.util.UUID

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus
import com.google.common.hash.Hashing
import com.twitter.algebird.HyperLogLogMonoid
import hyperloglog.HyperLogLog.HyperLogLogBuilder
import hyperloglog.HyperLogLogUtils

/**
 * Comparison of implementations
 */
object Comparison {

  /**
   * Generate test data. 1000000 of rnd UUID values and 1000000 copies of a same value. 2000000 in total
   * @return
   */
  def generateTestData(): Seq[String] = {
    val commonData = (1 to 1000000).map(_ => "0x0000000000000000")

    val rndData = for {
      _ <- 1 to 1000000
    } yield {
      UUID.randomUUID().toString
    }

    commonData ++ rndData
  }

  def main(args: Array[String]) {
    val testData = generateTestData()

    val algebirdResult  = algebird(testData)
    val prasanthjResult = prasanthjHll(testData)
    val streamResult    = streamHll(testData)
    val agknResult         = agknHll(testData)

    val realCount = 1000000 + 1
    algebirdResult.print(realCount)
    prasanthjResult.print(realCount)
    streamResult.print(realCount)
    agknResult.print(realCount)
  }

  def algebird(testData: Seq[String]): EstimationResult = {
    //create algebird HLL
    val monoid = new HyperLogLogMonoid(bits = 16)
    var merged = monoid.zero

    val startTime = System.currentTimeMillis()
    //convert data elements to a seq of hlls
    testData.foreach{ str =>
      val bytes = str.getBytes("utf-8")
      val hll = monoid.create(bytes)
      merged = monoid.plus(merged, hll)
    }
    val calcTime = System.currentTimeMillis() - startTime

    EstimationResult(
      name = "algebird",
      estimateCount = merged.approximateSize.estimate,
      size = merged.size,
      durationMsec = calcTime
    )
  }

  def prasanthjHll(testData: Seq[String]): EstimationResult = {
    val hllBuilder = new HyperLogLogBuilder()
    val merged = hllBuilder.build()

    val startTime = System.currentTimeMillis()
    testData.foreach { elem =>
      val bytes = elem.getBytes("utf-8")
      merged.addBytes(bytes)
    }
    val calcTime = System.currentTimeMillis() - startTime

    val byteStream = new ByteArrayOutputStream(1024)
    HyperLogLogUtils.serializeHLL(byteStream, merged)
    val array = byteStream.toByteArray

    EstimationResult(
      name = "prasanthj",
      estimateCount = merged.count(),
      size = array.size,
      durationMsec = calcTime
    )
  }

  def streamHll(testData: Seq[String]): EstimationResult = {
    val merged = new HyperLogLogPlus(16, 25)

    val startTime = System.currentTimeMillis()
    testData.foreach{ elem =>
      val bytes = elem.getBytes("utf-8")
      merged.offer(bytes)
    }
    val calcTime = System.currentTimeMillis() - startTime

    EstimationResult(
      name = "stream",
      estimateCount = merged.cardinality(),
      size = merged.getBytes.size,
      durationMsec = calcTime
    )
  }

  def agknHll(testData: Seq[String]): EstimationResult = {
    import net.agkn.hll.HLL

    val seed = 123456
    val hash = Hashing.murmur3_128(seed)

    def toHash(str: String): Long = {
      val hasher = hash.newHasher()
      //As always we set encoding explicitly
      hasher.putBytes(str.getBytes("utf-8"))
      hasher.hash().asLong()
    }

    val merged = new HLL(16, 5)

    val startTime = System.currentTimeMillis()

    testData.foreach(str => merged.addRaw(toHash(str)))

    val calcTime = System.currentTimeMillis() - startTime

    EstimationResult(
      name = "agkn-hll",
      estimateCount = merged.cardinality(),
      size = merged.toBytes.size,
      durationMsec = calcTime
    )
  }

  case class EstimationResult(name: String, estimateCount: Long, size: Long, durationMsec: Long) {
    def print(realCount: Long): Unit = {
      val error = 1d - estimateCount.toDouble / realCount.toDouble
      println(f"$name[error: $error%2.6f%%, calcTime: $durationMsec msecs, estimateCount: $estimateCount, dataSize: $size bytes]")
    }
  }
}
