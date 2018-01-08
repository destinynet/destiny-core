/**
 * @author smallufo
 * Created on 2007/7/25 at 上午 12:11:33
 */
package destiny.core.calendar

import java.io.Serializable

class GoogleMapsUrlBuilder : LocationUrlBuilder, Serializable {

  // http://maps.google.com/maps?&z=10&q=25.039059+121.517675&ll=25.039059+121.517675
  override fun getUrl(lat: Double, lng: Double): String {
    return String.format("http://maps.google.com/maps?&z=10&q=%f+%f&ll=%f+%f&z=14", lat, lng, lat, lng)
  }

}
