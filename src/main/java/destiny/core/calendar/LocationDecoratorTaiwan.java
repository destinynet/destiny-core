/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:21:46
 */
package destiny.core.calendar;

import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class LocationDecoratorTaiwan implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(@NotNull Location location) {


    StringBuilder sb = new StringBuilder();
    sb.append(location.getEastWest() == EastWest.EAST ? "東經" : "西經").append(" ");
    sb.append(location.getLongitudeDegree()).append("度 ");
    sb.append(location.getLongitudeMinute()).append("分 ");
    sb.append(formatter.format(location.getLongitudeSecond())).append("秒, ");

    sb.append(location.getNorthSouth() == NorthSouth.NORTH ? "北緯" : "南緯").append(" ");
    sb.append(location.getLatitudeDegree()).append("度 ");
    sb.append(location.getLatitudeMinute()).append("分 ");

    sb.append(formatter.format(location.getLatitudeSecond())).append("秒.");
    sb.append("高度 ").append(location.getAltitudeMeter()).append(" 公尺.");
    sb.append(" 時區 ").append(location.getTimeZone().getID());
    if (location.isMinuteOffsetSet())
      sb.append(" 時差 ").append(location.getMinuteOffset()).append(" 分鐘.");

    return sb.toString();
  }

}
