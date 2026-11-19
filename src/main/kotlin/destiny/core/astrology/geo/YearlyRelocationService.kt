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
import jakarta.inject.Named

/**
 * 產品 A：「今年住在 A、B、C 哪裡好」—— 換置本命盤 ＋ relocated Solar Return（居住地派）。
 *
 * 回傳 [nowGmtJulDay] 當下生效的那一次太陽回歸（回歸時刻在 now 之前，管到下一次回歸）。
 * 計分與月版共用 [ReturnRelocationRanker]；月版見 [MonthlyRelocationService]。
 */
@Named
class YearlyRelocationService(
  private val ranker: ReturnRelocationRanker,
  @param:Named("solarReturnContext")
  private val solarReturnContext: IReturnContext,
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
    config: ReturnRelocationConfig = ReturnRelocationConfig(),
  ): List<ReturnRelocationResult> =
    ranker.rank(solarReturnContext, natalModel, nowGmtJulDay, places, baseLoc, stars, horoscopeConfig, config)
}
