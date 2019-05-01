package destiny.astrology

import java.io.Serializable

interface IPos {
  val lng: Double
  val lat: Double
  val distance: Double  // in AU
  val speedLng: Double  // speed in lng (degree / day)
  val speedLat: Double  // speed in lat (degree / day)
  val speedDistance: Double // speed in distance (AU / day)

  /** 黃道什麼星座 */
  val sign: ZodiacSign
    get() = ZodiacSign.of(lng)

  /** 黃道什麼星座 , 以及該星座的度數 (0~30) */
  val signDegree: Pair<ZodiacSign, Double>
    get() = ZodiacSign.getSignAndDegree(lng)
}

data class Position(
  override val lng: Double,
  override val lat: Double,
  override val distance: Double,
  override val speedLng: Double,
  override val speedLat: Double,
  override val speedDistance: Double) : IPos, Serializable


interface IPositionWithAzimuth : IPos {
  val azimuth: Azimuth
}

data class PositionWithAzimuth(
  val pos: IPos,
  override val azimuth: Azimuth) : IPositionWithAzimuth, IPos by pos, Serializable
