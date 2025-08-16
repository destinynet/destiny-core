/**
 * @author smallufo
 * Created on 2007/11/24 at 下午 9:01:16
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import jakarta.inject.Named
import java.io.Serializable
import kotlin.math.abs

/**
 * 現代占星的交角容許度
 * 參考資料
 * http://www.myastrologybook.com/aspects-and-orbs.htm
 */
@Named
class AspectEffectiveModern(
  /** 「不考慮行星」的交角容許度 */
  private val aspectOrbsImpl: IAspectOrbs = AspectOrbsDefaultImpl(),
  /** 「考量行星的」交角容許度實作 */
  val aspectOrbsPlanetImpl: IAspectOrbsPlanet = AspectOrbsPlanetDefaultImpl()
) : IAspectEffective, Serializable {

  override val applicableAspects: Set<Aspect> = Aspect.entries.toSet()


  /** 直接比對度數是否形成交角，不考慮星體  */
  fun isEffective(deg1: Double, deg2: Double, aspect: Aspect): Boolean {
    val angle = ZodiacDegree.getAngle(deg1, deg2)
    return abs(angle - aspect.degree) <= aspectOrbsImpl.getAspectOrb(aspect)
  }

  /** 兩星交角容許度是多少 */
  private fun getOrb(p1: AstroPoint, p2: AstroPoint, aspect: Aspect): Double {
    //從「考量行星」的交角容許度實作找起
    return aspectOrbsPlanetImpl.getPlanetAspectOrb(p1, p2, aspect)
      ?: aspectOrbsImpl.getAspectOrb(aspect) // 再從「不考慮行星」的交角容許度尋找
  }

  /** 兩星交角容許度是多少 , 以及過了容許度的起始分數為多少 (0~1) */
  private fun getOrbAndThresholdScore(p1: AstroPoint, p2: AstroPoint, aspect: Aspect): Pair<Double, Double> {
    if (p1 is FixedStar || p2 is FixedStar) {
      // TODO : 恆星的合相容許度則極其狹窄，通常只看 1度到 1.5度 以內。只有像軒轅十四 (Regulus)、畢宿五 (Aldebaran)、心宿二 (Antares)、北落師門 (Fomalhaut) 這四顆古代的「王室之星」，容許度才可能放寬到2度左右。
      return 1.5 to 0.8
    }
    //從「考量行星」的交角容許度實作找起
    return aspectOrbsPlanetImpl.getPlanetAspectOrbAndThreshold(p1, p2, aspect)
    // 再從「不考慮行星」的交角容許度尋找
      ?: aspectOrbsImpl.getAspectOrbAndThreshold(aspect)
  }

  override fun getEffectiveErrorAndScore(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Pair<Double, Score>? {
    val (orb, threshold) = getOrbAndThresholdScore(p1, p2, aspect)
    val angle = deg1.getAngle(deg2)
    val angleDiff = abs(angle - aspect.degree)

    return angleDiff
      .takeIf { it <= orb }
      ?.let { it to (threshold + (1 - threshold) * (orb - angleDiff) / orb).toScore() }
  }

  fun getEffectiveErrorAndScore(p1: AstroPoint, deg1: Double, p2: AstroPoint, deg2: Double, aspect: Aspect): Pair<Double, Score>? {
    return getEffectiveErrorAndScore(p1, deg1.toZodiacDegree(), p2, deg2.toZodiacDegree(), aspect)
  }

  /** 有些版本有考慮星體，例如：太陽月亮的交角，會有較高的容許度  */
  override fun isEffective(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Boolean {
    val orb = getOrb(p1, p2, aspect)
    val angle = deg1.getAngle(deg2)
    val angleDiff = abs(angle - aspect.degree)

    return (angleDiff <= orb)
  }


  companion object {

    fun isEffective(deg1: Double, deg2: Double, aspect: Aspect, orb: Double): Boolean {
      return isEffective(deg1.toZodiacDegree(), deg2.toZodiacDegree(), aspect, orb)
    }

    fun isEffective(deg1: ZodiacDegree, deg2: ZodiacDegree, aspect: Aspect, orb: Double): Boolean {
      val angle = deg1.getAngle(deg2)
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

