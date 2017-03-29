/**
 * @author smallufo
 * Created on 2007/7/25 at 上午 12:09:59
 */
package destiny.core.calendar;

/**
 * 從 Location 傳回網址，可用於 Google Maps 或是 Yahoo Maps
 */
public interface LocationUrlBuilder {

  String getUrl(double lat , double lng);

  default String getUrl(Location location) {
    return getUrl(location.getLatitude() , location.getLongitude());
  }
}
