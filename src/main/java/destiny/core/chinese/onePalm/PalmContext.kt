/**
 * Created by smallufo on 2018-04-30.
 */
package destiny.core.chinese.onePalm

import destiny.astrology.Coordinate
import destiny.astrology.HouseSystem
import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateHour
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.calendar.eightwords.IRisingSign
import destiny.core.chinese.Branch
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


class PalmContext(val ewImpl: IEightWordsStandardFactory,
                  override val positiveImpl: IPositive,
                  val chineseDateImpl: IChineseDate,
                  val risingSignImpl: IRisingSign,
                  override val monthAlgo: IFinalMonthNumber.MonthAlgo,
                  override val trueRisingSign: Boolean,
                  override val clockwiseHouse: Boolean,
                  val branchDescImpl: IBranchDesc) : IPalmContext, IEightWordsStandardFactory by ewImpl, Serializable {

  /** 沒帶入節氣資料 , 內定把月份計算採用 [IFinalMonthNumber.MonthAlgo.MONTH_LEAP_SPLIT15] 的演算法  */
  override fun getPalmWithoutSolarTerms(gender: Gender,
                                        yearBranch: Branch,
                                        leap: Boolean,
                                        monthNum: Int,
                                        dayNum: Int,
                                        hourBranch: Branch): IPalmModel {
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
    val main = hour.next(steps * positive)

    val houseMap = (0..11).map { i -> if (clockwiseHouse) main.next(i) else main.prev(i) }
      .zip(IPalmModel.House.values()).toMap()

    return PalmModel(gender, yearBranch, month, day, hour, houseMap)
  }


  /**
   * 本命盤：最完整的計算方式 , 包含時分秒、經緯度、時區
   */
  override fun getPalm(gender: Gender,
                       lmt: ChronoLocalDateTime<*>,
                       loc: ILocation,
                       place: String?,
                       name: String?): IPalmMetaModel {

    val cDate = chineseDateImpl.getChineseDate(lmt, loc, ewImpl.dayHourImpl)
    val hourBranch = ewImpl.dayHourImpl.getHour(lmt, loc)
    val chineseDateHour = ChineseDateHour(cDate, hourBranch)

    val trueRising: Branch? = if (trueRisingSign) {
      // 真實上升星座
      risingSignImpl.getRisingSign(lmt, loc, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
    } else {
      null
    }

    // 節氣的月支
    val monthBranch = ewImpl.yearMonthImpl.getMonth(lmt, loc).branch
    val palm = getPalm(gender, chineseDateHour, trueRising, monthBranch)

    return PalmMetaModel(palm, lmt, loc, place, name, chineseDateHour)
  }

  override fun getPalmWithDesc(gender: Gender,
                               lmt: ChronoLocalDateTime<*>,
                               loc: ILocation,
                               place: String?,
                               name: String?): IPalmMetaModelDesc {
    val palmMetaModel: IPalmMetaModel = getPalm(gender, lmt, loc, place, name)
    val palmModelDesc: IPalmModelDesc = getPalmModelDesc(palmMetaModel)
    return PalmMetaModelDesc(palmMetaModel, palmModelDesc)
  }

  private fun getPalmModelDesc(palmModel: IPalmModel): IPalmModelDesc {
    val houseDescriptions = palmModel.nonEmptyPillars.keys.map { branch ->
      val dao = IPalmModel.getDao(branch)
      val star = IPalmModel.getStar(branch)
      val houseIntro = branchDescImpl.getHouseIntro(branch)
      val map = palmModel.getPillars(branch).map { pillar ->
        pillar to branchDescImpl.getContent(pillar, branch)
      }.toMap()
      HouseDescription(branch, dao, star, houseIntro, map)
    }.toList()

    val hourBranch = palmModel.getBranch(IPalmModel.Pillar.時)
    val hourPoem = branchDescImpl.getPoem(hourBranch)
    val hourContent = branchDescImpl.getContent(hourBranch)

    return PalmModelDesc(houseDescriptions, hourPoem, hourContent)
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
