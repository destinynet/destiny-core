package destiny.core.calendar.eightwords;

import java.io.Serializable;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;

/**
 * @author smallufo
 * TODO : 當地時間/轉換成東經120度 排八字
 */
public class EightWordsContext implements Serializable
{
  private YearMonthIF yearMonthImpl;          // 換年, 以及月支計算的實作
  private DayIF       dayImpl;                // 計算日干支的介面
  private HourIF      hourImpl;               // 計算時支的介面

  private MidnightIF midnightImpl;            // 計算「子正」的介面
  private boolean    changeDayAfterZi = true; // 子初是否換日，內定是：true (換日)

  public EightWordsContext(YearMonthIF yearMonth , DayIF day, HourIF hour, MidnightIF midnight , boolean changeDayAfterZi)
  {
    this.yearMonthImpl = yearMonth;
    this.dayImpl = day;
    this.hourImpl = hour;
    this.midnightImpl = midnight;
    this.changeDayAfterZi = changeDayAfterZi;
  }

  /** 子初是否換日 */
  public boolean isChangeDayAfterZi()
  {
    return changeDayAfterZi;
  }
  
  /**
   * 計算八字 , 不用轉換，直接以 LMT 來計算即可！
   * TODO : 當地時間是否轉換成中原時間
   */
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
    HeavenlyStems 臨時日干 = day.getStem();

    EarthlyBranches 時支 = this.hourImpl.getHour(lmt, location);

    HeavenlyStems 時干 = null;

    Time nextZi = hourImpl.getLmtNextStartOf(lmt, location, EarthlyBranches.子);

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
        臨時日干 = HeavenlyStems.getHeavenlyStems(臨時日干.getIndex() + 1);

    switch (HeavenlyStems.getIndex(臨時日干))
    {
      case 0:
      case 5:
        時干 = HeavenlyStems.getHeavenlyStems(EarthlyBranches.getIndex(時支));
        break;
      case 1:
      case 6:
        時干 = HeavenlyStems.getHeavenlyStems(EarthlyBranches.getIndex(時支) + 2);
        break;
      case 2:
      case 7:
        時干 = HeavenlyStems.getHeavenlyStems(EarthlyBranches.getIndex(時支) + 4);
        break;
      case 3:
      case 8:
        時干 = HeavenlyStems.getHeavenlyStems(EarthlyBranches.getIndex(時支) + 6);
        break;
      case 4:
      case 9:
        時干 = HeavenlyStems.getHeavenlyStems(EarthlyBranches.getIndex(時支) + 8);
        break;
      default:
        throw new RuntimeException("Wrong");
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
}
