/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 傳統紫微計算命宮
 *
 * 「生月宮位起子時，逆時安命順安身。」
 * 命宮 : (月數 , 時支) -> 地支
 * 寅宮開始，順數月數，再逆數時支
 *
 * 但是「月數」又與「是否閏月、是否看節氣」有關
 *
 *
 * 命宮計算依據，是「年干」以及「時支」  :
 * 而「年干」要分「陰曆」或是「節氣」
 * 因此必須傳入一大堆參數，才能計算出「陰曆」或是「節氣」的「年」
 * 再由 [YearType] 來決定要挑哪一個
 */
class MainBodyHouseTradImpl(val yearMonthImpl: IYearMonth,
                            val dayHourImpl: IDayHour,
                            private val chineseDateImpl: IChineseDate,
                            val mainStarsAlgo: IFinalMonthNumber.MonthAlgo?) : IMainBodyHouse, Serializable {

  /** 命宮、身宮 、以及「最後要給主星所使用的月數 (若為占星算法，此值為空) 」 */
  override fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: ILocation): Triple<Branch, Branch , Int?> {

    val cDate = chineseDateImpl.getChineseDate(lmt, loc, dayHourImpl)

    val monthBranch = yearMonthImpl.getMonth(lmt, loc).branch

    val lunarMonth = cDate.month
    val days = cDate.day

    val hour = dayHourImpl.getHour(lmt, loc)

    // 最終要計算的「月份」數字 , for 主星
    val finalMonthNumForMainStars = IFinalMonthNumber.getFinalMonthNumber(lunarMonth, cDate.leapMonth, monthBranch, days, mainStarsAlgo)

    val mainHouse = Ziwei.getMainHouseBranch(finalMonthNumForMainStars, hour)
    val bodyHouse = Ziwei.getBodyHouseBranch(finalMonthNumForMainStars, hour)

    return Triple(mainHouse, bodyHouse , finalMonthNumForMainStars)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MainBodyHouseTradImpl) return false

    if (yearMonthImpl != other.yearMonthImpl) return false
    if (dayHourImpl != other.dayHourImpl) return false
    if (chineseDateImpl != other.chineseDateImpl) return false
    if (mainStarsAlgo != other.mainStarsAlgo) return false

    return true
  }

  override fun hashCode(): Int {
    var result = yearMonthImpl.hashCode()
    result = 31 * result + dayHourImpl.hashCode()
    result = 31 * result + chineseDateImpl.hashCode()
    result = 31 * result + (mainStarsAlgo?.hashCode() ?: 0)
    return result
  }


}
