/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.TimeTools;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * 純粹以地方平均時（手錶時間）來判定
 */
public class MidnightLmtImpl implements MidnightIF, Serializable {

  @Override
  public double getNextMidnight(double gmtJulDay, @NotNull Location loc) {
    LocalDateTime gmt = new Time(gmtJulDay).toLocalDateTime();
    LocalDateTime lmt = Time.getLmtFromGmt(gmt , loc);
    LocalDateTime resultLmt = LocalDateTime.from(lmt).plusDays(1).withHour(0).withSecond(0).withSecond(0).withNano(0);
    LocalDateTime resultGmt = Time.getGmtFromLmt(resultLmt , loc);
    return TimeTools.getGmtJulDay(resultGmt);
  }

  /**
   * 2017-02-10 : 為了避免 Time 的 round-off error , 所以這裡仍保留實作
   */
  @Override
  public LocalDateTime getNextMidnight(LocalDateTime lmt, Location loc) {
    return LocalDate.of(lmt.getYear() , lmt.getMonth() , lmt.getDayOfMonth())
      .plus(1 , ChronoUnit.DAYS)
      .atTime(0 , 0 , 0);
  }


  @NotNull
  public String getTitle(Locale locale) {
    return "以地方平均時（ LMT）來判定";
  }

  @NotNull
  public String getDescription(Locale locale) {
    return "晚上零時就是子正，不校正經度差以及真太陽時";
  }

}
