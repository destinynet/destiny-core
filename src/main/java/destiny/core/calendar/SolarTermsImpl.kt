/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 03:36:11
 */
package destiny.core.calendar

import destiny.astrology.*
import destiny.astrology.Coordinate.ECLIPTIC
import destiny.astrology.Planet.SUN
import destiny.astrology.Utils.getNormalizeDegree
import java.io.Serializable

/**
 * 節氣實作
 */
class SolarTermsImpl(
  private val starTransitImpl: IStarTransit,
  private val starPositionImpl: IStarPosition<*>
                    ) : ISolarTerms, Serializable {


  /**
   * 計算某時刻當下的節氣
   * 步驟：
   * 1. 計算太陽在黃道面的度數
   * 2. 比對此度數 , 將此度數除以 15 取整數
   * 3. 將以上的值代入 SolarTerms 即是答案
   */
  override fun getSolarTermsFromGMT(gmtJulDay: Double): SolarTerms {
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
   *
   * @return List <SolarTermsTime>
  </SolarTermsTime> */
  override fun getPeriodSolarTermsGMTs(fromGmt: Double, toGmt: Double): List<SolarTermsTime> {
    var fromGmt = fromGmt
    var nowST = getSolarTermsFromGMT(fromGmt)

    var nextZodiacDegree = getNormalizeDegree((nowST.zodiacDegree + 15).toDouble())

    val resultList = mutableListOf<SolarTermsTime>()

    while (fromGmt < toGmt) {
      val solarTermsTime: SolarTermsTime

      val fromGmtTime = starTransitImpl.getNextTransitGmtDateTime(SUN, nextZodiacDegree, ECLIPTIC, fromGmt, true)
      fromGmt = TimeTools.getGmtJulDay(fromGmtTime)

      if (fromGmt > toGmt)
        break
      nowST = nowST.next()
      solarTermsTime = SolarTermsTime(nowST, fromGmtTime)
      resultList.add(solarTermsTime)
      nextZodiacDegree = getNormalizeDegree((nextZodiacDegree + 15))
    }
    return resultList
  }

  /**
   * @return 計算，從 某時刻開始，的下一個（或上一個）節氣的時間點為何
   */
  override fun getSolarTermsTime(solarTerms: SolarTerms, fromGmtJulDay: Double, forward: Boolean): Double {
    val zodiacDegree = solarTerms.zodiacDegree
    return starTransitImpl.getNextTransitGmt(SUN, zodiacDegree.toDouble(), ECLIPTIC, fromGmtJulDay, forward)
  }

  /**
   * 計算此時刻的...
   * 上一個「節」是什麼，其 GMT JulDay 為何
   * 下一個「節」是什麼，其 GMT JulDay 為何
   */
  override fun getMajorSolarTermsGmtBetween(gmtJulDay: Double): Pair<Pair<SolarTerms, Double>, Pair<SolarTerms, Double>> {

    return getSolarTermsBetween(gmtJulDay).let { pair ->
      if (pair.first.first.major) {
        // 前半段
        val nextMajorSolarTerms = pair.second.first.next()
        val nextMajorSolarTermsTime = starTransitImpl.getNextTransitGmt(SUN, nextMajorSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, true)
        pair.first to (nextMajorSolarTerms to nextMajorSolarTermsTime)
      } else {
        // 後半段
        val prevMajorSolarTerms = pair.first.first.previous()
        val prevMajorSolarTermsTime = starTransitImpl.getNextTransitGmt(SUN, prevMajorSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, false)
        (prevMajorSolarTerms to prevMajorSolarTermsTime) to pair.second
      }
    }
  }

  /**
   * 計算此時刻
   * 上一個 節/氣 是什麼，其 GMT JulDay 為何
   * 下一個 節/氣 是什麼，其 GMT JulDay 為何
   */
  override fun getSolarTermsBetween(gmtJulDay: Double): Pair<Pair<SolarTerms, Double>, Pair<SolarTerms, Double>> {
    val prevSolarTerms = getSolarTermsFromGMT(gmtJulDay)
    val prevGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, prevSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, false)
    val nextSolarTerms = prevSolarTerms.next()
    val nextGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, nextSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, true)
    return Pair(prevSolarTerms to prevGmtJulDay , nextSolarTerms to nextGmtJulDay)
  }

  /** 取得目前時刻與 兩個主要「節」、一個「氣」的相對位置 */
  override fun getSolarTermsPosition(gmtJulDay: Double): SolarTermsTimePos {

    return getSolarTermsBetween(gmtJulDay).let { pair ->
      if (pair.first.first.major) {
        // 前半段
        val middle = pair.second


        val endSolarTerms = middle.first.next()
        val nextGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, endSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, true)

        SolarTermsTimePos(gmtJulDay , pair.first , middle , (endSolarTerms to nextGmtJulDay))
      } else {
        // 後半段

        val prevSolarTerms = pair.first.first.previous()
        val prevGmtJulDay = starTransitImpl.getNextTransitGmt(SUN, prevSolarTerms.zodiacDegree.toDouble(), ECLIPTIC, gmtJulDay, false)

        SolarTermsTimePos(gmtJulDay , (prevSolarTerms to prevGmtJulDay) , pair.first , pair.second)
      }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SolarTermsImpl) return false

    if (starTransitImpl != other.starTransitImpl) return false

    return true
  }

  override fun hashCode(): Int {
    return starTransitImpl.hashCode()
  }

}
