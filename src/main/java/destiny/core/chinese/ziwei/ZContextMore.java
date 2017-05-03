/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.TianyiIF;

import java.util.*;

/**
 * 與紫微「計算」無關的設定
 * 例如
 * 是否顯示小限
 * 真太陽時(還是手錶平均時間)
 * 八字排列方向
 */
public class ZContextMore extends ZContext {

  private final String name;

  /** 宮干四化「自化」 顯示選項 */
  public enum SelfTransFour implements Descriptive {
    SELF_TRANS_FOUR_NONE,   /** 不顯示 */
    SELF_TRANS_FOUR_TEXT,   /** 文字顯示 */
    SELF_TRANS_FOUR_ARROW,;   /** 箭頭朝外 */

    @Override
    public String getTitle(Locale locale) {
      return ResourceBundle.getBundle(ZContextMore.class.getName(), locale).getString(name());
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final SelfTransFour selfTransFour;

  /** 宮干四化「化入對宮」的顯示選項 */
  public enum OppoTransFour implements Descriptive {
    OPPO_TRANS_FOUR_NONE,   /** 不顯示 */
    OPPO_TRANS_FOUR_ARROW,; /** 朝內(對宮) 箭頭 */

    @Override
    public String getTitle(Locale locale) {
      return ResourceBundle.getBundle(ZContextMore.class.getName(), locale).getString(name());
    }

    @Override
    public String getDescription(Locale locale) {
      return getTitle(locale);
    }
  }
  private final OppoTransFour oppoTransFour;

  /** 是否顯示小限 */
  private final boolean showSmallRange;

  /** 八字排盤，右至左 or 左至右 */
  protected final Direction direction;

  /** 時辰劃分 */
  protected final HourIF hourImpl;

  /** 子正判定 */
  protected final MidnightIF midnightImpl;

  /** 子初換日 (true) 或 子正換日 (false) */
  private final boolean changeDayAfterZi;

  /** 顯示雜曜 */
  private final boolean showMinors;

  /** 顯示博士12神煞 */
  private final boolean showDoctors;

  /** 顯示長生12神煞 */
  private final boolean showLongevity;


  public ZContextMore(IPurpleStarBranch purpleBranchImpl, LeapMonth leapMonth, MonthType monthType, IMainHouse mainHouseImpl, IHouseSeq houseSeqImpl, TianyiIF tianyiImpl, FireBell fireBell, HurtAngel hurtAngel, ITransFour transFourImpl, IStrength strengthImpl, IFlowYear flowYearImpl, IFlowMonth flowMonthImpl, IFlowDay flowDayImpl, IFlowHour flowHourImpl, FortuneOutput fortuneOutputput, IBigRange bigRangeImpl, RedBeauty redBeauty, String name, SelfTransFour selfTransFour, OppoTransFour oppoTransFour, boolean showSmallRange, Direction direction, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi, boolean showMinors, boolean showDoctors, boolean showLongevity) {
    super(purpleBranchImpl, leapMonth, monthType, mainHouseImpl , houseSeqImpl, tianyiImpl, fireBell, hurtAngel,
      transFourImpl, strengthImpl , flowYearImpl, flowMonthImpl, flowDayImpl, flowHourImpl, fortuneOutputput, bigRangeImpl, redBeauty);
    this.name = name;
    this.selfTransFour = selfTransFour;
    this.oppoTransFour = oppoTransFour;
    this.showSmallRange = showSmallRange;
    this.direction = direction;
    this.hourImpl = hourImpl;
    this.midnightImpl = midnightImpl;
    this.changeDayAfterZi = changeDayAfterZi;
    this.showMinors = showMinors;
    this.showDoctors = showDoctors;
    this.showLongevity = showLongevity;
  }

  public String getName() {
    return name;
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

  public HourIF getHourImpl() {
    return hourImpl;
  }

  public MidnightIF getMidnightImpl() {
    return midnightImpl;
  }

  public boolean isChangeDayAfterZi() {
    return changeDayAfterZi;
  }

  public boolean isShowMinors() {
    return showMinors;
  }

  public boolean isShowDoctors() {
    return showDoctors;
  }

  public boolean isShowLongevity() {
    return showLongevity;
  }

  public List<ZStar> getStars() {
    List<ZStar> starList = new ArrayList<>();
    starList.addAll(Arrays.asList(StarMain.values));
    starList.addAll(Arrays.asList(StarLucky.values));
    starList.addAll(Arrays.asList(StarUnlucky.values));
    if (showMinors)
      starList.addAll(Arrays.asList(StarMinor.values));

    if (showDoctors)
      starList.addAll(Arrays.asList(StarDoctor.values));

    if (showLongevity)
      starList.addAll(Arrays.asList(StarLongevity.values));
    return starList;
  }
}
