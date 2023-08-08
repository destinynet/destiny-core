/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import java.time.ZoneId
import java.util.*


interface IEventSpanService {

  fun getYearlyEvents(stars: Set<Star>, year: Int, zoneId: ZoneId, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarEventSpan>

  fun getMonthlyEvents(stars: Set<Star>, year: Int, month: Int, loc: ILocation, locale: Locale, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IStarEventSpan>

  /**
   * 內容
   * 包含三年的星體逆行資料
   * 最近三個月的星體空亡資料
   */
  fun getThisYearEvents(stars: Set<Star>, loc: ILocation, locale: Locale, phases: Set<RetrogradePhase>): List<IStarEventSpan>

}
