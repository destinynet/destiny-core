/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:22:50
 */
package destiny.core.calendar


import destiny.core.calendar.Constants.SECONDS_OF_DAY
import destiny.core.calendar.TimeTools.toGmtJulDay
import java.time.chrono.ChronoLocalDateTime

/**
 * 計算節氣的介面
 */
interface ISolarTerms {

  /** 計算某時刻當下的節氣  */
  fun getSolarTermsFromGMT(gmtJulDay: GmtJulDay): SolarTerms

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
  fun getSolarTermsTime(solarTerms: SolarTerms, fromGmtJulDay: GmtJulDay, forward: Boolean): GmtJulDay


  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   */
  fun getPeriodSolarTermsEvents(fromGmt: GmtJulDay, toGmt: GmtJulDay): List<SolarTermsEvent>

  /**
   * 計算從某時(fromLmtTime) 到某時(toLmtTime) 之間的節氣 , in LMT
   * @return List of **LMT** Time , 傳回 LMT 表示的節氣列表
   * 注意，此方法因為經過 Julian Day 的轉換，精確度比 GMT 差了 約萬分之一秒
   * List < SolarTermsTime >
   */
  fun getPeriodSolarTermsLMTs(fromLmt: ChronoLocalDateTime<*>,
                              toLmt: ChronoLocalDateTime<*>,
                              location: ILocation): List<SolarTermsEvent> {
    return getPeriodSolarTermsEvents(fromLmt.toGmtJulDay(location), toLmt.toGmtJulDay(location))
  }


  /**
   * 計算此時刻
   * 上一個 節/氣 是什麼，其 GMT JulDay 為何
   * 下一個 節/氣 是什麼，其 GMT JulDay 為何
   */
  fun getSolarTermsBetween(gmtJulDay: GmtJulDay): Pair<SolarTermsEvent, SolarTermsEvent>

  /**
   * 計算此時刻的...
   * 上一個「節」是什麼，其 GMT JulDay 為何
   * 下一個「節」是什麼，其 GMT JulDay 為何
   */
  fun getMajorSolarTermsGmtBetween(gmtJulDay: GmtJulDay) : Pair<SolarTermsEvent, SolarTermsEvent>

  fun getMajorSolarTermsGmtBetween(lmt: ChronoLocalDateTime<*> , location: ILocation) : Pair<SolarTermsEvent, SolarTermsEvent> {
    return getMajorSolarTermsGmtBetween(lmt.toGmtJulDay(location))
  }

  /**
   * 計算此時刻，距離上一個「節」有幾秒，距離下一個「節」又有幾秒
   */
  fun getMajorSolarTermsBetween(lmt: ChronoLocalDateTime<*> , location: ILocation) : Pair<Pair<SolarTerms, Double>, Pair<SolarTerms, Double>> {
    val gmtJulDay = lmt.toGmtJulDay(location)
    val (prevPair , nextPair) = getMajorSolarTermsGmtBetween(lmt, location)
    val dur1 = (gmtJulDay - prevPair.begin.value).value * SECONDS_OF_DAY
    val dur2 = (nextPair.begin.value - gmtJulDay.value) * SECONDS_OF_DAY
    return Pair(Pair(prevPair.solarTerms , dur1) , Pair(nextPair.solarTerms , dur2))
  }


  /** 取得目前時刻與 兩個主要「節」、一個「氣」的相對位置 */
  fun getSolarTermsPosition(gmtJulDay: GmtJulDay) : SolarTermsTimePos
}
