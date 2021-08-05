package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.tools.Feature
import mu.KotlinLogging
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

class DayConfig(var changeDayAfterZi: Boolean = true)

interface IDayProcessor {
  fun getDayModel(gmtJulDay: GmtJulDay, location: ILocation, dayConfig: DayConfig): StemBranch
}


class DayFeature(private val defaultConfig: DayConfig,
                 private val dayProcessor : IDayProcessor) : Feature<DayConfig, IDayProcessor, StemBranch> {

  private var cfg = defaultConfig

  override val key: String = "day"

  override fun prepare(block: DayConfig.() -> Unit): IDayProcessor {
    cfg = defaultConfig.apply(block)

    return dayProcessor
  }

  override fun IDayProcessor.getModel(gmtJulDay: GmtJulDay, loc: ILocation): StemBranch {
    return this.getDayModel(gmtJulDay, loc, cfg)
  }
}



class DayHourSolarTransProcessor(val hourImpl: IHour,
                                 val midnightImpl : IMidnight,
                                 val julDayResolver: JulDayResolver
) : IDayProcessor {

  override fun getDayModel(gmtJulDay: GmtJulDay, location: ILocation, dayConfig: DayConfig): StemBranch {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, location, julDayResolver)

    return getDay(lmt, location, dayConfig.changeDayAfterZi)
  }

  /**
   * @param nextZi 下個子初時刻
   */
  private fun getIndex(index: Int,
                       nextMidnightLmt: ChronoLocalDateTime<*>,
                       lmt: ChronoLocalDateTime<*>,
                       hourImpl: IHour,
                       location: ILocation,
                       changeDayAfterZi: Boolean,
                       nextZi: ChronoLocalDateTime<*>
  ): Int {
    var result = index
    //子正，在 LMT 零時之前
    if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
      // lmt 落於 當日零時之後，子正之前（餅最大的那一塊）
      val midnightNextZi = hourImpl.getLmtNextStartOf(nextMidnightLmt, location, Branch.子, julDayResolver)

      if (changeDayAfterZi && nextZi.get(ChronoField.DAY_OF_MONTH) == midnightNextZi.get(ChronoField.DAY_OF_MONTH)) {
        result++
      }
    } else {
      // lmt 落於 子正之後，到 24 時之間 (其 nextMidnight 其實是明日的子正) , 則不論是否早子時換日，都一定換日
      result++
    }
    return result
  }

  private fun getDay(lmt: ChronoLocalDateTime<*>, location: ILocation, changeDayAfterZi: Boolean): StemBranch {
    // 這是很特別的作法，將 lmt 當作 GMT 取 JulDay
    val lmtJulDay = (TimeTools.getGmtJulDay(lmt).value + 0.5).toInt()
    var index = (lmtJulDay - 11) % 60



    // 下個子初時刻
    val nextZiStart = hourImpl.getLmtNextStartOf(lmt, location, Branch.子, julDayResolver)


    // 下個子正時刻
    val nextMidnightLmt = midnightImpl.getNextMidnight(lmt, location, julDayResolver).let {
      val dur = Duration.between(nextZiStart, it).abs()
      if (dur.toMinutes() <= 1) {
        logger.warn("子初子正 幾乎重疊！ 可能是 DST 切換. 下個子初 = {} , 下個子正 = {} . 相隔秒 = {}", nextZiStart, it, dur.seconds) // DST 結束前一天，可能會出錯
        it.plus(1 , ChronoUnit.HOURS)
      } else {
        it
      }
    }


    if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
      //子正，在 LMT 零時之前
      index = getIndex(index, nextMidnightLmt, lmt, hourImpl, location, changeDayAfterZi, nextZiStart)
    } else {
      //子正，在 LMT 零時之後（含）
      if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
        // lmt 落於當地 零時 到 子正的這段期間
        if (TimeTools.isBefore(nextZiStart, nextMidnightLmt)) {
          // lmt 落於零時到子初之間 (這代表當地地點「極西」) , 此時一定還沒換日
          index--
        } else {
          // lmt 落於子初到子正之間
          if (!changeDayAfterZi)
          //如果子正才換日
            index--
        }
      } else {
        // lmt 落於前一個子正之後，到當天24時為止 (範圍最大的一塊「餅」)
        if (changeDayAfterZi
          && lmt.get(ChronoField.DAY_OF_MONTH) != nextZiStart.get(ChronoField.DAY_OF_MONTH)
          && nextZiStart.get(ChronoField.HOUR_OF_DAY) >= 12)
        // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
          index++
      }
    }
    return StemBranch[index]
  } // LMT 版本

  companion object {
    private val logger = KotlinLogging.logger {  }
  }
}
