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
import mu.KotlinLogging
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


  private var atmosphericPressure = 1013.25
  private var atmosphericTemperature = 0.0
  private var discCenter = true
  private var refraction = true

  fun setDiscCenter(isDiscCenter: Boolean) {
    this.discCenter = isDiscCenter
  }

  fun setHasRefraction(hasRefraction: Boolean) {
    this.refraction = hasRefraction
  }

  override fun getHour(gmtJulDay: Double, location: ILocation): Branch {

    val nextMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)
    val nextNadir = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

    if (nextNadir > nextMeridian) {
      //子正到午正（上半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      val previousNadirGmt = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

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
      val previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

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

  // 午前
  private val 丑to午 = listOf(丑, 寅, 卯, 辰, 巳, 午)
  // 午後 (不含子)
  private val 未to亥 = listOf(未, 申, 酉, 戌, 亥)

  override fun getGmtNextStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {
    val resultGmt: Double
    // 下個午正
    val nextMeridianGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)
    // 下個子正
    val nextNadirGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

    val currentEb: Branch = getHour(gmtJulDay, location) // 取得目前在哪個時辰之中

    if (nextNadirGmt > nextMeridianGmt) {
      // 目前時刻 位於子正到午正（上半天）
      val twelveHoursAgo = gmtJulDay - 0.5
      // 上一個子正
      val previousNadir = riseTransImpl.getGmtTransJulDay(twelveHoursAgo, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)


      val oneUnit1 = (nextMeridianGmt - previousNadir) / 12.0 // 單位為 day , 左半部
      val oneUnit2 = (nextNadirGmt - nextMeridianGmt) / 12.0  // 右半部


      if (eb.index > currentEb.index || eb == 子) {
        //代表現在所處的時辰，未超過欲求的時辰
        resultGmt = when {
          丑to午.contains(eb) -> previousNadir + oneUnit1 * ((eb.index - 1) * 2 + 1)
          未to亥.contains(eb) -> nextMeridianGmt + oneUnit2 * ((eb.index - 7) * 2 + 1)
          else -> nextMeridianGmt + oneUnit2 * 11 // eb == 子時
        }
      } else {
        // 欲求的時辰，早於現在所處的時辰 ==> 代表算的是明天的時辰 : ex 目前是寅時，要計算「下一個丑時」 ==> 算的是明天的丑時
        val nextNextMeridianGmt = riseTransImpl.getGmtTransJulDay(nextNadirGmt, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit3 = (nextNextMeridianGmt - nextNadirGmt) / 12.0
        val nextNextNadir = riseTransImpl.getGmtTransJulDay(nextNextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit4 = (nextNextNadir - nextNextMeridianGmt) / 12.0
        resultGmt = when {
          丑to午.contains(eb) -> nextNadirGmt + oneUnit3 * ((eb.index - 1) * 2 + 1)
          未to亥.contains(eb) -> nextNextMeridianGmt + oneUnit4 * ((eb.index - 7) * 2 + 1)
          else -> throw RuntimeException("Runtime Exception : 沒有子時的情況") //沒有子時的情況
        }
      }

    } else {
      // 目前時刻 位於 午正到子正（下半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      // 上一個午正
      val previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

      val oneUnit1 = (nextMeridianGmt - nextNadirGmt) / 12.0 //從 下一個子正 到 下一個午正，總共幾天
      val oneUnit2 = (nextNadirGmt - previousMeridian) / 12.0 //從 下一個子正 到 上一個午正，總共幾秒

      if (currentEb.index in 6..11 &&  //如果現在時辰在晚子時之前 : 午6 ~ 亥11
        (eb.index >= 6 && eb.index > currentEb.index || eb == 子) //而且現在所處的時辰，未超過欲求的時辰
      ) {
        resultGmt = when {
          未to亥.contains(eb) -> previousMeridian + oneUnit2 * ((eb.index - 7) * 2 + 1)
          丑to午.contains(eb) -> nextNadirGmt + oneUnit1 * ((eb.index - 1) * 2 + 1)
          else -> previousMeridian + oneUnit2 * 11 // 晚子時之始
        }
      } else {
        // 欲求的時辰，早於現在所處的時辰
        val oneUnit3 = (nextMeridianGmt - nextNadirGmt) / 12.0
        val nextNextNadir = riseTransImpl.getGmtTransJulDay(nextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)
        val oneUnit4 = (nextNextNadir - nextMeridianGmt) / 12.0
        resultGmt = when {
          未to亥.contains(eb) -> nextMeridianGmt + oneUnit4 * ((eb.index - 7) * 2 + 1)
          丑to午.contains(eb) -> nextNadirGmt + oneUnit3 * ((eb.index - 1) * 2 + 1)
          else -> nextMeridianGmt + oneUnit4 * 11 // 子
        }
      }
    }
    logger.debug("resultGmt = {}", resultGmt)
    return resultGmt
  }


  /**
   * 取得「前一個」此地支的開始時刻
   */
  override fun getGmtPrevStartOf(gmtJulDay: Double, location: ILocation, eb: Branch): Double {
    // 下個午正
    val nextMeridianGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)
    // 下個子正
    val nextNadirGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

    // 上一個子正
    val previousNadir = riseTransImpl.getGmtTransJulDay(gmtJulDay, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

    // 上一個午正 : 用「上一個子正」減去 0.75 (約早上六點) , 使其必定能夠算出「上一個午正」
    val prevMeridianGmt = riseTransImpl.getGmtTransJulDay(previousNadir - 0.75, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

    val currentEb: Branch = getHour(gmtJulDay, location) // 取得目前在哪個時辰之中

    if (nextNadirGmt > nextMeridianGmt) {
      // 目前時刻 位於子正到午正（上半天）

      // 上、上一個子正： 用「上一個午正」減去 0.75 (約晚上六點) , 使其必定能算出「上上一個子正」
      val prevPrevNadir = riseTransImpl.getGmtTransJulDay(prevMeridianGmt - 0.75, Planet.SUN, TransPoint.NADIR, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

      return if (eb.index > currentEb.index || eb == 子) {
        // 目前時辰，小於欲求的時辰 ==> 算的是昨天的時辰
        // ex : 目前是丑時，要計算「上一個寅時」 , 丑 < 寅
        // ex : 目前是丑時，要計算「上一個酉時」 , 丑 < 酉
        // ex : 目前是丑時，要計算「上一個子時」
        val oneUnit1 = (prevMeridianGmt - prevPrevNadir) / 12.0 // 左半部
        val oneUnit2 = (previousNadir - prevMeridianGmt) / 12.0 // 右半部
        when {
          丑to午.contains(eb) -> prevPrevNadir + oneUnit1 * ((eb.index - 1) * 2 + 1)    // ex : 目前丑時，要算「上一個寅時」
          未to亥.contains(eb) -> prevMeridianGmt + oneUnit2 * ((eb.index - 7) * 2 + 1)  // ex : 目前巳時，要算「上一個未時」
          else -> prevMeridianGmt + oneUnit2 * 11 // eb = 子時
        }
      } else {
        // 欲求的時辰，早於(小於)現在所處的時辰 ==> 算的是今天的時辰
        // ex : 目前是寅時，要計算「上一個丑時」 , 寅 >= 丑
        // ex : 目前是巳時，要計算「上一個巳時」 , 巳 >= 巳
        // ex : 目前是子時，要計算「上一個子時」 , 子 >= 子 ==> 其實就是計算子初
        val oneUnit3 = (nextMeridianGmt - prevMeridianGmt) / 12.0
        val oneUnit4 = (previousNadir - prevMeridianGmt) / 12.0
        when {
          丑to午.contains(eb) -> previousNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)  // ex : 目前寅時 , 計算「上一個丑時」
          eb == 子 -> prevMeridianGmt + oneUnit4 * 11  // ex : 目前寅時 , 計算「上一個子時」
          else -> throw RuntimeException("error")
        }
      }
    } else {
      // 目前時刻 位於 午正到子正（下半天）

      return if (eb.index > currentEb.index || eb == 子) {
        // 欲求的時辰 大於目前時辰 ,
        // ex : 目前是 未時 , 要計算「上一個申時」 , 申 > 未
        // ex : 目前是 未時 , 要計算「上一個子時」

        // 上、上一個午正 : 用「上一個子正」減去 0.75 (約上午六點), 必定能算出「上上一個午正」
        val prevPrevMeridian = riseTransImpl.getGmtTransJulDay(previousNadir - 0.75, Planet.SUN, TransPoint.MERIDIAN, location, discCenter, refraction, atmosphericTemperature, atmosphericPressure)

        val oneUnit = (previousNadir - prevPrevMeridian) / 12.0
        if (eb == 子) {
          prevPrevMeridian + oneUnit * 11
        } else {
          prevPrevMeridian + oneUnit * ((eb.index - 7) * 2 + 1) // 必定是 未to亥
        }
      } else {
        // 欲求的時辰 小於或等於目前的時辰 ,
        // ex : 目前是 酉時 , 要計算「上一個申時」 , 申 <= 酉 ,
        // ex : 目前是 酉時 , 要計算「上一個丑時」 , 丑 <= 酉
        // ex : 目前是 子時 , 要計算「上一個子時」 , 子 <= 子 ==> 其實就是計算該「子初」
        val oneUnit3 = (prevMeridianGmt - previousNadir) / 12.0
        val oneUnit4 = (nextNadirGmt - prevMeridianGmt) / 12.0
        when {
          丑to午.contains(eb) -> previousNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)
          未to亥.contains(eb) -> prevMeridianGmt + oneUnit4 * ((eb.index - 7) * 2 + 1)
          else -> prevMeridianGmt + oneUnit4 * 11 // 子時
        }
      }
    }
  }


  override fun getTitle(locale: Locale): String {
    return "真太陽時"
  }

  override fun getDescription(locale: Locale): String {
    return "利用太陽過天底 到天頂之間，劃分十二等份，再從太陽過天頂到天底，平均劃分十二等份，依此來切割 12 時辰"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as HourSolarTransImpl

    if (atmosphericPressure != other.atmosphericPressure) return false
    if (atmosphericTemperature != other.atmosphericTemperature) return false
    if (discCenter != other.discCenter) return false
    if (refraction != other.refraction) return false

    return true
  }

  override fun hashCode(): Int {
    var result = atmosphericPressure.hashCode()
    result = 31 * result + atmosphericTemperature.hashCode()
    result = 31 * result + discCenter.hashCode()
    result = 31 * result + refraction.hashCode()
    return result
  }


  companion object {

    val logger = KotlinLogging.logger {}
    private val revJulDayFunc = { it: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }

}
