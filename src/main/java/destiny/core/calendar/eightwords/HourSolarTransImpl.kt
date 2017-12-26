/*
 * @author smallufo
 * @date 2004/12/10
 * @time 下午 03:53:08
 */
package destiny.core.calendar.eightwords

import destiny.astrology.IRiseTrans
import destiny.astrology.Planet
import destiny.astrology.TransPoint
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import java.util.function.Function

/**
 * <PRE>
 * 時辰的劃分實作
 * 利用太陽過天底 到天頂之間，劃分十二等份
 * 再從太陽過天頂到天底，平均劃分十二等份
 * 依此來切割 12 時辰
</PRE> *
 */
class HourSolarTransImpl(private val riseTransImpl: IRiseTrans) : HourIF, Serializable {
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

  override fun getHour(gmtJulDay: Double, location: Location): Branch {

    val nextMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
    val nextNadir = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

    if (nextNadir > nextMeridian) {
      //子正到午正（上半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousNadirGmt = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

      logger.debug("gmtJulDay = {} , 上一個子正(GMT) = {}", gmtJulDay, revJulDayFunc.apply(previousNadirGmt))

      val diffDays = nextMeridian - previousNadirGmt // 從子正到午正，總共幾秒
      val oneUnitDays = diffDays / 12.0
      logger.debug("diffDays = {} , oneUnitDays = {}", diffDays, oneUnitDays)
      return if (gmtJulDay < previousNadirGmt + oneUnitDays)
        Branch.子
      else if (gmtJulDay < previousNadirGmt + oneUnitDays * 3)
        Branch.丑
      else if (gmtJulDay < previousNadirGmt + oneUnitDays * 5)
        Branch.寅
      else if (gmtJulDay < previousNadirGmt + oneUnitDays * 7)
        Branch.卯
      else if (gmtJulDay < previousNadirGmt + oneUnitDays * 9)
        Branch.辰
      else if (gmtJulDay < previousNadirGmt + oneUnitDays * 11)
        Branch.巳
      else
        Branch.午
    } else {
      //午正到子正（下半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)

      val diffDays = nextNadir - previousMeridian
      val oneUnitDays = diffDays / 12.0

      return if (gmtJulDay < previousMeridian + oneUnitDays)
        Branch.午
      else if (gmtJulDay < previousMeridian + oneUnitDays * 3)
        Branch.未
      else if (gmtJulDay < previousMeridian + oneUnitDays * 5)
        Branch.申
      else if (gmtJulDay < previousMeridian + oneUnitDays * 7)
        Branch.酉
      else if (gmtJulDay < previousMeridian + oneUnitDays * 9)
        Branch.戌
      else if (gmtJulDay < previousMeridian + oneUnitDays * 11)
        Branch.亥
      else
        Branch.子
    }
  }

  override fun getGmtNextStartOf(gmtJulDay: Double, location: Location, targetEb: Branch): Double {
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
      if (targetEb.index > currentEb.index || targetEb == Branch.子) {
        //代表現在所處的時辰，未超過欲求的時辰
        if (targetEb == Branch.丑 || targetEb == Branch.寅 || targetEb == Branch.卯 || targetEb == Branch.辰 || targetEb == Branch.巳 || targetEb == Branch.午) {
          resultGmt = previousNadir + oneUnit1 * ((targetEb.index - 1) * 2 + 1)
        } else if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥) {
          resultGmt = nextMeridianGmt + oneUnit2 * ((targetEb.index - 7) * 2 + 1)
        } else {
          resultGmt = nextMeridianGmt + oneUnit2 * 11 // eb == 子時
        }
      } else {
        //欲求的時辰，早於現在所處的時辰 ==> 代表算的是明天的時辰
        val nextNextMeridianGmt = riseTransImpl.getGmtTransJulDay(nextNadirGmt, Planet.SUN, TransPoint.MERIDIAN, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit3 = (nextNextMeridianGmt - nextNadirGmt) / 12.0
        val nextNextNadir = riseTransImpl.getGmtTransJulDay(nextNextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit4 = (nextNextNadir - nextNextMeridianGmt) / 12.0
        if (targetEb == Branch.丑 || targetEb == Branch.寅 || targetEb == Branch.卯 || targetEb == Branch.辰 || targetEb == Branch.巳 || targetEb == Branch.午) {
          resultGmt = nextNadirGmt + oneUnit3 * ((targetEb.index - 1) * 2 + 1)
        } else if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥) {
          resultGmt = nextNextMeridianGmt + oneUnit4 * ((targetEb.index - 7) * 2 + 1)
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
      if (currentEb.index >= 6 && currentEb.index <= 11 &&  //如果現在時辰在晚子時之前 : 午6 ~ 亥11
        (targetEb.index >= 6 && targetEb.index > currentEb.index || targetEb == Branch.子) //而且現在所處的時辰，未超過欲求的時辰
      ) {
        if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥) {
          resultGmt = previousMeridian + oneUnit2 * ((targetEb.index - 7) * 2 + 1)
        } else if (targetEb == Branch.丑 || targetEb == Branch.寅 || targetEb == Branch.卯 || targetEb == Branch.辰 || targetEb == Branch.巳 || targetEb == Branch.午) {
          resultGmt = nextNadirGmt + oneUnit1 * ((targetEb.index - 1) * 2 + 1)
        } else {
          resultGmt = previousMeridian + oneUnit2 * 11 // 晚子時之始
        }
      } else {
        // 欲求的時辰，早於現在所處的時辰
        val oneUnit3 = (nextMeridianGmt - nextNadirGmt) / 12.0
        val nextNextNadir = riseTransImpl.getGmtTransJulDay(nextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit4 = (nextNextNadir - nextMeridianGmt) / 12.0
        if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥) {
          resultGmt = nextMeridianGmt + oneUnit4 * ((targetEb.index - 7) * 2 + 1)
        } else if (targetEb == Branch.子) {
          resultGmt = nextMeridianGmt + oneUnit4 * 11
        } else {
          //丑寅卯辰巳午
          resultGmt = nextNadirGmt + oneUnit3 * ((targetEb.index - 1) * 2 + 1)
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

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
