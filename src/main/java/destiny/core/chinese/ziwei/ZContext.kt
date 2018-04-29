/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.IntAgeNote
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.chinese.ITianyi
import java.io.Serializable

/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 */
open class ZContext(
  /** 命宮、身宮 演算法  */
  override val mainBodyHouseImpl: IMainBodyHouse,

  /** 紫微星，在閏月時，該如何處理  */
  override val purpleBranchImpl: IPurpleStarBranch,

  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouseImpl] 為占星實作 [MainBodyHouseAstroImpl] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  override val mainStarsAlgo: MonthAlgo?,

  /** 月系星，如何計算月令  */
  override val monthStarsAlgo: MonthAlgo,

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  override val yearType: YearType,

  /** 宮位名字  */
  override val houseSeqImpl: IHouseSeq,

  /** 天乙貴人 算法 , 影響 [StarLucky.天魁] , [StarLucky.天鉞]  */
  override val tianyiImpl: ITianyi,

  override val fireBell: FireBell,

  override val hurtAngel: HurtAngel,

  /** 四化設定  */
  override val transFourImpl: ITransFour,

  /** 廟旺弱陷  */
  override val strengthImpl: IStrength,

  /** 流年設定  */
  override val flowYearImpl: IFlowYear,

  /** 流月設定  */
  override val flowMonthImpl: IFlowMonth,

  /** 流日設定  */
  override val flowDayImpl: IFlowDay,

  /** 流時設定  */
  override val flowHourImpl: IFlowHour,

  /** 歲數註解 (西元？ 民國？ or others)  */
  override val ageNoteImpls: List<IntAgeNote>,

  /** 大限計算方式  */
  override val bigRangeImpl: IBigRange,

  /** 紅豔 */
  override val redBeauty: RedBeauty) : IZiweiContext , Serializable
