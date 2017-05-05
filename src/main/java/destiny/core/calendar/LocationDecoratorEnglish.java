/**
 * @author smallufo
 * Created on 2008/1/19 at 下午 10:05:34
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Locale;

public class LocationDecoratorEnglish implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(@NotNull Location location) {
    StringBuilder sb = new StringBuilder();
    sb.append(LngLatDecorator.getOutputString(location , Locale.ENGLISH));

    sb.append(" GMT offset ").append(location.getTimeZone().getRawOffset() / (60000 * 60)).append(" hours , ");
    sb.append("Alt ").append(location.getAltitudeMeter()).append(" m.");
    return sb.toString();
  }

}
