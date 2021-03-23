package destiny.core.astrology

import destiny.tools.CircleTools
import java.io.Serializable

interface IPos : Serializable {
  val lng: Double
  val lat: Double

  /** 黃道什麼星座 */
  val sign: ZodiacSign
    get() = ZodiacSign.of(lng)

  /** 黃道什麼星座 , 以及該星座的度數 (0~30) */
  val signDegree: Pair<ZodiacSign, Double>
    get() = ZodiacSign.getSignAndDegree(lng)

  operator fun plus(p: IPos): IPos {
    return Pos(CircleTools.getNormalizeDegree(this.lng + p.lng), this.lat + p.lat)
  }

  operator fun minus(p: IPos): IPos {
    return Pos(CircleTools.getNormalizeDegree(this.lng - p.lng), this.lat - p.lat)
  }
}

data class Pos(override val lng: Double,
               override val lat: Double) : IPos


interface IPosWithAzimuth : IPos, IAzimuth

data class PosWithAzimuth(val pos: IPos,
                          val azimuth: IAzimuth) : IPosWithAzimuth, IPos by pos, IAzimuth by azimuth

interface IStarPos : IPos {
  val distance: Double  // in AU
  val speedLng: Double  // speed in lng (degree / day)
  val speedLat: Double  // speed in lat (degree / day)
  val speedDistance: Double // speed in distance (AU / day)

  operator fun plus(p: IStarPos): IStarPos {
    return StarPosition(super.plus(p),
      this.distance + p.distance,
      CircleTools.getNormalizeDegree(this.speedLng + p.speedLng),
      this.speedLat + p.speedLat,
      this.speedDistance + p.speedDistance)
  }

  operator fun minus(p: IStarPos): IStarPos {
    return StarPosition(super.minus(p),
      this.distance - p.distance,
      CircleTools.getNormalizeDegree(this.speedLng - p.speedLng),
      this.speedLat - p.speedLat,
      this.speedDistance - p.speedDistance)
  }
}

data class StarPosition(
  val pos: IPos,
  override val distance: Double,
  override val speedLng: Double,
  override val speedLat: Double,
  override val speedDistance: Double
) : IStarPos, IPos by pos

interface IStarPositionWithAzimuth : IStarPos, IPosWithAzimuth, IAzimuth

data class StarPosWithAzimuth(
  val starPos: IStarPos,
  val azimuth: Azimuth) : IStarPositionWithAzimuth, IStarPos by starPos, IAzimuth by azimuth, Serializable
