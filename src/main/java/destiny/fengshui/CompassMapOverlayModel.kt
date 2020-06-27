/**
 * Created by smallufo on 2020-01-04.
 */
package destiny.fengshui

import destiny.astrology.Planet
import destiny.core.ITimeLoc
import destiny.tools.location.IStaticMap
import java.io.Serializable

interface ICompassMapOverlayModel : ITimeLoc, Serializable {
  val timeLoc: ITimeLoc
  val place: String?

  val width: Int

  /** 北方朝上 */
  val rotate: Double

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

  /** 內定為道路圖 */
  val mapType: IStaticMap.MapType
}

data class CompassMapOverlayModel(
  override val timeLoc: ITimeLoc,
  override val place: String? = null,
  override val width: Int = 640,
  /** 北方朝上 */
  override val rotate: Double = 0.0,
  /**
   * 14 : 城鎮
   * 16 : 街道
   * 18 : 建物
   */
  override val zoom: Int = 16,
  /** 不透明度 , 0.0 最透明 */
  override val opaque: Float = 0.4f,
  /** 放大倍數 1,2 or 4 */
  override val scale: Int = 1,
  /** 內定為道路圖 */
  override val mapType: IStaticMap.MapType = IStaticMap.MapType.roadmap
) : ITimeLoc by timeLoc, ICompassMapOverlayModel {

  constructor(model: ICompassMapOverlayModel) : this(
    model.timeLoc, model.place, model.width,
    model.rotate, model.zoom, model.opaque,
    model.scale, model.mapType)
}

interface ICompassPointsMapOverlayModel : ICompassMapOverlayModel {
  /** 要繪製的行星 */
  val planets: Set<Planet>
}

data class CompassPointsMapOverlayModel(val embedded: ICompassMapOverlayModel,
                                        override val planets: Set<Planet> = emptySet()) :
  ICompassPointsMapOverlayModel, ICompassMapOverlayModel by embedded
