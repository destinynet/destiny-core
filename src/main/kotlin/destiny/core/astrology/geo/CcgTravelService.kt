/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.IHoroscopeConfig
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IObliquityCalculator
import destiny.core.astrology.IStarPositionWithAzimuthCalculator
import destiny.core.astrology.Planet
import destiny.core.astrology.Star
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ILocationPlace
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import jakarta.inject.Named

/**
 * 產品 B1 的組態。行運星預設火星–冥王（Jim Lewis CCG 正統）：
 * 月亮 13°/天是噪音，日/水/金每月掃過每城鑑別度低（設計 doc 5.4）。
 * orb 沿用 [scoreConfig] 的同一把尺。
 */
data class CcgTravelConfig(
  val basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
  val scoreConfig: RelocationScoreConfig = RelocationScoreConfig(),
  val transitStars: Set<Planet> = setOf(Planet.MARS, Planet.JUPITER, Planet.SATURN, Planet.URANUS, Planet.NEPTUNE, Planet.PLUTO),
  val natalWeight: Double = 0.5,
  val transitWeight: Double = 0.5,
  val sampleStepDays: Double = 1.0,
) {
  init {
    require(natalWeight + transitWeight > 0.0) { "natalWeight + transitWeight must be positive" }
    require(sampleStepDays > 0.0) { "sampleStepDays must be positive" }
  }
}

/**
 * 一個候選城市的旅行評估：natal 層（終身靜態）+ 行運 hit 窗口（時效）。
 * 依設計 doc 第 8 節，B1 的文案只能講「這趟旅程的調性」，不得講「你的運勢」。
 */
data class TravelRecommendation(
  val place: ILocationPlace,
  val natal: RelocationCandidate,
  val hits: List<TransitLineHit>,
  val score: Score,
  val natalScore: Score,
  val transitScore: Score,
  val natalReasons: List<ScoreReason>,
  val transitReasons: List<ScoreReason>,
)

/**
 * 產品 B1：「下個月去哪玩」—— 本命 ACG ∩ CycloCartoGraphy（設計 doc 5.4）。
 *
 * 行運線凍結本命 θ（[AstroGeoContext.ofCyclo]），對每組（城市 × 行運星 × 角）
 * 做時間軸取樣 + [segmentHits] 窗口切割；natal 層與行運層加權相加。
 * 純演算法組合，置於 destiny-core。
 */
@Named
class CcgTravelService(
  private val candidateService: RelocationCandidateService,
  private val scorer: IRelocationScorer,
  private val starPosition: IStarPositionWithAzimuthCalculator,
  private val obliquityCalculator: IObliquityCalculator,
) {

  /** @return 依綜合分數降冪 */
  fun rank(
    natalModel: IHoroscopeModel,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    places: List<ILocationPlace>,
    baseLoc: ILocation? = null,
    natalStars: Collection<Star> = Planet.list,
    horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
    config: CcgTravelConfig = CcgTravelConfig(),
  ): List<TravelRecommendation> {
    require(toGmt > fromGmt) { "toGmt must be after fromGmt" }
    val base = baseLoc ?: natalModel.location
    val natalCandidates = candidateService.getCandidates(natalModel.gmtJulDay, base, places, config.basis, natalStars, horoscopeConfig)

    val obliquity = obliquityCalculator.getObliquity(natalModel.gmtJulDay)
    val grid = buildList {
      var t = fromGmt.value
      while (t < toGmt.value) {
        add(GmtJulDay(t))
        t += config.sampleStepDays
      }
      add(toGmt)
    }
    // 網格 context 全星共用；細化時的任意時刻退回單星建構
    val gridCtx: Map<Double, AstroGeoContext> =
      grid.associate { it.value to AstroGeoContext.ofCyclo(natalModel.gmtJulDay, it, config.transitStars, starPosition, obliquity) }

    fun cycloCtx(t: GmtJulDay, planet: Planet): AstroGeoContext =
      gridCtx[t.value] ?: AstroGeoContext.ofCyclo(natalModel.gmtJulDay, t, listOf(planet), starPosition, obliquity)

    val maxAngleWeight = config.scoreConfig.angleWeights.values.maxOrNull() ?: 0.0
    val transitDenominator = config.transitStars.sumOf { config.scoreConfig.starWeights[it] ?: 0.0 } * maxAngleWeight

    return natalCandidates.map { natal ->
      val place = natal.place
      val hits = config.transitStars.flatMap { planet ->
        GeoAngle.entries.flatMap { angle ->
          segmentHits(planet, angle, grid, config.scoreConfig.orbDeg) { t ->
            cycloCtx(t, planet).angularityToLine(place, planet, angle, config.basis)
          }
        }
      }
      val transitReasons = hits.mapNotNull { hit ->
        config.scoreConfig.contribution(hit.planet, hit.angle, hit.peakOrbDeg)
          ?.takeIf { it > 0.0 }
          ?.let { ScoreReason(hit.planet, hit.angle, hit.peakOrbDeg, it) }
      }.sortedByDescending { it.contribution }

      val transitScore = if (transitDenominator <= 0.0) 0.0.toScore()
      else (transitReasons.sumOf { it.contribution } / transitDenominator).coerceIn(0.0, 1.0).toScore()
      val natalScore = scorer.score(natal, config.scoreConfig)
      val combined = (config.natalWeight * natalScore.value + config.transitWeight * transitScore.value) /
        (config.natalWeight + config.transitWeight)

      TravelRecommendation(
        place = place,
        natal = natal,
        hits = hits,
        score = combined.toScore(),
        natalScore = natalScore,
        transitScore = transitScore,
        natalReasons = scorer.reasons(natal, config.scoreConfig),
        transitReasons = transitReasons,
      )
    }.sortedByDescending { it.score }
  }
}
