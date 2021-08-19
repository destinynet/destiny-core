/**
 * Created by smallufo on 2021-08-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit


@Serializable
data class DayConfig(val changeDayAfterZi: Boolean = true ,
                     val midnight : MidnightImpl = MidnightImpl.NADIR) {
  enum class MidnightImpl {
    CLOCK0, // 當地時間手錶零時
    NADIR   // 太陽劃過天底(子午線)
  }
}

@DestinyMarker
class DayConfigBuilder : Builder<DayConfig> {

  var changeDayAfterZi: Boolean = true

  var midnight : DayConfig.MidnightImpl = DayConfig.MidnightImpl.NADIR

  override fun build() : DayConfig {
    return DayConfig(changeDayAfterZi, midnight)
  }

  companion object {
    fun dayConfig(block : DayConfigBuilder.() -> Unit = {}): DayConfig {
      return DayConfigBuilder().apply(block).build()
    }
  }
}



@Serializable
data class DayHourConfig(
  val dayConfig: DayConfig = DayConfig(),
  val hourBranchConfig: HourBranchConfig = HourBranchConfig()
)


@DestinyMarker
class DayHourConfigBuilder : Builder<DayHourConfig> {

  private var dayConfig = DayConfig()

  fun day(block: DayConfigBuilder.() -> Unit) {
    this.dayConfig = DayConfigBuilder.dayConfig(block)
  }

  var hourBranchConfig: HourBranchConfig = HourBranchConfig()
  fun hourBranch(block : HourBranchConfigBuilder.() -> Unit = {}) {
    hourBranchConfig = HourBranchConfigBuilder.hourBranchConfig(block)
  }

  override fun build(): DayHourConfig {
    return DayHourConfig(dayConfig, hourBranchConfig)
  }

  companion object {
    fun dayHour(block: DayHourConfigBuilder.() -> Unit = {}): DayHourConfig {
      return DayHourConfigBuilder().apply(block).build()
    }
  }
}


class DayHourFeature(val midnightFeature: MidnightFeature,
                     private val hourBranchFeature: HourBranchFeature,
                     private val riseTransFeature: RiseTransFeature,
                     private val riseTransImpl: IRiseTrans,
                     private val julDayResolver: JulDayResolver) : Feature<DayHourConfig, Pair<StemBranch, StemBranch>> {

  override val key: String = "dayHour"

  override val defaultConfig: DayHourConfig = DayHourConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DayHourConfig): Pair<StemBranch, StemBranch> {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DayHourConfig): Pair<StemBranch, StemBranch> {

    val hourImpl = getHourImpl(config.hourBranchConfig.hourImpl , riseTransImpl, julDayResolver)
    logger.trace { "[LMT] hourImpl = $hourImpl" }

    // 下個子初時刻
    val nextZiStart = hourImpl.getLmtNextStartOf(lmt, loc, 子, julDayResolver)

    // 下個子正時刻
    val nextMidnightLmt = TimeTools.getLmtFromGmt(midnightFeature.getModel(lmt, loc, config.dayConfig) , loc, julDayResolver)
      .let { dstSwitchCheck.invoke(it, nextZiStart) }

    val day: StemBranch = getDay(lmt, loc, hourImpl, nextZiStart, nextMidnightLmt, config.dayConfig.changeDayAfterZi, julDayResolver)

    val hourBranch = hourBranchFeature.getModel(lmt, loc, config.hourBranchConfig)

    val hourStem = getHourStem(hourImpl, lmt, loc, day, hourBranch, config.dayConfig.changeDayAfterZi, nextZiStart, nextMidnightLmt, julDayResolver)

    val hour = StemBranch[hourStem, hourBranch]
    return day to hour
  }

  /**
   * @param lmt 傳入當地手錶時間
   * @param loc 當地的經緯度等資料
   * @param eb 欲求之下一個地支開始時刻
   * @return 回傳 LMT 時刻
   */
  fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, hourBranchConfig: HourBranchConfig) : ChronoLocalDateTime<*> {
    val impl: INextBranchHourStart = when(hourBranchConfig.hourImpl) {
      HourBranchConfig.HourImpl.TST -> NextBranchHourStartTST()
      HourBranchConfig.HourImpl.LMT -> NextBranchHourStartLMT()
    }
    return impl.getLmtNextStartOf(lmt, loc, eb, hourBranchConfig.transConfig)
  }



  private sealed interface INextBranchHourStart {
    val hourBranchImpl: HourBranchConfig.HourImpl
    fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay
    fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, transConfig: TransConfig): ChronoLocalDateTime<*>
  }

  private inner class NextBranchHourStartTST : INextBranchHourStart {

    // 午前
    private val 丑to午 = listOf(丑, 寅, 卯, 辰, 巳, 午)

    // 午後 (不含子)
    private val 未to亥 = listOf(未, 申, 酉, 戌, 亥)

    override val hourBranchImpl: HourBranchConfig.HourImpl = HourBranchConfig.HourImpl.TST

    override fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay {

      val resultGmt: GmtJulDay
      // 下個午正
      val nextMeridian = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.MERIDIAN, transConfig))!!
      // 下個子正
      val nextNadir = riseTransFeature.getModel(gmtJulDay, loc, RiseTransConfig(Planet.SUN, TransPoint.NADIR, transConfig))!!

      val currentEb: Branch = Tst.getHourBranch(gmtJulDay, loc, riseTransImpl, transConfig) // 取得目前在哪個時辰之中

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

    override fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, transConfig: TransConfig): ChronoLocalDateTime<*> {
      val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)

      val resultGmtJulDay = getGmtNextStartOf(gmtJulDay, loc, eb, transConfig)

      val resultGmt = julDayResolver.getLocalDateTime(resultGmtJulDay)
      return TimeTools.getLmtFromGmt(resultGmt, loc)
    }
  }

  private inner class NextBranchHourStartLMT : INextBranchHourStart {
    override val hourBranchImpl: HourBranchConfig.HourImpl = HourBranchConfig.HourImpl.LMT

    override fun getGmtNextStartOf(gmtJulDay: GmtJulDay, loc: ILocation, eb: Branch, transConfig: TransConfig): GmtJulDay {
      val gmt = julDayResolver.getLocalDateTime(gmtJulDay)
      val lmt = TimeTools.getLmtFromGmt(gmt, loc)
      val lmtResult = getLmtNextStartOf(lmt, loc, eb, transConfig)
      val gmtResult = TimeTools.getGmtFromLmt(lmtResult, loc)
      return TimeTools.getGmtJulDay(gmtResult)
    }

    override fun getLmtNextStartOf(lmt: ChronoLocalDateTime<*>, loc: ILocation, eb: Branch, transConfig: TransConfig): ChronoLocalDateTime<*> {
      val lmtAtHourStart = lmt.with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0).with(ChronoField.NANO_OF_SECOND, 0)

      return when (eb) {
        //欲求下一個子時時刻
        子 -> if (lmt.get(ChronoField.HOUR_OF_DAY) >= 23)
          lmtAtHourStart.plus(1, ChronoUnit.DAYS).with(ChronoField.HOUR_OF_DAY, 23)
        else
          lmtAtHourStart.with(ChronoField.HOUR_OF_DAY, 23)

        else     -> {
          val hourStart = eb.index * 2 - 1
          if (lmt.get(ChronoField.HOUR_OF_DAY) < hourStart)
            lmtAtHourStart.with(ChronoField.HOUR_OF_DAY, hourStart.toLong())
          else
            lmtAtHourStart.plus(1, ChronoUnit.DAYS).with(ChronoField.HOUR_OF_DAY, hourStart.toLong())
        }
      }
    }
  }



  private val dstSwitchCheck = { nextMn : ChronoLocalDateTime<*> , nextZiStart : ChronoLocalDateTime<*> ->
    val dur = Duration.between(nextZiStart, nextMn).abs()
    if (dur.toMinutes() <= 1) {
      logger.warn("子初子正 幾乎重疊！ 可能是 DST 切換. 下個子初 = {} , 下個子正 = {} . 相隔秒 = {}", nextZiStart, nextMn, dur.seconds) // DST 結束前一天，可能會出錯
      nextMn.plus(1, ChronoUnit.HOURS)
    } else {
      nextMn
    }
  }

  private fun getHourStem(
    hourImpl: IHour,
    lmt: ChronoLocalDateTime<*>,
    loc: ILocation,
    day: StemBranch,
    hourBranch: Branch,
    cdaz: Boolean,
    nextZiStart: ChronoLocalDateTime<*>,
    nextMidnightLmt : ChronoLocalDateTime<*>,
    julDayResolver: JulDayResolver
  ): Stem {
    val nextZi: ChronoLocalDateTime<*> = hourImpl.getLmtNextStartOf(lmt, loc, 子, julDayResolver)

    val tempDayStem = day.stem.let {
      // 如果「子正」才換日
      if (!cdaz) {
        /**
         * <pre>
         * 而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
         * 第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
         * 第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
         *
         * 子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
         * |------------------|--------------------|--------------------|------------------|
        </pre> *
         */

        if (day !== getDay(nextZi, loc, hourImpl, nextZiStart, nextMidnightLmt, cdaz, julDayResolver))
          it.next
        else
          it
      } else
        it
    }
    // 時干
    return StemBranchUtils.getHourStem(tempDayStem, hourBranch)
  }
}
