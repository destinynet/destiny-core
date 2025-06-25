/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:34:57
 */
package destiny.core.astrology

import destiny.core.IGmtJulDay
import destiny.core.astrology.IPointAspectPattern.AspectType
import destiny.core.calendar.GmtJulDay
import destiny.tools.Score
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ScoreTwoDecimalSerializer
import java.io.Serializable


interface IAspectData : IPointAspectPattern, IGmtJulDay

/**
 * 存放兩顆「不同」星體交角的資料結構
 * */
@kotlinx.serialization.Serializable
data class AspectData(
  val pointAspectPattern: PointAspectPattern,
  /** 交會型態 : 接近 or 分離 */
  override val aspectType: AspectType? = null,
  /** orb 不列入 equals / hashCode 計算  */
  @kotlinx.serialization.Serializable(with = DoubleTwoDecimalSerializer::class)
  override val orb: Double = 0.0,
  /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
  @kotlinx.serialization.Serializable(with = ScoreTwoDecimalSerializer::class)
  override val score: Score? = null,
  override val gmtJulDay: GmtJulDay
) : IAspectData, IPointAspectPattern by pointAspectPattern, Serializable {

  private constructor(
    /** 存放形成交角的兩顆「不同」星體  */
    points: Set<AstroPoint>,
    /** 兩星所形成的交角 */
    aspect: Aspect,
    /** 交會型態 : 接近 or 分離 */
    aspectType: AspectType? = null,
    /** orb 不列入 equals / hashCode 計算  */
    orb: Double = 0.0,
    /** 交角緊密度評分 , nullable or (0~1) , 不列入 equals / hashCode 計算 */
    score: Score? = null,
    gmtJulDay: GmtJulDay
  ) : this(
    PointAspectPattern(points.toList(), aspect.degree, aspectType, orb, score),
    aspectType, orb, score, gmtJulDay
  )


  init {
    require(points.size == 2) { "INCORRECT points" }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AspectData) return false

    if (points != other.points) return false
    if (aspect != other.aspect) return false
    return aspectType == other.aspectType
  }

  override fun hashCode(): Int {
    var result = points.hashCode()
    result = 31 * result + aspect.hashCode()
    result = 31 * result + (aspectType?.hashCode() ?: 0)
    return result
  }


  companion object {

    fun of(p1: AstroPoint, p2: AstroPoint, aspect: Aspect, orb: Double, score: Score? = null, aspectType: AspectType? = null, gmtJulDay: GmtJulDay): AspectData {
      val points = if (p1 != p2) {
        sortedSetOf(AstroPointComparator, p1, p2)
      } else {
        listOf(p1, p2).toSet()
      }
      return AspectData(points, aspect, aspectType, orb, score, gmtJulDay)
    }

    fun of(points: List<AstroPoint>, aspect: Aspect, orb: Double, score: Score? = null, aspectType: AspectType? = null, gmtJulDay: GmtJulDay): AspectData? {
      return points.takeIf { it.toSet().size == 2 }
        ?.let { ps ->
          val (p1, p2) = ps.iterator().let { it.next() to it.next() }
          of(p1, p2, aspect, orb, score, aspectType, gmtJulDay)
        }
    }
  }


}
