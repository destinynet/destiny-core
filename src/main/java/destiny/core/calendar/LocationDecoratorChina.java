/**
 * @author smallufo 
 * Created on 2008/4/19 at �W�� 1:15:25
 */ 
package destiny.core.calendar;

import destiny.core.calendar.Location.EastWest;
import destiny.core.calendar.Location.NorthSouth;
import destiny.utils.Decorator;

public class LocationDecoratorChina implements Decorator<Location>
{
  @Override
  public String getOutputString(Location location)
  {
    StringBuffer sb = new StringBuffer();
    sb.append((location.getEastWest()==EastWest.EAST ? "东经" : "西经") +" ");
    sb.append(location.getLongitudeDegree() + "度");
    sb.append(location.getLongitudeMinute() + "分");
    sb.append(location.getLongitudeSecond() + "秒, ");

    sb.append((location.getNorthSouth()==NorthSouth.NORTH ? "北纬" : "南纬") + " ");
    sb.append(location.getLatitudeDegree() + "度");
    sb.append(location.getLatitudeMinute() + "分");
    sb.append(location.getLatitudeSecond() + "秒.");
    sb.append("高度 " + location.getAltitudeMeter() + " 米");
    sb.append(" 时区 " + location.getTimeZone().getID());
    if (location.isMinuteOffsetSet())
      sb.append(" 时差 " + location.getMinuteOffset() + " 分钟.");
    
    return sb.toString();
  }

}
