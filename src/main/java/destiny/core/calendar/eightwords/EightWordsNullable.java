/**
 * @author smallufo 
 * Created on 2006/6/12 at 上午 02:21:51
 */ 
package destiny.core.calendar.eightwords;

import destiny.core.chinese.EarthlyBranches;
import destiny.core.chinese.HeavenlyStems;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 八字資料結構，可以包含 null 值
 */
public class EightWordsNullable implements Serializable
{
  @Nullable
  protected StemBranch year  = null;
  protected StemBranch month = null;
  protected StemBranch day   = null;
  protected StemBranch hour  = null;
  
  private String NULL_CHAR ="　"; //空白字元，使用全形的空白, 在 toString() 時使用
  
  //null constructor 建立一個四支全為 null 的八字
  public EightWordsNullable()
  {
  }
  
  /** 年月日時 可以為 null */
  public EightWordsNullable( StemBranch year , StemBranch month , StemBranch day , StemBranch hour)
  {
    this.year  = year;
    this.month = month;
    this.day   = day;
    this.hour  = hour;
  }
  
  /**
   * 以 "甲子","甲子","甲子","甲子" 方式 construct 此物件 , 可以為 null
   */
  public EightWordsNullable( String year , String month , String day , String hour)
  {
    if (year != null)
    {
      char y1 = year.toCharArray()[0];
      char y2 = year.toCharArray()[1];
      this.year  = StemBranch.get(y1 , y2); 
    }
    
    if (month != null)
    {
      char m1 = month.toCharArray()[0];
      char m2 = month.toCharArray()[1];
      this.month = StemBranch.get(m1 , m2);  
    }
    
    if (day != null)
    {
      char d1 = day.toCharArray()[0];
      char d2 = day.toCharArray()[1];
      this.day   = StemBranch.get(d1 , d2);
    }
    
    if (hour != null)
    {
      char h1 = hour.toCharArray()[0];
      char h2 = hour.toCharArray()[1];
      this.hour  = StemBranch.get(h1 , h2);  
    }
  }
  
  public HeavenlyStems getYearStem()  { return ( year  == null ) ? null : year.getStem();  }
  public HeavenlyStems getMonthStem() { return ( month == null ) ? null : month.getStem(); }
  public HeavenlyStems getDayStem()   { return ( day   == null ) ? null : day.getStem();   }
  public HeavenlyStems getHourStem()  { return ( hour  == null ) ? null : hour.getStem();  }
  
  public EarthlyBranches getYearBranch()  { return ( year  == null ) ? null : year.getBranch();  }
  public EarthlyBranches getMonthBranch() { return ( month == null ) ? null : month.getBranch(); }
  public EarthlyBranches getDayBranch()   { return ( day   == null ) ? null : day.getBranch();   }
  public EarthlyBranches getHourBranch()  { return ( hour  == null ) ? null : hour.getBranch();  }

  /** 取得四柱 */
  public List<StemBranch> getStemBranches() {
    return new ArrayList() {{
      add(year);
      add(month);
      add(day);
      add(hour);
    }};
  }

  public boolean equals(Object o)
  {
    if ((o != null) && (o.getClass().equals(this.getClass())))
    {
      EightWordsNullable ew = (EightWordsNullable) o;
      return 
      (   (((year ==null)&&(ew.year ==null)) || (year  != null && year .equals(ew.year ))) && 
          (((month==null)&&(ew.month==null)) || (month != null && month.equals(ew.month))) && 
          (((day  ==null)&&(ew.day  ==null)) || (day   != null && day  .equals(ew.day  ))) && 
          (((hour ==null)&&(ew.hour ==null)) || (hour  != null && hour .equals(ew.hour )))
          );
    }
    else return false;
  }

  public int hashCode()
  {
    int hash = 19 ;
    hash = hash * 23 + ((year  == null) ? 0 : year.hashCode() );
    hash = hash * 23 + ((month == null) ? 0 : month.hashCode());
    hash = hash * 23 + ((day   == null) ? 0 : day.hashCode()  );
    hash = hash * 23 + ((hour  == null) ? 0 : hour.hashCode() );
    return hash;
  }
  
  
  public String toString()
  {
    return 
      "\n"+
      ((hour==null) ? NULL_CHAR : skipNull(hour.getStem()))   + ((day==null) ? NULL_CHAR : skipNull(day.getStem()))   + ((month==null) ? NULL_CHAR : skipNull(month.getStem()))   + ((year==null) ? NULL_CHAR : skipNull(year.getStem())) + "\n" +
      ((hour==null) ? NULL_CHAR : skipNull(hour.getBranch())) + ((day==null) ? NULL_CHAR : skipNull(day.getBranch())) + ((month==null) ? NULL_CHAR : skipNull(month.getBranch())) + ((year==null) ? NULL_CHAR : skipNull(year.getBranch())) ;
  }
  
  private String skipNull(Object o)
  {
    if (o == null)
      return NULL_CHAR;
    else
      return o.toString();
  }
  
  /** 取得 年干支 */
  public StemBranch getYear()  { return year; }
  /** 取得 月干支 */
  public StemBranch getMonth() { return month; }
  /** 取得 日干支 */
  public StemBranch getDay()   { return day; }
  /** 取得 時干支 */
  public StemBranch getHour()  { return hour; }
  
  public void setYear (StemBranch value) { year  = value; }
  public void setMonth(StemBranch value) { month = value; }
  public void setDay  (StemBranch value) { day   = value; }
  public void setHour (StemBranch value) { hour  = value; }
  
  /** 以字串的 ("甲子") 來設定年干支 */
  public void setYear (String value) { year  = (value==null ) ? null : StemBranch.get(value); }
  /** 以字串的 ("甲子") 來設定月干支 */
  public void setMonth(String value) { month = (value==null ) ? null : StemBranch.get(value); }
  /** 以字串的 ("甲子") 來設定日干支 */
  public void setDay  (String value) { day   = (value==null ) ? null : StemBranch.get(value); }
  /** 以字串的 ("甲子") 來設定時干支 */
  public void setHour (String value) { hour  = (value==null ) ? null : StemBranch.get(value); }
  
  
}
