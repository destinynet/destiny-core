/**
 * @author smallufo
 * Created on 2008/7/26 at 上午 2:10:55
 */
package destiny.core.calendar

import java.time.chrono.ChronoLocalDateTime

/** 某class 具有 Time 的資料結構  */
interface ITime {

  val time: ChronoLocalDateTime<*>
}
