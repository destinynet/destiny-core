/**
 * Created by smallufo on 2021-08-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import mu.KotlinLogging
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

val logger = KotlinLogging.logger { }


fun getDay(lmt: ChronoLocalDateTime<*>,
           location: ILocation,
           hourImpl: IHour,
           midnightImpl: IMidnight,
           changeDayAfterZi: Boolean,
           julDayResolver: JulDayResolver): StemBranch {

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
      it.plus(1, ChronoUnit.HOURS)
    } else {
      it
    }
  }


  if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
    //子正，在 LMT 零時之前
    index = getIndex(index, nextMidnightLmt, lmt, hourImpl, location, changeDayAfterZi, nextZiStart, julDayResolver)
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
        && nextZiStart.get(ChronoField.HOUR_OF_DAY) >= 12
      )
      // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
        index++
    }
  }
  return StemBranch[index]
}

private fun getIndex(index: Int, nextMidnightLmt: ChronoLocalDateTime<*>,
                     lmt: ChronoLocalDateTime<*>,
                     hourImpl: IHour,
                     location: ILocation,
                     changeDayAfterZi: Boolean,
                     nextZi: ChronoLocalDateTime<*>,
                     julDayResolver: JulDayResolver): Int {

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
