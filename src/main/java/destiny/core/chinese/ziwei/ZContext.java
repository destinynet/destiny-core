/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.TianyiIF;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 */
public class ZContext implements Serializable {

  /** 命宮、身宮 演算法 */
  private final IMainBodyHouse mainBodyHouseImpl;

  /** 紫微星，在閏月時，該如何處理 */
  protected final IPurpleStarBranch purpleBranchImpl;

  /** 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 {@link #mainBodyHouseImpl} 為占星實作 {@link MainBodyHouseAstroImpl} , 此值會被忽略 */
  @Nullable
  private final MonthAlgo mainStarsAlgo;


  /** 月系星，如何計算月份 */
  private final MonthAlgo monthStarsAlgo;


  /** 年系星系 */
  public enum YearType implements Descriptive {
    YEAR_LUNAR,   // 初一為界
    YEAR_SOLAR;   // 立春為界

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(ZContext.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final YearType yearType;


  /** 宮位名字 */
  protected final IHouseSeq houseSeqImpl;


  /** {@link StarLucky#天魁} , {@link StarLucky#天鉞} (貴人) 算法 */
  protected final TianyiIF tianyiImpl;


  /** {@link StarUnlucky#火星} ,  {@link StarUnlucky#鈴星} 設定 */
  public enum FireBell implements Descriptive {
    FIREBELL_COLLECT,  /** {@link StarUnlucky#fun火星_全集} , {@link StarUnlucky#fun鈴星_全集} : (年支、時支) -> 地支 */
    FIREBELL_BOOK;     /** {@link StarUnlucky#fun火星_全書} , {@link StarUnlucky#fun鈴星_全書} : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮 */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(ZContext.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final FireBell fireBell;

  /** {@link StarLucky#年馬} 或是 {@link StarLucky#月馬} */
  public enum Horse {
    年馬,  /** 年馬 (子由使用)  {@link StarLucky#fun年馬_年支} */
    月馬   /** 月馬           {@link StarLucky#fun月馬_月數} */
  }


  /** {@link StarMinor#天傷}、 {@link StarMinor#天使} 計算方式 */
  public enum HurtAngel implements Descriptive {
    HURT_ANGEL_FIXED,     /** 天傷固定於交友宮 {@link StarMinor#fun天傷_fixed交友} 、 天使固定疾厄宮 {@link StarMinor#fun天使_fixed疾厄} */
    HURT_ANGEL_YINYANG;   /** 陽順陰逆 {@link StarMinor#fun天傷_陽順陰逆} 、 {@link StarMinor#fun天使_陽順陰逆} */


    @Override
    public String getTitle(Locale locale) {
      return ResourceBundle.getBundle(ZContext.class.getName(), locale).getString(name());
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final HurtAngel hurtAngel;


  /** 四化設定 */
  protected final ITransFour transFourImpl;


  /** 廟旺弱陷 */
  protected final IStrength strengthImpl;


  /** 流年設定 */
  protected final IFlowYear flowYearImpl;


  /** 流月設定 */
  protected final IFlowMonth flowMonthImpl;

  /** 流日設定 */
  protected final IFlowDay flowDayImpl;


  /** 流時設定 */
  protected final IFlowHour flowHourImpl;


  /** 大限輸出格式 */
  private final FortuneOutput fortuneOutput;

  /** 大限計算方式 */
  protected final IBigRange bigRangeImpl;

  /** 紅艷 */
  public enum RedBeauty implements Descriptive {
    RED_BEAUTY_DIFF,  /** {@link StarMinor#fun紅艷_甲乙相同} */
    RED_BEAUTY_SAME,;  /** {@link StarMinor#fun紅艷_甲乙相異} */

    @Override
    public String getTitle(Locale locale) {
      return ResourceBundle.getBundle(ZContext.class.getName(), locale).getString(name());
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final RedBeauty redBeauty;

  public ZContext(IMainBodyHouse mainBodyHouseImpl, IPurpleStarBranch purpleBranchImpl, @Nullable MonthAlgo mainStarsAlgo, MonthAlgo monthStarsAlgo, YearType yearType, IHouseSeq houseSeqImpl, TianyiIF tianyiImpl, FireBell fireBell, HurtAngel hurtAngel, ITransFour transFourImpl, IStrength strengthImpl, IFlowYear flowYearImpl, IFlowMonth flowMonthImpl, IFlowDay flowDayImpl, IFlowHour flowHourImpl, FortuneOutput fortuneOutput, IBigRange bigRangeImpl, RedBeauty redBeauty) {
    this.mainBodyHouseImpl = mainBodyHouseImpl;
    this.purpleBranchImpl = purpleBranchImpl;
    this.mainStarsAlgo = mainStarsAlgo;
    this.monthStarsAlgo = monthStarsAlgo;
    this.yearType = yearType;
    this.houseSeqImpl = houseSeqImpl;
    this.tianyiImpl = tianyiImpl;
    this.fireBell = fireBell;
    this.hurtAngel = hurtAngel;
    this.transFourImpl = transFourImpl;
    this.strengthImpl = strengthImpl;
    this.flowYearImpl = flowYearImpl;
    this.flowMonthImpl = flowMonthImpl;
    this.flowDayImpl = flowDayImpl;
    this.flowHourImpl = flowHourImpl;
    this.fortuneOutput = fortuneOutput;
    this.bigRangeImpl = bigRangeImpl;
    this.redBeauty = redBeauty;
  }

  /** 命宮、身宮 演算法 */
  public IMainBodyHouse getMainBodyHouseImpl() {
    return mainBodyHouseImpl;
  }

  public IPurpleStarBranch getPurpleBranchImpl() {
    return purpleBranchImpl;
  }

  /** 14主星、命、身，如何計算月令 */
  @Nullable
  public MonthAlgo getMainStarsAlgo() {
    return mainStarsAlgo;
  }

  /** 月系星，如何計算月令 */
  public MonthAlgo getMonthStarsAlgo() {
    return monthStarsAlgo;
  }

  public YearType getYearType() {
    return yearType;
  }

  public IHouseSeq getHouseSeqImpl() {
    return houseSeqImpl;
  }


  public TianyiIF getTianyiImpl() {
    return tianyiImpl;
  }

  public ITransFour getTransFourImpl() {
    return transFourImpl;
  }

  public IStrength getStrengthImpl() {
    return strengthImpl;
  }

  public IFlowYear getFlowYearImpl() {
    return flowYearImpl;
  }

  public IFlowMonth getFlowMonthImpl() {
    return flowMonthImpl;
  }

  public IFlowDay getFlowDayImpl() {
    return flowDayImpl;
  }

  public IFlowHour getFlowHourImpl() {
    return flowHourImpl;
  }

  public FortuneOutput getFortuneOutput() {
    return fortuneOutput;
  }


  public IBigRange getBigRangeImpl() {
    return bigRangeImpl;
  }

  public FireBell getFireBell() {
    return fireBell;
  }

  public HurtAngel getHurtAngel() {
    return hurtAngel;
  }

  public RedBeauty getRedBeauty() {
    return redBeauty;
  }


}
