/**
 * @author smallufo
 * Created on 2006/6/30 at 下午 11:13:01
 */
package destiny.core.calendar.chinese;

import destiny.core.calendar.Time;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * 從 Time(LMT) / Location 取得 ChineseDate
 */
public interface ChineseDateIF {

  // =============== 陽曆轉陰曆 ===============

  @NotNull
  ChineseDate getChineseDate(int year , int month , int day);

  default ChineseDate getChineseDate(LocalDate localDate) {
    return getChineseDate(localDate.getYear() , localDate.getMonthValue() , localDate.getDayOfMonth());
  }

  default ChineseDate getChineseDate(Time time) {
    return getChineseDate(time.toLocalDateTime().toLocalDate());
  }

  // =============== 陰曆轉陽曆 ===============
  LocalDate getYangDate(int cycle, StemBranch year, boolean leap, int month, int day);


}
