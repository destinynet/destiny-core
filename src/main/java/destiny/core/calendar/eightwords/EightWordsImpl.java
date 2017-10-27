/**
 * Created by smallufo on 2015-06-21.
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.function.Function;

import static destiny.core.chinese.Branch.*;

public class EightWordsImpl implements EightWordsIF , Serializable {

  protected final YearMonthIF yearMonthImpl;          // 換年, 以及月支計算的實作
  protected final DayIF       dayImpl;                // 計算日干支的介面
  protected final HourIF      hourImpl;               // 計算時支的介面

  protected final MidnightIF midnightImpl;            // 計算「子正」的介面
  boolean    changeDayAfterZi = true; // 子初是否換日，內定是：true (換日)

  private final static Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  private Logger logger = LoggerFactory.getLogger(getClass());

  public EightWordsImpl(@NotNull YearMonthIF yearMonthImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi) {
    this.yearMonthImpl = yearMonthImpl;
    this.dayImpl = dayImpl;
    this.hourImpl = hourImpl;
    this.midnightImpl = midnightImpl;
    this.changeDayAfterZi = changeDayAfterZi;
  }


  @NotNull
  @Override
  public EightWords getEightWords(double gmtJulDay, Location loc) {
    StemBranch year = yearMonthImpl.getYear(gmtJulDay , loc);

    StemBranch month = yearMonthImpl.getMonth(gmtJulDay, loc);
    StemBranch day =dayImpl.getDay(gmtJulDay, loc, midnightImpl, hourImpl, changeDayAfterZi);
    Stem 臨時日干 = day.getStem();
    Branch 時支 = this.hourImpl.getHour(gmtJulDay, loc);

    Stem 時干;

    ChronoLocalDateTime lmt = TimeTools.getLmtFromGmt(gmtJulDay , loc , revJulDayFunc);

    ChronoLocalDateTime nextZi = hourImpl.getLmtNextStartOf(lmt, loc, 子 , revJulDayFunc);

    // 如果「子正」才換日
    if (!changeDayAfterZi) {
      /**
       * <pre>
       *  而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
       *  第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
       *  第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
       *
       *  子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
       *    |------------------|--------------------|--------------------|------------------|
       * </pre>
       */
      if (day != dayImpl.getDay(nextZi, loc, midnightImpl, hourImpl, changeDayAfterZi))
        臨時日干 = Stem.get(臨時日干.getIndex() + 1);
    }

    switch (Stem.getIndex(臨時日干)) {
      case 0:
      case 5:
        時干 = Stem.get(getIndex(時支));
        break;
      case 1:
      case 6:
        時干 = Stem.get(getIndex(時支) + 2);
        break;
      case 2:
      case 7:
        時干 = Stem.get(getIndex(時支) + 4);
        break;
      case 3:
      case 8:
        時干 = Stem.get(getIndex(時支) + 6);
        break;
      case 4:
      case 9:
        時干 = Stem.get(getIndex(時支) + 8);
        break;
      default:
        throw new AssertionError("Error");
    }
    return new EightWords(year , month , day , StemBranch.get(時干, 時支) );
  }

  /**
   * 計算八字 , 不用轉換，直接以 LMT 來計算即可！
   */
  @NotNull
  @Override
  public EightWords getEightWords(ChronoLocalDateTime lmt, Location loc) {

    StemBranch year = yearMonthImpl.getYear(lmt, loc);
    StemBranch month = yearMonthImpl.getMonth(lmt, loc);
    StemBranch day =dayImpl.getDay(lmt, loc, midnightImpl, hourImpl, changeDayAfterZi);
    Stem 臨時日干 = day.getStem();
    Branch 時支 = this.hourImpl.getHour(lmt, loc);

    Stem 時干;

    ChronoLocalDateTime nextZi = hourImpl.getLmtNextStartOf(lmt, loc, 子 , revJulDayFunc);

    // 如果「子正」才換日
    if (!changeDayAfterZi) {
      /**
       * <pre>
       *  而且 LMT 的八字日柱 不同於 下一個子初的八字日柱 發生情況有兩種：
       *  第一： LMT 零時 > 子正 > LMT > 子初 ,（即下圖之 LMT1)
       *  第二： 子正 > LMT > LMT 零時 (> 子初) , （即下圖之 LMT3)
       *
       *  子末(通常1)  LMT4    子正      LMT3       0|24     LMT2        子正    LMT1    子初（通常23)
       *    |------------------|--------------------|--------------------|------------------|
       * </pre>
       */
      if (day != dayImpl.getDay(nextZi, loc, midnightImpl, hourImpl, changeDayAfterZi))
        臨時日干 = Stem.get(臨時日干.getIndex() + 1);
    }

    switch (Stem.getIndex(臨時日干)) {
      case 0:
      case 5:
        時干 = Stem.get(getIndex(時支));
        break;
      case 1:
      case 6:
        時干 = Stem.get(getIndex(時支) + 2);
        break;
      case 2:
      case 7:
        時干 = Stem.get(getIndex(時支) + 4);
        break;
      case 3:
      case 8:
        時干 = Stem.get(getIndex(時支) + 6);
        break;
      case 4:
      case 9:
        時干 = Stem.get(getIndex(時支) + 8);
        break;
      default:
        throw new AssertionError("Error");
    }
    return new EightWords(year , month , day , StemBranch.get(時干, 時支) );
  }

  public YearMonthIF getYearMonthImpl() {
    return yearMonthImpl;
  }

  public DayIF getDayImpl() {
    return dayImpl;
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
}
