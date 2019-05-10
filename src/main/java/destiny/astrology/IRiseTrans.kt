/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.astrology

import destiny.core.calendar.ILocation
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 * SwissEph 的實作，是 destiny.astrology.swissephImpl.RiseTransImpl
 */
interface IRiseTrans {

  /**
   * 來源、目標時間都是 GMT
   *
   * @param atmosphericTemperature 攝氏溫度
   * @param atmosphericPressure    壓力 , 例如 1013.25
   *
   *
   * 根據測試資料 , 美國海軍天文台的計算結果，「似乎」傾向 center = false , refraction = true. 亦即： 計算「邊緣」以及「考量折射」
   *
   * TODO : 極區 可能無 rise / set 之值
   */
  fun getGmtTransJulDay(fromGmtJulDay: Double,
                        star: Star,
                        point: TransPoint,
                        location: ILocation,
                        discCenter: Boolean = false,
                        refraction: Boolean = true,
                        atmosphericTemperature: Double = 0.0,
                        atmosphericPressure: Double = 1013.25): Double?


  /**
   * 來源、目標時間都是 GMT
   */
  fun getGmtTrans(fromGmt: ChronoLocalDateTime<*>,
                  star: Star,
                  point: TransPoint,
                  location: ILocation,
                  revJulDayFunc: (Double) -> ChronoLocalDateTime<*>,
                  discCenter: Boolean = false,
                  refraction: Boolean = true,
                  atmosphericTemperature: Double = 0.0,
                  atmosphericPressure: Double = 1013.25): ChronoLocalDateTime<*>? {
    val fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt)

    return getGmtTransJulDay(fromGmtJulDay, star, point, location, discCenter, refraction, atmosphericTemperature,
      atmosphericPressure)?.let(revJulDayFunc)
  }

  /**
   * 來源、目標時間都是 LMT
   */
  fun getLmtTrans(fromLmtTime: ChronoLocalDateTime<*>,
                  star: Star,
                  point: TransPoint,
                  location: ILocation,
                  revJulDayFunc: (Double) -> ChronoLocalDateTime<*>,
                  discCenter: Boolean = false,
                  refraction: Boolean = true,
                  atmosphericTemperature: Double = 0.0,
                  atmosphericPressure: Double = 1013.25): ChronoLocalDateTime<*>? {
    val fromGmtTime = TimeTools.getGmtFromLmt(fromLmtTime, location)

    return getGmtTrans(fromGmtTime, star, point, location, revJulDayFunc, discCenter, refraction,
      atmosphericTemperature, atmosphericPressure)?.let{TimeTools.getLmtFromGmt(it , location)}
  }


  /**
   * 取得某段時間（LMT）之內，某星體的通過某 Point 的時刻（GMT）
   */
  fun getPeriodStarRiseTransGmtJulDay(fromLmtTime: ChronoLocalDateTime<*>,
                                      toLmtTime: ChronoLocalDateTime<*>,
                                      star: Star,
                                      point: TransPoint,
                                      location: Location,
                                      discCenter: Boolean = false,
                                      refraction: Boolean = true,
                                      atmosphericTemperature: Double = 0.0,
                                      atmosphericPressure: Double = 1013.25): List<Double> {
    val fromGmtJulDay = TimeTools.getGmtJulDay(TimeTools.getGmtFromLmt(fromLmtTime, location))
    val toGmtJulDay = TimeTools.getGmtJulDay(TimeTools.getGmtFromLmt(toLmtTime, location))

    return generateSequence(
      getGmtTransJulDay(fromGmtJulDay, star, point, location, discCenter, refraction, atmosphericTemperature,
                        atmosphericPressure)) {
      getGmtTransJulDay(it + 0.01, star, point, location, discCenter, refraction, atmosphericTemperature,
                        atmosphericPressure)
    }.takeWhile { it < toGmtJulDay }
      .toList()
  }

  /**
   * 取得某段時間（LMT）之內，某星體的通過某 Point 的時刻（LMT）
   * @param fromLmtTime 開始時間
   * @param toLmtTime 結束時間
   * @param star 星體
   * @param point 接觸點 : RISING , MERIDIAN , SETTING , NADIR
   * @param location 地點
   * @param atmosphericTemperature 溫度
   * @param atmosphericPressure 大氣壓力
   * @param discCenter 是否是星體中心（只影響 日、月），通常設為 false
   * @param refraction 是否考量濛氣差 , 通常設為 true
   * @return List <Time> in LMT
   * */
  fun getPeriodStarRiseTransTime(fromLmtTime: ChronoLocalDateTime<*>,
                                 toLmtTime: ChronoLocalDateTime<*>,
                                 star: Star,
                                 point: TransPoint,
                                 location: Location,
                                 revJulDayFunc: (Double) -> ChronoLocalDateTime<*>,
                                 discCenter: Boolean = false,
                                 refraction: Boolean = true,
                                 atmosphericTemperature: Double = 0.0,
                                 atmosphericPressure: Double = 1013.25): List<ChronoLocalDateTime<*>> {
    return getPeriodStarRiseTransGmtJulDay(fromLmtTime, toLmtTime, star, point, location, discCenter,
      refraction, atmosphericTemperature, atmosphericPressure)
      .map { gmtJulDay ->
        val gmt = revJulDayFunc.invoke(gmtJulDay)
        TimeTools.getLmtFromGmt(gmt, location)
      }.toList()
  }

  companion object {
    val logger = KotlinLogging.logger {  }
  }
}