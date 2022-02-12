/**
 * Created by smallufo on 2021-08-21.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.chinese.Branch
import destiny.core.chinese.lunarStation.ILunarStationFeature.Companion.getOppoHouse
import destiny.core.chinese.lunarStation.ILunarStationFeature.Companion.getSelfHouse
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named


@Serializable
data class LunarStationConfig(val yearlyConfig: YearlyConfig = YearlyConfig(),
                              val monthlyConfig: MonthlyConfig = MonthlyConfig(),
                              val hourlyConfig: HourlyConfig = HourlyConfig(),
                              val ewConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable

@DestinyMarker
class LunarStationConfigBuilder : Builder<LunarStationConfig> {

  var yearlyConfig: YearlyConfig = YearlyConfig()
  fun yearly(block: YearlyConfigBuilder.() -> Unit = {}) {
    this.yearlyConfig = YearlyConfigBuilder.yearly(block)
  }

  var monthlyConfig: MonthlyConfig = MonthlyConfig()
  fun monthly(block: MonthlyConfigBuilder.() -> Unit = {}) {
    this.monthlyConfig = MonthlyConfigBuilder.monthly(block)
  }

  var hourlyConfig: HourlyConfig = HourlyConfig()
  fun hourly(block: HourlyConfigBuilder.() -> Unit = {}) {
    this.hourlyConfig = HourlyConfigBuilder.hourly(block)
  }

  var ewConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}) {
    ewConfig = EightWordsConfigBuilder().apply(block).build()
  }

  override fun build(): LunarStationConfig {
    return LunarStationConfig(yearlyConfig, monthlyConfig, hourlyConfig, ewConfig)
  }

  companion object {

    fun lunarStation(block: LunarStationConfigBuilder.() -> Unit = {}): LunarStationConfig {
      return LunarStationConfigBuilder().apply(block).build()
    }
  }

}


/**
 * 禽星排盤 interface
 */
interface ILunarStationFeature : Feature<LunarStationConfig, ContextModel>{

  fun getScaleMap(
    lmt: ChronoLocalDateTime<*>, loc: ILocation, config: LunarStationConfig,
    scales: List<Scale> = listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR)
  ): Map<Scale, LunarStation>

  companion object {
    /**
     * 禽星鎖泊：
     *
     * 山水田園井，刀天草岸風；
     * 火月周流轉，七曜長生宮。
     * 日午月未上，水土俱申中；
     * 木亥火寅地，金生與巳同。
     * 彼禽算外圈，我禽算內圈。
     */
    private fun startBranch(planet: Planet): Branch {
      return when (planet) {
        Planet.SUN                    -> Branch.午
        Planet.MOON                   -> Branch.未
        Planet.MARS                   -> Branch.寅
        Planet.MERCURY, Planet.SATURN -> Branch.申
        Planet.JUPITER                -> Branch.亥
        Planet.VENUS                  -> Branch.巳
        else                          -> throw IllegalArgumentException()
      }
    }

    /** 彼禽 */
    fun getOppoHouse(oppo: LunarStation, hourBranch: Branch): OppoHouse {
      return startBranch(oppo.planet).let { b ->
        val steps = hourBranch.getAheadOf(b)
        OppoHouse.山.next(steps)
      }
    }

    /** 彼禽 外圈 */
    val oppoHouseMap: Map<Branch, OppoHouse> by lazy {
      val startPair = Branch.子 to OppoHouse.湯火
      generateSequence(startPair) { (branch, oppoHouse) ->
        branch.next to oppoHouse.next
      }.take(12)
        .toMap()
    }

    /** 我禽 */
    fun getSelfHouse(self: LunarStation, hourBranch: Branch): SelfHouse {
      return startBranch(self.planet).let { b ->
        val steps = hourBranch.getAheadOf(b)
        SelfHouse.山.next(steps)
      }
    }

    /** 我禽 內圈 */
    val selfHouseMap: Map<Branch, SelfHouse> by lazy {
      val startPair = Branch.子 to SelfHouse.湖
      generateSequence(startPair) { (branch, selfHouse) ->
        branch.next to selfHouse.next
      }.take(12)
        .toMap()
    }
  }
}

/**
 * 禽星排盤
 */
@Named
class LunarStationFeature(val yearlyFeature: LunarStationYearlyFeature,
                          val monthlyFeature: ILunarStationMonthlyFeature,
                          val dailyFeature: LunarStationDailyFeature,
                          val hourlyFeature: LunarStationHourlyFeature,
                          private val eightWordsFeature : EightWordsFeature,
                          private val chineseDateFeature: ChineseDateFeature,
                          private val hiddenVenusFoeFeature: HiddenVenusFoeFeature,
                          private val julDayResolver: JulDayResolver) : ILunarStationFeature, AbstractCachedFeature<LunarStationConfig, ContextModel>() {
  override val key: String = "lunarStation"

  override val defaultConfig: LunarStationConfig = LunarStationConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: LunarStationConfig): ContextModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: LunarStationConfig): ContextModel {

    val ew = eightWordsFeature.getModel(lmt, loc, config.ewConfig)
    val models = getScaleMap(lmt, loc, config, listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR))

    val dayIndex = dailyFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig)
    val hourStation = hourlyFeature.getModel(lmt, loc, config.hourlyConfig)

    val oppo = LunarStationHourlyFeature.getOpponent(dayIndex, hourStation)
    val self = LunarStationHourlyFeature.getSelf1(hourStation, ew.hour.branch)

    val oppoHouse = getOppoHouse(oppo, ew.hour.branch)
    val selfHouse = getSelfHouse(self, ew.hour.branch)

    val reversed = LunarStationHourlyFeature.getReversed(dayIndex, hourStation)

    val hiddenVenusFoe: Set<Pair<Scale, Scale>> = hiddenVenusFoeFeature.getModel(lmt, loc, config)

    return ContextModel(
      ew,
      models[Scale.YEAR]!!,
      models[Scale.MONTH]!!,
      dayIndex.station(),
      hourStation,
      dayIndex,
      oppo,
      oppoHouse,
      self,
      selfHouse,
      reversed,
      hiddenVenusFoe
    )
  }

  override fun getScaleMap(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: LunarStationConfig, scales: List<Scale>): Map<Scale, LunarStation> {
    return scales.associate { scale ->
      when (scale) {
        Scale.YEAR  -> Scale.YEAR to yearlyFeature.getModel(lmt, loc, config.yearlyConfig).station
        Scale.MONTH -> {
          val yearlyStation: LunarStation = yearlyFeature.getModel(lmt, loc, config.yearlyConfig).station
          val monthBranch = eightWordsFeature.getModel(lmt, loc, config.ewConfig).month.branch
          val chineseDate = chineseDateFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, config.monthlyConfig.monthAlgo
          )
          Scale.MONTH to monthlyFeature.getMonthly(yearlyStation, monthNumber, config.monthlyConfig.impl)
        }
        Scale.DAY   -> Scale.DAY to dailyFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig).station()
        Scale.HOUR  -> Scale.HOUR to hourlyFeature.getModel(lmt, loc, config.hourlyConfig)
      }
    }
  }

}
