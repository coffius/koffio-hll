package io.koff.hll

import java.io.ByteArrayOutputStream
import java.util.UUID

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus
import com.twitter.algebird.HyperLogLogMonoid
import hyperloglog.HyperLogLog.HyperLogLogBuilder
import hyperloglog.HyperLogLogUtils

/**
 * Comparison of implementations
 */
object Comparison {

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

    algebirdResult.print(1000000 + 1)
    prasanthjResult.print(1000000 + 1)
    streamResult.print(1000000 + 1)
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
    //create a builder for HLL.
    val hllBuilder = new HyperLogLogBuilder()

    //create hll object in which we will merge our data
    val merged = hllBuilder.build()

    val startTime = System.currentTimeMillis()
    //merge data
    testData.foreach { elem =>
      //explicitly set using encoding
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
    //create HLL object in which we will add our data.
    // You can set parameters here in a constructor
    val merged = new HyperLogLogPlus(16, 25)

    val startTime = System.currentTimeMillis()
    //adding data in hll
    testData.foreach{ elem =>
      //in order to control string encoding during string conversion to bytes we explicitly set using encoding
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

  case class EstimationResult(name: String, estimateCount: Long, size: Long, durationMsec: Long) {
    def print(realCount: Long): Unit = {
      val error = 1d - estimateCount.toDouble / realCount.toDouble
      println(f"$name[error: $error%2.6f%%]= " + this)
    }
  }
}
