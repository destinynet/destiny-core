package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoUnit

class HourBoundaryTST(private val riseTransFeature: RiseTransFeature,
                      private val julDayResolver: JulDayResolver) : IHourBoundary {

  // 午前
  private val 丑to午 = listOf(丑, 寅, 卯, 辰, 巳, 午)

  // 午後 (不含子)
  private val 未to亥 = listOf(未, 申, 酉, 戌, 亥)

  override val hourBranchImpl: HourBranchConfig.HourImpl = HourBranchConfig.HourImpl.TST

  override fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val resultGmt: GmtJulDay
    // 下個午正
    val nextMeridian = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!
    // 下個子正
    val nextNadir = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

    val currentEb = Tst.getHourBranch(lmt, loc, riseTransFeature, transConfig)  // 取得目前在哪個時辰之中
    //val currentEb: Branch = Tst.getHourBranch(gmtJulDay, loc, riseTransFeature, transConfig)

    if (nextNadir > nextMeridian) {
      // 目前時刻 位於子正到午正（上半天）
      val twelveHoursAgo = gmtJulDay - 0.5
      // 上一個子正
      val previousNadir = riseTransFeature.getModel(twelveHoursAgo, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

      val oneUnit1 = (nextMeridian - previousNadir) / 12.0 // 單位為 day , 左半部
      val oneUnit2 = (nextNadir - nextMeridian) / 12.0  // 右半部

      if (eb.index > currentEb.index || eb == 子) {
        //代表現在所處的時辰，未超過欲求的時辰
        resultGmt = when {
          丑to午.contains(eb) -> previousNadir + oneUnit1 * ((eb.index - 1) * 2 + 1)
          未to亥.contains(eb) -> nextMeridian + oneUnit2 * ((eb.index - 7) * 2 + 1)
          else -> nextMeridian + oneUnit2 * 11 // eb == 子時
        }
      } else {
        // 欲求的時辰，早於現在所處的時辰 ==> 代表算的是明天的時辰 : ex 目前是寅時，要計算「下一個丑時」 ==> 算的是明天的丑時
        val nextNextMeridian = riseTransFeature.getModel(nextNadir, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!
        val oneUnit3 = (nextNextMeridian - nextNadir) / 12.0
        val nextNextNadir = riseTransFeature.getModel(nextNextMeridian, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!
        val oneUnit4 = (nextNextNadir - nextNextMeridian) / 12.0

        resultGmt = when {
          丑to午.contains(eb) -> nextNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)
          未to亥.contains(eb) -> nextNextMeridian + oneUnit4 * ((eb.index - 7) * 2 + 1)
          else -> throw IllegalStateException("沒有子時的情況") //沒有子時的情況
        }
      }

    } else {
      // 目前時刻 位於 午正到子正（下半天）
      val thirteenHoursAgo = gmtJulDay - 13 / 24.0
      // 上一個午正
      val previousMeridian = riseTransFeature.getModel(thirteenHoursAgo, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!

      val oneUnit1 = (nextMeridian - nextNadir) / 12.0 //從 下一個子正 到 下一個午正，總共幾天
      val oneUnit2 = (nextNadir - previousMeridian) / 12.0 //從 下一個子正 到 上一個午正，總共幾秒

      if (currentEb.index in 6..11 &&  //如果現在時辰在晚子時之前 : 午6 ~ 亥11
        (eb.index >= 6 && eb.index > currentEb.index || eb == 子) //而且現在所處的時辰，未超過欲求的時辰
      ) {
        resultGmt = when {
          未to亥.contains(eb) -> previousMeridian + oneUnit2 * ((eb.index - 7) * 2 + 1)
          丑to午.contains(eb) -> nextNadir + oneUnit1 * ((eb.index - 1) * 2 + 1)
          else -> previousMeridian + oneUnit2 * 11 // 晚子時之始
        }
      } else {
        // 欲求的時辰，早於現在所處的時辰
        val oneUnit3 = (nextMeridian - nextNadir) / 12.0
        val nextNextNadir = riseTransFeature.getModel(nextMeridian, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!
        val oneUnit4 = (nextNextNadir - nextMeridian) / 12.0
        resultGmt = when {
          未to亥.contains(eb) -> nextMeridian + oneUnit4 * ((eb.index - 7) * 2 + 1)
          丑to午.contains(eb) -> nextNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)
          else -> nextMeridian + oneUnit4 * 11 // 子
        }
      }
    }
    logger.debug("resultGmt = {}", resultGmt)
    return resultGmt
  }

  override fun getGmtPrevStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    // 下個午正
    val nextMeridian = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!
    // 下個子正
    val nextNadir = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

    val currentEb: Branch = Tst.getHourBranch(lmt, loc, riseTransFeature, transConfig) // 取得目前在哪個時辰之中

    logger.debug("目前是 {}時 , 要計算「上一個{}時」", currentEb, eb)

    if (nextNadir > nextMeridian) {
      // 目前時刻 位於子正到午正（上半天）

      // 上一個子正
      val previousNadir = riseTransFeature.getModel(nextMeridian - 0.75, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

      //上一個午正 : 用「上一個子正」減去 0.75 (約早上六點) , 使其必定能夠算出「上一個午正」
      val prevMeridian = riseTransFeature.getModel(previousNadir - 0.75, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!

      // 上、上一個子正： 用「上一個午正」減去 0.75 (約晚上六點) , 使其必定能算出「上上一個子正」

      val prevPrevNadir = riseTransFeature.getModel(prevMeridian - 0.75, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

      return if (eb.index > currentEb.index || eb == 子) {
        // 目前時辰，小於欲求的時辰 ==> 算的是昨天的時辰
        // ex : 目前是丑時，要計算「上一個寅時」 , 丑 < 寅
        // ex : 目前是丑時，要計算「上一個酉時」 , 丑 < 酉
        // ex : 目前是丑時，要計算「上一個子時」
        val oneUnit1 = (prevMeridian - prevPrevNadir) / 12.0 // 左半部
        val oneUnit2 = (previousNadir - prevMeridian) / 12.0 // 右半部
        when {
          丑to午.contains(eb) -> prevPrevNadir + oneUnit1 * ((eb.index - 1) * 2 + 1)    // ex : 目前丑時，要算「上一個寅時」
          未to亥.contains(eb) -> prevMeridian + oneUnit2 * ((eb.index - 7) * 2 + 1)  // ex : 目前巳時，要算「上一個未時」
          else -> prevMeridian + oneUnit2 * 11 // eb = 子時
        }
      } else {
        // 欲求的時辰，早於(小於)或等於現在所處的時辰 ==> 算的是今天的時辰
        // ex : 目前是寅時，要計算「上一個丑時」 , 寅 >= 丑
        // ex : 目前是巳時，要計算「上一個巳時」 , 巳 >= 巳
        // ex : 目前是子時，要計算「上一個子時」 , 子 >= 子 ==> 其實就是計算子初
        val oneUnit3 = (nextMeridian - previousNadir) / 12.0
        val oneUnit4 = (previousNadir - prevMeridian) / 12.0
        when {
          丑to午.contains(eb) -> previousNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)  // ex : 目前寅時 , 計算「上一個丑時」
          eb == 子    -> prevMeridian + oneUnit4 * 11  // ex : 目前寅時 , 計算「上一個子時」
          else              -> throw RuntimeException("error")
        }
      }
    } else {
      // 目前時刻 位於 午正到子正（下半天）

      // 上一個午正
      val prevMeridian = riseTransFeature.getModel(nextNadir - 0.75, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!

      // 上一個子正
      val previousNadir = riseTransFeature.getModel(prevMeridian - 0.75, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

      // 上、上一個午正 : 用「上一個子正」減去 0.75 (約上午六點), 必定能算出「上上一個午正」
      val prevPrevMeridian = riseTransFeature.getModel(previousNadir - 0.75, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!

      val oneUnit3 = (prevMeridian - previousNadir) / 12.0

      return if (currentEb.index >= eb.index || currentEb == 子) {
        val oneUnit4 = (nextNadir - prevMeridian) / 12.0
        when {
          未to亥.contains(eb) -> prevMeridian + oneUnit4 * ((eb.index - 7) * 2 + 1)
          丑to午.contains(eb) -> previousNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)
          else -> {
            if (currentEb == 子)
              prevMeridian + oneUnit4 * 11
            else
              prevPrevMeridian + (previousNadir - prevPrevMeridian) / 12.0 * 11
          }
        }
      } else {
        val oneUnit4 = (previousNadir - prevPrevMeridian) / 12.0
        when {
          未to亥.contains(eb) -> prevPrevMeridian + oneUnit4 * ((eb.index - 7) * 2 + 1)
          丑to午.contains(eb) -> previousNadir + oneUnit3 * ((eb.index - 1) * 2 + 1)
          else -> {
            prevPrevMeridian + oneUnit4 * 11
          }
        }
      }
    }
  }

  override fun getLmtNextMiddleOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, next: Boolean, hourBranchConfig: HourBranchConfig): ChronoLocalDateTime<*> {

    val currentBranch: Branch = Tst.getHourBranch(lmt, loc, riseTransFeature, hourBranchConfig.transConfig)

    val (targetDate , targetBranch) = lmt.toLocalDate().let { localDate ->
      if (currentBranch == 子 && !next)
        localDate.minus(1, ChronoUnit.DAYS) to 亥
      else if (currentBranch == 亥 && next)
        localDate.plus(1, ChronoUnit.DAYS) to 子
      else
        localDate to ( if (next) currentBranch.next else currentBranch.prev )
    }

    return getDailyBranchMiddleMap(targetDate , loc , julDayResolver, hourBranchConfig)[targetBranch]!!
  }
}
