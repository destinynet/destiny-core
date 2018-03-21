/**
 * @author smallufo
 * Created on 2007/7/25 at 上午 12:09:59
 */
package destiny.core.calendar

/**
 * 從 Location 傳回網址，可用於 Google Maps 或是 Yahoo Maps
 */
interface LocationUrlBuilder {

  fun getUrl(lat: Double, lng: Double): String

  fun getUrl(location: Location): String {
    return getUrl(location.lat, location.lng)
  }
}
