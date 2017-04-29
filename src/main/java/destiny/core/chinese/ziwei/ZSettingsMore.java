/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.HourLmtImpl;
import destiny.core.calendar.eightwords.HourSolarTransImpl;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.TianyiIF;

/**
 * 與紫微「計算」無關的設定
 * 例如
 * 是否顯示小限
 * 真太陽時(還是手錶平均時間)
 * 八字排列方向
 */
public class ZSettingsMore extends ZSettings {

  /** 宮干四化「自化」 顯示選項 */
  public enum SelfTransFour {
    NONE  ,   /** 不顯示 */
    TEXT  ,   /** 文字顯示 */
    ARROW ,   /** 箭頭朝外 */
  }
  private final SelfTransFour selfTransFour;

  /** 宮干四化「化入對宮」的顯示選項 */
  public enum OppoTransFour {
    NONE  ,   /** 不顯示 */
    ARROW ,   /** 朝內(對宮) 箭頭 */
  }
  private final OppoTransFour oppoTransFour;



  /** 是否顯示小限 */
  private final boolean showSmallRange;

  /** 八字排盤，右至左 or 左至右 */
  protected final Direction direction;

  /** 時辰劃分 */
  public enum HourChoose {
    TRUE_SOLAR ,  /** 真太陽時   {@link HourSolarTransImpl}  */
    LMT           /** 地方平均時 {@link HourLmtImpl} */
  }
  private final HourChoose hourChoose;

  /** 子正判定 */
  public enum Midnight {
    SOLAR_TRANS , /** 太陽過天底 (Natal)  */
    LMT           /** 當地手錶時間 */
  }
  protected final Midnight midnight;

  /** 子初換日 (true) 或 子正換日 (false) */
  private final boolean changeDayAfterZi;


  public ZSettingsMore(LeapPurple leapPurple, LeapMonth leapMonth, MonthType monthType, IMainHouse mainHouseImpl,
                       IHouseSeq houseSeqImpl, TianyiIF tianyiImpl, FireBell fireBell, Horse horse, HurtAngel hurtAngel,
                       ITransFour transFourImpl, IStrength strengthImpl, IFlowYear flowYearImpl, IFlowMonth flowMonthImpl,
                       IFlowDay flowDayImpl, IFlowHour flowHourImpl, FortuneOutput rangeOutput, IBigRange bigRangeImpl,
                       RedBeauty redBeauty, SelfTransFour selfTransFour, OppoTransFour oppoTransFour, boolean showSmallRange, Direction direction, HourChoose hourChoose, Midnight midnight, boolean changeDayAfterZi) {
    super(leapPurple, leapMonth, monthType, mainHouseImpl , houseSeqImpl, tianyiImpl, fireBell, horse, hurtAngel,
      transFourImpl, strengthImpl , flowYearImpl, flowMonthImpl, flowDayImpl, flowHourImpl, rangeOutput, bigRangeImpl, redBeauty);
    this.selfTransFour = selfTransFour;
    this.oppoTransFour = oppoTransFour;
    this.showSmallRange = showSmallRange;
    this.direction = direction;
    this.hourChoose = hourChoose;
    this.midnight = midnight;
    this.changeDayAfterZi = changeDayAfterZi;
  }


  public SelfTransFour getSelfTransFour() {
    return selfTransFour;
  }

  public OppoTransFour getOppoTransFour() {
    return oppoTransFour;
  }

  public boolean isShowSmallRange() {
    return showSmallRange;
  }

  public Direction getDirection() {
    return direction;
  }

  public HourChoose getHourChoose() {
    return hourChoose;
  }

  public Midnight getMidnight() {
    return midnight;
  }

  public boolean isChangeDayAfterZi() {
    return changeDayAfterZi;
  }
}
