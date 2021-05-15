/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.core.astrology

import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
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
  fun getRelativeTransit(transitStar: Star,
                         relativeStar: Star,
                         angle: Double,
                         gmtJulDay: Double,
                         isForward: Boolean): Double?

  fun getRelativeTransit(transitStar: Star,
                         relativeStar: Star,
                         angle: Double,
                         fromGmt: ChronoLocalDateTime<*>,
                         isForward: Boolean,
                         julDayResolver: JulDayResolver): ChronoLocalDateTime<*>? {
    val gmtJulDay = TimeTools.getGmtJulDay(fromGmt)

    return getRelativeTransit(
      transitStar, relativeStar, angle, gmtJulDay, isForward
    )?.let { value -> julDayResolver.getLocalDateTime(value) }
  }


  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return 傳回一連串的 gmtJulDays
   */
  fun getPeriodRelativeTransitGmtJulDays(transitStar: Star,
                                         relativeStar: Star,
                                         fromJulDay: Double,
                                         toJulDay: Double,
                                         angle: Double): List<Double> {

    return generateSequence(getRelativeTransit(transitStar, relativeStar, angle, fromJulDay, true)) {
      getRelativeTransit(transitStar, relativeStar, angle, it + 0.000001, true)
    }.takeWhile { it < toJulDay }
      .toList()
  }

  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return List < Map < angle , Time > >
   * 傳回的是 GMT 時刻
   */
  fun getPeriodRelativeTransitGMTs(transitStar: Star,
                                   relativeStar: Star,
                                   fromJulDay: Double,
                                   toJulDay: Double,
                                   angle: Double,
                                   julDayResolver: JulDayResolver): List<ChronoLocalDateTime<*>> {
    return getPeriodRelativeTransitGmtJulDays(transitStar, relativeStar, fromJulDay, toJulDay, angle)
      .map { d -> julDayResolver.getLocalDateTime(d) }
      .toList()
  }

  /** 傳回 GMT  */
  fun getPeriodRelativeTransitGMTs(transitStar: Star,
                                   relativeStar: Star,
                                   fromGmt: ChronoLocalDateTime<*>,
                                   toGmt: ChronoLocalDateTime<*>,
                                   angle: Double,
                                   julDayResolver: JulDayResolver): List<ChronoLocalDateTime<*>> {
    val fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt)
    val toGmtJulDay = TimeTools.getGmtJulDay(toGmt)
    return getPeriodRelativeTransitGMTs(transitStar, relativeStar, fromGmtJulDay, toGmtJulDay, angle, julDayResolver)
  }


  /** 承上 , LMT 的 ChronoLocalDateTime 版本  */
  fun getPeriodRelativeTransitLMTs(transitStar: Star,
                                   relativeStar: Star,
                                   fromLmt: ChronoLocalDateTime<*>,
                                   toLmt: ChronoLocalDateTime<*>,
                                   location: ILocation,
                                   angle: Double,
                                   julDayResolver: JulDayResolver): List<ChronoLocalDateTime<*>> {
    val fromGmt = TimeTools.getGmtFromLmt(fromLmt, location)
    val toGmt = TimeTools.getGmtFromLmt(toLmt, location)

    return getPeriodRelativeTransitGmtJulDays(
      transitStar,
      relativeStar,
      TimeTools.getGmtJulDay(fromGmt),
      TimeTools.getGmtJulDay(toGmt),
      angle
    )
      .map { gmtJulDay ->
        val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
        TimeTools.getLmtFromGmt(gmt, location)
      }
      .toList()
  }


  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   *
   * result 有可能為 empty , 例如計算 太陽/水星 [90,180,270] 的度數，將不會有結果
   *
   * @return 傳回的 Pair , 前者為 GMT 時間，後者為角度
   */
  fun getNearestRelativeTransitGmtJulDay(transitStar: Star,
                                         relativeStar: Star,
                                         fromGmtJulDay: Double,
                                         angles: Collection<Double>,
                                         forward: Boolean): Pair<Double, Double>? {
    /**
     * 相交 270 度也算 90 度
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */

    val realAngles: List<Double> = angles
      .flatMap {
        if (it != 0.0)
          listOf(it, 360.0 - it)
        else
          listOf(it)
      }
      .toList()

    return realAngles.mapNotNull { angle ->
      getRelativeTransit(transitStar, relativeStar, angle, fromGmtJulDay, forward)?.let { resultGmtJulDay ->
        resultGmtJulDay to angle
      }
    }
      .sortedBy { (julDay, _) -> julDay }
      .let {
        if (forward)
          it.firstOrNull()  // 順推，取第一個（最接近當下）
        else
          it.lastOrNull()   // 逆推，取最後一個（最接近當下）
      }?.let { (julDay, angle) ->
        if (angle > 180)
          julDay to 360 - angle
        else
          julDay to angle
      }

  }


  /**
   * 承上 , Date Time 版本
   * @return 傳回的 Pair , 前者為 GMT 時間，後者為角度
   */
  fun getNearestRelativeTransitGmtJulDay(transitStar: Star,
                                         relativeStar: Star,
                                         fromGmt: ChronoLocalDateTime<*>,
                                         angles: Collection<Double>,
                                         isForward: Boolean): Pair<Double, Double>? {
    val gmtJulDay = TimeTools.getGmtJulDay(fromGmt)
    return getNearestRelativeTransitGmtJulDay(transitStar, relativeStar, gmtJulDay, angles, isForward)
  }

}
