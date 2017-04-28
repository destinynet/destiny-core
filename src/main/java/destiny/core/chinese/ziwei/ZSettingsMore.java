/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.HourLmtImpl;
import destiny.core.calendar.eightwords.HourSolarTransImpl;
import destiny.core.chinese.FortuneOutput;

/**
 * 與紫微「計算」無關的設定
 * 例如
 * 是否顯示小限
 * 真太陽時(還是手錶平均時間)
 * 八字排列方向
 */
public class ZSettingsMore extends ZSettings {

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


  public ZSettingsMore(LeapPurple leapPurple, LeapMonth leapMonth, MonthType monthType, MainHouse mainHouse, HouseSeq houseSeq, Tianyi tianyi, FireBell fireBell, Horse horse, HurtAngel hurtAngel, TransFour transFour, Strength strength, FlowYear flowYear, FlowMonth flowMonth, FlowDay flowDay, FlowHour flowHour, FortuneOutput rangeOutput, BigRange bigRange, RedBeauty redBeauty, boolean showSmallRange, Direction direction, HourChoose hourChoose, Midnight midnight, boolean changeDayAfterZi) {
    super(leapPurple, leapMonth, monthType, mainHouse, houseSeq, tianyi, fireBell, horse, hurtAngel, transFour, strength, flowYear, flowMonth, flowDay, flowHour, rangeOutput, bigRange, redBeauty);
    this.showSmallRange = showSmallRange;
    this.direction = direction;
    this.hourChoose = hourChoose;
    this.midnight = midnight;
    this.changeDayAfterZi = changeDayAfterZi;
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
