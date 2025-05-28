/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.misc

import destiny.core.FlowScale
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.eightwords.*
import destiny.core.calendar.eightwords.FlowDayHourPatterns.affecting
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowDayHourPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowDayHourPatterns.trilogyToFlow
import destiny.core.chinese.eightwords.PersonPresentConfig
import destiny.core.chinese.eightwords.PersonPresentFeature
import jakarta.inject.Named


@Named
class DayHourSelectionService(
  private val ewPersonPresentFeature: PersonPresentFeature,
  private val ewFeature: EightWordsFeature,
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit
) {

  fun search(model: Electional.ITraversalModel, ewConfig: IPersonPresentConfig = PersonPresentConfig()): Sequence<DayHourEvent> {
    require(model.toDate.isAfter(model.fromDate)) { "toDate must be after the fromDate" }

    val fromGmtJulDay = model.fromDate.atTime(0, 0).toGmtJulDay(model.loc)
    val toGmtJulDay = model.toDate.plusDays(1).atTime(0, 0).toGmtJulDay(model.loc)

    return (searchAstrologyEvents(model.bdnp, fromGmtJulDay, toGmtJulDay) + searchEw(model.bdnp, fromGmtJulDay, toGmtJulDay, model.loc, ewConfig))
  }

  private fun searchAstrologyEvents(bdnp: IBirthDataNamePlace, fromGmtJulDay: GmtJulDay, toGmtJulDay: GmtJulDay): Sequence<DayHourEvent.AstroEvent> {
    val innerStars = setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)

    val harmonyAngles = setOf(0.0, 60.0, 120.0, 240.0, 300.0)
    val tensionAngles = setOf(90.0, 180.0)

    val innerStarPosMap: Map<Planet, ZodiacDegree> = innerStars.associateWith { planet ->
      starPositionImpl.getPosition(planet, bdnp.gmtJulDay, bdnp.location).lngDeg
    }

    fun searchEvents(outerStars: Set<Planet>, angles: Set<Double>): Sequence<AspectData> {
      return outerStars.asSequence().flatMap { outer ->
        innerStars.flatMap { inner ->
          val innerDeg: ZodiacDegree = innerStarPosMap[inner]!!
          val degrees = angles.map { it.toZodiacDegree() }.map { it + innerDeg }.toSet()
          starTransitImpl.getRangeTransitGmt(outer, degrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC).map { (zDeg, gmt) ->
            val angle: Double = zDeg.getAngle(innerDeg)
            val pattern = PointAspectPattern(listOf(outer, inner), angle, null, 0.0)
            AspectData(pattern, null, 0.0, null, gmt)
          }
        }
      }
    }

    return sequenceOf(
      searchEvents(innerStars, harmonyAngles).map { aspectData -> DayHourEvent.AstroEvent(DayHourEvent.Type.GOOD, aspectData) },
      searchEvents(innerStars, tensionAngles).map { aspectData -> DayHourEvent.AstroEvent(DayHourEvent.Type.BAD, aspectData) }
    ).flatten()
  }

  private fun matchEwEvents(gmtJulDay: GmtJulDay, outer: IEightWords, inner: IEightWords): Sequence<DayHourEvent.EwEvent> {
    val affecting = with(affecting) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern ->
        pattern as FlowPattern.Affecting
      }.map { pattern -> DayHourEvent.EwEvent.StemAffecting(gmtJulDay, pattern, outer) }
    }

    val stemCombined = with(stemCombined) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.StemCombined
      }.map { pattern -> DayHourEvent.EwEvent.StemCombined(gmtJulDay, pattern, outer) }
    }

    val branchCombined = with(branchCombined) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.BranchCombined
      }.map { pattern -> DayHourEvent.EwEvent.BranchCombined(gmtJulDay, pattern, outer) }
    }

    val trilogyToFlow = with(trilogyToFlow) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.TrilogyToFlow
      }.map { pattern -> DayHourEvent.EwEvent.TrilogyToFlow(gmtJulDay, pattern, outer) }
    }

    val toFlowTrilogy = with(toFlowTrilogy) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.ToFlowTrilogy
      }.filter { pattern ->
        /**
         * 限制 [FlowPattern.ToFlowTrilogy.flows] 必須至少包含 [FlowScale.DAY] or [FlowScale.HOUR]
         * 這樣找「流日」「流時」才有意義
         */
        val flowScales = pattern.flows.map { it.first }
        flowScales.any { it == FlowScale.DAY || it == FlowScale.HOUR }
      }.map { pattern -> DayHourEvent.EwEvent.ToFlowTrilogy(gmtJulDay, pattern, outer) }
    }

    val branchOpposition = with(branchOpposition) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.BranchOpposition
      }.map { pattern -> DayHourEvent.EwEvent.BranchOpposition(gmtJulDay, pattern, outer) }
    }

    return sequenceOf(affecting, stemCombined, branchCombined, trilogyToFlow, toFlowTrilogy, branchOpposition).flatten()
  }

  private fun searchEw(
    bdnp: IBirthDataNamePlace,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation = bdnp.location,
    config: IPersonPresentConfig = PersonPresentConfig()
  ): Sequence<DayHourEvent.EwEvent> {

    val personEw = ewPersonPresentFeature.getPersonModel(bdnp, config).eightWords
    logger.debug { "本命八字 : $personEw" }
    val fromEw: IEightWords = ewFeature.getModel(fromGmtJulDay, loc, config)

    return generateSequence(fromEw to fromGmtJulDay) { (outerEw, gmtJulDay) ->


      ewFeature.next(gmtJulDay + 0.01, loc, config)
    }.takeWhile { (outerEw, gmtJulDay) -> gmtJulDay < toGmtJulDay }
      .flatMap { (outerEw, gmtJulDay) ->
        matchEwEvents(gmtJulDay, outerEw, personEw)
      }
  }
}
