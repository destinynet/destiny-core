/**
 * @author smallufo
 * Created on 2011/5/24 at 上午12:13:25
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfigBuilder.Companion.ewConfig
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算八字的介面
 */
interface IEightWordsFactory {

  fun getEightWords(gmtJulDay: GmtJulDay, loc: ILocation): IEightWords

  fun getEightWords(lmt: ChronoLocalDateTime<*>, loc: ILocation): IEightWords {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getEightWords(gmtJulDay, loc)
  }
}

/**
 * 將八字計算拆成 年月 / 日時 兩個介面
 */
interface IEightWordsStandardFactory : IEightWordsFactory {
  /** 換年, 以及月支計算的實作 */
  val yearMonthImpl: IYearMonth

  /** 日、時 的實作 */
  val dayHourImpl: IDayHour

  val ewConfig: EightWordsConfig
    get() {
      return ewConfig {
        yearMonthConfig = yearMonthImpl.config
        dayHourConfig = dayHourImpl.config
      }
    }
}
