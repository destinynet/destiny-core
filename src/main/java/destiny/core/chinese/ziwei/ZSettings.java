/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.TianyiIF;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ZSettings implements Serializable {


  /** 紫微星，在閏月時，該如何處理 */
  protected final IPurpleStarBranch purpleBranchImpl;

  /** 閏月該如何處理 */
  public enum LeapMonth implements Descriptive {
    LEAP_THIS_MONTH,   // 一律當作本月
    LEAP_NEXT_MONTH,   // 一律當作下月 (全書)
    LEAP_SPLIT_15;   // 15日(含)之前當本月，之後當下月

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(ZSettings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final LeapMonth leapMonth;


  public enum MonthType implements Descriptive {
    MONTH_LUNAR,   // 陰曆盤
    MONTH_SOLAR;   // 節氣盤

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(ZSettings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final MonthType monthType;



  /** 命宮起法 */
  protected final IMainHouse mainHouseImpl;


  /** 宮位名字 */
  protected final IHouseSeq houseSeqImpl;


  /** {@link StarLucky#天魁} , {@link StarLucky#天鉞} (貴人) 算法 */
  protected final TianyiIF tianyiImpl;


  /** {@link StarUnlucky#火星} ,  {@link StarUnlucky#鈴星} 設定 */
  public enum FireBell implements Descriptive {
    FIREBELL_全集,  /** {@link StarUnlucky#fun火星_全集} , {@link StarUnlucky#fun鈴星_全集} : (年支、時支) -> 地支 */
    FIREBELL_全書;   /** {@link StarUnlucky#fun火星_全書} , {@link StarUnlucky#fun鈴星_全書} : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮 */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(ZSettings.class.getName(), locale).getString(name());
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
  private final Horse horse;


  /** {@link StarMinor#天傷}、 {@link StarMinor#天使} 計算方式 */
  public enum HurtAngel {
    FIXED,    /** 天傷固定於交友宮 {@link StarMinor#fun天傷_fixed交友} 、 天使固定疾厄宮 {@link StarMinor#fun天使_fixed疾厄} */
    YINYANG   /** 陽順陰逆 {@link StarMinor#fun天傷_陽順陰逆} 、 {@link StarMinor#fun天使_陽順陰逆} */
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
  private final FortuneOutput rangeOutput;

  /** 大限計算方式 */
  protected final IBigRange bigRangeImpl;


  /** TODO : 紅艷 */
  public enum RedBeauty {
    甲乙相異 ,  /** {@link StarMinor#fun紅艷_甲乙相同} */
    甲乙相同 ,  /** {@link StarMinor#fun紅艷_甲乙相異} */
  }
  private final RedBeauty redBeauty;

  public ZSettings(IPurpleStarBranch purpleBranchImpl, LeapMonth leapMonth, MonthType monthType, IMainHouse mainHouseImpl, IHouseSeq houseSeqImpl, TianyiIF tianyiImpl, FireBell fireBell, Horse horse, HurtAngel hurtAngel, ITransFour transFourImpl, IStrength strengthImpl, IFlowYear flowYearImpl, IFlowMonth flowMonthImpl, IFlowDay flowDayImpl, IFlowHour flowHourImpl, FortuneOutput rangeOutput, IBigRange bigRangeImpl, RedBeauty redBeauty) {
    this.purpleBranchImpl = purpleBranchImpl;
    this.leapMonth = leapMonth;
    this.monthType = monthType;
    this.mainHouseImpl = mainHouseImpl;
    this.houseSeqImpl = houseSeqImpl;
    this.tianyiImpl = tianyiImpl;
    this.fireBell = fireBell;
    this.horse = horse;
    this.hurtAngel = hurtAngel;
    this.transFourImpl = transFourImpl;
    this.strengthImpl = strengthImpl;
    this.flowYearImpl = flowYearImpl;
    this.flowMonthImpl = flowMonthImpl;
    this.flowDayImpl = flowDayImpl;
    this.flowHourImpl = flowHourImpl;
    this.rangeOutput = rangeOutput;
    this.bigRangeImpl = bigRangeImpl;
    this.redBeauty = redBeauty;
  }

  public IPurpleStarBranch getPurpleBranchImpl() {
    return purpleBranchImpl;
  }

  public LeapMonth getLeapMonth() {
    return leapMonth;
  }

  public MonthType getMonthType() {
    return monthType;
  }


  public IMainHouse getMainHouseImpl() {
    return mainHouseImpl;
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

  public FortuneOutput getRangeOutput() {
    return rangeOutput;
  }


  public IBigRange getBigRangeImpl() {
    return bigRangeImpl;
  }



  public FireBell getFireBell() {
    return fireBell;
  }

  public Horse getHorse() {
    return horse;
  }

  public HurtAngel getHurtAngel() {
    return hurtAngel;
  }

  public RedBeauty getRedBeauty() {
    return redBeauty;
  }


}
