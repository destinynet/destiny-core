/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.serializers.ZodiacDegreeTwoDecimalSerializer
import kotlinx.serialization.Serializable


@Serializable
data class MundanePointData(
  /** 天頂赤經 (Right Ascension of the Midheaven)，也等於當地恆星時（以度數表示） */
  val armc: Double,
  /** 上升點 (Ascendant) 的黃道經度 */
  @Serializable(with = ZodiacDegreeTwoDecimalSerializer::class)
  val ascendant: ZodiacDegree,
  /** 天頂 (Midheaven) 的黃道經度 */
  @Serializable(with = ZodiacDegreeTwoDecimalSerializer::class)
  val mc: ZodiacDegree,
  /** 宿命點 (Vertex) 的黃道經度 */
  @Serializable(with = ZodiacDegreeTwoDecimalSerializer::class)
  val vertex: ZodiacDegree,
  /** 赤道上升點 (Equatorial Ascendant)，又稱東方點，其值為赤經度數 */
  val equatorialAscendant: Double
): java.io.Serializable

interface IMundanePoint {
  fun getMundanePoints(gmtJulDay: GmtJulDay, loc: ILocation): MundanePointData
}
