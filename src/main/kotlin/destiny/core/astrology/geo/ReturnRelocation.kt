/**
 * Created by smallufo on 2026-11-20.
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

/**
 * 回歸盤 × 換置的計分組態：natal（終身結構）與回歸盤（時效）兩層各自計分後加權。
 * 年版（太陽回歸）預設 natal 0.6／回歸 0.4，月版（月亮回歸）見 [MonthlyRelocationService.DEFAULT_CONFIG]。
 */
data class ReturnRelocationConfig(
  val basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
  val scoreConfig: RelocationScoreConfig = RelocationScoreConfig(),
  val natalWeight: Double = 0.6,
  val returnWeight: Double = 0.4,
) {
  init {
    require(natalWeight >= 0.0 && returnWeight >= 0.0) { "weights must be non-negative" }
    require(natalWeight + returnWeight > 0.0) { "natalWeight + returnWeight must be positive" }
  }
}

/**
 * 一個候選地點的評估：natal 與回歸盤各一張 [RelocationCandidate]（設計 doc 5.2）。
 * [score] 為排名鍵 = 兩層 suitability 加權（**0.5 = 中性**）；
 * intensity（強度）與 suitability（吉凶）雙軸並陳，valence ⊥ intensity。
 *
 * [validFrom]/[validTo]：這張回歸盤的有效期間（回歸時刻 → 下一次回歸）。
 * 採**居住地派**：同一次回歸在各城市各起一張盤，「若你這段期間住在該地」就看那一張。
 */
data class ReturnRelocationResult(
  /** 回歸的星：[Planet.SUN] ＝ 太陽回歸（年）、[Planet.MOON] ＝ 月亮回歸（月） */
  val returnPlanet: Planet,
  val place: ILocationPlace,
  val natal: RelocationCandidate,
  val returnChart: RelocationCandidate,
  val score: Score,
  val natalIntensity: Score,
  val natalSuitability: Score,
  val returnIntensity: Score,
  val returnSuitability: Score,
  val natalReasons: List<ScoreReason>,
  val returnReasons: List<ScoreReason>,
  val validFrom: GmtJulDay,
  val validTo: GmtJulDay,
)

/** 一批城市起好的盤（[ReturnRelocationRanker.candidates]）：兩張換置盤，與同一次回歸的有效期間 */
data class ReturnCandidates(
  val returnPlanet: Planet,
  val natal: List<RelocationCandidate>,
  val returnChart: List<RelocationCandidate>,
  val validFrom: GmtJulDay,
  val validTo: GmtJulDay,
)

/**
 * 換置本命盤 ＋ relocated 回歸盤的排名，年版與月版共用。
 *
 * 回歸時刻與地點無關（星體回到本命度數的瞬間全球同時），只解一次；
 * 兩層各自建 [AstroGeoContext]（jd 一律當參數）再交給 [RelocationCandidateService]。
 */
class ReturnRelocationRanker(
  private val candidateService: RelocationCandidateService,
  private val scorer: IRelocationScorer,
) {

  /**
   * @param returnContext 決定回歸哪顆星；需為 forward 版本（回傳 [nowGmtJulDay] 當下生效的那一次回歸）
   * @param baseLoc houseShifts 的「基準地」（現居地）；未提供退化為出生地
   * @return 依綜合分數降冪
   */
  fun rank(
    returnContext: IReturnContext,
    natalModel: IHoroscopeModel,
    nowGmtJulDay: GmtJulDay,
    places: List<ILocationPlace>,
    baseLoc: ILocation?,
    stars: Collection<Star>,
    horoscopeConfig: IHoroscopeConfig,
    config: ReturnRelocationConfig,
  ): List<ReturnRelocationResult> =
    score(candidates(returnContext, natalModel, nowGmtJulDay, places, baseLoc, stars, horoscopeConfig, config.basis), config)

  /**
   * 只起盤、不計分：每座城市的換置本命盤與換置回歸盤。同一批城市要用好幾組權重排名（多個主題）時，盤只起一次，再各自 [score]
   */
  fun candidates(
    returnContext: IReturnContext,
    natalModel: IHoroscopeModel,
    nowGmtJulDay: GmtJulDay,
    places: List<ILocationPlace>,
    baseLoc: ILocation?,
    stars: Collection<Star>,
    horoscopeConfig: IHoroscopeConfig,
    basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
  ): ReturnCandidates {
    val base = baseLoc ?: natalModel.location
    val returnModel = returnContext.getReturnHoroscope(natalModel, nowGmtJulDay, base)
    // 兩張基準盤都從手上的盤換過來：本命換到基準地、回歸盤本來就起在基準地（設定相同時不再整張起盤）
    return ReturnCandidates(
      returnPlanet = returnContext.planet,
      natal = candidateService.getCandidates(candidateService.chartAt(natalModel, base, horoscopeConfig), places, basis, stars),
      returnChart = candidateService.getCandidates(candidateService.chartAt(returnModel.horoscope, base, horoscopeConfig), places, basis, stars),
      validFrom = returnModel.validFrom,
      validTo = returnModel.validTo,
    )
  }

  /** 一組權重的排名（依綜合分數降冪）；[config] 的 basis 要與起盤時相同 */
  fun score(c: ReturnCandidates, config: ReturnRelocationConfig): List<ReturnRelocationResult> {
    // 依城市對齊（不依順序）：極圈附近的城市可能只有一張盤算得出來（RelocationCandidateService 會略過算不出宮的）
    val returns = c.returnChart.associateBy { it.place }
    return c.natal.mapNotNull { natal ->
      val ret = returns[natal.place] ?: return@mapNotNull null
      val natalSuitability = scorer.suitability(natal, config.scoreConfig)
      val returnSuitability = scorer.suitability(ret, config.scoreConfig)
      // 排名鍵用吉凶軸：凶星正壓的地點必須沉下去，強度另陳（設計 doc 5.3 雙軸）
      val combined = (config.natalWeight * natalSuitability.value + config.returnWeight * returnSuitability.value) /
        (config.natalWeight + config.returnWeight)
      ReturnRelocationResult(
        returnPlanet = c.returnPlanet,
        place = natal.place,
        natal = natal,
        returnChart = ret,
        score = combined.toScore(),
        natalIntensity = scorer.score(natal, config.scoreConfig),
        natalSuitability = natalSuitability,
        returnIntensity = scorer.score(ret, config.scoreConfig),
        returnSuitability = returnSuitability,
        natalReasons = scorer.reasons(natal, config.scoreConfig),
        returnReasons = scorer.reasons(ret, config.scoreConfig),
        validFrom = c.validFrom,
        validTo = c.validTo,
      )
    }.sortedByDescending { it.score }
  }
}

/**
 * 月版：「這個月住在 A、B、C 哪裡好」—— 換置本命盤 ＋ relocated Lunar Return。
 *
 * 回傳 [nowGmtJulDay] 當下生效的那一次月亮回歸（約 27.3 天一次，與日曆月不對齊）；
 * 要問「下個月」，[nowGmtJulDay] 傳下個月裡的某一天，並以結果的 [ReturnRelocationResult.validFrom]／
 * [ReturnRelocationResult.validTo] 告知實際涵蓋的期間。
 *
 * 月亮回歸盤的四角每次都大幅移動，結果只適合講「這段期間」，不適合拿來決定搬家（那是年版與 natal 層的事）。
 */
class MonthlyRelocationService(
  private val ranker: ReturnRelocationRanker,
  /** 參數名即 bean 名：同型別有多個 bean，Spring 以參數名挑選 */
  private val lunarReturnContext: IReturnContext,
) {

  fun rank(
    natalModel: IHoroscopeModel,
    nowGmtJulDay: GmtJulDay,
    places: List<ILocationPlace>,
    baseLoc: ILocation? = null,
    stars: Collection<Star> = Planet.list,
    horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
    config: ReturnRelocationConfig = DEFAULT_CONFIG,
  ): List<ReturnRelocationResult> =
    ranker.rank(lunarReturnContext, natalModel, nowGmtJulDay, places, baseLoc, stars, horoscopeConfig, config)

  companion object {
    /** 月版重時效：回歸盤的權重高於年版（natal 0.4／LR 0.6） */
    val DEFAULT_CONFIG = ReturnRelocationConfig(natalWeight = 0.4, returnWeight = 0.6)
  }
}
