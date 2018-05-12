package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDateHourModel
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IHour
import destiny.core.calendar.eightwords.IMidnight
import destiny.core.chinese.Branch
import org.slf4j.LoggerFactory
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
   * 本命盤，已經預先計算命宮
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

    val finalMonthNum =
      IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, monthBranch, dayNum,
                                            monthAlgo)

    // 年上起月
    val month: Branch = yearBranch.next((finalMonthNum - 1) * positive)

    // 月上起日
    val day = month.next((dayNum - 1) * positive)

    // 日上起時
    val hour = day.next(hourBranch.index * positive)

    val houseMap = (0..11).map { i -> if (clockwiseHouse) main.next(i) else main.prev(i) }
      .zip(IPalmModel.House.values()).toMap()

    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }


  /**
   * 本命盤 , 沒有預先計算命宮
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

    val finalMonthNum =
      IFinalMonthNumber.getFinalMonthNumber(monthNum, leap, monthBranch, dayNum,
                                            monthAlgo)

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

    val houseMap = (0..11).map { i -> if (clockwiseHouse) main.next(i) else main.prev(i) }
      .zip(IPalmModel.House.values()).toMap()

    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }

  /** 沒帶入節氣資料 , 內定把月份計算採用 [IFinalMonthNumber.MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  fun getPalmWithoutSolarTerms(gender: Gender,
                               yearBranch: Branch,
                               leap: Boolean,
                               monthNum: Int,
                               dayNum: Int,
                               hourBranch: Branch) : IPalmModel


  /**
   * 本命盤 , 純粹以農曆、節氣計算，不包含確切時間、地點
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
   * 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  fun getPalm(gender: Gender,
              lmt: ChronoLocalDateTime<*>,
              loc: ILocation,
              place: String?,
              name: String?): IPalmMetaModel

  /** 計算 [IBirthDataNamePlace] */
  fun getPalm(data: IBirthDataNamePlace) : IPalmMetaModel {
    return getPalm(data.gender , data.time , data.location , data.place , data.name)
  }

  companion object {
    val logger = LoggerFactory.getLogger(IPalmContext::class.java)!!
  }
}