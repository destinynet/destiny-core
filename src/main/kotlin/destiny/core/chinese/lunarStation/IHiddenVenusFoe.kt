/**
 * Created by smallufo on 2021-03-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.StemBranch
import destiny.core.chinese.lunarStation.HiddenVenusFoeFeature.Companion.isDayFoeForDay
import destiny.core.chinese.lunarStation.HiddenVenusFoeFeature.Companion.isDayFoeForYear
import destiny.core.chinese.lunarStation.HiddenVenusFoeFeature.Companion.isHourFoeForHour
import destiny.core.chinese.lunarStation.HiddenVenusFoeFeature.Companion.monthFoeMap
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/**
 * 暗金伏斷
 */
interface IHiddenVenusFoe {

  /**
   * @return Pair<Scale,Scale> 前者代表「此時刻的什麼時段」 , 犯了 後者的 暗金伏斷煞
   */
  fun getHiddenVenusFoe(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Pair<Scale, Scale>>

}

/**
 * 《禽星易見・演禽賦》
 *
 * 暗曜，即 [亢] 、 [牛] 、 [婁] 、 [鬼] 四金也，乃天上之太白星。凡日時直之，即暗金也。諸事忌之，惟番禽得亢婁猶好。然其伏斷之惡本官，所犯非輕。
 *
 * 時直空亡，遇吉禽猶當取的；
 *
 * 空亡者，乃時上天干帶壬癸是也。歷書云：截路空亡，出行大忌，此時若出離門，諸事不利。
 * 《三軍一覽》云：此時用事，如人在路途中遇水，不能濟也。
 * 《禽書》云：凡選用時若天干帶壬癸，更直氐房心虛室奎婁昴觜鬼柳參此十二宿者，則截其路而不能濟也。此時縱得奇門，亦主阻滯，切不可用，此兵家之大忌。
 *
 */
class HiddenVenusFoeAnimalStar(private val yearlyImpl: ILunarStationYearly,
                               private val monthlyImpl: ILunarStationMonthly,
                               private val dailyImpl: ILunarStationDaily,
                               private val hourlyImpl: ILunarStationHourly,
                               private val yearMonthImpl: IYearMonth,
                               private val chineseDateImpl: IChineseDate,
                               private val dayHourImpl: IDayHour,
                               val monthAlgo: MonthAlgo = MonthAlgo.MONTH_SOLAR_TERMS) :
  IHiddenVenusFoe, Serializable {

  override fun getHiddenVenusFoe(lmt: ChronoLocalDateTime<*>, loc: ILocation): Set<Pair<Scale, Scale>> {
    val yearly = yearlyImpl.getYearly(lmt, loc)
    val monthBranch = yearMonthImpl.getMonth(lmt, loc).branch
    val chineseDate = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl)
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month,
      chineseDate.leapMonth,
      monthBranch,
      chineseDate.day,
      monthAlgo
    )
    val monthlyStation = monthlyImpl.getMonthly(yearly, monthNumber)
    val daySb: StemBranch = dayHourImpl.getDay(lmt, loc)
    val dailyStation = dailyImpl.getDaily(lmt, loc)
    val hourlyStation = hourlyImpl.getHourly(lmt, loc)
    val hourBranch = dayHourImpl.getHour(lmt, loc)


    return mutableSetOf<Pair<Scale, Scale>>().apply {
      // 年
      if (isDayFoeForYear(yearly.planet, daySb.branch, dailyStation)) {
        add(Scale.DAY to Scale.YEAR)
      }

      // 月
      if (monthFoeMap[monthNumber] == monthlyStation) {
        add(Scale.MONTH to Scale.MONTH)
      }

      // 日
      if (isDayFoeForDay(dailyStation, daySb.branch)) {
        add(Scale.DAY to Scale.DAY)
      }

      // 時
      if (isHourFoeForHour(hourlyStation, hourBranch)) {
        add(Scale.HOUR to Scale.HOUR)
      }
    }
  }

  companion object {
    const val VALUE = "animalStar"
  }
}
