/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 03:36:11
 */
package destiny.core.calendar

import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate.ECLIPTIC
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.astrology.Planet.SUN
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import java.io.Serializable

/**
 * 節氣實作
 */
class SolarTermsImpl(private val starTransitImpl: IStarTransit,
                     private val starPositionImpl: IStarPosition<*>,
                     private val julDayResolver: JulDayResolver
) : ISolarTerms, Serializable {


  /**
   * 計算某時刻當下的節氣
   * 步驟：
   * 1. 計算太陽在黃道面的度數
   * 2. 比對此度數 , 將此度數除以 15 取整數
   * 3. 將以上的值代入 SolarTerms 即是答案
   */
  override fun getSolarTermsFromGMT(gmtJulDay: GmtJulDay): SolarTerms {
    // Step 1: Calculate the Longitude of SUN
    val sp = starPositionImpl.getPosition(SUN, gmtJulDay, Centric.GEO, ECLIPTIC)
    // Step 2
    var index = (sp.lng / 15).toInt() + 3
    if (index >= 24)
      index -= 24
    return SolarTerms[index]
  }

  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   */
  override fun getPeriodSolarTermsEvents(fromGmt: GmtJulDay, toGmt: GmtJulDay): List<SolarTermsEvent> {
    var nowST = getSolarTermsFromGMT(fromGmt)
    var fromGmtValue = fromGmt

    var nextZodiacDegree = nowST.zodiacDegree.toZodiacDegree() + 15

    val resultList = mutableListOf<SolarTermsEvent>()

    while (fromGmtValue < toGmt) {
      fromGmtValue = starTransitImpl.getNextTransitGmt(SUN, nextZodiacDegree, fromGmtValue, true, ECLIPTIC)

      if (fromGmtValue > toGmt)
        break
      nowST = nowST.next()
      val event = SolarTermsEvent(fromGmtValue, nowST)
      resultList.add(event)
      nextZodiacDegree += 15
    }
    return resultList
  }

  /**
   * @return 計算，從 某時刻開始，的下一個（或上一個）節氣的時間點為何
   */
  override fun getSolarTermsTime(solarTerms: SolarTerms, fromGmtJulDay: GmtJulDay, forward: Boolean): GmtJulDay {
    val zodiacDegree = solarTerms.zodiacDegree
    return starTransitImpl.getNextTransitGmt(SUN, solarTerms.zodiacDegree.toZodiacDegree(), fromGmtJulDay, forward, ECLIPTIC)
  }

  /**
   * 計算此時刻的...
   * 上一個「節」是什麼，其 GMT JulDay 為何
   * 下一個「節」是什麼，其 GMT JulDay 為何
   */
  override fun getMajorSolarTermsGmtBetween(gmtJulDay: GmtJulDay): Pair<SolarTermsEvent, SolarTermsEvent> {

    return getSolarTermsBetween(gmtJulDay).let { (prior , after) ->
      if (prior.solarTerms.major) {
        // 前半段
        val nextMajorSolarTerms = after.solarTerms.next()
        val nextMajorSolarTermsTime = starTransitImpl.getNextTransitGmt(
          SUN, nextMajorSolarTerms.zodiacDegree.toZodiacDegree(), gmtJulDay, true, ECLIPTIC
        )
        prior to  SolarTermsEvent(nextMajorSolarTermsTime, nextMajorSolarTerms)
      } else {
        // 後半段
        val prevMajorSolarTerms = prior.solarTerms.previous()
        val prevMajorSolarTermsTime = starTransitImpl.getNextTransitGmt(
          SUN, prevMajorSolarTerms.zodiacDegree.toZodiacDegree(), gmtJulDay, false, ECLIPTIC
        )
        SolarTermsEvent(prevMajorSolarTermsTime, prevMajorSolarTerms) to after
      }
    }
  }

  /**
   * 計算此時刻
   * 上一個 節/氣 是什麼，其 GMT JulDay 為何
   * 下一個 節/氣 是什麼，其 GMT JulDay 為何
   */
  override fun getSolarTermsBetween(gmtJulDay: GmtJulDay): Pair<SolarTermsEvent, SolarTermsEvent> {
    val prevSolarTerms = getSolarTermsFromGMT(gmtJulDay)
    val prevGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, prevSolarTerms.zodiacDegree.toZodiacDegree(), gmtJulDay, false, ECLIPTIC)
    val nextSolarTerms = prevSolarTerms.next()
    val nextGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, nextSolarTerms.zodiacDegree.toZodiacDegree(), gmtJulDay, true, ECLIPTIC)
    return SolarTermsEvent(prevGmtJulDay, prevSolarTerms) to SolarTermsEvent(nextGmtJulDay, nextSolarTerms)
  }

  /** 取得目前時刻與 兩個主要「節」、一個「氣」的相對位置 */
  override fun getSolarTermsPosition(gmtJulDay: GmtJulDay): SolarTermsTimePos {

    return getSolarTermsBetween(gmtJulDay).let { (prior , after) ->
      if (prior.solarTerms.major) {
        // 前半段
        val endSolarTerms = after.solarTerms.next()
        val nextGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, endSolarTerms.zodiacDegree.toZodiacDegree(), gmtJulDay, true, ECLIPTIC)
        SolarTermsTimePos(gmtJulDay, prior, after, SolarTermsEvent(nextGmtJulDay, endSolarTerms))
      } else {
        // 後半段
        val prevSolarTerms = prior.solarTerms.previous()
        val prevGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, prevSolarTerms.zodiacDegree.toZodiacDegree(), gmtJulDay, false, ECLIPTIC)
        SolarTermsTimePos(gmtJulDay, SolarTermsEvent(prevGmtJulDay, prevSolarTerms), prior, after)
      }
    }
  }


}
