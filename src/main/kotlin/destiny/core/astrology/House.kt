/**
 * Created by smallufo on 2022-03-11.
 */
package destiny.core.astrology

import destiny.tools.serializers.IZodiacDegreeSerializer
import kotlinx.serialization.Serializable


enum class HouseType {
  ANGULAR,
  SUCCEDENT,
  CADENT
}

data class House(val index: Int,
                 val cusp: ZodiacDegree,
                 val pointPositions: List<Pair<AstroPoint, IPosWithAzimuth>>) : java.io.Serializable

@Serializable
data class HouseDto(
  val id: Int,
  @Serializable(with = IZodiacDegreeSerializer::class)
  val cusp: IZodiacDegree,
  val ruler: AstroPoint,
  val stars: List<AstroPoint> = emptyList()
)
