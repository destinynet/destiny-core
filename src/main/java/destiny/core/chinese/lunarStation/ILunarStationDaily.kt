package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField

/**
 * 二十八星宿值日
 *
 * 《協紀辨方》二十八宿分配六十甲子：
 * 一元甲子起 [虛]，
 * 二元甲子起 [奎]，
 * 三元甲子起 [畢]，
 * 四元起 [鬼]，
 * 五元起 [翼]，
 * 六元起 [氐]，
 * 七元起 [箕]，
 *
 * 凡四百二十日而週，共得甲子七次，故曰七元。
 *
 * */
interface ILunarStationDaily {

  /** 日禽 , 幾元 , 幾將 */
  fun getDaily(lmt: ChronoLocalDateTime<*>, loc: ILocation): DayIndex


  companion object {
    fun getLeader(yuan: Int, general: Int): LunarStation {
      require(yuan in 1..7)
      require(general in 1..4)
      return 虛.next((yuan - 1) * 60 + (general - 1) * 15)
    }
  }
}


/**
 * 日禽 實作
 */
class LunarStationDailyImpl(private val dayHourImpl: IDayHour,
                            private val julDayResolver: JulDayResolver) : ILunarStationDaily, Serializable {


  /**
   * 下個子初 與 下個子正 的差距 , 取絕對值
   */
  private fun getNextZiMidnightDuration(lmt: ChronoLocalDateTime<*>, loc: ILocation): Duration {
    val nextMidnight = dayHourImpl.midnightImpl.getNextMidnight(lmt, loc, julDayResolver)
    val nextZiStart = dayHourImpl.getLmtNextStartOf(lmt, loc, Branch.子, julDayResolver)
    return Duration.between(nextZiStart, nextMidnight).abs()
  }

  override fun getDaily(lmt: ChronoLocalDateTime<*>, loc: ILocation): DayIndex {

    val hourSb: Branch = dayHourImpl.getHour(lmt, loc)

    val noon = lmt.with(ChronoField.HOUR_OF_DAY, 12)
      .with(ChronoField.MINUTE_OF_HOUR, 0)
      .with(ChronoField.SECOND_OF_MINUTE, 0)
    val noonJulDay = TimeTools.getGmtJulDay(noon).toInt().let {

      if (hourSb == Branch.子) {
        if (lmt.get(ChronoField.HOUR_OF_DAY) > 12) {
          // 24 時之前
          if (dayHourImpl.changeDayAfterZi) {
            // 子初換日
            it + 1
          } else {
            // 子正換日
            getNextZiMidnightDuration(lmt, loc).toHours().let { hourDiff ->
              if (hourDiff > 12)
                it
              else
                it + 1
            }
          }
        } else {
          // 0時之後
          if (dayHourImpl.changeDayAfterZi) {
            // 子初換日
            it
          } else {
            // 子正換日
            getNextZiMidnightDuration(lmt, loc).toHours().let { hourDiff ->
              if (hourDiff > 12)
                it - 1
              else
                it
            }
          }
        }
      } else {
        // 其他時辰
        it
      }
    }

    /** 陽曆 , 西元 1993年 10月 10日 一元一將 甲子日 中午 , julDay = 2451791 , [虛] 值日 */
    // 0 .. 419
    val index420 = ((noonJulDay - epoch) % 420).let {
      if (it < 0)
        it + 420
      else
        it
    }
    return DayIndex(index420)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationDailyImpl) return false

    if (dayHourImpl != other.dayHourImpl) return false
    if (julDayResolver != other.julDayResolver) return false

    return true
  }

  override fun hashCode(): Int {
    var result = dayHourImpl.hashCode()
    result = 31 * result + julDayResolver.hashCode()
    return result
  }


  companion object {
    /** 陽曆 , 西元 1993年 10月 10日 一元一將 甲子日 中午 , julDay = 2451791 , [虛] 值日 */
    private const val epoch: Int = 2449271
  }
}
