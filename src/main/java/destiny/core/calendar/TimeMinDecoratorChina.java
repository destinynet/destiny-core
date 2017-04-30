/**
 * Created by smallufo on 2017-04-30.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

import static destiny.core.calendar.TimeMinDecoratorChinese.alignRight;
import static java.time.chrono.IsoEra.BCE;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;

public class TimeMinDecoratorChina implements Decorator<LocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();

    sb.append("西元");
    if (time.toLocalDate().getEra() == BCE) {
      sb.append("前" );
    }
    else
      sb.append("　");
    sb.append(alignRight(time.get(YEAR_OF_ERA), 4)).append("年");
    sb.append(time.getMonthValue() < 10 ? "0" : "").append(time.getMonthValue()).append("月");
    sb.append(time.getDayOfMonth() < 10 ? "0" : "").append(time.getDayOfMonth()).append("日");
    sb.append("　");
    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour()).append("时");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute()).append("分");

    return sb.toString();
  }
}
