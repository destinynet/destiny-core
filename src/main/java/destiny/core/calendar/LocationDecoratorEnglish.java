/**
 * @author smallufo 
 * Created on 2008/1/19 at 下午 10:05:34
 */ 
package destiny.core.calendar;

import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.utils.Decorator;

public class LocationDecoratorEnglish implements Decorator<Location>
{

  @Override
  public String getOutputString(Location location)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(location.getEastWest()==EastWest.EAST ? "East " : "West ");
    sb.append(location.getLongitudeDegree() + " ° ");
    sb.append(location.getLongitudeMinute() + " ' ");
    sb.append(location.getLongitudeSecond() + " \" , ");

    sb.append(location.getNorthSouth()==NorthSouth.NORTH ? "North " : "South ");
    sb.append(location.getLatitudeDegree() + " ° ");
    sb.append(location.getLatitudeMinute() + " ' ");
    sb.append(location.getLatitudeSecond() + " \".");
    sb.append(" GMT offset " + (location.getTimeZone().getRawOffset() / (60000*60)) + " hours , ");
    sb.append("Alt " + location.getAltitudeMeter() + " m.");
    return sb.toString();
  }

}
