/*
 * @author smallufo
 * @date 2004/12/6
 * @time 上午 11:23:37
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.absoluteValue

/** 定義「子正」的介面，是要以當地手錶 0時 為子正，亦或是太陽過當地天底 ... 或是其他實作  */
interface IMidnight : Descriptive {

  /** 取得下一個「子正」的 GMT 時刻  */
  fun getNextMidnight(gmtJulDay: Double, loc: ILocation): Double

  /**
   * 取得「上一個」子正 的 GMT 時刻
   * 因為底層 API 並未提供相關實作，只能透過 [getNextMidnight] 來求解
   * 必須要一步步用 0.5 去減 , 因為一日的長度可能超過 1.0
   */
  fun getPrevMidnight(gmtJulDay: Double, loc: ILocation): Double {
    val nextMidnight = getNextMidnight(gmtJulDay, loc)

    return generateSequence(gmtJulDay - 0.5 to getNextMidnight(gmtJulDay - 0.5, loc)) { pair ->
      pair.first to getNextMidnight(pair.first - 0.5, loc)
    }.first { (it.second - nextMidnight).absoluteValue > 0.5 }
      .second
  }


  /** 取得下一個「子正」的 LMT 時刻 */
  fun getNextMidnight(lmt: ChronoLocalDateTime<*>, loc: ILocation, revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val gmtResultJulDay = getNextMidnight(gmtJulDay, loc)
    val gmtResult = revJulDayFunc.invoke(gmtResultJulDay)
    return TimeTools.getLmtFromGmt(gmtResult, loc)
  }

  /**
   * 取得上一個「子正」的 LMT 時刻
   */
  fun getPrevMidnight(lmt: ChronoLocalDateTime<*>, loc: ILocation, revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    val gmt = TimeTools.getGmtFromLmt(lmt, loc)
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getPrevMidnight(gmtJulDay, loc).let {
      val gmtResult = revJulDayFunc.invoke(it)
      TimeTools.getLmtFromGmt(gmtResult , loc)
    }
  }
}
