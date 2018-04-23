/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:22:50
 */
package destiny.core.calendar


import java.time.chrono.ChronoLocalDateTime

/**
 * 計算節氣的介面
 */
interface ISolarTerms {

  /** 計算某時刻當下的節氣  */
  fun getSolarTermsFromGMT(gmtJulDay: Double): SolarTerms

  /** 承上， ChronoLocalDateTime 版本  */
  fun getSolarTermsFromGMT(gmt: ChronoLocalDateTime<*>): SolarTerms {
    val gmtJulDay = TimeTools.getGmtJulDay(gmt)
    return getSolarTermsFromGMT(gmtJulDay)
  }

  /**
   * 承上 , LMT + Location 版本
   */
  fun getSolarTerms(lmt: ChronoLocalDateTime<*>, location: ILocation): SolarTerms {
    val gmt = TimeTools.getGmtFromLmt(lmt, location)
    return getSolarTermsFromGMT(gmt)
  }

  /**
   * @return 計算，從 某時刻開始，的下一個（或上一個）節氣的時間點為何
   */
  fun getSolarTermsTime(solarTerms: SolarTerms, fromGmtJulDay: Double, isForward: Boolean): Double


  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   * @return List <SolarTermsTime>
   */
  fun getPeriodSolarTermsGMTs(fromGmt: Double, toGmt: Double): List<SolarTermsTime>


  /**
   * @return 傳回某段時間內的節氣列表， GMT 時刻
   */
  fun getPeriodSolarTermsGMTs(fromGmtTime: ChronoLocalDateTime<*>,
                              toGmtTime: ChronoLocalDateTime<*>): List<SolarTermsTime> {
    return getPeriodSolarTermsGMTs(TimeTools.getGmtJulDay(fromGmtTime), TimeTools.getGmtJulDay(toGmtTime))
  }

  /**
   * 計算從某時(fromLmtTime) 到某時(toLmtTime) 之間的節氣 , in LMT
   * @return List of **LMT** Time , 傳回 LMT 表示的節氣列表
   * 注意，此方法因為經過 Julian Day 的轉換，精確度比 GMT 差了 約萬分之一秒
   * List < SolarTermsTime >
   */
  fun getPeriodSolarTermsLMTs(fromLmt: ChronoLocalDateTime<*>,
                              toLmt: ChronoLocalDateTime<*>,
                              location: ILocation): List<SolarTermsTime> {
    val fromGmt = TimeTools.getGmtJulDay(fromLmt, location)
    val toGmt = TimeTools.getGmtJulDay(toLmt, location)

    return getPeriodSolarTermsGMTs(fromGmt, toGmt).map { stt ->
      val gmt = stt.time
      SolarTermsTime(stt.solarTerms, TimeTools.getLmtFromGmt(gmt, location))
    }.toList()
  }

  /**
   * 計算此時刻的...
   * 上一個「節」是什麼，其 GMT JulDay 為何
   * 下一個「節」是什麼，其 GMT JulDay 為何
   */
  fun getMajorSolarTermsGmtBetween(lmt: ChronoLocalDateTime<*> , location: ILocation) : Pair<Pair<SolarTerms, Double>, Pair<SolarTerms, Double>>

  /**
   * 計算此時刻，距離上一個「節」有幾秒，距離下一個「節」又有幾秒
   */
  fun getMajorSolarTermsBetween(lmt: ChronoLocalDateTime<*> , location: ILocation) : Pair<Pair<SolarTerms, Double>, Pair<SolarTerms, Double>> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    val (prevPair , nextPair) = getMajorSolarTermsGmtBetween(lmt, location)
    val dur1 = (gmtJulDay - prevPair.second) * 60 * 60 * 24
    val dur2 = (nextPair.second - gmtJulDay) * 60 * 60 * 24
    return Pair(Pair(prevPair.first , dur1) , Pair(nextPair.first , dur2))
  }

}
