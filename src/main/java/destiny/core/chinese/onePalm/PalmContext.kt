/**
 * Created by smallufo on 2018-04-30.
 */
package destiny.core.chinese.onePalm

import com.google.common.collect.HashBiMap
import destiny.astrology.Coordinate
import destiny.astrology.HouseSystem
import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateHour
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IChineseDateHourModel
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


interface IPalmContext {

  /**
   * [A] 本命盤，已經預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   */
  fun getPalm(gender: Gender,
              yearBranch: Branch,
              leap: Boolean,
              monthNum: Int,
              dayNum: Int,
              hourBranch: Branch,
              positiveImpl: IPositive,
              rising: Branch,
              monthBranch: Branch,
              monthAlgo: IFinalMonthNumber.MonthAlgo,
              clockwiseHouse: Boolean): IPalmModel {
    val positive = if (positiveImpl.isPositive(gender, yearBranch)) 1 else -1

    val finalMonthNum = IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, monthBranch, dayNum, monthAlgo)

    // 年上起月
    val month: Branch = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    val houseMap = HashBiMap.create<Branch, Palm.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = Palm.House.values()[i]
    }
    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }


  /**
   * [B] 本命盤 , 沒有預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   */
  fun getPalm(gender: Gender,
              yearBranch: Branch,
              leap: Boolean,
              monthNum: Int,
              dayNum: Int,
              hourBranch: Branch,
              positiveImpl: IPositive,
              monthBranch: Branch,
              monthAlgo: IFinalMonthNumber.MonthAlgo,
              clockwiseHouse: Boolean): IPalmModel {
    val positive = if (positiveImpl.isPositive(gender, yearBranch)) 1 else -1

    logger.trace("positive = {}", positive)

    val finalMonthNum = IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, monthBranch, dayNum, monthAlgo)

    logger.trace("yearBranch = {}", yearBranch)

    // 年上起月
    val month = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    // 命宮
    val steps = Branch.卯.getAheadOf(hourBranch)
    val rising = hour.next(steps * positive)
    val houseMap = HashBiMap.create<Branch, Palm.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = Palm.House.values()[i]
    }
    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }

  /** [C] 沒帶入節氣資料 , 內定把月份計算採用 [MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  fun getPalm(gender: Gender,
              yearBranch: Branch,
              leap: Boolean,
              monthNum: Int,
              dayNum: Int,
              hourBranch: Branch) : IPalmModel


  /**
   * [D] 本命盤
   * @param trueRising : 是否已經預先計算好了真實的上升星座
   * @param monthBranch : 「節氣」的月支
   */
  fun getPalm(gender: Gender,
              chineseDateHour: IChineseDateHourModel,
              trueRising: Branch?,
              monthBranch: Branch): IPalmModel

  /**
   * [E] 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  fun getPalm(gender: Gender, lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String?): IPalmModel

  companion object {
    val logger = LoggerFactory.getLogger(IPalmContext::class.java)!!
  }
}

class PalmContext(val positiveImpl: IPositive,
                  val chineseDateImpl: IChineseDate,
                  val dayImpl: IDay,
                  val hourImpl: IHour,
                  val midnightImpl: IMidnight,
                  val risingSignImpl: IRisingSign,
                  val yearMonthImpl: IYearMonth,
                  val monthAlgo: IFinalMonthNumber.MonthAlgo,
                  val changeDayAfterZi: Boolean,
                  val trueRisingSign: Boolean,
                  val clockwiseHouse: Boolean) : IPalmContext, Serializable {

  /** [C] 沒帶入節氣資料 , 內定把月份計算採用 [MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  override fun getPalm(gender: Gender,
                       yearBranch: Branch,
                       leap: Boolean,
                       monthNum: Int,
                       dayNum: Int,
                       hourBranch: Branch) : IPalmModel {
    val positive = if (positiveImpl.isPositive(gender, yearBranch)) 1 else -1

    logger.trace("positive = {}", positive)

    var finalMonthNum = monthNum
    if (leap && dayNum > 15)
    // 若為閏月，15日以後算下個月
      finalMonthNum++

    logger.trace("yearBranch = {}", yearBranch)

    // 年上起月
    val month = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    // 命宮
    val steps = Branch.卯.getAheadOf(hourBranch)
    val rising = hour.next(steps * positive)
    val houseMap = HashBiMap.create<Branch, Palm.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = Palm.House.values()[i]
    }
    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }

  /** [D] */
  override fun getPalm(gender: Gender,
                       chineseDateHour: IChineseDateHourModel,
                       trueRising: Branch?,
                       monthBranch: Branch): IPalmModel {
    return trueRising?.let { rising ->
      getPalm(gender, chineseDateHour.year.branch, chineseDateHour.isLeapMonth, chineseDateHour.month,
              chineseDateHour.day, chineseDateHour.hourBranch, positiveImpl, rising, monthBranch, monthAlgo,
              clockwiseHouse)
    } ?: getPalm(gender, chineseDateHour.year.branch, chineseDateHour.isLeapMonth, chineseDateHour.month,
                 chineseDateHour.day, chineseDateHour.hourBranch, positiveImpl, monthBranch, monthAlgo, clockwiseHouse)
  }

  /**
   * [E] 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  override fun getPalm(gender: Gender, lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String?): IPalmModel {

    val cDate = chineseDateImpl.getChineseDate(lmt, loc, dayImpl, hourImpl, midnightImpl, changeDayAfterZi)
    val hourBranch = hourImpl.getHour(lmt, loc)
    val chineseDateHour = ChineseDateHour(cDate, hourBranch)

    val trueRising: Branch? = if (trueRisingSign) {
      // 真實上升星座
      risingSignImpl.getRisingSign(lmt, loc, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    } else {
      null
    }

    // 節氣的月支
    val monthBranch = yearMonthImpl.getMonth(lmt, loc).branch
    val palm = getPalm(gender, chineseDateHour, trueRising, monthBranch)

    return palm
  }

  companion object {
    val logger = LoggerFactory.getLogger(PalmContext::class.java)!!
  }
}