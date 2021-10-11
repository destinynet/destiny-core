package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.Scale.*
import destiny.core.astrology.LunarStation
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/**
 * 二十八宿 禽星占卜 Context
 */
interface ILunarStationContext {

  val yearlyImpl: ILunarStationYearly
  val monthlyImpl: ILunarStationMonthly
  val dailyImpl: ILunarStationDaily
  val hourlyImpl: ILunarStationHourly

  val eightWordsImpl: IEightWordsFactory
  val monthAlgo: IFinalMonthNumber.MonthAlgo

  fun getScaleMap(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale> = listOf(YEAR, MONTH, DAY, HOUR)): Map<Scale, LunarStation>


  fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation): IContextModel

}


/**
 * 禽星占卜 Context
 * Deprecated for [LunarStationFeature]
 */
@Deprecated("LunarStationFeature")
class LunarStationContext(override val yearlyImpl: ILunarStationYearly,
                          override val monthlyImpl: ILunarStationMonthly,
                          override val dailyImpl: ILunarStationDaily,
                          override val hourlyImpl: ILunarStationHourly,

                          override val eightWordsImpl: IEightWordsStandardFactory,
                          val chineseDateImpl: IChineseDate,
                          override val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS) : ILunarStationContext,
                                                                                                                                 IHiddenVenusFoe,
                                                                                                                                 Serializable {


  /** 暗金伏斷 */
  override fun getHiddenVenusFoe(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Pair<Scale, Scale>> {
    val impl = HiddenVenusFoeAnimalStar(
      yearlyImpl, monthlyImpl, dailyImpl, hourlyImpl, eightWordsImpl.yearMonthImpl, chineseDateImpl, eightWordsImpl.dayHourImpl, monthAlgo
    )
    return impl.getHiddenVenusFoe(lmt, loc)
  }

  override fun getScaleMap(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale>): Map<Scale, LunarStation> {
    return scales.associate { scale ->
      when (scale) {
        YEAR  -> YEAR to yearlyImpl.getYearly(lmt, loc)
        MONTH -> {
          val yearlyStation: LunarStation = yearlyImpl.getYearly(lmt, loc)
          val monthBranch = eightWordsImpl.yearMonthImpl.getMonth(lmt, loc).branch
          val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, eightWordsImpl.dayHourImpl)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, monthAlgo
          )
          MONTH to monthlyImpl.getMonthly(yearlyStation, monthNumber)
        }
        DAY   -> DAY to dailyImpl.getDaily(lmt, loc)
        HOUR  -> HOUR to hourlyImpl.getHourly(lmt, loc)
      }
    }
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation): IContextModel {
    val eightWords = eightWordsImpl.getEightWords(lmt, loc)
    val models = getScaleMap(lmt, loc)

    val dayIndex = dailyImpl.getDailyIndex(lmt, loc)
    val hourStation = hourlyImpl.getHourly(lmt, loc)
    val oppo = LunarStationHourlyFeature.getOpponent(dayIndex, hourStation)
    val self = LunarStationHourlyFeature.getSelf1(hourStation, eightWords.hour.branch)

    val oppoHouse = ILunarStationFeature.getOppoHouse(oppo, eightWords.hour.branch)
    val selfHouse = ILunarStationFeature.getSelfHouse(self, eightWords.hour.branch)

    val reversed = LunarStationHourlyFeature.getReversed(dayIndex, hourStation)
    val hiddenVenusFoe: Set<Pair<Scale, Scale>> = getHiddenVenusFoe(lmt, loc)

    return ContextModel(
      eightWords,
      models[YEAR]!!,
      models[MONTH]!!,
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


  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationContext) return false

    if (yearlyImpl != other.yearlyImpl) return false
    if (monthlyImpl != other.monthlyImpl) return false
    if (dailyImpl != other.dailyImpl) return false
    if (hourlyImpl != other.hourlyImpl) return false
    if (eightWordsImpl != other.eightWordsImpl) return false
    if (chineseDateImpl != other.chineseDateImpl) return false
    if (monthAlgo != other.monthAlgo) return false

    return true
  }

  override fun hashCode(): Int {
    var result = yearlyImpl.hashCode()
    result = 31 * result + monthlyImpl.hashCode()
    result = 31 * result + dailyImpl.hashCode()
    result = 31 * result + hourlyImpl.hashCode()
    result = 31 * result + eightWordsImpl.hashCode()
    result = 31 * result + chineseDateImpl.hashCode()
    result = 31 * result + monthAlgo.hashCode()
    return result
  }

}



