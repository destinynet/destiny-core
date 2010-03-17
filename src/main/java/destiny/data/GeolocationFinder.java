/**
 * Created by smallufo at 2009/3/17 下午 9:44:34
 */
package destiny.data;

import java.util.Locale;

import destiny.core.calendar.Location;

public interface GeolocationFinder
{
  /** 給定一個地址，找出相對應的經緯度等資料 */
  public Location getLocation(String place , Locale locale);
}
