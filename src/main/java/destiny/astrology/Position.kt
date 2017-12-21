package destiny.astrology

import java.io.Serializable

/**
 * Point 的位置 , 不限定 Star,
 * Point(南北交點) 也會有 Position
 */
open class Position(lng: Double
                    , val lat: Double
                    , val distance: Double //in AU
                    , val speedLng: Double //speed in lng (degree / day)
                    , val speedLat: Double //speed in lat (degree / day)
                    , val speedDistance: Double //speed in distance (AU / day)
) : Serializable {

  /** 座標系統 赤道/黃道/恆星  */
  val lng: Double = Utils.getNormalizeDegree(lng)


  override fun toString(): String {
    return "[Position " + "lng=" + lng + ", lat=" + lat + ", distance=" + distance + ", speedLng=" + speedLng + ", speedLat=" + speedLat + ", speedDistance=" + speedDistance + ']'.toString()
  }
}