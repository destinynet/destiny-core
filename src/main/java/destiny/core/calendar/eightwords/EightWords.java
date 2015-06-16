package destiny.core.calendar.eightwords;
 
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
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
  }
   
  /** 從 EightWordsNullable 建立 EightWords , 其中 EightWordsNullable 任何一柱都不可以為 null , 否則會出現 RuntimeException */
  public EightWords(EightWordsNullable nullable)
  {
    super(nullable.getYear() , nullable.getMonth() , nullable.getDay() , nullable.getHour());
  }

  /** 直接用八個干支建立八字 */
  public EightWords(Stem yearStem , Branch yearBranch , Stem monthStem , Branch monthBranch , Stem dayStem , Branch dayBranch , Stem hourStem , Branch hourBranch) {
    super(
      StemBranch.get(yearStem, yearBranch),
      StemBranch.get(monthStem , monthBranch) ,
      StemBranch.get(dayStem , dayBranch) ,
      StemBranch.get(hourStem , hourBranch)
    );
  }

  /**
   * null Constructor , 避免誤用而建構出有 null 的四柱
   */
  @SuppressWarnings("unused")
  private EightWords()
  {}

  @NotNull
  @Override public Stem getYearStem()  { return year.getStemOptional().get();  }

  @NotNull
  @Override public Stem getMonthStem() { return month.getStemOptional().get(); }

  @NotNull
  @Override public Stem getDayStem()   { return day.getStemOptional().get();   }

  @NotNull
  @Override public Stem getHourStem()  { return hour.getStemOptional().get();  }

  @NotNull
  @Override public Branch getYearBranch()  { return year.getBranchOptional().get();  }

  @NotNull
  @Override public Branch getMonthBranch() { return month.getBranchOptional().get(); }

  @NotNull
  @Override public Branch getDayBranch()   { return day.getBranchOptional().get();   }

  @NotNull
  @Override public Branch getHourBranch()  { return hour.getBranchOptional().get();  }

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
      hour.getStemOptional().get()  + day.getStemOptional().get()  + month.getStemOptional().get()   + year.getStemOptional().get() + "\n" +
      hour.getBranchOptional().get() + day.getBranchOptional().get() + month.getBranchOptional().get() + year.getBranchOptional().get() ;
  }
}
