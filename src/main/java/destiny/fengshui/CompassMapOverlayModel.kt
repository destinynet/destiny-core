/**
 * Created by smallufo on 2020-01-04.
 */
package destiny.fengshui

import destiny.astrology.Planet
import destiny.core.TimeLoc
import destiny.core.calendar.ILocation
import destiny.tools.location.MapType
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface ICompassMapOverlayModel : ICompassMapsModel, Serializable {

  val width: Int

  /** 北方朝上 */
  val rotate: Double

  /** 地磁偏角 */
  val magDeclination: Double

  val finalRotate: Double
    get() = if (northType == NorthType.TRUE) rotate else rotate + magDeclination

  /**
   * 14 : 城鎮
   * 16 : 街道
   * 18 : 建物
   */
  val zoom: Int

  /** 不透明度 , 0.0 最透明 */
  val opaque: Float

  /** 放大倍數 1,2 or 4 (Pay-As-You-Go 不支援 4) */
  val scale: Int

}

data class CompassMapOverlayModel(
  val embedded: ICompassMapsModel,

  override val width: Int = 640,

  /** 北方朝上 */
  override val rotate: Double = 0.0,

  /** 地磁偏角 */
  override val magDeclination: Double,
  /**
   * 14 : 城鎮
   * 16 : 街道
   * 18 : 建物
   */
  override val zoom: Int = 16,
  /** 不透明度 , 0.0 最透明 */
  override val opaque: Float = 0.4f,
  /** 放大倍數 1,2 or 4 */
  override val scale: Int = 1
) : ICompassMapsModel by embedded, ICompassMapOverlayModel {

  constructor(time: ChronoLocalDateTime<*>, location: ILocation, place: String? = null, northType: NorthType = NorthType.TRUE, mapType: MapType = MapType.roadmap,
              width: Int = 640, rotate: Double = 0.0, magDeclination: Double,
              zoom: Int = 16, opaque: Float = 0.4f, scale: Int = 1) : this(
    CompassMapsModel(TimeLoc(time, location) , place, northType, mapType) , width, rotate, magDeclination, zoom, opaque, scale
  )
}

interface ICompassPointsMapOverlayModel : ICompassMapOverlayModel {
  /** 要繪製的行星 */
  val planets: Set<Planet>
}

data class CompassPointsMapOverlayModel(
  val embedded: ICompassMapOverlayModel,
  override val planets: Set<Planet> = emptySet()
) :
  ICompassPointsMapOverlayModel, ICompassMapOverlayModel by embedded
