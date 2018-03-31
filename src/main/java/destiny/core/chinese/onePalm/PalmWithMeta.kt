/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.onePalm

import destiny.core.calendar.ILocation
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IDay
import destiny.core.calendar.eightwords.IHour
import destiny.core.calendar.eightwords.IMidnight

import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

class PalmWithMeta(val palm: Palm,
                   val lmt: ChronoLocalDateTime<*>,
                   val loc: ILocation,
                   val place: String?,
                   val chineseDateImpl: IChineseDate,
                   val dayImpl: IDay,
                   val positiveImpl: IPositive,
                   val hourImpl: IHour,
                   val midnightImpl: IMidnight,
                   val isChangeDayAfterZi: Boolean,
                   val isTrueRisingSign: Boolean,
                   val monthAlgo: IFinalMonthNumber.MonthAlgo) : Serializable {

  val chineseDate: ChineseDate
    get() = chineseDateImpl.getChineseDate(lmt, loc, dayImpl, hourImpl, midnightImpl, isChangeDayAfterZi)
}
