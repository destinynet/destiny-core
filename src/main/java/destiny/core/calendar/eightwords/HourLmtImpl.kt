/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:51:18
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.KEY_HOUR
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField.*
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

/**
 * 最簡單 , 以當地平均時間來區隔時辰 , 兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時
 */
@Impl([Domain(KEY_HOUR, HourLmtImpl.VALUE)])
class HourLmtImpl(val julDayResolver: JulDayResolver) : IHour, Serializable {

  override fun getHour(gmtJulDay: Double, location: ILocation): Branch {
    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmtHour = TimeTools.getLmtFromGmt(gmt, location).get(HOUR_OF_DAY)
    return getHour(lmtHour)
  }

  private fun getHour(lmtHour: Int): Branch {
    when (lmtHour) {
      23, 0 -> return 子
      1, 2 -> return 丑
      3, 4 -> return 寅
      5, 6 -> return 卯
      7, 8 -> return 辰
      9, 10 -> return 巳
      11, 12 -> return 午
      13, 14 -> return 未
      15, 16 -> return 申
      17, 18 -> return 酉
      19, 20 -> return 戌
      21, 22 -> return 亥
    }
    throw IllegalArgumentException("HourLmtImpl : Cannot find EarthlyBranches for this LMT : $lmtHour")
  }

  override fun getGmtNextStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {

    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, location)
    val lmtResult = getLmtNextStartOf(lmt, location, eb, julDayResolver)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, location)
    return TimeTools.getGmtJulDay(gmtResult)
  }


  /**
   * 要實作，不然會有一些 round-off 的問題
   */
  override fun getLmtNextStartOf(
    lmt: ChronoLocalDateTime<*>,
    location: ILocation,
    eb: Branch,
    julDayResolver: JulDayResolver
  ): ChronoLocalDateTime<*> {

    val lmtAtHourStart = lmt.with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)

    return when (eb) {
      //欲求下一個子時時刻
      子 -> if (lmt.get(HOUR_OF_DAY) >= 23)
        lmtAtHourStart.plus(1, DAYS).with(HOUR_OF_DAY, 23)
      else
        lmtAtHourStart.with(HOUR_OF_DAY, 23)

      else -> {
        val hourStart = eb.index * 2 - 1
        if (lmt.get(HOUR_OF_DAY) < hourStart)
          lmtAtHourStart.with(HOUR_OF_DAY, hourStart.toLong())
        else
          lmtAtHourStart.plus(1, DAYS).with(HOUR_OF_DAY, hourStart.toLong())
      }
    }
  }


  /**
   * 取得「前一個」此地支的開始時刻
   */
  override fun getGmtPrevStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {
    val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
    val lmt = TimeTools.getLmtFromGmt(gmt, location)
    val lmtResult = getLmtPrevStartOf(lmt, location, eb, julDayResolver)
    val gmtResult = TimeTools.getGmtFromLmt(lmtResult, location)
    return TimeTools.getGmtJulDay(gmtResult)
  }

  /**
   * 取得「前一個」此地支的開始時刻
   */
  override fun getLmtPrevStartOf(
    lmt: ChronoLocalDateTime<*>,
    location: ILocation,
    eb: Branch,
    julDayResolver: JulDayResolver
  ): ChronoLocalDateTime<*> {
    val lmtAtHourStart = lmt.with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0)

    val hourOfDay = lmt.get(HOUR_OF_DAY)
    val yesterdayHourStart = lmtAtHourStart.minus(1, DAYS)

    return when (eb) {
      子 -> if (hourOfDay < 23)
        yesterdayHourStart.with(HOUR_OF_DAY, 23)
      else
        lmtAtHourStart.with(HOUR_OF_DAY, 23)

      else -> {
        val hourStart = eb.index * 2 - 1
        if (hourOfDay < hourStart)
          yesterdayHourStart.with(HOUR_OF_DAY, hourStart.toLong())
        else
          lmtAtHourStart.with(HOUR_OF_DAY, hourStart.toLong())
      }
    }
  }

  override fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, location: ILocation, next: Boolean, julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
    val currentHour = getHour(lmt, location)
    return if (next) {
      getLmtNextStartOf(lmt, location, currentHour.next, julDayResolver).plus(1, ChronoUnit.HOURS)
    } else {
      getLmtPrevStartOf(lmt, location, currentHour.prev, julDayResolver).plus(1, ChronoUnit.HOURS)
    }
  }


  /**
   * LMT 要實作
   */
  override fun toString(locale: Locale): String {
    return name
  }


  override fun getDescription(locale: Locale): String {
    return "兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


  companion object {
    const val VALUE = "lmt"
    const val name = "以地方平均時（LMT）來區隔"
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
