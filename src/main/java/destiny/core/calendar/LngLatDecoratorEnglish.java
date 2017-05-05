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
    sb.append(location.getLongitudeDegree()).append("° ");
    sb.append(location.getLongitudeMinute()).append("' ");
    sb.append(formatter.format(location.getLongitudeSecond())).append("\" , ");

    sb.append(location.getNorthSouth() == Location.NorthSouth.NORTH ? "North " : "South ");
    sb.append(location.getLatitudeDegree()).append("° ");
    sb.append(location.getLatitudeMinute()).append("' ");
    sb.append(formatter.format(location.getLatitudeSecond())).append("\".");

    return sb.toString();
  }
}
