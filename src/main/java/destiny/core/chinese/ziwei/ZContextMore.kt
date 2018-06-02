/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.IntAgeNote
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.IHour
import destiny.core.calendar.eightwords.IMidnight
import destiny.core.chinese.ITianyi
import java.io.Serializable
import java.util.*

/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 *
 * 另外附加 與紫微「計算」無關的設定
 * 例如
 * 是否顯示小限
 * 真太陽時(還是手錶平均時間)
 * 八字排列方向
 */
interface IZContextPresent : IZiweiContext {

  val selfTransFour: SelfTransFour

  val oppoTransFour: OppoTransFour


  /** 是否顯示小限  */
  val showSmallRange: Boolean

  /** 民用曆法 or 天文曆法  */
  val chineseDateImpl: IChineseDate

  /** 是否顯示八字盤  */
  val showEightWords: Boolean

  /** 八字排盤，右至左 or 左至右  */
  val direction: Direction?

  /** 時辰劃分  */
  val hourImpl: IHour

  /** 子正判定  */
  val midnightImpl: IMidnight

  /** 子初換日 (true) 或 子正換日 (false)  */
  val changeDayAfterZi: Boolean

  /** 顯示雜曜  */
  val showMinors: Boolean

  /** 顯示博士12神煞  */
  val showDoctors: Boolean

  /** 顯示長生12神煞  */
  val showLongevity: Boolean

  /** 顯示 將前12星  */
  val showGeneralFront: Boolean

  /** 顯示 歲前12星  */
  val showYearFront: Boolean

  /** 宮干四化「自化」 顯示選項  */
  enum class SelfTransFour : Descriptive {

    /** 不顯示  */
    SELF_TRANS_FOUR_NONE,

    /** 文字顯示  */
    SELF_TRANS_FOUR_TEXT,

    /** 箭頭朝外  */
    SELF_TRANS_FOUR_ARROW;

    override fun getTitle(locale: Locale): String {
      return ResourceBundle.getBundle(IZContextPresent::class.java.name, locale).getString(name)
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }

  /** 宮干四化「化入對宮」的顯示選項  */
  enum class OppoTransFour : Descriptive {
    /** 不顯示  */
    OPPO_TRANS_FOUR_NONE,

    /** 朝內(對宮) 箭頭  */
    OPPO_TRANS_FOUR_ARROW;

    override fun getTitle(locale: Locale): String {
      return ResourceBundle.getBundle(IZContextPresent::class.java.name, locale).getString(name)
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }
}


class ZContextPresent(
  private val zContext : ZContext ,
  override val selfTransFour: IZContextPresent.SelfTransFour = IZContextPresent.SelfTransFour.SELF_TRANS_FOUR_TEXT,
  override val oppoTransFour: IZContextPresent.OppoTransFour = IZContextPresent.OppoTransFour.OPPO_TRANS_FOUR_ARROW,
  /** 是否顯示小限 */
  override val showSmallRange: Boolean = false,
  /** 民用曆法 or 天文曆法  */
  override val chineseDateImpl: IChineseDate,
  /** 是否顯示八字盤  */
  override val showEightWords: Boolean = true,
  /** 八字排盤，右至左 or 左至右  */
  override val direction: Direction? = Direction.R2L,
  /** 時辰劃分  */
  override val hourImpl: IHour,
  /** 子正判定  */
  override val midnightImpl: IMidnight,
  /** 子初換日 (true) 或 子正換日 (false)  */
  override val changeDayAfterZi: Boolean = true,
  /** 顯示雜曜  */
  override val showMinors: Boolean = true,
  /** 顯示博士12神煞  */
  override val showDoctors: Boolean = true,
  /** 顯示長生12神煞  */
  override val showLongevity: Boolean = true,
  /** 顯示 將前12星  */
  override val showGeneralFront: Boolean = true,
  /** 顯示 歲前12星  */
  override val showYearFront: Boolean = true) : IZContextPresent , IZiweiContext by zContext , Serializable

/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 *
 * 另外附加 與紫微「計算」無關的設定
 * 例如
 * 是否顯示小限
 * 真太陽時(還是手錶平均時間)
 * 八字排列方向
 */
@Deprecated("ZContextPresent")
class ZContextMore(mainBodyHouseImpl: IMainBodyHouse,
                   purpleBranchImpl: IPurpleStarBranch,
                   mainStarsMonthAlgo: IFinalMonthNumber.MonthAlgo?,
                   monthStarsMonthAlgo: IFinalMonthNumber.MonthAlgo,
                   yearType: YearType, houseSeqImpl: IHouseSeq, tianyiImpl: ITianyi, fireBell: FireBell,
                   hurtAngel: HurtAngel, transFourImpl: ITransFour, strengthImpl: IStrength,
                   flowYearImpl: IFlowYear, flowMonthImpl: IFlowMonth, flowDayImpl: IFlowDay, flowHourImpl: IFlowHour,
                   ageNoteImpls: List<IntAgeNote>, bigRangeImpl: IBigRange, redBeauty: RedBeauty,

                   override val selfTransFour: IZContextPresent.SelfTransFour,
                   override val oppoTransFour: IZContextPresent.OppoTransFour,
                   /** 是否顯示小限  */
                   override val showSmallRange: Boolean,
                   /** 民用曆法 or 天文曆法  */
                   override val chineseDateImpl: IChineseDate,
                   /** 是否顯示八字盤  */
                   override val showEightWords: Boolean = true,
                   /** 八字排盤，右至左 or 左至右  */
                   override val direction: Direction? = Direction.R2L,
                   /** 時辰劃分  */
                   override val hourImpl: IHour,
                   /** 子正判定  */
                   override val midnightImpl: IMidnight,
                   /** 子初換日 (true) 或 子正換日 (false)  */
                   override val changeDayAfterZi: Boolean,
                   /** 顯示雜曜  */
                   override val showMinors: Boolean,
                   /** 顯示博士12神煞  */
                   override val showDoctors: Boolean,
                   /** 顯示長生12神煞  */
                   override val showLongevity: Boolean,
                   /** 顯示 將前12星  */
                   override val showGeneralFront: Boolean,
                   /** 顯示 歲前12星  */
                   override val showYearFront: Boolean) : IZContextPresent , IZiweiContext by
  ZContext(mainBodyHouseImpl, purpleBranchImpl, mainStarsMonthAlgo, monthStarsMonthAlgo, yearType, houseSeqImpl,
           tianyiImpl, fireBell, hurtAngel, transFourImpl, strengthImpl, flowYearImpl, flowMonthImpl, flowDayImpl,
           flowHourImpl, ageNoteImpls, bigRangeImpl, redBeauty) {


  override fun toString(): String {
    return "[ZContextMore purpleBranchImpl=$purpleBranchImpl, selfTransFour=$selfTransFour, oppoTransFour=$oppoTransFour, showSmallRange=$showSmallRange, direction=$direction, houseSeqImpl=$houseSeqImpl, hourImpl=$hourImpl, midnightImpl=$midnightImpl, tianyiImpl=$tianyiImpl, changeDayAfterZi=$changeDayAfterZi, showMinors=$showMinors, showDoctors=$showDoctors, showLongevity=$showLongevity, transFourImpl=$transFourImpl, strengthImpl=$strengthImpl, flowYearImpl=$flowYearImpl, flowMonthImpl=$flowMonthImpl, flowDayImpl=$flowDayImpl, flowHourImpl=$flowHourImpl, bigRangeImpl=$bigRangeImpl]"
  }
}
