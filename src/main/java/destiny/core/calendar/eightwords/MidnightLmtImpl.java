/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

/**
 * 純粹以地方平均時（手錶時間）來判定
 */
public class MidnightLmtImpl implements MidnightIF, Serializable {

  @Override
  public double getNextMidnight(double gmtJulDay, @NotNull Location loc) {
    ChronoLocalDateTime gmt = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(gmtJulDay);
    ChronoLocalDateTime lmt = TimeTools.getLmtFromGmt(gmt , loc);

    ChronoLocalDateTime resultLmt = lmt.plus(1 , ChronoUnit.DAYS)
      .with(HOUR_OF_DAY , 0)
      .with(MINUTE_OF_HOUR , 0)
      .with(SECOND_OF_MINUTE , 0)
      .with(NANO_OF_SECOND , 0);
    ChronoLocalDateTime resultGmt = TimeTools.getGmtFromLmt(resultLmt , loc);
    return TimeTools.getGmtJulDay(resultGmt);
  }



  @Override
  public ChronoLocalDateTime getNextMidnight(ChronoLocalDateTime lmt, Location loc) {
    return lmt
      .plus(1 , ChronoUnit.DAYS)
      .with(HOUR_OF_DAY , 0)
      .with(MINUTE_OF_HOUR , 0)
      .with(SECOND_OF_MINUTE , 0)
      .with(NANO_OF_SECOND , 0);
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
