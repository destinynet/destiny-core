/**
 * @author smallufo
 * Created on 2007/11/22 at 下午 10:42:18
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IAspectEffective
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.DestinyMarker
import java.io.Serializable
import kotlin.math.abs

/**
 * 「古典占星」的交角判定，古典占星只計算「星體光芒的容許度」與「交角」是否有效，並沒有考慮交角的類型。
 * （「現代占星」則是各種交角都有不同的容許度）
 * 演算法採用 Template Method design pattern
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 *
 * @param planetOrbsImpl 星芒交角 , 內定採用 [PointDiameterAlBiruniImpl]
 */
class AspectEffectiveClassical(
  val planetOrbsImpl: IPointDiameter = PointDiameterAlBiruniImpl(),
  /** 符合交角的評分，內定從幾分開始算起 */
  private val defaultThreshold: Double = 0.6) : IAspectEffective, Serializable {

  override val applicableAspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()

  private fun getAngleDiff(deg1: ZodiacDegree, deg2: ZodiacDegree, angle: Double): Double {
    return abs(deg1.getAngle(deg2) - angle)
  }

  private fun getSumOfRadius(p1: AstroPoint, p2: AstroPoint): Double {
    return (planetOrbsImpl.getDiameter(p1) + planetOrbsImpl.getDiameter(p2)) / 2
  }

  override fun getEffectiveErrorAndScore(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Pair<Double, Double>? {
    val angleDiff = getAngleDiff(deg1, deg2, aspect.degree)
    val sumOfRadius = getSumOfRadius(p1, p2)

    return angleDiff
      .takeIf { it <= sumOfRadius }
      ?.let { it to (defaultThreshold + (1 - defaultThreshold) * (sumOfRadius - angleDiff) / sumOfRadius) }
  }

  /**
   * classical 交角不談容許度 (orb)
   */
  fun isEffective(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, angle: Double): Boolean {
    val angleDiff = getAngleDiff(deg1, deg2, angle)
    val sumOfRadius = getSumOfRadius(p1, p2)
    return (angleDiff <= sumOfRadius)
  }

  /**
   * classical 交角不談容許度 (orb)
   */
  fun isEffective(p1: AstroPoint, deg1: Double, p2: AstroPoint, deg2: Double, angle: Double): Boolean {
    return isEffective(p1 , deg1.toZodiacDegree() , p2 , deg2.toZodiacDegree(), angle)
  }

  /** 兩星體是否形成有效交角  */
  override fun isEffective(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree, aspect: Aspect): Boolean {
    return isEffective(p1, deg1, p2, deg2, aspect.degree)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AspectEffectiveClassical) return false

    if (planetOrbsImpl != other.planetOrbsImpl) return false
    if (defaultThreshold != other.defaultThreshold) return false
    if (applicableAspects != other.applicableAspects) return false

    return true
  }

  override fun hashCode(): Int {
    var result = planetOrbsImpl.hashCode()
    result = 31 * result + defaultThreshold.hashCode()
    result = 31 * result + applicableAspects.hashCode()
    return result
  }
}

/**
 * builder for 古典占星 [AspectEffectiveClassical] 交角容許度
 */
@DestinyMarker
class AspectEffectiveClassicalBuilder {
  var planetOrbsImpl: IPointDiameter = PointDiameterAlBiruniImpl()
  var defaultThreshold: Double = 0.6
  operator fun invoke(block: AspectEffectiveClassicalBuilder.() -> Unit = {}): AspectEffectiveClassical {
    block.invoke(this)
    return AspectEffectiveClassical(planetOrbsImpl, defaultThreshold)
  }

  companion object {
    fun aspectEffectiveClassical(block: AspectEffectiveClassicalBuilder.() -> Unit = {}): AspectEffectiveClassical {
      val builder = AspectEffectiveClassicalBuilder()
      return builder {
        block()
      }
    }
  }
}
