/*
 * @author smallufo
 * @date 2004/12/20
 * @time 下午 04:59:54
 */
package destiny.core.calendar.chinese;

import destiny.core.chinese.StemBranch;
import destiny.tools.ChineseStringTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * 農曆日期的表示法（無時辰）
 * epoch : -2636/2/15
 */
public class ChineseDate implements Serializable {

  /** 第幾輪 */
  @Nullable
  private Integer cycle;

  /** 年干支 */
  @NotNull
  private StemBranch year;

  /** 月 */
  private final int month;

  /** 是否是潤月 */
  private boolean leapMonth = false;

  /** 日 */
  private final int day;

  public ChineseDate(@Nullable Integer cycle, @NotNull StemBranch year, int month, boolean leapMonth, int day) {
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

  @NotNull
  public StemBranch getYear() {
    return year;
  }

  public int getCycle() {
    return cycle == null ? 0 : cycle;
  }


  @Override
  public String toString() {
    return year.toString()+"年"+(leapMonth?"閏":"")+toChinese(month)+"月"+toChinese(day)+"日";
  }

  /**
   * 從 cycle + 年干支 , 取得西元年份
   * 注意：只論 cycle + 年干支 , 以農曆為準 , 不考慮「西元過年」至「農曆過年」之間的 gap , 仍將其視為「西元」的前一年
   */
  public int getWestYear() {
    return -2636 + (getCycle() -1)* 60 + year.getIndex();
  }

  public static String toChinese(int num) {
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

  public static String digitToChinese(int digit) {
    return ChineseStringTools.digitToChinese(digit);
  }
}
