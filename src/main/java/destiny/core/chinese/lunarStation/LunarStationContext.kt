package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IMonth
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/**
 * 二十八宿 Context
 */
interface ILunarStationContext {

  fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                scales: List<Scale> = listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR)): Map<Scale, LunarStation>
}


class LunarStationContext(private val yearlyImpl: ILunarStationYearly,
                          private val monthlyImpl: ILunarStationMonthly,
                          private val dailyImpl: ILunarStationDaily,
                          private val hourlyImpl: ILunarStationHourly,

                          private val monthImpl: IMonth,
                          private val dayHourImpl: IDayHour,
                          private val chineseDateImpl: IChineseDate,
                          private val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS
) : ILunarStationContext, Serializable {

  override fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation, scales: List<Scale>): Map<Scale, LunarStation> {
    return scales.map { scale ->
      when (scale) {
        Scale.YEAR -> Scale.YEAR to yearlyImpl.getYearlyStation(lmt, loc)
        Scale.MONTH -> {
          val yearStation: LunarStation = yearlyImpl.getYearlyStation(lmt, loc)
          val monthBranch = monthImpl.getMonth(lmt, loc).branch
          val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl)
          val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
            chineseDate.month,
            chineseDate.leapMonth,
            monthBranch,
            chineseDate.day,
            monthAlgo
          )
          Scale.MONTH to monthlyImpl.getMonthlyStation(yearStation, monthNumber)
        }
        Scale.DAY -> Scale.DAY to dailyImpl.getDailyStation(lmt, loc).first
        Scale.HOUR -> Scale.HOUR to hourlyImpl.getHourlyStation(lmt, loc)
      }
    }.toMap()
  }
}
