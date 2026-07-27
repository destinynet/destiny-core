/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.calendar.ILatLng
import destiny.core.calendar.ILocationPlace
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore

/** 相對「基準地」，某星體從第 [from] 宮移到第 [to] 宮 */
data class HouseShift(val point: AstroPoint, val from: Int, val to: Int)

/** 某星體最近的一條角線：角別 + 地表大圓角距（度）+ 公里（同一量，設計 doc 4.4） */
data class LineDistance(val angle: GeoAngle, val deg: Double, val km: Double)

/**
 * 一個候選地點的客觀幾何描述（產品層共用型別，設計 doc 5.1）。
 *
 * [houseShifts] 為空 + 各線距離變化量低 → 兩地「占星上等價」。
 * [relocatedChart] 每個候選都要跑一次完整換置盤，候選多時可只對 top-N 展開 → nullable。
 */
data class RelocationCandidate(
  val place: ILocationPlace,
  val angularity: Map<AstroPoint, Map<GeoAngle, Double>>,
  val nearestLine: Map<AstroPoint, LineDistance>,
  val houseShifts: List<HouseShift>,
  val relocatedChart: IHoroscopeModel? = null,
)

/** 從幾何層取每顆星最近的一條角線（[angularity] 的 argmin） */
fun AstroGeoContext.nearestLines(loc: ILatLng, basis: AstroGeoBasis): Map<AstroPoint, LineDistance> =
  angularity(loc, basis).mapNotNull { (p, byAngle) ->
    byAngle.minByOrNull { it.value }?.let { (angle, deg) ->
      p to LineDistance(angle, deg, Math.toRadians(deg) * EARTH_RADIUS_KM)
    }
  }.toMap()

/**
 * 結構化的計分理由（不吐人話字串 —— i18n 與 AI 詮釋層各自接手）。
 * [orbDeg] 為該星到該線的地表大圓角距；[contribution] = 角別權重 × 星體權重 × orb 衰減。
 */
data class ScoreReason(
  val point: AstroPoint,
  val angle: GeoAngle,
  val orbDeg: Double,
  val contribution: Double,
)

/**
 * 計分權重表 —— 全部進 config，調參不改 library（設計 doc 5.3）。
 * [starWeights] 是重要度而非吉凶（benefic/malefic 拍板不建全域枚舉，呼叫端可自行覆寫）。
 * [orbDeg] 單位為地表大圓角距（度），預設 10° ≈ Jim Lewis 慣例的 ~700 mi。
 */
data class RelocationScoreConfig(
  val angleWeights: Map<GeoAngle, Double> = mapOf(
    GeoAngle.MC to 1.0,
    GeoAngle.ASC to 1.0,
    GeoAngle.IC to 0.9,
    GeoAngle.DESC to 0.8,
  ),
  val starWeights: Map<AstroPoint, Double> = mapOf(
    Planet.SUN to 1.0,
    Planet.MOON to 1.0,
    Planet.VENUS to 0.9,
    Planet.JUPITER to 0.9,
    Planet.MARS to 0.8,
    Planet.SATURN to 0.8,
    Planet.MERCURY to 0.7,
    Planet.URANUS to 0.6,
    Planet.NEPTUNE to 0.6,
    Planet.PLUTO to 0.6,
  ),
  val orbDeg: Double = 10.0,
)

interface IRelocationScorer {
  fun score(c: RelocationCandidate, config: RelocationScoreConfig): Score
  fun reasons(c: RelocationCandidate, config: RelocationScoreConfig): List<ScoreReason>
}

/**
 * 預設規則式實作：角別權重 × 星體權重 × orb 線性衰減。
 *
 * 正規化：除以「每顆有權重的星都正壓最強角線」的理論最大值，落在 [0,1]。
 * 權重表沒列的星體不計分、也不進分母（否則會拖低所有分數）。
 */
class RelocationScorerClassicImpl : IRelocationScorer {

  override fun score(c: RelocationCandidate, config: RelocationScoreConfig): Score {
    val maxAngleWeight = config.angleWeights.values.maxOrNull() ?: 0.0
    val denominator = c.nearestLine.keys.sumOf { p -> config.starWeights[p] ?: 0.0 } * maxAngleWeight
    if (denominator <= 0.0)
      return 0.0.toScore()
    val numerator = reasons(c, config).sumOf { it.contribution }
    return (numerator / denominator).coerceIn(0.0, 1.0).toScore()
  }

  override fun reasons(c: RelocationCandidate, config: RelocationScoreConfig): List<ScoreReason> =
    c.nearestLine.mapNotNull { (p, nearest) ->
      val starWeight = config.starWeights[p] ?: return@mapNotNull null
      val angleWeight = config.angleWeights[nearest.angle] ?: 0.0
      val decay = (1.0 - nearest.deg / config.orbDeg).coerceAtLeast(0.0)
      val contribution = starWeight * angleWeight * decay
      if (contribution > 0.0) ScoreReason(p, nearest.angle, nearest.deg, contribution) else null
    }.sortedByDescending { it.contribution }
}
