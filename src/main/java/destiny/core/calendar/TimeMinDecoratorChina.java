/**
 * Created by smallufo on 2017-04-30.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;

import static destiny.core.calendar.TimeMinDecoratorChinese.alignRight;
import static java.time.chrono.IsoEra.BCE;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;

public class TimeMinDecoratorChina implements Decorator<ChronoLocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(ChronoLocalDateTime time) {
    StringBuilder sb = new StringBuilder();

    sb.append("西元");
    if (time.toLocalDate().getEra() == BCE) {
      sb.append("前" );
    }
    else
      sb.append("　");
    sb.append(alignRight(time.get(YEAR_OF_ERA), 4)).append("年");
    sb.append(time.get(MONTH_OF_YEAR) < 10 ? "0" : "").append(time.get(MONTH_OF_YEAR)).append("月");
    sb.append(time.get(DAY_OF_MONTH) < 10 ? "0" : "").append(time.get(DAY_OF_MONTH)).append("日");
    sb.append("　");
    sb.append(time.get(HOUR_OF_DAY) < 10 ? "0" : "").append(time.get(HOUR_OF_DAY)).append("时");
    sb.append(time.get(MINUTE_OF_HOUR) < 10 ? "0" : "").append(time.get(MINUTE_OF_HOUR)).append("分");

    return sb.toString();
  }
}
