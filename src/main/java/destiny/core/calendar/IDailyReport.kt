/**
 * Created by smallufo on 2018-07-18.
 */
package destiny.core.calendar

import java.time.chrono.ChronoLocalDateTime
import java.util.*

interface IDailyReport {

  fun getList(lmt: ChronoLocalDateTime<*>, loc: ILocation, locale: Locale): List<TimeDesc>
}