/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.calendar.eightwords.Direction;
import destiny.core.chinese.impls.TianyiAuthorizedImpl;
import destiny.core.chinese.impls.TianyiLiurenPithyImpl;
import destiny.core.chinese.impls.TianyiOceanImpl;
import destiny.core.chinese.impls.TianyiZiweiBookImpl;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Settings implements Serializable {

  /** 閏月該如何處理 */
  public enum LeapMonth implements Descriptive {
    LEAP_THIS_MONTH,   // 一律當作本月
    LEAP_NEXT_MONTH,   // 一律當作下月
    LEAP_SPLIT_15;   // 15日(含)之前當本月，之後當下月

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
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
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
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
  public enum MainHouse implements Descriptive {
    MAIN_HOUSE_DEFAULT,   /** 內定算法       {@link MainHouseDefaultImpl} */
    MAIN_HOUSE_SOLAR;     /** 不依五星要過節  {@link MainHouseSolarTermsImpl} */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final MainHouse mainHouse;

  /** 宮位名字 */
  public enum HouseSeq implements Descriptive {
    HOUSE_DEFAULT,   /** 內定 {@link HouseSeqDefaultImpl} */
    HOUSE_TAIYI,     /** 太乙 {@link HouseSeqTaiyiImpl} */
    HOUSE_ASTRO;     /** 星宗 {@link HouseSeqAstroImpl} */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final HouseSeq houseSeq;

  /** {@link StarLucky#天魁} , {@link StarLucky#天鉞} (貴人) 算法 */
  public enum Tianyi implements Descriptive {
    TIANYI_ZIWEI_BOOK,   /** 紫微斗數全書 {@link TianyiZiweiBookImpl} */
    TIANYI_AUTHORIZED,   /** 協紀辨方書 {@link TianyiAuthorizedImpl} */
    TIANYI_OCEAN,        /** 淵海子平  {@link TianyiOceanImpl} */
    TIANYI_LIUREN_PITHY; /** 大六壬金口訣 {@link TianyiLiurenPithyImpl} */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return null;
    }
  }
  private final Tianyi tianyi;

  /** {@link StarUnlucky#火星} ,  {@link StarUnlucky#鈴星} 設定 */
  public enum FireBell implements Descriptive {
    FIREBELL_全集,  /** {@link StarUnlucky#fun火星_全集} , {@link StarUnlucky#fun鈴星_全集} : (年支、時支) -> 地支 */
    FIREBELL_全書;   /** {@link StarUnlucky#fun火星_全書} , {@link StarUnlucky#fun鈴星_全書} : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮 */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
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
  public enum TransFour implements Descriptive {
    TRANSFOUR_DEFAULT,   /** {@link TransFourDefaultImpl} */
    TRANSFOUR_NORTH,   /** {@link TransFourNorthImpl} */
    TRANSFOUR_SOUTH,   /** {@link TransFourSouthImpl} */
    TRANSFOUR_MIDDLE,   /** {@link TransFourMiddleImpl} */
    TRANSFOUR_DIVINE,   /** {@link TransFourDivineImpl} */
    TRANSFOUR_ZIYUN,;   /** {@link TransFourZiyunImpl} */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final TransFour transFour;


  /** 廟旺弱陷 */
  public enum Strength implements Descriptive {
    STRENGTH_MIDDLE,    /** {@link StrengthMiddleImpl} 中州派 */
    STRENGTH_NORTH;     /** {@link StrengthNorthImpl} 北派 */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final Strength strength;


  /** 流年設定 */
  public enum FlowYear implements Descriptive {
    FLOW_YEAR_BRANCH, /** {@link FlowYearBranchImpl} : 流年地支 */
    FLOW_YEAR_ANCHOR,; /** {@link FlowYearAnchorImpl} : 流年斗君 */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final FlowYear flowYear;


  /** 流月設定 */
  public enum FlowMonth implements Descriptive {
    FLOW_MONTH_DEFAULT,   /** {@link FlowMonthDefaultImpl}           : 流年斗君 順數月  */
    FLOW_MONTH_FIXED,     /** {@link FlowMonthFixedImpl}             : 流月地支        */
    FLOW_MONTH_YEAR_DEP,; /** {@link FlowMonthYearMainHouseDepImpl}  : 流年命宮，順數月  */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final FlowMonth flowMonth;

  /** 流日設定 */
  public enum FlowDay implements Descriptive {
    FLOW_DAY_MONTH_DEP, /** {@link FlowDayFlowMonthMainHouseDepImpl} : 流月命宮，順數日 */
    FLOW_DAY_FIXED,;    /** {@link FlowDayBranchImpl}                : 流日地支       */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final FlowDay flowDay;

  /** 流時設定 */
  public enum FlowHour implements Descriptive {
    FLOW_HOUR_DAY_DEP, /** {@link FlowHourDayMainHouseDepImpl}   : 流日命宮，順數時 */
    FLOW_HOUR_FIXED;   /** {@link FlowHourBranchImpl}            : 流時地支       */

    @Override
    public String getTitle(Locale locale) {
      try {
        return ResourceBundle.getBundle(Settings.class.getName(), locale).getString(name());
      } catch (MissingResourceException e) {
        return name();
      }
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final FlowHour flowHour;

  /** 八字排盤，右至左 or 左至右 */
  private final Direction direction;

  public Settings(LeapMonth leapMonth, MonthType monthType, MainHouse mainHouse, HouseSeq houseSeq, Tianyi tianyi, FireBell fireBell, Horse horse, HurtAngel hurtAngel, TransFour transFour, Strength strength, FlowYear flowYear, FlowMonth flowMonth, FlowDay flowDay, FlowHour flowHour, Direction direction) {
    this.leapMonth = leapMonth;
    this.monthType = monthType;
    this.mainHouse = mainHouse;
    this.houseSeq = houseSeq;
    this.tianyi = tianyi;
    this.fireBell = fireBell;
    this.horse = horse;
    this.hurtAngel = hurtAngel;
    this.transFour = transFour;
    this.strength = strength;
    this.flowYear = flowYear;
    this.flowMonth = flowMonth;
    this.flowDay = flowDay;
    this.flowHour = flowHour;
    this.direction = direction;
  }

  public LeapMonth getLeapMonth() {
    return leapMonth;
  }

  public MonthType getMonthType() {
    return monthType;
  }

  public MainHouse getMainHouse() {
    return mainHouse;
  }

  public HouseSeq getHouseSeq() {
    return houseSeq;
  }

  public Tianyi getTianyi() {
    return tianyi;
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

  public TransFour getTransFour() {
    return transFour;
  }

  public Strength getStrength() {
    return strength;
  }

  public FlowYear getFlowYear() {
    return flowYear;
  }

  public FlowMonth getFlowMonth() {
    return flowMonth;
  }

  public FlowDay getFlowDay() {
    return flowDay;
  }

  public FlowHour getFlowHour() {
    return flowHour;
  }

  public Direction getDirection() {
    return direction;
  }
}
