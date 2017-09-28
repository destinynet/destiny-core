/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.IsoEra;

import static java.time.temporal.ChronoField.*;

public class TimeMinDecoratorEnglish implements Decorator<ChronoLocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(ChronoLocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    sb.append(time.get(YEAR_OF_ERA) );
    if(time.toLocalDate().getEra() == IsoEra.CE)
      sb.append("AD");
    else
      sb.append("BC");
    sb.append(" ");

    sb.append(time.get(MONTH_OF_YEAR) < 10 ? "0" : "").append(time.get(MONTH_OF_YEAR));
    sb.append("/");
    sb.append(time.get(DAY_OF_MONTH) < 10 ? "0" : "").append(time.get(DAY_OF_MONTH));
    sb.append(" ");

    sb.append(time.get(HOUR_OF_DAY) < 10 ? "0" : "").append(time.get(HOUR_OF_DAY));
    sb.append(":");
    sb.append(time.get(MINUTE_OF_HOUR) < 10 ? "0" : "").append(time.get(MINUTE_OF_HOUR));
    return sb.toString();
  }
}
