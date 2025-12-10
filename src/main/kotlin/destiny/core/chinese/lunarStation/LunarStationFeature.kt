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
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWordsConfig
import destiny.core.chinese.Branch
import destiny.core.chinese.YearType
import destiny.core.chinese.lunarStation.ILunarStationFeature.Companion.getOppoHouse
import destiny.core.chinese.lunarStation.ILunarStationFeature.Companion.getSelfHouse
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

@Serializable
data class LunarStationConfig(
  override var yearType: YearType = YearType.YEAR_SOLAR,
  override var yearEpoch: YearEpoch = YearEpoch.EPOCH_1564,
  override var monthlyImpl: MonthlyImpl = MonthlyImpl.AoHead,
  override var monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS,
  override var hourlyImpl: HourlyImpl = HourlyImpl.Fixed,
  override val ewConfig: EightWordsConfig = EightWordsConfig(),
) : ILunarStationConfig, IEightWordsConfig by ewConfig

@DestinyMarker
class LunarStationConfigBuilder(val iEwConfig: IEightWordsConfig) : Builder<LunarStationConfig> {
  var yearType: YearType = YearType.YEAR_SOLAR
  var yearEpoch: YearEpoch = YearEpoch.EPOCH_1564
  var monthlyImpl: MonthlyImpl = MonthlyImpl.AoHead
  var monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS
  var hourlyImpl: HourlyImpl = HourlyImpl.Fixed

  override fun build(): LunarStationConfig {
    return LunarStationConfig(yearType, yearEpoch, monthlyImpl, monthAlgo, hourlyImpl, iEwConfig.ewConfig)
  }

  companion object {
    context(config: IEightWordsConfig)
    fun lunarStation(block: LunarStationConfigBuilder.() -> Unit = {}): LunarStationConfig {
      return LunarStationConfigBuilder(config).apply(block).build()
    }
  }
}


/**
 * 禽星排盤 interface
 */
interface ILunarStationFeature : Feature<ILunarStationConfig, ContextModel> {

  fun getScaleMap(
    lmt: ChronoLocalDateTime<*>, loc: ILocation, config: ILunarStationConfig,
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
class LunarStationFeature(
  private val yearlyFeature: LunarStationYearlyFeature,
  private val monthlyFeature: ILunarStationMonthlyFeature,
  private val dailyFeature: LunarStationDailyFeature,
  val hourlyFeature: LunarStationHourlyFeature,
  private val eightWordsFeature: EightWordsFeature,
  private val chineseDateFeature: ChineseDateFeature,
  private val hiddenVenusFoeFeature: HiddenVenusFoeFeature,
  private val julDayResolver: JulDayResolver
) : ILunarStationFeature, AbstractCachedFeature<ILunarStationConfig, ContextModel>() {
  override val key: String = "lunarStation"

  override val defaultConfig: ILunarStationConfig = LunarStationConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: ILunarStationConfig): ContextModel {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: ILunarStationConfig): ContextModel {

    val ewConfig = (config as LunarStationConfig).ewConfig


    val ew = eightWordsFeature.getModel(lmt, loc, ewConfig)
    val models = getScaleMap(lmt, loc, config, listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR))

    val dayIndex = dailyFeature.getModel(lmt, loc, config.dayHourConfig)
    val hourStation = hourlyFeature.getModel(lmt, loc, config)

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

  override fun getScaleMap(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: ILunarStationConfig, scales: List<Scale>): Map<Scale, LunarStation> {

    val ewConfig = (config as LunarStationConfig).ewConfig

    return scales.associate { scale ->
      when (scale) {
        Scale.YEAR  -> Scale.YEAR to yearlyFeature.getModel(lmt, loc, config).station
        Scale.MONTH -> {
          val yearlyStation: LunarStation = yearlyFeature.getModel(lmt, loc, config).station
          val monthBranch = eightWordsFeature.getModel(lmt, loc, ewConfig).month.branch
          val chineseDate = chineseDateFeature.getModel(lmt, loc, config)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, config.monthAlgo
          )
          Scale.MONTH to monthlyFeature.getMonthly(yearlyStation, monthNumber, config.monthlyImpl)
        }

        Scale.DAY   -> Scale.DAY to dailyFeature.getModel(lmt, loc, config.dayHourConfig).station()
        Scale.HOUR  -> Scale.HOUR to hourlyFeature.getModel(lmt, loc, config)
      }
    }
  }

}
