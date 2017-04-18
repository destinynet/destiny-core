/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.impls.TianyiAuthorizedImpl;
import destiny.core.chinese.impls.TianyiLiurenPithyImpl;
import destiny.core.chinese.impls.TianyiOceanImpl;
import destiny.core.chinese.impls.TianyiZiweiBookImpl;

import java.io.Serializable;

public class Settings implements Serializable {



  /** 命宮起法 */
  public enum MainHouse {
    DEFAULT ,         /** 內定算法 */
    SOLAR_TERMS_DEP   /** 不依五星要過節 */
  }
  private final MainHouse mainHouse;

  /** 宮位名字 */
  public enum HouseSeq {
    DEFAULT ,   /** 內定 {@link HouseSeqDefaultImpl} */
    TAIYI       /** 太乙 {@link HouseSeqTaiyiImpl} */
  }
  private final HouseSeq houseSeq;

  /** {@link StarLucky#天魁} , {@link StarLucky#天鉞} (貴人) 算法 */
  public enum Tianyi {
    ZIWEI_BOOK,   /** 紫微斗數全書 {@link TianyiZiweiBookImpl} */
    AUTHORIZED ,  /** 協紀辨方書 {@link TianyiAuthorizedImpl} */
    OCEAN,        /** 淵海子平  {@link TianyiOceanImpl} */
    LIUREN_PITHY  /** 大六壬金口訣 {@link TianyiLiurenPithyImpl} */
  }
  private final Tianyi tianyi;

  /** {@link StarUnlucky#火星} ,  {@link StarUnlucky#鈴星} 設定 */
  public enum FireBell {
    全集,  // (年支、時支) -> 地支
    全書   // 年支 -> 地支
  }
  private final FireBell fireBell;

  /** {@link StarLucky#天馬} */
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
  public enum TransFour {
    DEFAULT ,   /** {@link TransFourDefaultImpl} */
    NORTH   ,   /** {@link TransFourNorthImpl} */
    SOUTH   ,   /** {@link TransFourSouthImpl} */
    MIDDLE  ,   /** {@link TransFourMiddleImpl} */
    DIVINE  ,   /** {@link TransFourDivineImpl} */
    ZIYUN   ,   /** {@link TransFourZiyunImpl} */
  }
  private final TransFour transFour;


  /** 流年設定 */
  public enum FlowYear {
    DEFAULT , /** {@link FlowYearDefaultImpl} */
    ANCHOR  , /** {@link FlowYearAnchorImpl} */
  }
  private final FlowYear flowYear;


  /** 流月設定 */
  public enum FlowMonth {
    DEFAULT   ,  /** {@link FlowMonthDefaultImpl} */
    FIXED     ,  /** {@link FlowMonthFixedImpl} */
    YEAR_DEP  ,  /** {@link FlowMonthYearMainHouseDepImpl} */
  }
  private final FlowMonth flowMonth;

  /** 流日設定 */
  public enum FlowDay {
    MONTH_DEP , /** {@link FlowDayFlowMonthMainHouseDepImpl} */
    FIXED     , /** {@link FlowDayFixedImpl} */
  }
  private final FlowDay flowDay;

  /** 流時設定 */
  public enum FlowHour {
    DAY_DEP , /** {@link FlowHourDayMainHouseDepImpl} */
    FIXED     /** {@link FlowHourFixedImpl} */
  }
  private final FlowHour flowHour;

  public Settings(MainHouse mainHouse, HouseSeq houseSeq, Tianyi tianyi, FireBell fireBell, Horse horse, HurtAngel hurtAngel, TransFour transFour, FlowYear flowYear, FlowMonth flowMonth, FlowDay flowDay, FlowHour flowHour) {
    this.mainHouse = mainHouse;
    this.houseSeq = houseSeq;
    this.tianyi = tianyi;
    this.fireBell = fireBell;
    this.horse = horse;
    this.hurtAngel = hurtAngel;
    this.transFour = transFour;
    this.flowYear = flowYear;
    this.flowMonth = flowMonth;
    this.flowDay = flowDay;
    this.flowHour = flowHour;
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
}
