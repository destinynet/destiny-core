/**
 * @author smallufo
 * Created on 2006/6/30 at 下午 11:13:01
 */
package destiny.core.calendar.chinese;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 從 Time(LMT) / Location 取得 ChineseDate
 */
public interface ChineseDateIF {

  Logger logger = LoggerFactory.getLogger(ChineseDateIF.class);

  // =============== 陽曆轉陰曆 ===============

  @NotNull
  ChineseDate getChineseDate(int year , int month , int day);

  default ChineseDate getChineseDate(LocalDate localDate) {
    return getChineseDate(localDate.getYear() , localDate.getMonthValue() , localDate.getDayOfMonth());
  }

  /**
   * 最完整的「陽曆轉陰曆」演算法
   * 必須另外帶入 地點、日干支紀算法、時辰劃分法、子正計算方式、是否子初換日 5個參數
   */
  ChineseDate getChineseDate(LocalDateTime lmt , Location location , DayIF dayImpl , HourIF hourImpl , MidnightIF midnightImpl , boolean changeDayAfterZi);

  default ChineseDate getChineseDate(Time lmt , Location location , DayIF dayImpl , HourIF hourImpl , MidnightIF midnightImpl ,  boolean changeDayAfterZi) {
    return getChineseDate(lmt.toLocalDateTime() , location , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);
  }

  // =============== 陰曆轉陽曆 ===============
  LocalDate getYangDate(int cycle, StemBranch year, boolean leap, int month, int day);

  default LocalDate getYangDate(ChineseDate cdate) {
    return getYangDate(cdate.getCycle() , cdate.getYear() , cdate.isLeapMonth() , cdate.getMonth() , cdate.getDay());
  }


}
