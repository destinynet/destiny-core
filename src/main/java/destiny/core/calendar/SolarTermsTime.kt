/**
 * Created by smallufo on 2015-06-01.
 */
package destiny.core.calendar

import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 存放 一對 SolarTerms 以及 Time 的小 class
 */
data class SolarTermsTime internal constructor(
  /** 節氣  */
  val solarTerms: SolarTerms,
  /** 可能是 GMT , 也可能是 LMT  */
  val time: ChronoLocalDateTime<*>) : Serializable
