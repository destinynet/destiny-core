/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.IHoroscopeConfig
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.Star
import destiny.core.astrology.prediction.IReturnContext
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ILocationPlace
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import jakarta.inject.Named

/**
 * 產品 A 的計分組態：natal（終身結構）與 SR（今年時效）兩層各自計分後加權。
 */
data class YearlyRelocationConfig(
  val basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
  val scoreConfig: RelocationScoreConfig = RelocationScoreConfig(),
  val natalWeight: Double = 0.6,
  val srWeight: Double = 0.4,
) {
  init {
    require(natalWeight + srWeight > 0.0) { "natalWeight + srWeight must be positive" }
  }
}

/**
 * 一個候選地點的年度評估：natal 與 SR 各一張 [RelocationCandidate] 掛同一 candidate（設計 doc 5.2）。
 * [validFrom]/[validTo]：此 SR 的有效期間。
 */
data class YearlyRelocationResult(
  val place: ILocationPlace,
  val natal: RelocationCandidate,
  val solarReturn: RelocationCandidate,
  val score: Score,
  val natalScore: Score,
  val srScore: Score,
  val natalReasons: List<ScoreReason>,
  val srReasons: List<ScoreReason>,
  val validFrom: GmtJulDay,
  val validTo: GmtJulDay,
)

/**
 * 產品 A：「今年該搬去哪」—— 換置本命盤 + relocated Solar Return 排名。
 *
 * SR 時刻與地點無關（太陽回歸本命度數的瞬間全球同時），只解一次；
 * 兩層各自建 [AstroGeoContext]（jd 一律當參數，設計 doc 拍板）再交給 [RelocationCandidateService]。
 * 純演算法組合，置於 destiny-core。
 */
@Named
class YearlyRelocationService(
  private val candidateService: RelocationCandidateService,
  @param:Named("solarReturnContext")
  private val solarReturnContext: IReturnContext,
  private val scorer: IRelocationScorer,
) {

  /**
   * @param baseLoc houseShifts 的「基準地」（現居地）；未提供退化為出生地
   * @return 依綜合分數降冪
   */
  fun rank(
    natalModel: IHoroscopeModel,
    nowGmtJulDay: GmtJulDay,
    places: List<ILocationPlace>,
    baseLoc: ILocation? = null,
    stars: Collection<Star> = Planet.list,
    horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
    config: YearlyRelocationConfig = YearlyRelocationConfig(),
  ): List<YearlyRelocationResult> {
    val base = baseLoc ?: natalModel.location
    val returnModel = solarReturnContext.getReturnHoroscope(natalModel, nowGmtJulDay, base)
    val srJd = returnModel.horoscope.gmtJulDay

    val natalCandidates = candidateService.getCandidates(natalModel.gmtJulDay, base, places, config.basis, stars, horoscopeConfig)
    val srCandidates = candidateService.getCandidates(srJd, base, places, config.basis, stars, horoscopeConfig)

    return natalCandidates.zip(srCandidates) { natal, sr ->
      val natalScore = scorer.score(natal, config.scoreConfig)
      val srScore = scorer.score(sr, config.scoreConfig)
      val combined = (config.natalWeight * natalScore.value + config.srWeight * srScore.value) /
        (config.natalWeight + config.srWeight)
      YearlyRelocationResult(
        place = natal.place,
        natal = natal,
        solarReturn = sr,
        score = combined.toScore(),
        natalScore = natalScore,
        srScore = srScore,
        natalReasons = scorer.reasons(natal, config.scoreConfig),
        srReasons = scorer.reasons(sr, config.scoreConfig),
        validFrom = returnModel.validFrom,
        validTo = returnModel.validTo,
      )
    }.sortedByDescending { it.score }
  }
}
