/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class LngLatDecoratorEnglish implements Decorator<Location> {

  DecimalFormat formatter = new DecimalFormat("00.00");

  @NotNull
  @Override
  public String getOutputString(Location location) {

    StringBuilder sb = new StringBuilder();
    sb.append(location.getEastWest() == Location.EastWest.EAST ? "East " : "West ");
    sb.append(location.getLngDeg()).append("° ");
    sb.append(location.getLngMin()).append("' ");
    sb.append(formatter.format(location.getLngSec())).append("\" , ");

    sb.append(location.getNorthSouth() == Location.NorthSouth.NORTH ? "North " : "South ");
    sb.append(location.getLatDeg()).append("° ");
    sb.append(location.getLatMin()).append("' ");
    sb.append(formatter.format(location.getLatSec())).append("\".");

    return sb.toString();
  }
}
