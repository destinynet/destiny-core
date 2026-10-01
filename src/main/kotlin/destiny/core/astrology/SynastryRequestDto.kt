/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.core.RequestDto
import destiny.core.SynastryGrain
import destiny.core.SynastryRelationship
import destiny.core.astrology.classical.Overcoming
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import kotlinx.serialization.Serializable

@Serializable
data class SynastryMidpointTree(val inner: AstroPoint,
                                val outer: AstroPoint,
                                val aspect: Aspect,
                                @Serializable(with = DoubleTwoDecimalSerializer::class)
                                val orb: Double,
                                val involved: List<MidPointFocalAspect>)

@RequestDto
@Serializable
class SynastryRequestDto(
  @Serializable(with = IBirthDataNamePlaceSerializer::class)
  val inner: IBirthDataNamePlace,
  @Serializable(with = IBirthDataNamePlaceSerializer::class)
  val outer: IBirthDataNamePlace,
  val grain: SynastryGrain,
  val relationship: SynastryRelationship?,
  val aspects: List<SynastryAspect>,
  val midpointTrees: List<SynastryMidpointTree>,
  val houseOverlayMap: Map<Int, List<HouseOverlay>>,
  /** 兩盤之間的居上關係 (僅古典七星) */
  val overcomings: List<SynastryOvercoming> = emptyList(),
)

/**
 * 合盤中的居上關係 : [overcoming] 的 superior 屬於 [superiorSide] 那一盤 , inferior 屬於另一盤
 */
@Serializable
data class SynastryOvercoming(
  val superiorSide: Side,
  val overcoming: Overcoming,
) {
  enum class Side { INNER, OUTER }

  val outerPoint: AstroPoint
    get() = if (superiorSide == Side.OUTER) overcoming.superior else overcoming.inferior

  val innerPoint: AstroPoint
    get() = if (superiorSide == Side.INNER) overcoming.superior else overcoming.inferior
}

data class HouseOverlayRow(val point: AstroPoint, val inner: Int, val innerToOuter: Int, val outer : Int, val outerToInner: Int)


/**
 * outer 星體，映射到 inner natal 的第幾宮 , 以及距離宮首幾度
 */
@Serializable
data class HouseOverlay(
  val outerPoint: AstroPoint,
  val innerHouse: Int,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val degreeToCusp: Double,
)

@Serializable
data class Synastry(
  val aspects: List<SynastryAspect>,
  val houseOverlayMap: Map<Int, List<HouseOverlay>>
)
