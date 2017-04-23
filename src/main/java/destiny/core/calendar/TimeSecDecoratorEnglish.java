/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeSecDecoratorEnglish implements Decorator<LocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();

    TimeMinDecoratorEnglish min = new TimeMinDecoratorEnglish();
    sb.append(min.getOutputString(time));
    sb.append(":");

    if (time.getSecond() < 10) {
      sb.append("0");
    }
    sb.append(time.getSecond());

    if (time.getNano() == 0) {
      sb.append(".00");
    } else {
      sb.append(".");
      sb.append(String.valueOf(time.getNano()).substring(0,2));
    }

    return sb.toString();
  }
}
