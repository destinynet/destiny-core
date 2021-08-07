/**
 * Created by smallufo on 2021-08-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.News
import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.Planet
import destiny.core.calendar.*
import destiny.core.chinese.*
import mu.KotlinLogging
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

val logger = KotlinLogging.logger { }


fun getDay(
  lmt: ChronoLocalDateTime<*>,
  location: ILocation,
  hourImpl: IHour,
  midnightImpl: IMidnight,
  changeDayAfterZi: Boolean,
  julDayResolver: JulDayResolver
): StemBranch {

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

private fun getIndex(
  index: Int, nextMidnightLmt: ChronoLocalDateTime<*>,
  lmt: ChronoLocalDateTime<*>,
  hourImpl: IHour,
  location: ILocation,
  changeDayAfterZi: Boolean,
  nextZi: ChronoLocalDateTime<*>,
  julDayResolver: JulDayResolver
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


fun getMonth(
  gmtJulDay: GmtJulDay,
  location: ILocation,
  southernHemisphereOpposition: Boolean,
  hemisphereBy: HemisphereBy,
  solarTermsImpl: ISolarTerms,
  starPositionImpl: IStarPosition<*>,
  yearImpl: IYear
): IStemBranch {
  val resultMonthBranch: Branch
  //先算出太陽在黃經上的度數

  // 目前的節氣
  val solarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

  val monthIndex = (SolarTerms.getIndex(solarTerms) / 2 + 2).let {
    if (it >= 12)
      it - 12
    else
      it
  }

  // 月支
  val monthBranch = Branch[monthIndex]

  if (southernHemisphereOpposition) {
    /**
     * 解決南半球月支正沖的問題
     */
    if (hemisphereBy == HemisphereBy.EQUATOR) {
      //如果是依據赤道來區分南北半球
      resultMonthBranch = if (location.northSouth == News.NorthSouth.SOUTH)
        Branch[monthIndex + 6]
      else
        monthBranch
    } else {
      /**
       * 如果 hemisphereBy == DECLINATION (赤緯) , 就必須計算 太陽在「赤緯」的度數
       */
      val solarEquatorialDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.EQUATORIAL).lat

      if (solarEquatorialDegree >= 0) {
        //如果太陽在赤北緯
        resultMonthBranch = if (location.northSouth == News.NorthSouth.NORTH) {
          //地點在北半球
          if (location.lat >= solarEquatorialDegree)
            monthBranch
          else
            Branch[monthIndex + 6] //所在地緯度低於 太陽赤緯，取對沖月份
        } else {
          //地點在南半球 , 取正沖
          Branch[monthIndex + 6]
        }
      } else {
        //太陽在赤南緯
        resultMonthBranch = if (location.northSouth == News.NorthSouth.SOUTH) {
          //地點在南半球
          if (location.lat <= solarEquatorialDegree)
            Branch[monthIndex + 6] //所在地緯度高於 太陽赤南緯，真正的南半球
          else
            monthBranch //雖在南半球，但緯度低於太陽赤南緯，視為北半球
        } else {
          //地點在北半球，月支不變
          monthBranch
        }
      }
    }
  } else
    resultMonthBranch = monthBranch

  // 年干
  val yearStem = yearImpl.getYear(gmtJulDay, location).stem
  return StemBranch[getMonthStem(gmtJulDay, yearStem, resultMonthBranch , yearImpl.changeYearDegree , starPositionImpl), resultMonthBranch]
}

/**
 * 五虎遁月 取得月干
 *
 * 甲己之年丙作首
 * 乙庚之歲戊為頭
 * 丙辛之歲由庚上
 * 丁壬壬位順行流
 * 若言戊癸何方發
 * 甲寅之上好追求。
 *
 */
private fun getMonthStem(gmtJulDay: GmtJulDay, yearStem: Stem, monthBranch: Branch , changeYearDegree : Double , starPositionImpl: IStarPosition<*>): Stem {

  // 月干
  var monthStem: Stem = StemBranchUtils.getMonthStem(yearStem, monthBranch)

  if (changeYearDegree != 315.0) {

    val sunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC).lng

    if (changeYearDegree < 315) {
      logger.debug("換年點在立春前 , changeYearDegree < 315 , value = {}", changeYearDegree)
      if (sunDegree > changeYearDegree && 315 > sunDegree) {
        // t <---立春---- LMT -----換年點
        monthStem = Stem[monthStem.index - 2]
      }
    } else if (changeYearDegree > 315) {
      //換年點在立春後 , 還沒測試
      if (sunDegree > 315 && changeYearDegree > sunDegree)
        monthStem = Stem[monthStem.index + 2]
    }
  }
  return monthStem
}
