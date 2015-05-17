/*
 * @author smallufo
 * @date 2004/12/20
 * @time 下午 04:59:54
 */
package destiny.core.calendar.chinese;

import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 農曆日期的表示法（無時辰）
 * epoch : -2636/2/15
 */
public class ChineseDate implements Serializable {

  /** 第幾輪 */
  private int cycle;

  /** 年干支 */
  @NotNull
  private StemBranch year;

  /** 月 */
  private int month;

  /** 是否是潤月 */
  private boolean leapMonth = false;

  /** 日 */
  private int day;

  public ChineseDate(int cycle, StemBranch year, int month, boolean leapMonth, int day) {
    this.cycle = cycle;
    this.year = year;
    this.month = month;
    this.leapMonth = leapMonth;
    this.day = day;
  }

  public int getDay() {
    return day;
  }

  public boolean isLeapMonth() {
    return leapMonth;
  }

  public int getMonth() {
    return month;
  }

  public void setYear(StemBranch year) {
    this.year = year;
  }

  @NotNull
  public StemBranch getYear() {
    return year;
  }

  public int getCycle() {
    return cycle;
  }

  public void setCycle(int cycle) {
    this.cycle = cycle;
  }


  @Override
  public String toString() {
    return year.toString()+"年"+(leapMonth?"閏":"")+toChinese(month)+"月"+toChinese(day)+"日";
  }

  private String toChinese(int num) {
    if (num < 10) {
      return digitToChinese(num);
    } else if (num == 10)
      return "十";
    else if (num > 10 && num < 20) {
      return "十"+ digitToChinese(num - 10);
    } else if (num == 20)
      return "二十";
    else if (num > 20 && num < 30)
      return "二十"+ digitToChinese(num - 20);
    else
      return "三十";
  }

  private String digitToChinese(int digit) {
    switch (digit) {
      case 1 : return "一";
      case 2 : return "二";
      case 3 : return "三";
      case 4 : return "四";
      case 5 : return "五";
      case 6 : return "六";
      case 7 : return "七";
      case 8 : return "八";
      case 9 : return "九";
    }
    throw new IllegalArgumentException("digitToChinese : " + digit);
  }
}
