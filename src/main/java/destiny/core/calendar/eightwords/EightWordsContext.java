package destiny.core.calendar.eightwords;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.StemBranchUtils;

import java.time.LocalDateTime;

/**
 * 除了計算八字，另外新增輸出農曆以及命宮的方法
 */
public class EightWordsContext extends EightWordsImpl {

  protected final ChineseDateIF chineseDateImpl;      // 農曆計算

  protected final RisingSignIF risingSignImpl;        // 命宮 (上升星座)

  protected final StarPositionIF starPositionImpl;    // 星體位置

  public EightWordsContext(ChineseDateIF chineseDateImpl, YearMonthIF yearMonth, DayIF day, HourIF hour, MidnightIF midnight, boolean changeDayAfterZi, RisingSignIF risingSignImpl, StarPositionIF starPositionImpl) {
    super(yearMonth, day, hour, midnight, changeDayAfterZi);
    this.chineseDateImpl = chineseDateImpl;
    this.starPositionImpl = starPositionImpl;
    this.yearMonthImpl = yearMonth;
    this.risingSignImpl = risingSignImpl;
  }


  /** 取得農曆 */
  public ChineseDate getChineseDate(LocalDateTime lmt, Location location) {
    return chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl, midnightImpl, isChangeDayAfterZi());
  }

  /** 承上 , time 版本 */
  public ChineseDate getChineseDate(Time lmt, Location location) {
    return getChineseDate(lmt.toLocalDateTime() , location);
  }

  /**
   * 取得命宮的計算方法
   */
  public RisingSignIF getRisingSignImpl() {
    return risingSignImpl;
  }

  /** 取得陰陽曆轉換的實作 */
  public ChineseDateIF getChineseDateImpl() {
    return chineseDateImpl;
  }

  /**
   * 計算命宮干支
   */
  public StemBranch getRisingStemBranch(LocalDateTime lmt, Location location) {
    EightWords ew = getEightWords(lmt, location);
    // 命宮地支
    Branch risingBranch = risingSignImpl.getRisingSign(lmt, location , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC).getBranch();
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    Stem risingStem = StemBranchUtils.getMonthStem(ew.getYearStem(), risingBranch);
    // 組合成干支
    return StemBranch.get(risingStem, risingBranch);
  }

  public Branch getBranchOf(Star star , LocalDateTime lmt , Location location) {
    Position pos = starPositionImpl.getPosition(star , lmt , location , Centric.GEO ,Coordinate.ECLIPTIC);
    return ZodiacSign.getZodiacSign(pos.getLongitude()).getBranch();
  }

}
