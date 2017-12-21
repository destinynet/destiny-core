/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:33:11
 */
package destiny.astrology

/**
 * 星體的位置，加上地平方位角
 */
class PositionWithAzimuth(position: Position, val azimuth: Azimuth) : Position(position.lng, position.lat, position.distance, position.speedLng, position.speedLat, position.speedDistance) {

  override fun toString(): String {
    val sb = StringBuilder()

    sb.append("[PositionWithAzimuth " + "lng=").append(lng).append(", lat=").append(lat).append(", distance=").append(distance).append(", speedLng=").append(speedLng).append(", speedLat=").append(speedLat).append(", speedDistance=").append(speedDistance)
    sb.append(" , azimuth=").append(azimuth).append(']')

    return sb.toString()
  }
}
