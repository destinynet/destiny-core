/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

public class TimeSecDecoratorJapanese implements Decorator<ChronoLocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(ChronoLocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    TimeMinDecoratorJapanese min = new TimeMinDecoratorJapanese();
    sb.append(min.getOutputString(time));

    sb.append(' ');
    if (time.get(SECOND_OF_MINUTE) < 10) {
      sb.append("0");
    }
    sb.append(time.get(SECOND_OF_MINUTE));

    if (time.get(NANO_OF_SECOND) == 0) {
      sb.append(".00");
    } else {
      sb.append(".");
      sb.append(String.valueOf(time.get(NANO_OF_SECOND)).substring(0,2));
    }
    sb.append("秒");

    return sb.toString();
  }
}
