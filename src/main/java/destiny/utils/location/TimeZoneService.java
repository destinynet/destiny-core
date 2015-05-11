/**
 * @author smallufo
 * Created on 2011/4/12 at 上午10:41:20
 */
package destiny.utils.location;

import java.util.TimeZone;

public interface TimeZoneService {

  TimeZone getTimeZone(double longitude, double latitude);
}
