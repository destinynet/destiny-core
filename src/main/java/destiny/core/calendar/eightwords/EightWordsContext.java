package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.StemBranchUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author smallufo
 * TODO : 當地時間/轉換成東經120度 排八字
 */
public class EightWordsContext implements EightWordsIF , Serializable
{
  private ChineseDateIF chineseDateImpl;      // 農曆計算
  private YearMonthIF yearMonthImpl;          // 換年, 以及月支計算的實作
  private DayIF       dayImpl;                // 計算日干支的介面
  private HourIF      hourImpl;               // 計算時支的介面

  private MidnightIF midnightImpl;            // 計算「子正」的介面
  private boolean    changeDayAfterZi = true; // 子初是否換日，內定是：true (換日)

  private RisingSignIF risingSignImpl;        // 命宮

  public EightWordsContext(ChineseDateIF chineseDateImpl, YearMonthIF yearMonth, DayIF day, HourIF hour, MidnightIF midnight, boolean changeDayAfterZi, RisingSignIF risingSignImpl)
  {
    this.chineseDateImpl = chineseDateImpl;
    this.yearMonthImpl = yearMonth;
    this.dayImpl = day;
    this.hourImpl = hour;
    this.midnightImpl = midnight;
    this.changeDayAfterZi = changeDayAfterZi;
    this.risingSignImpl = risingSignImpl;
  }

  /** 子初是否換日 */
  public boolean isChangeDayAfterZi()
  {
    return changeDayAfterZi;
  }

  /** 取得農曆 */
  public ChineseDate getChineseDate(Time lmt ,Location location) {
    return chineseDateImpl.getChineseDate(lmt , location, dayImpl , hourImpl , midnightImpl , changeDayAfterZi);
  }


  /**
   * 計算八字 , 不用轉換，直接以 LMT 來計算即可！
   * TODO : 當地時間是否轉換成中原時間
   */
  @NotNull
  @Override
  public EightWords getEightWords(Time lmt, Location location)
  {
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

    Time nextZi = hourImpl.getLmtNextStartOf(lmt, location, Branch.子);

    /** 如果「子正」才換日 */
    if (!changeDayAfterZi)
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

    switch (Stem.getIndex(臨時日干))
    {
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
  
  /**
   * 設定年月的實作
   */
  public void setYearMonthImpl(YearMonthIF impl)
  {
    this.yearMonthImpl = impl;
  }
  
  /**
   * @return 取得年月的實作
   */
  public YearMonthIF getYearMonthImpl()
  {
    return this.yearMonthImpl;
  }

  /**
   * 設定換日柱的方法
   */
  public void setDayImpl(DayIF impl)
  {
    this.dayImpl = impl;
  }

  /** 取得日柱的實作 */
  public DayIF getDayImpl()
  {
    return this.dayImpl;
  }
  
  /**
   * 設定換時柱的方法
   */
  public void setHourImpl(HourIF impl)
  {
    this.hourImpl = impl;
  }

  /**
   * 取得子正 (MidnightIF) 的實作方法
   */
  public MidnightIF getMidnightImpl()
  {
    return midnightImpl;
  }

  /**
   * 取得時辰劃分 (HourIF) 的實作方法
   */
  public HourIF getHourImpl()
  {
    return hourImpl;
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
  public StemBranch getRisingStemBranch(Time lmt , Location location) {
    EightWords ew = getEightWords(lmt , location);
    // 命宮地支
    Branch risingBranch = risingSignImpl.getRisingSign(lmt , location).getBranch();
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    Stem risingStem = StemBranchUtils.getMonthStem(ew.getYearStem(), risingBranch);
    // 組合成干支
    return StemBranch.get(risingStem, risingBranch);
  }
}
