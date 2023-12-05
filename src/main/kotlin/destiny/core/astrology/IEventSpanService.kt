/**
 * Created by smallufo on 2023-08-05.
 */
package destiny.core.astrology

import destiny.core.calendar.IEvent
import destiny.core.calendar.ILocation
import java.time.LocalDate
import java.time.ZoneId


interface IEventSpanService {

  fun getYearlyEvents(stars: Set<Star>, year: Int, zoneId: ZoneId, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent>

  fun getMonthlyEventSpans(stars: Set<Star>, year: Int, month: Int, loc: ILocation, phases: Set<RetrogradePhase> = setOf(RetrogradePhase.RETROGRADING)): List<IEvent>

  fun getDailyEventPoints(stars: Set<Star>, localDate: LocalDate, loc: ILocation): List<IEvent>

  /**
   * 內容
   * 包含三年的星體逆行資料
   * 最近三個月的星體空亡資料
   */
  fun getThisYearEvents(stars: Set<Star>, loc: ILocation, phases: Set<RetrogradePhase>): List<IEvent>

}
