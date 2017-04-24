/**
 * @author smallufo
 * Created on 2008/4/19 at �W�� 1:15:25
 */
package destiny.core.calendar;

import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class LocationDecoratorChina implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(@NotNull Location location) {
    StringBuilder sb = new StringBuilder();
    sb.append(location.getEastWest() == EastWest.EAST ? "东经" : "西经").append(" ");
    sb.append(location.getLongitudeDegree()).append("度");
    sb.append(location.getLongitudeMinute()).append("分");
    sb.append(formatter.format(location.getLongitudeSecond())).append("秒, ");

    sb.append(location.getNorthSouth() == NorthSouth.NORTH ? "北纬" : "南纬").append(" ");
    sb.append(location.getLatitudeDegree()).append("度");
    sb.append(location.getLatitudeMinute()).append("分");

    sb.append(formatter.format(location.getLatitudeSecond())).append("秒.");
    sb.append("高度 ").append(location.getAltitudeMeter()).append(" 米");
    sb.append(" 时区 ").append(location.getTimeZone().getID());
    if (location.isMinuteOffsetSet())
      sb.append(" 时差 ").append(location.getMinuteOffset()).append(" 分钟.");

    return sb.toString();
  }

}
