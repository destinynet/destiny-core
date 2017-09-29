/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:21:46
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Locale;

public class LocationDecoratorTaiwan implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(@NotNull Location location) {


    StringBuilder sb = new StringBuilder();
    sb.append(LngLatDecorator.getOutputString(location , Locale.TAIWAN));

    sb.append("高度 ").append(location.getAltitudeMeter()).append(" 公尺.");
    sb.append(" 時區 ").append(location.getTimeZone().getID());
    if (location.hasMinuteOffset())
      sb.append(" 時差 ").append(location.getMinuteOffset()).append(" 分鐘.");

    return sb.toString();
  }

}
