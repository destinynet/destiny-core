/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:51:18
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

/**
 * 最簡單 , 以當地平均時間來區隔時辰 , 兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時
 */
class HourLmtImpl : HourIF, Serializable {

  override fun getHour(gmtJulDay: Double, location: Location): Branch {
    val gmt = revJulDayFunc.invoke(gmtJulDay)
    val lmtHour = TimeTools.getLmtFromGmt(gmt, location).get(ChronoField.HOUR_OF_DAY)
    return getHour(lmtHour)
  }

  private fun getHour(lmtHour: Int): Branch {
    when (lmtHour) {
      23, 0 -> return Branch.子
      1, 2 -> return Branch.丑
      3, 4 -> return Branch.寅
      5, 6 -> return Branch.卯
      7, 8 -> return Branch.辰
      9, 10 -> return Branch.巳
      11, 12 -> return Branch.午
      13, 14 -> return Branch.未
      15, 16 -> return Branch.申
      17, 18 -> return Branch.酉
      19, 20 -> return Branch.戌
      21, 22 -> return Branch.亥
    }
    throw RuntimeException("HourLmtImpl : Cannot find EarthlyBranches for this LMT : " + lmtHour)
  }

  override fun getGmtNextStartOf(gmtJulDay: Double, location: Location, eb: Branch): Double {

    val gmt = revJulDayFunc.invoke(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, location)
    val lmtResult = getLmtNextStartOf(lmt, location, eb, revJulDayFunc)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, location)
    return TimeTools.getGmtJulDay(gmtResult)
  }


  /**
   * 要實作，不然會有一些 round-off 的問題
   */
  override fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, location: Location, eb: Branch, revJulDayFunc: Function1<Double , ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {

    when (eb.index) {
      0 //欲求下一個子時時刻
      -> return if (lmt.get(HOUR_OF_DAY) >= 23)
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 23).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.with(HOUR_OF_DAY, 23).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      1 //欲求下一個丑時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 1)
        lmt.with(HOUR_OF_DAY, 1).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 1).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      2 //欲求下一個寅時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 3)
        lmt.with(HOUR_OF_DAY, 3).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 3).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      3 //欲求下一個卯時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 5)
        lmt.with(HOUR_OF_DAY, 5).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 5).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      4 //欲求下一個辰時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 7)
        lmt.with(HOUR_OF_DAY, 7).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 7).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      5 //欲求下一個巳時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 9)
        lmt.with(HOUR_OF_DAY, 9).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 9).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      6 //欲求下一個午時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 11)
        lmt.with(HOUR_OF_DAY, 11).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 11).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      7 //欲求下一個未時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 13)
        lmt.with(HOUR_OF_DAY, 13).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 13).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      8 //欲求下一個申時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 15)
        lmt.with(HOUR_OF_DAY, 15).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 15).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      9 //欲求下一個酉時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 17)
        lmt.with(HOUR_OF_DAY, 17).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 17).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      10 //欲求下一個戌時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 19)
        lmt.with(HOUR_OF_DAY, 19).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 19).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      11 //欲求下一個亥時的時刻
      -> return if (lmt.get(HOUR_OF_DAY) < 21)
        lmt.with(HOUR_OF_DAY, 21).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else
        lmt.plus(1, DAYS).with(HOUR_OF_DAY, 21).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)
      else -> throw RuntimeException("Cannot get next start time of $eb , LMT = $lmt")
    }
  }


  override fun getTitle(locale: Locale): String {
    return "以當地標準鐘錶時間區隔時辰"
  }


  override fun getDescription(locale: Locale): String {
    return "兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時"
  }

  companion object {
    private val revJulDayFunc =  { it:Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
