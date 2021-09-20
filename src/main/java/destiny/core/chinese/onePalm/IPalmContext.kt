package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IChineseDateHourModel
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.chinese.Branch
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime


interface IPalmContext : IEightWordsStandardFactory {
  val positiveImpl: IPositive
  val monthAlgo: IFinalMonthNumber.MonthAlgo
  val trueRisingSign: Boolean
  val clockwiseHouse: Boolean

  /** 沒帶入節氣資料 */
  fun getPalmWithoutSolarTerms(gender: Gender, yearBranch: Branch, leap: Boolean, monthNum: Int, dayNum: Int, hourBranch: Branch, monthAlgo: IFinalMonthNumber.MonthAlgo): IPalmModel


  /**
   * 本命盤 , 純粹以農曆、節氣計算，不包含確切時間、地點
   * @param trueRising : 是否已經預先計算好了真實的上升星座
   * @param monthBranch : 「節氣」的月支
   */
  fun getPalm(gender: Gender,
              chineseDateHour: IChineseDateHourModel,
              trueRising: Branch?,
              monthBranch: Branch): IPalmModel {

    val positive = positiveImpl.isPositive(gender, chineseDateHour.year.branch)
    val positiveValue = if (positive) 1 else -1


    val finalMonthNum = if (monthAlgo === IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS) {
      // 節氣計算月份盤
       IFinalMonthNumber.getFinalMonthNumber(chineseDateHour.month, chineseDateHour.leapMonth, monthBranch, chineseDateHour.day, monthAlgo)
    } else {
      // 陰曆計算月份
      IFinalMonthNumber.getFinalMonthNumber(chineseDateHour.month, chineseDateHour.leapMonth, chineseDateHour.day, monthAlgo)
    }

    // 年上起月
    val month: Branch = chineseDateHour.year.branch.next((finalMonthNum - 1) * positiveValue)
    // 月上起日
    val day: Branch = month.next((chineseDateHour.day - 1) * positiveValue)
    // 日上起時
    val hour: Branch = day.next(chineseDateHour.hourBranch.index * positiveValue)


    val main: Branch = if (trueRisingSign) {
      trueRising!!
    } else {
      val steps: Int = Branch.卯.getAheadOf(chineseDateHour.hourBranch)
      hour.next(steps * (if (positive) 1 else -1))
    }

    val houseMap = (0..11).map { i -> if (clockwiseHouse) main.next(i) else main.prev(i) }
      .zip(IPalmModel.House.values()).toMap()

    return PalmModel(gender, chineseDateHour.year.branch, month, day, hour, houseMap)

  }

  /**
   * 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  fun getPalm(gender: Gender,
              lmt: ChronoLocalDateTime<*>,
              loc: ILocation,
              place: String?,
              name: String?): IPalmMetaModel

  /**
   * 承上 , 除了計算本命盤，還額外傳回命盤的文字解釋
   */
  fun getPalmWithDesc(gender: Gender,
                      lmt: ChronoLocalDateTime<*>,
                      loc: ILocation,
                      place: String?,
                      name: String?): IPalmMetaModelDesc

  /**
   * 計算 [IBirthDataNamePlace]
   **/
  fun getPalm(data: IBirthDataNamePlace): IPalmMetaModel {
    return getPalm(data.gender, data.time, data.location, data.place, data.name)
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
