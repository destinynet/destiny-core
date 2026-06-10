/**
 * Created for the World Cup national-chart experiment (three-ring Transit→SA→Natal).
 *
 * A time-robust projection of [EventModel] for charts whose NATAL has no birth time
 * (national/foundation charts cast at noon). Everything time-dependent is dropped:
 * houses, angles (Axis = ASC/MC/...), profections, firdaria, lunar returns, and the
 * SP/TP rings. Only planet-to-planet aspects across the three kept rings survive.
 *
 * The unreliable bodies are the NATAL Moon and its solar-arc image (noon ±6.5°); the
 * TRANSIT Moon is kept (the event/kickoff IS timed). See docs/worldcup/three-ring-projection.md.
 *
 * Typed (not JSON-key deletion) on purpose: if [EventModel] changes shape, this fails at
 * COMPILE time — critical for a blind test where a silent time-leak invalidates results.
 */
package destiny.core.astrology

import destiny.tools.serializers.IZodiacDegreeTwoDecimalSerializer
import destiny.tools.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class NatalOnlyDayEventModel(
  @Serializable(with = LocalDateSerializer::class)
  val localDate: LocalDate,
  /** natal planet/node longitudes (no Axis; natal Moon dropped when requested) */
  val natalPositions: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>,
  /** natal internal planet-to-planet aspects */
  val natalAspects: List<SynastryAspect>,
  /** solar-arc directed positions */
  val solarArcPositions: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>,
  /** SA → natal aspects (outer = SA, inner = natal) */
  val solarArcToNatal: List<SynastryAspect>,
  /** transit (kickoff) longitudes — transit Moon kept (reliable) */
  val transitPositions: Map<AstroPoint, @Serializable(with = IZodiacDegreeTwoDecimalSerializer::class) IZodiacDegree>,
  /** transit → natal aspects (outer = transit, inner = natal) */
  val transitToNatal: List<SynastryAspect>,
  /** transit → SA aspects (outer = transit, inner = SA) */
  val transitToSolarArc: List<SynastryAspect>,
)

/**
 * Project a full [EventModel] down to the time-robust [NatalOnlyDayEventModel].
 *
 * @param dropNatalSaMoon when true (default), drops the natal Moon and its solar-arc image
 *   (and any aspect touching them); the transit Moon is always kept.
 */
fun EventModel.toNatalOnlyDay(dropNatalSaMoon: Boolean = true): NatalOnlyDayEventModel {
  fun AstroPoint.isAxis() = this is Axis
  fun AstroPoint.isMoon() = this == Planet.MOON

  // --- Natal ring (planets only; drop Axis + optionally natal Moon) ---
  val natalPos = natal.stars
    .filterKeys { !it.isAxis() && !(dropNatalSaMoon && it.isMoon()) }
    .mapValues { it.value.signDegree }

  val natalAsp = natal.tightestAspects
    .filter { ap ->
      ap.points.size == 2 &&
        ap.points.none { it.isAxis() } &&
        !(dropNatalSaMoon && ap.points.any { it.isMoon() })
    }
    .map { ap ->
      SynastryAspect(
        outerPoint = ap.points[0], innerPoint = ap.points[1],
        aspect = ap.aspect, orb = ap.orb, aspectType = ap.aspectType, score = ap.score
      )
    }

  // --- SA ring (positions + SA→natal; outer = SA, inner = natal) ---
  val saPos = solarArcModel.positionMap
    .filterKeys { !it.isAxis() && !(dropNatalSaMoon && it.isMoon()) }

  val saToNatal = solarArcModel.synastryAspects.filter { a ->
    !a.outerPoint.isAxis() && !a.innerPoint.isAxis() &&
      !(dropNatalSaMoon && (a.outerPoint.isMoon() || a.innerPoint.isMoon()))
  }

  // --- Transit ring (keep transit Moon; only drop natal/SA Moon on the inner side) ---
  val transitPos = transit.horoscope.stars
    .filterKeys { !it.isAxis() }
    .mapValues { it.value.signDegree }

  val tToNatal = transit.synastry.aspects.filter { a ->
    !a.outerPoint.isAxis() && !a.innerPoint.isAxis() &&
      !(dropNatalSaMoon && a.innerPoint.isMoon())   // inner = natal; transit Moon (outer) kept
  }

  val tToSa = transitToSolarArcAspects.filter { a ->
    !a.outerPoint.isAxis() && !a.innerPoint.isAxis() &&
      !(dropNatalSaMoon && a.innerPoint.isMoon())   // inner = SA; transit Moon (outer) kept
  }

  return NatalOnlyDayEventModel(
    localDate = localDate,
    natalPositions = natalPos,
    natalAspects = natalAsp,
    solarArcPositions = saPos,
    solarArcToNatal = saToNatal,
    transitPositions = transitPos,
    transitToNatal = tToNatal,
    transitToSolarArc = tToSa,
  )
}
