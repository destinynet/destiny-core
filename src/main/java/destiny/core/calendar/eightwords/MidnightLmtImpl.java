/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    Time gmt = new Time(gmtJulDay);
//    LocalDateTime resultLmt = gmt.toLocalDateTime().toLocalDate()
//      .plus(1 , ChronoUnit.DAYS)
//      .atTime(0 , 0 , 0);

    Time lmt = Time.getLMTfromGMT(gmt, loc);
    Time resultLmt = new Time(lmt.isAd(), lmt.getYear(), lmt.getMonth(), lmt.getDay() + 1, 0, 0, 0);
    Time resultGmt = Time.getGMTfromLMT(resultLmt , loc);
    return resultGmt.getGmtJulDay();
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

  /**
   * 2017-02-10 : 為了避免 Time 的 round-off error , 所以這裡仍保留實作
   */
  @NotNull
  public Time getNextMidnight(@Nullable Time lmt, @Nullable Location location) {
    if (lmt == null || location == null)
      throw new RuntimeException("lmt and location cannot be null !");

    return new Time(lmt.isAd(), lmt.getYear(), lmt.getMonth(), lmt.getDay() + 1, 0, 0, 0);
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
