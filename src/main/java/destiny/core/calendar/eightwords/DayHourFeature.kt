/**
 * Created by smallufo on 2021-08-12.
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.子
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Named


@Serializable
data class DayConfig(val changeDayAfterZi: Boolean = true ,
                     val midnight: MidnightImpl = MidnightImpl.NADIR): java.io.Serializable {
  enum class MidnightImpl {
    CLOCK0, // 當地時間手錶零時
    NADIR   // 太陽劃過天底(子午線)
  }
}


fun DayConfig.MidnightImpl.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      DayConfig.MidnightImpl.CLOCK0 -> "以地方平均時（LMT）夜半零時來判定"
      DayConfig.MidnightImpl.NADIR  -> "太陽過天底"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when (this@asDescriptive) {
      DayConfig.MidnightImpl.CLOCK0 -> "晚上零時就是子正，不校正經度差以及真太陽時"
      DayConfig.MidnightImpl.NADIR  -> "以太陽過當地『天底』的時刻為『子正』"
    }
  }
}

fun DayConfig.MidnightImpl.toString(locale: Locale): String {
  return this.asDescriptive().toString(locale)
}

fun DayConfig.MidnightImpl.getDescription(locale: Locale): String {
  return this.asDescriptive().getDescription(locale)
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
): java.io.Serializable


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


interface IDayHourFeature : Feature<DayHourConfig, Pair<StemBranch, StemBranch>>

@Named
class DayHourFeature(private val midnightFeature: MidnightFeature,
                     private val hourBranchFeature: IHourBranchFeature,
                     private val julDayResolver: JulDayResolver) : AbstractCachedFeature<DayHourConfig, Pair<StemBranch, StemBranch>>() {

  override val key: String = "dayHour"

  override val defaultConfig: DayHourConfig = DayHourConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: DayHourConfig): Pair<StemBranch, StemBranch> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DayHourConfig): Pair<StemBranch, StemBranch> {

    // 下個子初時刻
    val nextZiStart = hourBranchFeature.getLmtNextStartOf(lmt, loc, 子, config.hourBranchConfig)

    // 下個子正時刻
    val nextMidnightLmt = TimeTools.getLmtFromGmt(midnightFeature.getModel(lmt, loc, config.dayConfig) , loc, julDayResolver)
      .let { dstSwitchCheck.invoke(it, nextZiStart) }

    val day: StemBranch = this.getDay(lmt, loc, nextZiStart, nextMidnightLmt, config)

    val hourBranch = hourBranchFeature.getModel(lmt, loc, config.hourBranchConfig)

    val hourStem = getHourStem(lmt, loc, day, hourBranch, nextZiStart, nextMidnightLmt, config)

    val hour = StemBranch[hourStem, hourBranch]
    return day to hour
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

  private fun getHourStem(lmt: ChronoLocalDateTime<*>, loc: ILocation, day: StemBranch, hourBranch: Branch, nextZiStart: ChronoLocalDateTime<*>, nextMidnightLmt: ChronoLocalDateTime<*>, config: DayHourConfig): Stem {

    val nextZi = hourBranchFeature.getLmtNextStartOf(lmt, loc, 子, config.hourBranchConfig)

    val tempDayStem = day.stem.let {
      // 如果「子正」才換日
      if (!config.dayConfig.changeDayAfterZi) {
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

        if (day !== this.getDay(nextZi, loc, nextZiStart, nextMidnightLmt, config))
          it.next
        else
          it
      } else
        it
    }
    // 時干
    return StemBranchUtils.getHourStem(tempDayStem, hourBranch)
  }


  private fun getDay(
    lmt: ChronoLocalDateTime<*>, loc: ILocation,
    // 下個子初時刻
    nextZiStart: ChronoLocalDateTime<*>,
    // 下個子正時刻
    nextMidnightLmt: ChronoLocalDateTime<*>,
    config: DayHourConfig
  ): StemBranch {

    val changeDayAfterZi = config.dayConfig.changeDayAfterZi

    // 這是很特別的作法，將 lmt 當作 GMT 取 JulDay
    val lmtJulDay = (TimeTools.getGmtJulDay(lmt).value + 0.5).toInt()
    var index = (lmtJulDay - 11) % 60

    if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >= 12) {
      //子正，在 LMT 零時之前
      index = this.getIndex(index, nextMidnightLmt, lmt, loc, nextZiStart, config)
    } else {
      //子正，在 LMT 零時之後（含）
      if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
        // lmt 落於當地 零時 到 子正的這段期間
        if (TimeTools.isBefore(nextZiStart, nextMidnightLmt)) {
          // lmt 落於零時到子初之間 (這代表當地地點「極西」) , 此時一定還沒換日
          index--
        } else {
          // lmt 落於子初到子正之間
          if (!changeDayAfterZi)
          //如果子正才換日
            index--
        }
      } else {
        // lmt 落於前一個子正之後，到當天24時為止 (範圍最大的一塊「餅」)
        if (changeDayAfterZi
          && lmt.get(ChronoField.DAY_OF_MONTH) != nextZiStart.get(ChronoField.DAY_OF_MONTH)
          && nextZiStart.get(ChronoField.HOUR_OF_DAY) >= 12
        )
        // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
          index++
      }
    }
    return StemBranch[index]
  }

  private fun getIndex(
    index: Int,
    nextMidnightLmt: ChronoLocalDateTime<*>,
    lmt: ChronoLocalDateTime<*>,
    loc: ILocation,
    nextZi: ChronoLocalDateTime<*>,
    config: DayHourConfig
  ): Int {

    var result = index
    //子正，在 LMT 零時之前
    if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
      // lmt 落於 當日零時之後，子正之前（餅最大的那一塊）

      val midnightNextZi = hourBranchFeature.getLmtNextStartOf(nextMidnightLmt, loc, 子, config.hourBranchConfig)

      if (config.dayConfig.changeDayAfterZi && nextZi.get(ChronoField.DAY_OF_MONTH) == midnightNextZi.get(ChronoField.DAY_OF_MONTH)) {
        result++
      }
    } else {
      // lmt 落於 子正之後，到 24 時之間 (其 nextMidnight 其實是明日的子正) , 則不論是否早子時換日，都一定換日
      result++
    }
    return result
  }
}
