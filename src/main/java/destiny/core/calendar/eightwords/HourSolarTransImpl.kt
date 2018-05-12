/*
 * @author smallufo
 * @date 2004/12/10
 * @time 下午 03:53:08
 */
package destiny.core.calendar.eightwords

import destiny.astrology.IRiseTrans
import destiny.astrology.Planet
import destiny.astrology.TransPoint
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

/**
 * <PRE>
 * 時辰的劃分實作
 * 利用太陽過天底 到天頂之間，劃分十二等份
 * 再從太陽過天頂到天底，平均劃分十二等份
 * 依此來切割 12 時辰
</PRE> *
 */
class HourSolarTransImpl(private val riseTransImpl: IRiseTrans) : IHour, Serializable {
  private val logger = LoggerFactory.getLogger(javaClass)

  private val atmosphericPressure = 1013.25
  private val atmosphericTemperature = 0.0
  private var isDiscCenter = true
  private var hasRefraction = true

  fun setDiscCenter(isDiscCenter: Boolean) {
    this.isDiscCenter = isDiscCenter
  }

  fun setHasRefraction(hasRefraction: Boolean) {
    this.hasRefraction = hasRefraction
  }

  override fun getHour(gmtJulDay: Double, location: ILocation): Branch {

    val nextMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
    val nextNadir = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

    if (nextNadir > nextMeridian) {
      //子正到午正（上半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousNadirGmt = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

      logger.debug("gmtJulDay = {} , 上一個子正(GMT) = {}", gmtJulDay, revJulDayFunc.invoke(previousNadirGmt))

      val diffDays = nextMeridian - previousNadirGmt // 從子正到午正，總共幾秒
      val oneUnitDays = diffDays / 12.0
      logger.debug("diffDays = {} , oneUnitDays = {}", diffDays, oneUnitDays)
      return when {
        gmtJulDay < previousNadirGmt + oneUnitDays -> 子
        gmtJulDay < previousNadirGmt + oneUnitDays * 3 -> 丑
        gmtJulDay < previousNadirGmt + oneUnitDays * 5 -> 寅
        gmtJulDay < previousNadirGmt + oneUnitDays * 7 -> 卯
        gmtJulDay < previousNadirGmt + oneUnitDays * 9 -> 辰
        gmtJulDay < previousNadirGmt + oneUnitDays * 11 -> 巳
        else -> 午
      }
    } else {
      //午正到子正（下半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

      val diffDays = nextNadir - previousMeridian
      val oneUnitDays = diffDays / 12.0

      return when {
        gmtJulDay < previousMeridian + oneUnitDays -> 午
        gmtJulDay < previousMeridian + oneUnitDays * 3 -> 未
        gmtJulDay < previousMeridian + oneUnitDays * 5 -> 申
        gmtJulDay < previousMeridian + oneUnitDays * 7 -> 酉
        gmtJulDay < previousMeridian + oneUnitDays * 9 -> 戌
        gmtJulDay < previousMeridian + oneUnitDays * 11 -> 亥
        else -> 子
      }
    }
  }

  override fun getGmtNextStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {
    val resultGmt: Double
    val nextMeridianGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
    val nextNadirGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

    if (nextNadirGmt > nextMeridianGmt) {
      //LMT 位於子正到午正（上半天）
      val twelveHoursAgo = gmtJulDay - 0.5
      val previousNadir = riseTransImpl.getGmtTransJulDay(twelveHoursAgo, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

      val oneUnit1 = (nextMeridianGmt - previousNadir) / 12.0 // 單位為 day
      val oneUnit2 = (nextNadirGmt - nextMeridianGmt) / 12.0

      val currentEb = getHour(gmtJulDay, location) // 取得目前在哪個時辰之中
      if (eb.index > currentEb.index || eb == 子) {
        //代表現在所處的時辰，未超過欲求的時辰
        resultGmt = if (eb == 丑 || eb == 寅 || eb == 卯 || eb == 辰 || eb == 巳 || eb == 午) {
          previousNadir + oneUnit1 * ((eb.index - 1) * 2 + 1)
        } else if (eb == 未 || eb == 申 || eb == 酉 || eb == 戌 || eb == 亥) {
          nextMeridianGmt + oneUnit2 * ((eb.index - 7) * 2 + 1)
        } else {
          nextMeridianGmt + oneUnit2 * 11 // eb == 子時
        }
      } else {
        //欲求的時辰，早於現在所處的時辰 ==> 代表算的是明天的時辰
        val nextNextMeridianGmt = riseTransImpl.getGmtTransJulDay(nextNadirGmt, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit3 = (nextNextMeridianGmt - nextNadirGmt) / 12.0
        val nextNextNadir = riseTransImpl.getGmtTransJulDay(nextNextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit4 = (nextNextNadir - nextNextMeridianGmt) / 12.0
        resultGmt = if (eb == 丑 || eb == 寅 || eb == 卯 || eb == 辰 || eb == 巳 || eb == 午) {
          nextNadirGmt + oneUnit3 * ((eb.index - 1) * 2 + 1)
        } else if (eb == 未 || eb == 申 || eb == 酉 || eb == 戌 || eb == 亥) {
          nextNextMeridianGmt + oneUnit4 * ((eb.index - 7) * 2 + 1)
        } else {
          throw RuntimeException("Runtime Exception : 沒有子時的情況") //沒有子時的情況
        }
      }

    } else {
      //LMT 位於 午正到子正（下半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

      val oneUnit1 = (nextMeridianGmt - nextNadirGmt) / 12.0 //從 下一個子正 到 下一個午正，總共幾天
      val oneUnit2 = (nextNadirGmt - previousMeridian) / 12.0 //從 下一個子正 到 上一個午正，總共幾秒

      val currentEb = this.getHour(gmtJulDay, location) // 取得目前在哪個時辰中
      if (currentEb.index in 6..11 &&  //如果現在時辰在晚子時之前 : 午6 ~ 亥11
        (eb.index >= 6 && eb.index > currentEb.index || eb == 子) //而且現在所處的時辰，未超過欲求的時辰
      ) {
        resultGmt = if (eb == 未 || eb == 申 || eb == 酉 || eb == 戌 || eb == 亥) {
          previousMeridian + oneUnit2 * ((eb.index - 7) * 2 + 1)
        } else if (eb == 丑 || eb == 寅 || eb == 卯 || eb == 辰 || eb == 巳 || eb == 午) {
          nextNadirGmt + oneUnit1 * ((eb.index - 1) * 2 + 1)
        } else {
          previousMeridian + oneUnit2 * 11 // 晚子時之始
        }
      } else {
        // 欲求的時辰，早於現在所處的時辰
        val oneUnit3 = (nextMeridianGmt - nextNadirGmt) / 12.0
        val nextNextNadir = riseTransImpl.getGmtTransJulDay(nextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit4 = (nextNextNadir - nextMeridianGmt) / 12.0
        resultGmt = if (eb == 未 || eb == 申 || eb == 酉 || eb == 戌 || eb == 亥) {
          nextMeridianGmt + oneUnit4 * ((eb.index - 7) * 2 + 1)
        } else if (eb == 子) {
          nextMeridianGmt + oneUnit4 * 11
        } else {
          //丑寅卯辰巳午
          nextNadirGmt + oneUnit3 * ((eb.index - 1) * 2 + 1)
        }
      }
    }
    logger.debug("resultGmt = {}", resultGmt)
    return resultGmt
  }


  override fun getTitle(locale: Locale): String {
    return "真太陽時"
  }

  override fun getDescription(locale: Locale): String {
    return "利用太陽過天底 到天頂之間，劃分十二等份，再從太陽過天頂到天底，平均劃分十二等份，依此來切割 12 時辰"
  }

  companion object {

    private val revJulDayFunc =  {it:Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
