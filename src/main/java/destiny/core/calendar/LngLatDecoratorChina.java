/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class LngLatDecoratorChina implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(Location location) {
    StringBuilder sb = new StringBuilder();
    sb.append(location.getEastWest() == Location.EastWest.EAST ? "东经" : "西经").append(" ");
    sb.append(location.getLngDeg()).append("度");
    sb.append(location.getLngMin()).append("分");
    sb.append(formatter.format(location.getLngSec())).append("秒, ");

    sb.append(location.getNorthSouth() == Location.NorthSouth.NORTH ? "北纬" : "南纬").append(" ");
    sb.append(location.getLatDeg()).append("度");
    sb.append(location.getLatMin()).append("分");

    sb.append(formatter.format(location.getLatSec())).append("秒.");

    return sb.toString();
  }
}
