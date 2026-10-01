/**
 * Created by smallufo on 2026-10-01.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.JSerializable
import destiny.tools.Score
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.ScoreTwoDecimalSerializer
import kotlinx.serialization.Serializable
import kotlin.math.abs

/** 居上的判定基準 */
enum class OvercomingBasis {
  /** 整宮（星座）：只看兩星所在星座，度數不論 */
  SIGN,

  /** 度數：兩星須在容許度內形成相位，跨星座（dissociate）亦算 */
  DEGREE
}

/**
 * 居上 / 凌駕 (Overcoming , καθυπερτέρησις)
 *
 * [superior] 位於 [inferior] 的右側 (dexter) —— 即黃道順序上較前面，
 * 由 [inferior] 往後數，[superior] 落在第 11 / 10 / 9 個位置 ，分別為 六分 / 四分 / 三分 居上。
 * 其中四分（第 10）稱為 decimation ，為最強的居上。
 * 合相、對沖沒有居上。
 *
 * [bySign] 與 [byDegree] 兩個事實一律都計算，與採用哪種 [basis] 無關：
 * - SIGN 版本的結果 [bySign] 必為 true ，[byDegree] = false 代表「名義上居上，度數上未成相位」
 * - DEGREE 版本的結果 [byDegree] 必為 true ，[bySign] = false 代表跨星座 (dissociate)
 */
@Serializable
data class Overcoming(
  val superior: AstroPoint,
  val inferior: AstroPoint,
  /** [SEXTILE] , [SQUARE] 或 [TRINE] */
  val aspect: Aspect,
  /** 此結果由哪種基準判出 */
  val basis: OvercomingBasis,
  /** 實際角距 與 [aspect] 標準角度 的差 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  /** 星座關係是否為此 [aspect] 的居上 */
  val bySign: Boolean,
  /** 度數上是否在容許度內形成此 [aspect] */
  val byDegree: Boolean,
  /** 度數交角評分 ; 度數上未成相位則為 null */
  @Serializable(with = ScoreTwoDecimalSerializer::class)
  val score: Score? = null
) : JSerializable {

  /** 四分居上（第 10 個星座）, 最強 */
  val decimation: Boolean
    get() = aspect == SQUARE
}

interface IOvercoming {

  val basis: OvercomingBasis

  /**
   * 兩星之間是否有居上關係 , 有的話傳回誰居上 ; 無則 null
   */
  fun getOvercoming(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree): Overcoming?

  /**
   * 一張盤中 [points] 兩兩之間的居上關係
   */
  fun getOvercomings(points: Collection<AstroPoint>, posMap: Map<AstroPoint, IZodiacDegree>): List<Overcoming> {
    val list = points.filter { posMap.containsKey(it) }.distinct()
    return list.indices.flatMap { i ->
      (i + 1 until list.size).mapNotNull { j ->
        val p1 = list[i]
        val p2 = list[j]
        getOvercoming(p1, posMap.getValue(p1).zDeg.toZodiacDegree(), p2, posMap.getValue(p2).zDeg.toZodiacDegree())
      }
    }
  }

  companion object {

    /** 由 inferior 往前（逆黃道）數的星座差 → 居上相位 */
    internal val signStepsMap: Map<Int, Aspect> = mapOf(2 to SEXTILE, 3 to SQUARE, 4 to TRINE)

    internal val overcomingAspects: List<Aspect> = listOf(SEXTILE, SQUARE, TRINE)

    /** 由 [from] 順黃道走到 [to] 的角度 , [0, 360) */
    internal fun forward(from: ZodiacDegree, to: ZodiacDegree): Double = (to.value - from.value).mod(360.0)

    /** [superior] 星座 往後數到 [inferior] 星座 的步數 , 對應的居上相位 */
    internal fun signAspect(superior: ZodiacDegree, inferior: ZodiacDegree): Aspect? =
      signStepsMap[(inferior.sign.index - superior.sign.index).mod(12)]
  }
}

/**
 * 整宮（星座）版本 : 只要星座關係成立即為居上 , 度數僅作為附加資訊 ([Overcoming.byDegree])
 */
class OvercomingSignImpl(
  private val aspectEffective: IAspectEffective = AspectEffectiveClassical()
) : IOvercoming, JSerializable {

  override val basis: OvercomingBasis = OvercomingBasis.SIGN

  override fun getOvercoming(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree): Overcoming? {
    return listOf(Triple(p1, deg1, p2 to deg2), Triple(p2, deg2, p1 to deg1))
      .firstNotNullOfOrNull { (sup, supDeg, inf) ->
        val (infP, infDeg) = inf
        IOvercoming.signAspect(supDeg, infDeg)?.let { aspect ->
          val errorAndScore = aspectEffective.getEffectiveErrorAndScore(sup, supDeg, infP, infDeg, aspect)
          Overcoming(
            superior = sup,
            inferior = infP,
            aspect = aspect,
            basis = basis,
            orb = abs(IOvercoming.forward(supDeg, infDeg) - aspect.degree),
            bySign = true,
            byDegree = errorAndScore != null,
            score = errorAndScore?.second
          )
        }
      }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is OvercomingSignImpl) return false
    return aspectEffective == other.aspectEffective
  }

  override fun hashCode(): Int = aspectEffective.hashCode()
}

/**
 * 度數版本 : 兩星須在 [aspectEffective] 的容許度內形成 六分 / 四分 / 三分 ，
 * 黃道上在前（右側）者居上 ; 跨星座 (dissociate) 亦成立 , 此時 [Overcoming.bySign] = false
 */
class OvercomingDegreeImpl(
  private val aspectEffective: IAspectEffective = AspectEffectiveClassical()
) : IOvercoming, JSerializable {

  override val basis: OvercomingBasis = OvercomingBasis.DEGREE

  override fun getOvercoming(p1: AstroPoint, deg1: ZodiacDegree, p2: AstroPoint, deg2: ZodiacDegree): Overcoming? {
    val fwd = IOvercoming.forward(deg1, deg2)
    // 順黃道走 180 度以內抵達對方者，在對方之右 , 居上
    val (sup, supDeg, infP, infDeg) = when {
      fwd > 0 && fwd < 180 -> Quad(p1, deg1, p2, deg2)
      fwd > 180            -> Quad(p2, deg2, p1, deg1)
      else                 -> return null
    }
    val angle = IOvercoming.forward(supDeg, infDeg)

    return IOvercoming.overcomingAspects
      .mapNotNull { aspect ->
        aspectEffective.getEffectiveErrorAndScore(sup, supDeg, infP, infDeg, aspect)?.let { aspect to it.second }
      }
      .minByOrNull { (aspect, _) -> abs(angle - aspect.degree) }
      ?.let { (aspect, score) ->
        Overcoming(
          superior = sup,
          inferior = infP,
          aspect = aspect,
          basis = basis,
          orb = abs(angle - aspect.degree),
          bySign = IOvercoming.signAspect(supDeg, infDeg) == aspect,
          byDegree = true,
          score = score
        )
      }
  }

  private data class Quad(val sup: AstroPoint, val supDeg: ZodiacDegree, val inf: AstroPoint, val infDeg: ZodiacDegree)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is OvercomingDegreeImpl) return false
    return aspectEffective == other.aspectEffective
  }

  override fun hashCode(): Int = aspectEffective.hashCode()
}
