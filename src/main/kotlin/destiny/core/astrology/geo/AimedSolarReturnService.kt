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
import jakarta.inject.Named

/**
 * 一個候選城市的 Aimed SR 評估。
 * [srGmtJulDay]：**必須在此瞬間人在該地**，SR 盤才成立 —— 這是 B2 的使用門檻
 * （對台灣市場，能有效改變 MC 的東京/曼谷/首爾都在 3 小時航程內，設計 doc §2）。
 * [score] = suitability（0.5 中性）為排名鍵；[intensity] 並陳供 AI 詮釋。
 */
data class AimedSolarReturnResult(
  val place: ILocationPlace,
  val candidate: RelocationCandidate,
  val srGmtJulDay: GmtJulDay,
  val validFrom: GmtJulDay,
  val validTo: GmtJulDay,
  val score: Score,
  val intensity: Score,
  val reasons: List<ScoreReason>,
)

/**
 * 產品 B2：「生日去哪過」—— Aimed Solar Return（Discepolo 定向太陽返照，設計 doc 5.2）。
 *
 * 與產品 A 的關鍵差異：B2 取「**下一次**」SR（now 之後最近的回歸，可事先規劃），
 * 不是 A 的「當下生效」SR。SR 時刻與地點無關，解一次後把該瞬間的天空
 * 餵給 [RelocationCandidateService] 做單層雙軸排名。純演算法組合，置於 destiny-core。
 */
@Named
class AimedSolarReturnService(
  private val candidateService: RelocationCandidateService,
  @param:Named("solarReturnContext")
  private val solarReturnContext: IReturnContext,
  private val scorer: IRelocationScorer,
) {

  /**
   * @param baseLoc houseShifts 的「基準地」（現居地）；未提供退化為出生地
   * @param nowGmtJulDay 取其後最近的一次 SR；傳未來時刻即可規劃更晚的年度
   * @return 依 suitability 降冪
   */
  fun rank(
    natalModel: IHoroscopeModel,
    nowGmtJulDay: GmtJulDay,
    places: List<ILocationPlace>,
    baseLoc: ILocation? = null,
    basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
    stars: Collection<Star> = Planet.list,
    horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
    scoreConfig: RelocationScoreConfig = RelocationScoreConfig(),
  ): List<AimedSolarReturnResult> {
    val base = baseLoc ?: natalModel.location
    // 「當下生效」SR 的 validTo 即下一次回歸時刻；在其後再取一次即得 upcoming SR
    val current = solarReturnContext.getReturnHoroscope(natalModel, nowGmtJulDay, base)
    val upcoming = solarReturnContext.getReturnHoroscope(natalModel, current.validTo + 0.01, base)
    val srJd = upcoming.horoscope.gmtJulDay

    return candidateService.getCandidates(srJd, base, places, basis, stars, horoscopeConfig)
      .map { candidate ->
        AimedSolarReturnResult(
          place = candidate.place,
          candidate = candidate,
          srGmtJulDay = srJd,
          validFrom = upcoming.validFrom,
          validTo = upcoming.validTo,
          score = scorer.suitability(candidate, scoreConfig),
          intensity = scorer.score(candidate, scoreConfig),
          reasons = scorer.reasons(candidate, scoreConfig),
        )
      }
      .sortedByDescending { it.score }
  }
}
