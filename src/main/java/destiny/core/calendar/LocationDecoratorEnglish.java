/**
 * @author smallufo 
 * Created on 2008/1/19 at 下午 10:05:34
 */ 
package destiny.core.calendar;

import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.utils.Decorator;
import org.jetbrains.annotations.NotNull;

public class LocationDecoratorEnglish implements Decorator<Location>
{

  @NotNull
  @Override
  public String getOutputString(@NotNull Location location)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(location.getEastWest()==EastWest.EAST ? "East " : "West ");
    sb.append(location.getLongitudeDegree()).append(" ° ");
    sb.append(location.getLongitudeMinute()).append(" ' ");
    sb.append(location.getLongitudeSecond()).append(" \" , ");

    sb.append(location.getNorthSouth()==NorthSouth.NORTH ? "North " : "South ");
    sb.append(location.getLatitudeDegree()).append(" ° ");
    sb.append(location.getLatitudeMinute()).append(" ' ");
    sb.append(location.getLatitudeSecond()).append(" \".");
    sb.append(" GMT offset ").append(location.getTimeZone().getRawOffset() / (60000 * 60)).append(" hours , ");
    sb.append("Alt ").append(location.getAltitudeMeter()).append(" m.");
    return sb.toString();
  }

}
