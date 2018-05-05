/**
 * Created by smallufo on 2018-05-05.
 */
package destiny.astrology.eclipse

import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime


interface IEclipseFactory2 {

  // ================================== 日食 ==================================

  /** 從此時之後，全球各地的「一場」日食資料 (型態、開始、最大、結束...）  */
  fun getNextSolarEclipse(fromGmtJulDay: Double, forward: Boolean, types: Collection<ISolarEclipse.SolarType>):
    AbstractSolarEclipse2

  /** 承上 , 不指定 日食類型 [ISolarEclipse.SolarType]]  */
  fun getNextSolarEclipse(fromGmtJulDay: Double, forward: Boolean): AbstractSolarEclipse2 {
    return getNextSolarEclipse(fromGmtJulDay, forward, listOf(*ISolarEclipse.SolarType.values()))
  }

  /** 全球，某時間範圍內的日食記錄  */
  fun getRangeSolarEclipses(fromGmt: Double, toGmt: Double, types: Collection<ISolarEclipse.SolarType> = listOf(*ISolarEclipse.SolarType.values())) : List<AbstractSolarEclipse2> {
    require( fromGmt < toGmt) { "fromGmt : $fromGmt must less than toGmt : $toGmt" }

    return generateSequence (getNextSolarEclipse(fromGmt , true , types)) {
      getNextSolarEclipse(it.end , true , types)
    }.takeWhile { it.end < toGmt }
      .toList()
  }

  /** 承上 , [ChronoLocalDateTime] 版本 , 搜尋 全部 種類的日食 */
  fun getRangeSolarEclipses(fromGmt: ChronoLocalDateTime<*>, toGmt: ChronoLocalDateTime<*>) : List<AbstractSolarEclipse2> {
    return getRangeSolarEclipses(TimeTools.getGmtJulDay(fromGmt), TimeTools.getGmtJulDay(toGmt))
  }

  /** 承上 , [ChronoLocalDateTime] 版本 , 搜尋 單一種類的日食 */
  fun getRangeSolarEclipses(fromGmt: ChronoLocalDateTime<*>, toGmt: ChronoLocalDateTime<*> , type : ISolarEclipse.SolarType) : List<AbstractSolarEclipse2> {
    return getRangeSolarEclipses(TimeTools.getGmtJulDay(fromGmt) , TimeTools.getGmtJulDay(toGmt) , listOf(type))
  }

  // ================================== 月食 ==================================

  /** 從此時之後，全球各地的「一場」月食資料 (型態、開始、最大、結束...）  */
  fun getNextLunarEclipse(fromGmtJulDay: Double, forward: Boolean) : AbstractLunarEclipse2
}