package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.tools.CircleTools.normalize
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.Serializable

@Serializable
sealed interface IPos : IZodiacDegree {
  val lng: Double
  val lat: Double

  val lngDeg : ZodiacDegree
    get() = lng.toZodiacDegree()


  operator fun plus(p: IPos): IPos {
    return Pos((this.lngDeg + p.lngDeg).value, this.lat + p.lat)
  }

  operator fun minus(p: IPos): IPos {
    return Pos((this.lngDeg - p.lngDeg).value, this.lat - p.lat)
  }
}

@Serializable
data class Pos(
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val lng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val lat: Double) : IPos {
  override val zDeg: Double
    get() = lng
}


interface IPosWithAzimuth : IPos, IAzimuth

data class PosWithAzimuth(val pos: IPos, val azimuth: IAzimuth) : IPosWithAzimuth, IPos by pos, IAzimuth by azimuth

interface IStarPos : IPos {
  val distance: Double  // in AU
  val speedLng: Double  // speed in lng (degree / day)
  val speedLat: Double  // speed in lat (degree / day)
  val speedDistance: Double // speed in distance (AU / day)

  operator fun plus(p: IStarPos): IStarPos {
    return StarPosition(
      super.plus(p),
      this.distance + p.distance,
      (this.speedLng + p.speedLng).normalize(),
      this.speedLat + p.speedLat,
      this.speedDistance + p.speedDistance
    )
  }

  operator fun minus(p: IStarPos): IStarPos {
    return StarPosition(
      super.minus(p),
      this.distance - p.distance,
      (this.speedLng - p.speedLng).normalize(),
      this.speedLat - p.speedLat,
      this.speedDistance - p.speedDistance
    )
  }
}

@Serializable
data class StarPosition(
  val pos: IPos,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val distance: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val speedLng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val speedLat: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val speedDistance: Double
) : IStarPos, IPos by pos

interface IStarPositionWithAzimuth : IStarPos, IPosWithAzimuth, IAzimuth

data class StarPosWithAzimuth(
  val starPos: IStarPos,
  val azimuth: Azimuth) : IStarPositionWithAzimuth, IStarPos by starPos, IAzimuth by azimuth, java.io.Serializable
