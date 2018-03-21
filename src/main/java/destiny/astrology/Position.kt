package destiny.astrology

import java.io.Serializable

interface IPos {
  val lng: Double
  val lat: Double
  val distance: Double  // in AU
  val speedLng: Double  // speed in lng (degree / day)
  val speedLat: Double  // speed in lat (degree / day)
  val speedDistance: Double // speed in distance (AU / day)
}

data class Position(
  override val lng: Double,
  override val lat: Double,
  override val distance: Double,
  override val speedLng: Double,
  override val speedLat: Double,
  override val speedDistance: Double) : IPos, Serializable


interface IPositionWithAzimuth : IPos {
  val azimuth : Azimuth
}

data class PositionWithAzimuth(
  val pos: Position,
  override val azimuth: Azimuth) : IPositionWithAzimuth, IPos by pos, Serializable

/**
 * Point 的位置 , 不限定 Star,
 * Point(南北交點) 也會有 Position
 */
//open class Position(lng: Double
//                    , val lat: Double
//                    , val distance: Double //in AU
//                    , val speedLng: Double //speed in lng (degree / day)
//                    , val speedLat: Double //speed in lat (degree / day)
//                    , val speedDistance: Double //speed in distance (AU / day)
//                   ) : Serializable {
//
//  /** 座標系統 赤道/黃道/恆星  */
//  val lng: Double = Utils.getNormalizeDegree(lng)
//
//
//  override fun toString(): String {
//    return "[Position " + "lng=" + lng + ", lat=" + lat + ", distance=" + distance + ", speedLng=" + speedLng + ", speedLat=" + speedLat + ", speedDistance=" + speedDistance + ']'.toString()
//  }
//}


/**
 * 星體的位置，加上地平方位角
 */
//class PositionWithAzimuth(position: Position, val azimuth: Azimuth) : Position(position.lng, position.lat, position.distance, position.speedLng, position.speedLat, position.speedDistance) {
//
//  override fun toString(): String {
//    val sb = StringBuilder()
//
//    sb.append("[PositionWithAzimuth " + "lng=").append(lng).append(", lat=").append(lat).append(", distance=").append(distance).append(", speedLng=").append(speedLng).append(", speedLat=").append(speedLat).append(", speedDistance=").append(speedDistance)
//    sb.append(" , azimuth=").append(azimuth).append(']')
//
//    return sb.toString()
//  }
//}
