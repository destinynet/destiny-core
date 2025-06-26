/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.SynastryAspect
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import destiny.tools.serializers.GmtJulDaySerializer
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.IZodiacDegreeTwoDecimalSerializer
import kotlinx.serialization.Serializable


sealed interface ISolarArcModel {
  val natalGmtJulDay: GmtJulDay
  val natalHasTime : Boolean
  val viewGmtJulDay: GmtJulDay
  val forward: Boolean
  val convergentJulDay: GmtJulDay
  // may be negative (if forward = false)
  val degreeMoved: Double
  val loc : ILocation
  val positionMap: Map<AstroPoint, IZodiacDegree>
  val synastryAspects: List<SynastryAspect>
}

@Serializable
data class SolarArcModel(
  @Serializable(with = GmtJulDaySerializer::class)
  override val natalGmtJulDay: GmtJulDay,
  override val natalHasTime: Boolean,
  override val viewGmtJulDay: GmtJulDay,
  override val forward: Boolean = true,
  override val convergentJulDay: GmtJulDay,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val degreeMoved: Double,
  @Serializable(with = ILocationSerializer::class)
  override val loc: ILocation,
  override val positionMap: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>,
  override val synastryAspects: List<SynastryAspect>
) : ISolarArcModel
