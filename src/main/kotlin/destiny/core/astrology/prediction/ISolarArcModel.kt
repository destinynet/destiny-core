/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.SynastryAspect
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.IZodiacDegreeTwoDecimalSerializer
import kotlinx.serialization.Serializable


sealed interface ISolarArcModel {
  val natalGmtJulDay: GmtJulDay
  val natalHasTime : Boolean
  val viewGmtJulDay: GmtJulDay
  val loc : ILocation
  val positionMap: Map<AstroPoint, IZodiacDegree>
  val synastryAspects: List<SynastryAspect>
}

@Serializable
data class SolarArcModel(
  override val natalGmtJulDay: GmtJulDay,
  override val natalHasTime: Boolean,
  override val viewGmtJulDay: GmtJulDay,
  @Serializable(with = ILocationSerializer::class)
  override val loc: ILocation,
  override val positionMap: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>,
  override val synastryAspects: List<SynastryAspect>
) : ISolarArcModel
