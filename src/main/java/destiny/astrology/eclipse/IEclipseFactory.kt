/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology.eclipse

import destiny.astrology.eclipse.ISolarEclipse.SolarType
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools.Companion.getGmtJulDay
import java.time.chrono.ChronoLocalDateTime

/** 計算日食、月食的介面  */
interface IEclipseFactory {

  /** 從此時之後，全球各地的「一場」日食資料 (型態、開始、最大、結束...）  */
  fun getNextSolarEclipse(fromGmtJulDay: Double, forward: Boolean, types: Collection<SolarType>): AbstractSolarEclipse

  fun getNextSolarEclipse(fromGmtJulDay: Double, forward: Boolean): AbstractSolarEclipse {
    return getNextSolarEclipse(fromGmtJulDay, forward, listOf(*SolarType.values()))
  }

  /** 同上，月食  */
  fun getNextLunarEclipse(fromGmtJulDay: Double, forward: Boolean): AbstractLunarEclipse


  // ========================================================================================

  /** 從此之後 , 此地點下次發生日食的資訊為何 (tuple.v1) , 以及， 日食最大化的時間，該地的觀測資訊為何 (tuple.v2)  */
  fun getNextSolarEclipse(fromGmtJulDay: Double, lng: Double, lat: Double, alt: Double?=0.0, forward: Boolean): Pair<EclipseSpan, SolarEclipseObservation>

  fun getNextSolarEclipse(fromGmtJulDay: Double, loc: Location, forward: Boolean): Pair<EclipseSpan, SolarEclipseObservation> {
    return getNextSolarEclipse(fromGmtJulDay, loc.lng, loc.lat, loc.altitudeMeter, forward)
  }

  /** 從此之後 , 此地點下次發生月食的資訊為何 (tuple.v1) , 以及， 該地能否見到 半影、偏食、全蝕、的起訖 (tuple.v2)  */
  fun getNextLunarEclipse(fromGmtJulDay: Double, lng: Double, lat: Double, alt: Double, forward: Boolean): AbstractLunarEclipseObservation


  // ========================================================================================

  /** 此時此刻，哪裡有發生日食，其「中線」的地點為何 , 以及其相關日食觀測結果 . 此 method 專門計算「中線在哪裡 , 其太陽觀測為何」 (t.v1) , 是否出現中線了 (t.v2)  */
  fun getEclipseCenterInfo(gmtJulDay: Double): Pair<SolarEclipseObservation, Boolean>?


  /** 若當下 gmtJulDay 有日食，傳出此地點觀測此日食的相關資料  */
  fun getSolarEclipseObservation(gmtJulDay: Double, lng: Double, lat: Double, alt: Double): SolarEclipseObservation?

  /** 若當下 gmtJulDay 有月食，傳出此地點觀測此月食的相關資料  */
  fun getLunarEclipseObservation(gmtJulDay: Double, lng: Double, lat: Double, alt: Double): AbstractLunarEclipseObservation?


  fun getSolarEclipseObservation(gmt: ChronoLocalDateTime<*>, lng: Double, lat: Double, alt: Double): SolarEclipseObservation? {
    return getSolarEclipseObservation(getGmtJulDay(gmt), lng, lat, alt)
  }

  // ========================================================================================

  /**
   * 全球，某時間範圍內的日食記錄
   * [IEclipseFactory2.getRangeSolarEclipses]
   * */
  fun getRangeSolarEclipses(fromGmt: Double, toGmt: Double, types: Collection<SolarType> = listOf(*SolarType.values())): List<AbstractSolarEclipse> {
    require( fromGmt < toGmt) { "fromGmt : $fromGmt must less than toGmt : $toGmt" }

    val list = mutableListOf<AbstractSolarEclipse>()
    var gmt = fromGmt

    while (gmt < toGmt) {
      val e = getNextSolarEclipse(gmt, true, types)
      list.add(e)

      gmt = e.end
    }
    return list
  }

  /**
   * 承上 , ChronoLocalDateTime 版本 , 搜尋 全部 種類的日食
   * [IEclipseFactory2.getRangeSolarEclipses]
   * */
  fun getRangeSolarEclipses(fromGmt: ChronoLocalDateTime<*>, toGmt: ChronoLocalDateTime<*>): List<AbstractSolarEclipse> {
    return getRangeSolarEclipses(getGmtJulDay(fromGmt), getGmtJulDay(toGmt))
  }

  /** 承上 , ChronoLocalDateTime 版本 , 搜尋 單一 種類的日食  */
  fun getRangeSolarEclipses(fromGmt: ChronoLocalDateTime<*>, toGmt: ChronoLocalDateTime<*>, type: SolarType): List<AbstractSolarEclipse> {
    return getRangeSolarEclipses(getGmtJulDay(fromGmt), getGmtJulDay(toGmt), listOf(type))
  }


  /** 全球，某時間範圍內的月食記錄  */
  fun getRangeLunarEclipses(fromGmt: Double, toGmt: Double, types: Collection<ILunarEclipse.LunarType>?): List<AbstractLunarEclipse> {
    require( fromGmt < toGmt) { "fromGmt : $fromGmt must less than toGmt : $toGmt" }

    val list = mutableListOf<AbstractLunarEclipse>()
    var gmt = fromGmt

    while (gmt < toGmt) {
      val e = getNextLunarEclipse(gmt, true)
      list.add(e)

      gmt = e.end
    }
    return list
  }

  /** 承上 , ChronoLocalDateTime 版本 , 搜尋 全部 種類的日食  */
  fun getRangeLunarEclipses(fromGmt: ChronoLocalDateTime<*>, toGmt: ChronoLocalDateTime<*>): List<AbstractLunarEclipse> {
    return getRangeLunarEclipses(getGmtJulDay(fromGmt), getGmtJulDay(toGmt), null)
  }


}
/** 搜尋 全部 種類的日食  */
