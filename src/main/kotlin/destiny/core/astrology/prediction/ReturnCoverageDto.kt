/**
 * Created by smallufo on 2026-02-16.
 *
 * Lightweight Return chart summary with range coverage info,
 * for embedding in [destiny.core.astrology.ITimeLineEventsModel].
 *
 * Much smaller than [IReturnDto] which embeds full [destiny.core.astrology.IHoroscopeDto]
 * (harmonics, midpoints, graphPatterns, classicalPatterns, etc.)
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Axis
import destiny.core.astrology.IStarSummary
import destiny.core.astrology.Natal.StarPosInfo
import destiny.core.astrology.Planet
import destiny.core.astrology.StarSummary
import destiny.core.astrology.Synastry
import destiny.core.astrology.SynastryAspect
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable





// ── Shared extraction utilities ─────────────────────────────────────────────

/**
 * Filter aspects by score threshold and return sorted by score descending.
 */
fun Synastry.keyAspects(scoreThreshold: Double = 0.95): List<SynastryAspect> =
  aspects
    .filter { (it.score?.value ?: 0.0) >= scoreThreshold }
    .sortedByDescending { it.score }

/**
 * Convert [Synastry.houseOverlayMap] to a simplified Map of natal-house → list of return [Planet].
 * Filters out non-Planet points and empty entries.
 */
fun Synastry.simplifiedHouseOverlay(): Map<Int, List<Planet>> =
  houseOverlayMap
    .mapValues { (_, overlays) -> overlays.map { it.outerPoint }.filterIsInstance<Planet>() }
    .filter { (_, planets) -> planets.isNotEmpty() }


// ── ReturnCoverageDto ───────────────────────────────────────────────────────

@Serializable
data class ReturnCoverageDto(
  val returnType: ReturnType,
  @Contextual
  val validFrom: GmtJulDay,
  @Contextual
  val validTo: GmtJulDay,
  /** Coverage percentage within the search range (0–100) */
  val coveragePercent: Int,

  val ascSign: ZodiacSign,
  val ascDegree: Int,
  val mcSign: ZodiacSign,
  val mcDegree: Int,

  /** Key planet positions (all 10 planets) */
  val planets: Map<Planet, StarSummary>,

  /** Top aspects between return chart and natal chart (pre-filtered by score) */
  val keyAspectsToNatal: List<SynastryAspect>,

  /** Which return planets fall into which natal house */
  val houseOverlay: Map<Int, List<Planet>>
) {
  companion object {
    /**
     * Convert from [IReturnDto] to [ReturnCoverageDto].
     *
     * @param dto             Original return DTO (heavy, with full IHoroscopeDto)
     * @param fromGmt         Search range start
     * @param toGmt           Search range end
     * @param scoreThreshold  Only include aspects with score >= threshold (default 0.95)
     */
    fun from(
      dto: IReturnDto,
      fromGmt: GmtJulDay,
      toGmt: GmtJulDay,
      scoreThreshold: Double = 0.95
    ): ReturnCoverageDto {
      val chart = dto.returnChart
      val asc = chart.stars[Axis.RISING]
      val mc = chart.stars[Axis.MERIDIAN]

      // Coverage percentage
      val overlapStart = maxOf(dto.validFrom, fromGmt)
      val overlapEnd = minOf(dto.validTo, toGmt)
      val rangeDays = toGmt.value - fromGmt.value
      val coveragePercent = if (rangeDays > 0) ((overlapEnd.value - overlapStart.value) / rangeDays * 100).toInt() else 0

      return ReturnCoverageDto(
        returnType = dto.returnType,
        validFrom = dto.validFrom,
        validTo = dto.validTo,
        coveragePercent = coveragePercent,
        ascSign = asc?.signDegree?.sign ?: ZodiacSign.ARIES,
        ascDegree = asc?.signDegree?.signDegree?.second?.toInt() ?: 0,
        mcSign = mc?.signDegree?.sign ?: ZodiacSign.ARIES,
        mcDegree = mc?.signDegree?.signDegree?.second?.toInt() ?: 0,
        planets = Planet.values.map { planet ->
          planet to chart.stars[planet]
        }.filter { (_, v) -> v != null }.associate { (k, v) ->
          k to StarSummary.from(v!!)
        },
        keyAspectsToNatal = dto.synastry.keyAspects(scoreThreshold),
        houseOverlay = dto.synastry.simplifiedHouseOverlay()
      )
    }
  }
}
