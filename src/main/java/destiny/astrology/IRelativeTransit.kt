/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.astrology

import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * <pre>
 * 計算兩星呈現某交角的時間
 * Swiss ephemeris 的實作是 RelativeTransitImpl
</pre> *
 */
interface IRelativeTransit {

  /**
   * <pre>
   * 計算兩星下一個/上一個交角。
   * 注意！angle 有方向性，如果算相刑的角度，別忘了另外算 270度
   * TODO : 目前 RelativeTransitImpl 僅支援 Planet 以及 Asteroid
   * 傳回的 Time 是 GMT julDay
  </pre> *
   */
  fun getRelativeTransit(transitStar: Star, relativeStar: Star, angle: Double, gmtJulDay: Double, isForward: Boolean): Double?

  fun getRelativeTransit(transitStar: Star, relativeStar: Star, angle: Double, fromGmt: ChronoLocalDateTime<*>, isForward: Boolean,
                         revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*>? {
    val gmtJulDay = TimeTools.getGmtJulDay(fromGmt)
    val value = getRelativeTransit(transitStar, relativeStar, angle, gmtJulDay, isForward)
    return if (value != null)
      revJulDayFunc.invoke(value)
    else
      null
  }


  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return 傳回一連串的 gmtJulDays
   */
  fun getPeriodRelativeTransitGmtJulDays(transitStar: Star, relativeStar: Star, fromJulDay: Double, toJulDay: Double, angle: Double): List<Double> {
    var fromJD = fromJulDay
    val resultList = mutableListOf<Double>()
    while (fromJD < toJulDay) {
      val value = getRelativeTransit(transitStar, relativeStar, angle, fromJD, true)
      if (value != null) {
        fromJD = value
        if (fromJD > toJulDay)
          break

        resultList.add(value)
        fromJD += 0.000001
      }
    }
    return resultList
  }

  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return List < Map < angle , Time > >
   * 傳回的是 GMT 時刻
   */
  fun getPeriodRelativeTransitGMTs(transitStar: Star, relativeStar: Star, fromJulDay: Double, toJulDay: Double, angle: Double,
                                   revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): List<ChronoLocalDateTime<*>> {
    return getPeriodRelativeTransitGmtJulDays(transitStar, relativeStar, fromJulDay, toJulDay, angle)
      .map { d -> revJulDayFunc.invoke(d) }
      .toList()
  }

  /** 傳回 GMT  */
  fun getPeriodRelativeTransitGMTs(transitStar: Star, relativeStar: Star, fromGmt: ChronoLocalDateTime<*>, toGmt: ChronoLocalDateTime<*>, angle: Double,
                                   revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): List<ChronoLocalDateTime<*>> {
    val fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt)
    val toGmtJulDay = TimeTools.getGmtJulDay(toGmt)
    return getPeriodRelativeTransitGMTs(transitStar, relativeStar, fromGmtJulDay, toGmtJulDay, angle, revJulDayFunc)
  }


  /** 承上 , LMT 的 ChronoLocalDateTime 版本  */
  fun getPeriodRelativeTransitLMTs(transitStar: Star, relativeStar: Star, fromLmt: ChronoLocalDateTime<*>, toLmt: ChronoLocalDateTime<*>, location: Location, angle: Double,
                                   revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): List<ChronoLocalDateTime<*>> {
    val fromGmt = TimeTools.getGmtFromLmt(fromLmt, location)
    val toGmt = TimeTools.getGmtFromLmt(toLmt, location)

    return getPeriodRelativeTransitGmtJulDays(transitStar, relativeStar, TimeTools.getGmtJulDay(fromGmt), TimeTools.getGmtJulDay(toGmt), angle)
      .map { gmtJulDay ->
        val gmt = revJulDayFunc.invoke(gmtJulDay)
        TimeTools.getLmtFromGmt(gmt, location)
      }
      .toList()
  }


  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   *
   * result 有可能為 empty , 例如計算 太陽/水星 [90,180,270] 的度數，將不會有結果
   *
   * @return 傳回的 Tuple , 前者為 GMT 時間，後者為角度
   */
  fun getNearestRelativeTransitGmtJulDay(transitStar: Star, relativeStar: Star, fromGmtJulDay: Double, angles: Collection<Double>, isForward: Boolean): Pair<Double, Double>? {
    /**
     * 相交 270 度也算 90 度
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */

    val realAngles: List<Double> = angles
      .flatMap { it ->
        if (it != 0.0)
          listOf(it , 360.0-it)
        else
          listOf(it)
      }
      .toList()


    var resultGmtJulDay: Double? = null
    var resultAngle: Double? = null
    for (angle in realAngles) {
      val value = getRelativeTransit(transitStar, relativeStar, angle, fromGmtJulDay, isForward)

      if (resultGmtJulDay == null) {
        resultGmtJulDay = value
        resultAngle = angle
      } else {

        if (value != null) {
          //目前已經有一個結果，比較看看現在算出的，和之前的，哪個比較近
          if (isForward) {
            //順推
            if (value <= resultGmtJulDay) {
              resultGmtJulDay = value
              resultAngle = angle
            }
          } else {
            //逆推
            if (value > resultGmtJulDay) {
              resultGmtJulDay = value
              resultAngle = angle
            }
          }
        }
      }
    } // each realAngle


    if (resultAngle != null && resultAngle > 180)
      resultAngle = 360 - resultAngle

    return if (resultGmtJulDay != null) {
      Pair(resultGmtJulDay, resultAngle!!)
    } else {
      null
    }
  }


  /**
   * 承上 , Date Time 版本
   * @return 傳回的 Pair , 前者為 GMT 時間，後者為角度
   */
  fun getNearestRelativeTransitGmtJulDay(transitStar: Star, relativeStar: Star, fromGmt: ChronoLocalDateTime<*>, angles: Collection<Double>, isForward: Boolean): Pair<Double, Double>? {
    val gmtJulDay = TimeTools.getGmtJulDay(fromGmt)
    return getNearestRelativeTransitGmtJulDay(transitStar, relativeStar, gmtJulDay, angles, isForward)
  }

}
