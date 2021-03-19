package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYearMonth
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/**
 * 二十八宿 Context
 */
interface ILunarStationContext {

  fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                scales: List<Scale> = listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR)): Map<Scale, LunarStation>
}


class LunarStationContext(val yearlyImpl: ILunarStationYearly,
                          val monthlyImpl: ILunarStationMonthly,
                          val dailyImpl: ILunarStationDaily,
                          val hourlyImpl: ILunarStationHourly,

                          val yearMonthImpl: IYearMonth,
                          val dayHourImpl: IDayHour,
                          private val chineseDateImpl: IChineseDate,
                          val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS
) : ILunarStationContext, Serializable {

  override fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale>): Map<Scale, LunarStation> {
    return scales.map { scale ->
      when (scale) {
        Scale.YEAR -> Scale.YEAR to yearlyImpl.getYearlyStation(lmt, loc)
        Scale.MONTH -> {
          val yearlyStation: LunarStation = yearlyImpl.getYearlyStation(lmt, loc)
          val monthBranch = yearMonthImpl.getMonth(lmt, loc).branch
          val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month,
            chineseDate.leapMonth,
            monthBranch,
            chineseDate.day,
            monthAlgo
          )
          Scale.MONTH to monthlyImpl.getMonthlyStation(yearlyStation, monthNumber)
        }
        Scale.DAY -> Scale.DAY to dailyImpl.getDailyStation(lmt, loc).first
        Scale.HOUR -> Scale.HOUR to hourlyImpl.getHourlyStation(lmt, loc)
      }
    }.toMap()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationContext) return false

    if (yearlyImpl != other.yearlyImpl) return false
    if (monthlyImpl != other.monthlyImpl) return false
    if (dailyImpl != other.dailyImpl) return false
    if (hourlyImpl != other.hourlyImpl) return false
    if (yearMonthImpl != other.yearMonthImpl) return false
    if (dayHourImpl != other.dayHourImpl) return false
    if (chineseDateImpl != other.chineseDateImpl) return false
    if (monthAlgo != other.monthAlgo) return false

    return true
  }

  override fun hashCode(): Int {
    var result = yearlyImpl.hashCode()
    result = 31 * result + monthlyImpl.hashCode()
    result = 31 * result + dailyImpl.hashCode()
    result = 31 * result + hourlyImpl.hashCode()
    result = 31 * result + yearMonthImpl.hashCode()
    result = 31 * result + dayHourImpl.hashCode()
    result = 31 * result + chineseDateImpl.hashCode()
    result = 31 * result + monthAlgo.hashCode()
    return result
  }


}
