/**
 * @author smallufo
 * Created on 2007/11/24 at 下午 9:01:16
 */
package destiny.core.astrology

import java.io.Serializable
import kotlin.math.abs

/**
 * 現代占星的交角容許度
 * 參考資料
 * http://www.myastrologybook.com/aspects-and-orbs.htm
 */
class AspectEffectiveModern(
  /** 「不考慮行星」的交角容許度 */
  val aspectOrbsImpl: IAspectOrbs = AspectOrbsDefaultImpl(),
  /** 「考量行星的」交角容許度實作 */
  val aspectOrbsPlanetImpl: IAspectOrbsPlanet = AspectOrbsPlanetDefaultImpl()
) : IAspectEffective, Serializable {

  override val applicableAspects: Collection<Aspect> = Aspect.values().toList()


  /** 直接比對度數是否形成交角，不考慮星體  */
  fun isEffective(deg1: Double, deg2: Double, aspect: Aspect): Boolean {
    val angle = IHoroscopeModel.getAngle(deg1, deg2)
    return abs(angle - aspect.degree) <= aspectOrbsImpl.getAspectOrb(aspect)
  }

  /** 兩星交角容許度是多少 */
  private fun getOrb(p1: Point, p2: Point, aspect: Aspect): Double {
    //從「考量行星」的交角容許度實作找起
    return aspectOrbsPlanetImpl.getPlanetAspectOrb(p1, p2, aspect)
      ?: aspectOrbsImpl.getAspectOrb(aspect) // 再從「不考慮行星」的交角容許度尋找
  }

  /** 兩星交角容許度是多少 , 以及過了容許度的起始分數為多少 (0~1) */
  private fun getOrbAndThresholdScore(p1: Point, p2: Point, aspect: Aspect): Pair<Double, Double> {
    //從「考量行星」的交角容許度實作找起
    return aspectOrbsPlanetImpl.getPlanetAspectOrbAndThreshold(p1, p2, aspect)
    // 再從「不考慮行星」的交角容許度尋找
      ?: aspectOrbsImpl.getAspectOrbAndThreshold(aspect)
  }

  override fun getEffectiveErrorAndScore(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Pair<Double, Double>? {
    val (orb, threshold) = getOrbAndThresholdScore(p1, p2, aspect)
    val angle = IHoroscopeModel.getAngle(deg1, deg2)
    val angleDiff = abs(angle - aspect.degree)

    return if (angleDiff <= orb)
      angleDiff to (threshold + (1 - threshold) * (orb - angleDiff) / orb)
    else
      null
  }

  /** 有些版本有考慮星體，例如：太陽月亮的交角，會有較高的容許度  */
  override fun isEffective(p1: Point, deg1: Double, p2: Point, deg2: Double, aspect: Aspect): Boolean {
    val orb = getOrb(p1, p2, aspect)
    val angle = IHoroscopeModel.getAngle(deg1, deg2)
    val angleDiff = abs(angle - aspect.degree)

    return (angleDiff <= orb)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AspectEffectiveModern) return false

    if (aspectOrbsImpl != other.aspectOrbsImpl) return false
    if (aspectOrbsPlanetImpl != other.aspectOrbsPlanetImpl) return false
    if (applicableAspects != other.applicableAspects) return false

    return true
  }

  override fun hashCode(): Int {
    var result = aspectOrbsImpl.hashCode()
    result = 31 * result + aspectOrbsPlanetImpl.hashCode()
    result = 31 * result + applicableAspects.hashCode()
    return result
  }


  companion object {

    fun isEffective(deg1: Double, deg2: Double, aspect: Aspect, orb: Double): Boolean {
      val angle = IHoroscopeModel.getAngle(deg1, deg2)
      return abs(angle - aspect.degree) <= orb
    }
  }
}


/**
 * builder for 現代占星 [AspectEffectiveModern] 交角容許度
 */
class AspectEffectiveModernBuilder {
  /** 「不考慮行星」的交角容許度 */
  var aspectOrbsImpl: IAspectOrbs = AspectOrbsDefaultImpl()

  /** 「考量行星的」交角容許度實作 */
  var aspectOrbsPlanetImpl: IAspectOrbsPlanet = AspectOrbsPlanetDefaultImpl()

  operator fun invoke(block: AspectEffectiveModernBuilder.() -> Unit = {}): AspectEffectiveModern {
    block.invoke(this)
    return AspectEffectiveModern(aspectOrbsImpl, aspectOrbsPlanetImpl)
  }

  companion object {
    fun aspectEffectiveModern(block: AspectEffectiveModernBuilder.() -> Unit = {}): AspectEffectiveModern {
      val builder = AspectEffectiveModernBuilder()
      return builder {
        block()
      }
    }
  }
}
