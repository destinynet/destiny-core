/**
 * Created by smallufo on 2026-02-16.
 *
 * Common interface for star position summaries.
 * Implemented by both [Natal.StarPosInfo] (full detail) and
 * [destiny.core.astrology.prediction.StarSummary] (lightweight).
 */
package destiny.core.astrology

import destiny.core.astrology.Natal.StarPosInfo
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.Serializable

interface IStarSummary {
  val sign: ZodiacSign
  val degree: Double
  val house: Int?
  val isRetrograde: Boolean
}

/**
 * Lightweight planet position for Return chart summaries.
 * Simplified from [StarPosInfo] — drops element, quality, rulingHouses, dispositors, astroAspects.
 */
@Serializable
data class StarSummary(
  override val sign: ZodiacSign,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  override val degree: Double,
  override val house: Int,
  override val isRetrograde: Boolean = false
) : IStarSummary {
  companion object {
    fun from(starPosInfo: StarPosInfo): StarSummary {
      return StarSummary(
        sign = starPosInfo.sign,
        degree = starPosInfo.degree,
        house = starPosInfo.house ?: 1,
        isRetrograde = starPosInfo.isRetrograde
      )
    }
  }
}
