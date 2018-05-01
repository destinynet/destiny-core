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

  val positiveImpl : IPositive
  val hourImpl : IHour
  val midnightImpl : IMidnight
  val monthAlgo : IFinalMonthNumber.MonthAlgo
  val trueRisingSign: Boolean
  val changeDayAfterZi: Boolean
  val clockwiseHouse: Boolean


  /**
   * [A] 本命盤，已經預先計算命宮
   * @param main 預先計算的命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   */
  fun getPalmWithMainBranch(gender: Gender,
                            yearBranch: Branch,
                            leap: Boolean,
                            monthNum: Int,
                            dayNum: Int,
                            hourBranch: Branch,
                            positiveImpl: IPositive,
                            main: Branch,
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

    val houseMap = HashBiMap.create<Branch, IPalmModel.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) main.next(i) else main.prev(i)] = IPalmModel.House.values()[i]
    }
    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }


  /**
   * [B] 本命盤 , 沒有預先計算命宮
   * @param clockwiseHouse 宮位飛佈，順時針(true) or 逆時針(false)
   */
  fun getPalmWithoutMainBranch(gender: Gender,
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
    val main = hour.next(steps * positive)
    val houseMap = HashBiMap.create<Branch, IPalmModel.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) main.next(i) else main.prev(i)] = IPalmModel.House.values()[i]
    }
    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }

  /** [C] 沒帶入節氣資料 , 內定把月份計算採用 [MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  fun getPalmWithoutSolarTerms(gender: Gender,
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
              monthBranch: Branch): IPalmModel {

    return trueRising?.let { rising ->
      // 以上升星座 rising 作為預先計算的命宮
      getPalmWithMainBranch(gender, chineseDateHour.year.branch, chineseDateHour.isLeapMonth, chineseDateHour.month,
                            chineseDateHour.day, chineseDateHour.hourBranch, positiveImpl, rising, monthBranch, monthAlgo,
                            clockwiseHouse)
    } ?: getPalmWithoutMainBranch(gender, chineseDateHour.year.branch, chineseDateHour.isLeapMonth, chineseDateHour.month,
                                  chineseDateHour.day, chineseDateHour.hourBranch, positiveImpl, monthBranch, monthAlgo, clockwiseHouse)
  }

  /**
   * [E] 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  fun getPalm(gender: Gender, lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String?): IPalmModelMeta

  companion object {
    val logger = LoggerFactory.getLogger(IPalmContext::class.java)!!
  }
}

class PalmContext(override val positiveImpl: IPositive,
                  val chineseDateImpl: IChineseDate,
                  val dayImpl: IDay,
                  override val hourImpl: IHour,
                  override val midnightImpl: IMidnight,
                  val risingSignImpl: IRisingSign,
                  val yearMonthImpl: IYearMonth,
                  override val monthAlgo: IFinalMonthNumber.MonthAlgo,
                  override val changeDayAfterZi: Boolean,
                  override val trueRisingSign: Boolean,
                  override val clockwiseHouse: Boolean) : IPalmContext, Serializable {

  /** [C] 沒帶入節氣資料 , 內定把月份計算採用 [MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  override fun getPalmWithoutSolarTerms(gender: Gender,
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
    val houseMap = HashBiMap.create<Branch, IPalmModel.House>(12)
    for (i in 0..11) {
      houseMap[if (clockwiseHouse) rising.next(i) else rising.prev(i)] = IPalmModel.House.values()[i]
    }
    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }



  /**
   * [E] 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  override fun getPalm(gender: Gender, lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String?): IPalmModelMeta {

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
    return PalmModelMeta(palm , lmt, loc, place , chineseDateHour)
  }

  companion object {
    val logger = LoggerFactory.getLogger(PalmContext::class.java)!!
  }
}