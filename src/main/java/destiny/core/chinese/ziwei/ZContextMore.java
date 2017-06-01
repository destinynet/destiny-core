/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Descriptive;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.Direction;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.chinese.FortuneOutput;
import destiny.core.chinese.TianyiIF;

import java.util.*;

/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 *
 * 另外附加 與紫微「計算」無關的設定
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

  /** 民用曆法 or 天文曆法 */
  protected final ChineseDateIF chineseDateImpl;

  /** 是否顯示八字盤 */
  protected final boolean showEightWords;

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


  public ZContextMore(IPurpleStarBranch purpleBranchImpl, LeapMonth leapMonth, YearType yearType, MonthType monthType, IMainHouse mainHouseImpl, IHouseSeq houseSeqImpl, TianyiIF tianyiImpl, FireBell fireBell, HurtAngel hurtAngel, ITransFour transFourImpl, IStrength strengthImpl, IFlowYear flowYearImpl, IFlowMonth flowMonthImpl, IFlowDay flowDayImpl, IFlowHour flowHourImpl, FortuneOutput fortuneOutput, IBigRange bigRangeImpl, RedBeauty redBeauty, String name, SelfTransFour selfTransFour, OppoTransFour oppoTransFour, boolean showSmallRange, ChineseDateIF chineseDateImpl, boolean showEightWords, Direction direction, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi, boolean showMinors, boolean showDoctors, boolean showLongevity) {
    super(purpleBranchImpl, leapMonth, yearType, monthType, mainHouseImpl , houseSeqImpl, tianyiImpl, fireBell, hurtAngel,
      transFourImpl, strengthImpl , flowYearImpl, flowMonthImpl, flowDayImpl, flowHourImpl, fortuneOutput, bigRangeImpl, redBeauty);
    this.name = name;
    this.selfTransFour = selfTransFour;
    this.oppoTransFour = oppoTransFour;
    this.showSmallRange = showSmallRange;
    this.chineseDateImpl = chineseDateImpl;
    this.showEightWords = showEightWords;
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

  public boolean isShowEightWords() {
    return showEightWords;
  }

  public Direction getDirection() {
    return direction;
  }

  public HourIF getHourImpl() {
    return hourImpl;
  }

  public ChineseDateIF getChineseDateImpl() {
    return chineseDateImpl;
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


  @Override
  public String toString() {
    return "[ZContextMore " + "purpleBranchImpl=" + purpleBranchImpl + ", name='" + name + '\'' + ", selfTransFour=" + selfTransFour + ", oppoTransFour=" + oppoTransFour + ", showSmallRange=" + showSmallRange + ", mainHouseImpl=" + mainHouseImpl + ", direction=" + direction + ", houseSeqImpl=" + houseSeqImpl + ", hourImpl=" + hourImpl + ", midnightImpl=" + midnightImpl + ", tianyiImpl=" + tianyiImpl + ", changeDayAfterZi=" + changeDayAfterZi + ", showMinors=" + showMinors + ", showDoctors=" + showDoctors + ", showLongevity=" + showLongevity + ", transFourImpl=" + transFourImpl + ", strengthImpl=" + strengthImpl + ", flowYearImpl=" + flowYearImpl + ", flowMonthImpl=" + flowMonthImpl + ", flowDayImpl=" + flowDayImpl + ", flowHourImpl=" + flowHourImpl + ", bigRangeImpl=" + bigRangeImpl + ']';
  }
}
