/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.Location
import destiny.core.calendar.chinese.ChineseDateIF
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.DayIF
import destiny.core.calendar.eightwords.HourIF
import destiny.core.calendar.eightwords.MidnightIF
import destiny.core.calendar.eightwords.YearMonthIF
import destiny.core.chinese.Branch
import destiny.core.chinese.ziwei.ZContext.YearType
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 傳統紫微計算命宮
 *
 * 命宮計算依據，是「年干」以及「時支」
 * 而「年干」要分「陰曆」或是「節氣」
 * 因此必須傳入一大堆參數，才能計算出「陰曆」或是「節氣」的「年」
 * 再由 [YearType] 來決定要挑哪一個
 */
class MainBodyHouseTradImpl(private val yearMonthImpl: YearMonthIF, private val dayImpl: DayIF, private val chineseDateImpl: ChineseDateIF, private val hourImpl: HourIF,
                            private val midnightImpl: MidnightIF, private val changeDayAfterZi: Boolean, private val mainStarsAlgo: IFinalMonthNumber.MonthAlgo) : IMainBodyHouse, Serializable {

  override fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: Location): Pair<Branch, Branch> {

    val cDate = chineseDateImpl.getChineseDate(lmt, loc, dayImpl, hourImpl, midnightImpl, changeDayAfterZi)

    val monthBranch = yearMonthImpl.getMonth(lmt, loc).branch

    val lunarMonth = cDate.month
    val days = cDate.day

    val hour = hourImpl.getHour(lmt, loc)

    // 最終要計算的「月份」數字 , for 主星
    val finalMonthNumForMainStars = IFinalMonthNumber.getFinalMonthNumber(lunarMonth, cDate.isLeapMonth, monthBranch, days, mainStarsAlgo)

    val mainHouse = IZiwei.getMainHouseBranch(finalMonthNumForMainStars, hour)
    val bodyHouse = IZiwei.getBodyHouseBranch(finalMonthNumForMainStars, hour)

    return Pair(mainHouse, bodyHouse)
  }
}
