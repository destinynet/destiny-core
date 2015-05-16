package destiny.core.calendar.eightwords;
 
import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;


/**
 * <PRE>
 * 八字資料結構 , 四柱任何一柱都不可以為 null
 * 2006/06/12 將此 class 繼承 EightWordsNullable
 * </PRE>
 * @author smallufo
 * 2002/8/25 下午 11:22:48
 */
public class EightWords extends EightWordsNullable
{
 
  /** Constructor , 任何一柱都不可以為 null */
  public EightWords(@NotNull StemBranch year , @NotNull StemBranch month , @NotNull StemBranch day , @NotNull StemBranch hour)
  {
    super(year , month , day , hour);
//    this.year  = year;
//    this.month = month;
//    this.day   = day;
//    this.hour  = hour;
  }
   
  /**
   * 以 "甲子","甲子","甲子","甲子" 方式 construct 此物件 , 任何一柱都不可以為 null
   */
  public EightWords( String year , String month , String day , String hour)
  {
    super(
      StemBranch.get(year.toCharArray()[0] , year.toCharArray()[1]) ,
      StemBranch.get(month.toCharArray()[0] , month.toCharArray()[1]),
      StemBranch.get(day.toCharArray()[0] , day.toCharArray()[1]) ,
      StemBranch.get(hour.toCharArray()[0] , hour.toCharArray()[1])
      );
//    if (year == null || month == null || day == null || hour == null)
//      throw new RuntimeException("year / month / day / hour cannot be null !");
//    char y1 = year.toCharArray()[0];
//    char y2 = year.toCharArray()[1];
//    char m1 = month.toCharArray()[0];
//    char m2 = month.toCharArray()[1];
//    char d1 = day.toCharArray()[0];
//    char d2 = day.toCharArray()[1];
//    char h1 = hour.toCharArray()[0];
//    char h2 = hour.toCharArray()[1];
//
//    this.year  = StemBranch.get(y1 , y2);
//    this.month = StemBranch.get(m1 , m2);
//    this.day   = StemBranch.get(d1 , d2);
//    this.hour  = StemBranch.get(h1 , h2);
  }
   
  /** 從 EightWordsNullable 建立 EightWords , 其中 EightWordsNullable 任何一柱都不可以為 null , 否則會出現 RuntimeException */
  public EightWords(EightWordsNullable nullable)
  {
    super(nullable.getYear() , nullable.getMonth() , nullable.getDay() , nullable.getHour());
//    if (nullable.getYear()==null || nullable.getMonth()==null || nullable.getDay()==null || nullable.getHour()==null)
//      throw new RuntimeException("year / month / day / hour cannot be null !");
//    this.year  = nullable.getYear();
//    this.month = nullable.getMonth();
//    this.day   = nullable.getDay();
//    this.hour  = nullable.getHour();
  }
   
  /**
   * null Constructor , 避免誤用而建構出有 null 的四柱
   */
  @SuppressWarnings("unused")
  private EightWords()
  {}

  @NotNull
  @Override public HeavenlyStems getYearStem()  { return year.getStem();  }

  @NotNull
  @Override public HeavenlyStems getMonthStem() { return month.getStem(); }

  @NotNull
  @Override public HeavenlyStems getDayStem()   { return day.getStem();   }

  @NotNull
  @Override public HeavenlyStems getHourStem()  { return hour.getStem();  }

  @NotNull
  @Override public EarthlyBranches getYearBranch()  { return year.getBranch();  }

  @NotNull
  @Override public EarthlyBranches getMonthBranch() { return month.getBranch(); }

  @NotNull
  @Override public EarthlyBranches getDayBranch()   { return day.getBranch();   }

  @NotNull
  @Override public EarthlyBranches getHourBranch()  { return hour.getBranch();  }

  @NotNull
  public StemBranch getYear() {
    return (StemBranch) year;
  }

  @NotNull
  public StemBranch getMonth() {
    return (StemBranch) month;
  }

  @NotNull
  public StemBranch getDay() {
    return (StemBranch) day;
  }

  @NotNull
  public StemBranch getHour() {
    return (StemBranch) hour;
  }
   
  @Override
  public boolean equals(Object o)
  {
    if ((o != null) && (o.getClass().equals(this.getClass())))
    {
      EightWords ew = (EightWords) o;
      return (year.equals(ew.year) && month.equals(ew.month) && day.equals(ew.day) && hour.equals(ew.hour));
    }
    else return false;
  }
 
  @Override
  public int hashCode()
  {
    int hash = 13 ;
    hash = hash * 17 + year.hashCode();
    hash = hash * 17 + month.hashCode();
    hash = hash * 17 + day.hashCode();
    hash = hash * 17 + hour.hashCode();
    return hash;
  }
   
  @Override
  public String toString()
  {
    return
      "\n"+
      hour.getStem()   + day.getStem()   + month.getStem()   + year.getStem() + "\n" +
      hour.getBranch() + day.getBranch() + month.getBranch() + year.getBranch() ;
  }
//
//  @Override
//  /** 設定年干支 , 不可為 null */
//  public void setYear (@NotNull StemBranch branch)
//  {
//    year  = branch;
//  }
//
//  @Override
//  /** 設定月干支 , 不可為 null */
//  public void setMonth(@NotNull StemBranch branch)
//  {
//    month = branch;
//  }
//
//  @Override
//  /** 設定日干支 , 不可為 null */
//  public void setDay  (@NotNull StemBranch branch)
//  {
//    day   = branch;
//  }
//
//  @Override
//  /** 設定時干支 , 不可為 null */
//  public void setHour (@NotNull StemBranch branch)
//  {
//    hour  = branch;
//  }
//
//  /** 以字串的 ("甲子") 來設定年干支 */
//  public void setYear (String value) { year  = StemBranch.get(value); }
//  /** 以字串的 ("甲子") 來設定月干支 */
//  public void setMonth(String value) { month = StemBranch.get(value); }
//  /** 以字串的 ("甲子") 來設定日干支 */
//  public void setDay  (String value) { day   = StemBranch.get(value); }
//  /** 以字串的 ("甲子") 來設定時干支 */
//  public void setHour (String value) { hour  = StemBranch.get(value); }
 
}
