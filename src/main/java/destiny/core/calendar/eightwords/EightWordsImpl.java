/**
 * Created by smallufo on 2015-06-21.
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EightWordsImpl implements EightWordsIF , Serializable {

  protected YearMonthIF yearMonthImpl;          // 換年, 以及月支計算的實作
  protected DayIF       dayImpl;                // 計算日干支的介面
  protected HourIF      hourImpl;               // 計算時支的介面

  protected MidnightIF midnightImpl;            // 計算「子正」的介面
  protected boolean    changeDayAfterZi = true; // 子初是否換日，內定是：true (換日)

  public EightWordsImpl(YearMonthIF yearMonthImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi) {
    this.yearMonthImpl = yearMonthImpl;
    this.dayImpl = dayImpl;
    this.hourImpl = hourImpl;
    this.midnightImpl = midnightImpl;
    this.changeDayAfterZi = changeDayAfterZi;
  }

  /**
   * 計算八字 , 不用轉換，直接以 LMT 來計算即可！
   */
  @NotNull
  @Override
  public EightWords getEightWords(LocalDateTime lmt, Location location) {
    /*
    //校正 Location 的 GMT 時差
    TimeZone tz = location.getTimeZone();
    GregorianCalendar cal = new GregorianCalendar(lmt.getYear() , lmt.getMonth()-1 , lmt.getDay() , lmt.getHour() , lmt.getMinute() , (int)lmt.getSecond());
    double secondOffset= (tz.getOffset(cal.getTimeInMillis())-tz.getRawOffset()) /1000.0;
    location.setMinuteOffset((int) (secondOffset / 60));
    */

    StemBranch year = yearMonthImpl.getYear(lmt, location);
    StemBranch month = yearMonthImpl.getMonth(lmt, location);
    StemBranch day =dayImpl.getDay(lmt, location, midnightImpl, hourImpl, changeDayAfterZi);
    Stem 臨時日干 = day.getStem();
    Branch 時支 = this.hourImpl.getHour(lmt, location);

    Stem 時干;

    LocalDateTime nextZi = hourImpl.getLmtNextStartOf(lmt, location, Branch.子);

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
      if (day != dayImpl.getDay(nextZi, location, midnightImpl, hourImpl, changeDayAfterZi))
        臨時日干 = Stem.get(臨時日干.getIndex() + 1);
    }

    switch (Stem.getIndex(臨時日干)) {
      case 0:
      case 5:
        時干 = Stem.get(Branch.getIndex(時支));
        break;
      case 1:
      case 6:
        時干 = Stem.get(Branch.getIndex(時支) + 2);
        break;
      case 2:
      case 7:
        時干 = Stem.get(Branch.getIndex(時支) + 4);
        break;
      case 3:
      case 8:
        時干 = Stem.get(Branch.getIndex(時支) + 6);
        break;
      case 4:
      case 9:
        時干 = Stem.get(Branch.getIndex(時支) + 8);
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
