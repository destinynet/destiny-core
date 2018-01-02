/**
 * @author smallufo
 * Created on 2011/5/24 at 上午12:13:25
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.time.chrono.ChronoLocalDateTime

/**
 * 計算八字的介面
 */
interface IEightWords {

  fun getEightWords(gmtJulDay: Double, loc: Location): EightWords

  fun getEightWords(lmt: ChronoLocalDateTime<*>, loc: Location): EightWords {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getEightWords(gmtJulDay, loc)
  }

}
