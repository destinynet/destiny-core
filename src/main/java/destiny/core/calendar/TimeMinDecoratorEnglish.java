/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.chrono.IsoEra;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;

public class TimeMinDecoratorEnglish implements Decorator<LocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    sb.append(time.get(YEAR_OF_ERA) );
    if(time.toLocalDate().getEra() == IsoEra.CE)
      sb.append("AD");
    else
      sb.append("BC");
    sb.append(" ");

    sb.append(time.getMonthValue() < 10 ? "0" : "").append(time.getMonthValue());
    sb.append("/");
    sb.append(time.getDayOfMonth() < 10 ? "0" : "").append(time.getDayOfMonth());
    sb.append(" ");

    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour());
    sb.append(":");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute());
    return sb.toString();
  }
}
