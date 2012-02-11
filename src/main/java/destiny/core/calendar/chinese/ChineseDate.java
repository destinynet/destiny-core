/*
 * @author smallufo
 * @date 2004/12/20
 * @time 下午 04:59:54
 */
package destiny.core.calendar.chinese;

import destiny.core.chinese.StemBranch;

/**
 * 農曆的表示法
 * epoch : -2636/2/15
 */
public class ChineseDate
{
  /** 第幾輪 */
  private int cycle;
  /** 年干支 */
  private StemBranch year;
  /** 月 */
  private int month;
  /** 是否是潤月 */
  private boolean leapMonth = false;
  /** 日 */
  private int day;
  
  /** 利用 Time (LMT) 以及 Location 來查詢農曆日期 */
  public ChineseDate()
  {
  }
  
  public int getDay()
  {
    return day;
  }
  
  public boolean isLeapMonth()
  {
    return leapMonth;
  }
  
  public int getMonth()
  {
    return month;
  }
  
  public void setYear(StemBranch year)
  {
    this.year = year;
  }
  
  public StemBranch getYear()
  {
    return year;
  }

  public int getCycle()
  {
    return cycle;
  }

  public void setCycle(int cycle)
  {
    this.cycle = cycle;
  }
}
