/**
 * Created by smallufo on 2024-10-14.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.core.RequestDto
import destiny.core.SynastryGrain
import destiny.core.SynastryRelationship
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import kotlinx.serialization.SerialName
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
  @SerialName("aspects")
  val aspects: List<SynastryAspect>,
  val midpointTrees: List<SynastryMidpointTree>,
  val houseOverlayMap: Map<Int, List<HouseOverlay>>,
)

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
