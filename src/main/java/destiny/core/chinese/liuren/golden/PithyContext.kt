/**
 * Created by smallufo on 2018-05-02.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.IDayNight
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.chinese.*
import destiny.core.chinese.liuren.General
import destiny.core.chinese.liuren.IGeneralSeq
import destiny.core.chinese.liuren.IGeneralStemBranch
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IPithyContext {

  fun getModel(direction: Branch,
               clockwise: Clockwise,
               ew: IEightWords,
               /** 月將 */
               moonMaster: Branch,
               dayNight: DayNight): IPithyModel

  fun getModernModel(gender: Gender, lmt: ChronoLocalDateTime<*>, loc: ILocation, direction: Branch): IPithyModernModel

  fun getDetailModel(gender: Gender,
                     lmt: ChronoLocalDateTime<*>,
                     loc: ILocation,
                     direction: Branch,
                     place: String?,
                     question: String?,
                     method: IPithyDetailModel.Method): IPithyDetailModel

  fun getDetailModel(data: IBirthDataNamePlace, direction: Branch, question: String, method: IPithyDetailModel.Method): IPithyDetailModel {
    return getDetailModel(data.gender, data.time, data.location, direction, data.place, question, method)
  }
}

class PithyContext(
  val eightWordsImpl: IEightWordsStandardFactory,
  val monthMasterImpl: IMonthMaster,

  val clockwiseImpl: IClockwise,
  val dayNightImpl: IDayNight,
  val tianyiImpl: ITianyi,
  val generalSeqImpl: IGeneralSeq,
  val generalStemBranchImpl: IGeneralStemBranch) : IPithyContext, Serializable {


  override fun getModel(direction: Branch,
                        clockwise: Clockwise,
                        ew: IEightWords,
                        moonMaster: Branch,
                        dayNight: DayNight): IPithyModel {
    // 天乙貴人(起點)
    val tianYi = tianyiImpl.getFirstTianyi(ew.day.stem, dayNight)

    val steps = when (clockwise) {
      Clockwise.CLOCKWISE -> direction.getAheadOf(tianYi)
      Clockwise.COUNTER -> tianYi.getAheadOf(direction)
    }

    logger.debug("天乙貴人 (日干 {} + {} ) = {} . 地分 = {} , 順逆 = {} , steps = {}",
      ew.day.stem, dayNight, tianYi, direction, clockwise, steps)

    // 貴神
    val 貴神地支 = General.貴人.next(steps, generalSeqImpl).getStemBranch(generalStemBranchImpl).branch
    val 貴神天干 = StemBranchUtils.getHourStem(ew.day.stem, 貴神地支)
    logger.debug("推導貴神，從 {} 開始走 {} 步，得到 {} , 地支為 {} , 天干為 {}", General.貴人, steps, General.貴人.next(steps, generalSeqImpl),
      貴神地支,
      貴神天干)
    val 貴神 = StemBranch[貴神天干, 貴神地支]

    return Pithy(ew, direction, moonMaster, dayNight, 貴神)
  }

  override fun getModernModel(gender: Gender, lmt: ChronoLocalDateTime<*>, loc: ILocation,
                              direction: Branch): IPithyModernModel {
    val ew = eightWordsImpl.getEightWords(lmt, loc)
    // 月將
    val moonMaster = monthMasterImpl.getBranch(lmt, loc)
    val clockwise = clockwiseImpl.getClockwise(lmt, loc)
    val dayNight = dayNightImpl.getDayNight(lmt, loc)

    val pithy: IPithyModel = getModel(direction, clockwise, ew, moonMaster, dayNight)
    return PithyModernModel(pithy, gender, lmt, loc)
  }

  override fun getDetailModel(gender: Gender,
                              lmt: ChronoLocalDateTime<*>,
                              loc: ILocation,
                              direction: Branch,
                              place: String?,
                              question: String?,
                              method: IPithyDetailModel.Method): IPithyDetailModel {
    val modernModel = getModernModel(gender, lmt, loc, direction)
    return PithyDetailModel(modernModel, place, question, method)
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
