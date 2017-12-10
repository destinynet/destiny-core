/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.IntAgeNote
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.chinese.TianyiIF
import java.io.Serializable
import java.util.*

/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 */
open class ZContext(
  /** 命宮、身宮 演算法  */
  /** 命宮、身宮 演算法  */
  val mainBodyHouseImpl: IMainBodyHouse,
  /** 紫微星，在閏月時，該如何處理  */
  val purpleBranchImpl: IPurpleStarBranch,
  /** 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [.mainBodyHouseImpl] 為占星實作 [MainBodyHouseAstroImpl] , 此值會被忽略  */
  /** 14主星、命、身，如何計算月令  */
  val mainStarsAlgo: MonthAlgo?,
  /** 月系星，如何計算月份  */
  /** 月系星，如何計算月令  */
  val monthStarsAlgo: MonthAlgo, val yearType: YearType,
  /** 宮位名字  */
  val houseSeqImpl: IHouseSeq,
  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  val tianyiImpl: TianyiIF, val fireBell: FireBell, val hurtAngel: HurtAngel,
  /** 四化設定  */
  val transFourImpl: ITransFour,
  /** 廟旺弱陷  */
  val strengthImpl: IStrength,
  /** 流年設定  */
  val flowYearImpl: IFlowYear,
  /** 流月設定  */
  val flowMonthImpl: IFlowMonth,
  /** 流日設定  */
  val flowDayImpl: IFlowDay,
  /** 流時設定  */
  val flowHourImpl: IFlowHour,

  /** 歲數註解 (西元？ 民國？ or others)  */
  val ageNoteImpls: List<IntAgeNote>,

  /** 大限計算方式  */
  val bigRangeImpl: IBigRange, val redBeauty: RedBeauty) : Serializable {


  /** 年系星系  */
  enum class YearType : Descriptive {
    YEAR_LUNAR, // 初一為界
    YEAR_SOLAR;
    // 立春為界

    override fun getTitle(locale: Locale): String {
      return try {
        ResourceBundle.getBundle(ZContext::class.java.name, locale).getString(name)
      } catch (e: MissingResourceException) {
        name
      }
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }


  /** [StarUnlucky.火星] ,  [StarUnlucky.鈴星] 設定  */
  enum class FireBell : Descriptive {
    FIREBELL_COLLECT, /** [StarUnlucky.fun火星_全集] , [StarUnlucky.fun鈴星_全集] : (年支、時支) -> 地支  */
    FIREBELL_BOOK;

    /** [StarUnlucky.fun火星_全書] , [StarUnlucky.fun鈴星_全書] : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮  */

    override fun getTitle(locale: Locale): String {
      return try {
        ResourceBundle.getBundle(ZContext::class.java.name, locale).getString(name)
      } catch (e: MissingResourceException) {
        name
      }

    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }

  /** [StarLucky.年馬] 或是 [StarLucky.月馬]  */
  enum class Horse {
    /** 年馬 (子由使用)  [StarLucky.fun年馬_年支]  */
    年馬,
    /** 月馬           [StarLucky.fun月馬_月數]  */
    月馬
  }


  /** [StarMinor.天傷]、 [StarMinor.天使] 計算方式  */
  enum class HurtAngel : Descriptive {
    /** 天傷固定於交友宮 [StarMinor.fun天傷_fixed交友] 、 天使固定疾厄宮 [StarMinor.fun天使_fixed疾厄]  */
    HURT_ANGEL_FIXED,

    /** 陽順陰逆 [StarMinor.fun天傷_陽順陰逆] 、 [StarMinor.fun天使_陽順陰逆]  */
    HURT_ANGEL_YINYANG;

    override fun getTitle(locale: Locale): String {
      return ResourceBundle.getBundle(ZContext::class.java.name, locale).getString(name)
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }

  /** 紅艷  */
  enum class RedBeauty : Descriptive {
    /** [StarMinor.fun紅艷_甲乙相同]  */
    RED_BEAUTY_DIFF,

    /** [StarMinor.fun紅艷_甲乙相異]  */
    RED_BEAUTY_SAME;

    override fun getTitle(locale: Locale): String {
      return ResourceBundle.getBundle(ZContext::class.java.name, locale).getString(name)
    }

    override fun getDescription(locale: Locale): String {
      return getTitle(locale)
    }
  }
}
