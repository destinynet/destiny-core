/**
 * Created by smallufo on 2017-10-27.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.IStarPosition
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.IChineseDate
import java.time.chrono.ChronoLocalDateTime

/**
 * 取代 [EightWordsContext]
 */
interface IEightWordsDetail {

  fun getDetails(lmt: ChronoLocalDateTime<*>,
                 location: Location,
                 place: String,
                 eightWordsImpl: IEightWordsFactory,
                 yearMonthImpl: IYearMonth,
                 chineseDateImpl: IChineseDate,
                 dayImpl: IDay,
                 hourImpl: IHour,
                 midnightImpl: IMidnight,
                 changeDayAfterZi: Boolean,
                 risingSignImpl: IRisingSign,
                 starPositionImpl: IStarPosition<*>,
                 solarTermsImpl: ISolarTerms): IEightWordsContextModel

}
