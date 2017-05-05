/**
 * @author smallufo
 * Created on 2008/4/19 at �W�� 1:15:25
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Locale;

public class LocationDecoratorChina implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(@NotNull Location location) {
    StringBuilder sb = new StringBuilder();
    sb.append(LngLatDecorator.getOutputString(location , Locale.CHINA));

    sb.append("高度 ").append(location.getAltitudeMeter()).append(" 米");
    sb.append(" 时区 ").append(location.getTimeZone().getID());
    if (location.isMinuteOffsetSet())
      sb.append(" 时差 ").append(location.getMinuteOffset()).append(" 分钟.");

    return sb.toString();
  }

}
