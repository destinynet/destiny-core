/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.IBirthDataNamePlace
import destiny.core.Scale
import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.IVoidCourseFeature
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
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
import destiny.core.chinese.eightwords.PersonPresentFeature
import destiny.core.electional.DayHourEvent.*
import jakarta.inject.Named


@Named
class DayHourService(
  private val ewPersonPresentFeature: PersonPresentFeature,
  private val ewFeature: EightWordsFeature,
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val relativeTransitImpl: IRelativeTransit,
  private val voidCourseFeature: IVoidCourseFeature,
  private val retrogradeImpl : IRetrograde,
) {

  fun traverse(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config : Config): Sequence<DayHourEvent> {
    require(model.toDate.isAfter(model.fromDate)) { "toDate must be after the fromDate" }

    val loc = model.loc?:bdnp.location

    val fromGmtJulDay = model.fromDate.atTime(0, 0).toGmtJulDay(loc)
    val toGmtJulDay = model.toDate.plusDays(1).atTime(0, 0).toGmtJulDay(loc)

    return sequence {
      config.ewConfig?.also {
        yieldAll(searchEwEvents(bdnp, fromGmtJulDay, toGmtJulDay, loc, it))
      }
      config.astrologyConfig?.also {
        yieldAll(searchAstrologyEvents(bdnp, fromGmtJulDay, toGmtJulDay, loc, it))
      }
    }

  }

  private fun searchAstrologyEvents(bdnp: IBirthDataNamePlace, fromGmtJulDay: GmtJulDay, toGmtJulDay: GmtJulDay, loc: ILocation, config: Config.AstrologyConfig): Sequence<AstroEvent> {
    val innerStars = setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)

    val harmonyAngles = setOf(0.0, 60.0, 120.0, 240.0, 300.0)
    val tensionAngles = setOf(90.0, 180.0)

    val innerStarPosMap: Map<Planet, ZodiacDegree> = innerStars.associateWith { planet ->
      starPositionImpl.getPosition(planet, bdnp.gmtJulDay, bdnp.location).lngDeg
    }

    fun searchPersonalEvents(outerStars: Set<Planet>, angles: Set<Double>): Sequence<AspectData> {
      return outerStars.asSequence().flatMap { outer ->
        innerStars.flatMap { inner ->
          innerStarPosMap[inner]?.let { innerDeg ->
            val degrees = angles.map { it.toZodiacDegree() }.map { it + innerDeg }.toSet()
            starTransitImpl.getRangeTransitGmt(outer, degrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC).map { (zDeg, gmt) ->
              val angle: Double = zDeg.getAngle(innerDeg)
              val pattern = PointAspectPattern(listOf(outer, inner), angle, null, 0.0)
              AspectData(pattern, null, 0.0, null, gmt)
            }
          }?: emptyList()
        }
      }
    }

    val globalEvents = relativeTransitImpl.mutualAspectingEvents(
      innerStars, setOf(harmonyAngles, tensionAngles).flatten().toSet(),
      fromGmtJulDay, toGmtJulDay
    ).map { aspectData ->
      val type = when (aspectData.aspect) {
        Aspect.CONJUNCTION, Aspect.SEXTILE, Aspect.TRINE -> Type.GOOD
        Aspect.SQUARE, Aspect.OPPOSITION                 -> Type.BAD
        else                                             -> throw RuntimeException("Unknown aspect ${aspectData.aspect}")
      }
      AstroEvent.AspectEvent(type, aspectData, Impact.GLOBAL)
    }

    val vocConfig = VoidCourseConfig(MOON , vocImpl = VoidCourseImpl.Medieval)
    val moonVocSeq: Sequence<AstroEvent.MoonVoc> = voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, vocConfig).map { AstroEvent.MoonVoc(it) }

    // 滯留
    val planetStationaries = sequenceOf(MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
      retrogradeImpl.getRangeStationaries(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl).map { s ->
        AstroEvent.PlanetStationary(s)
      }
    }

    // 星體當日逆行
    val planetRetrogrades = sequenceOf(MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
      retrogradeImpl.getDailyRetrogrades(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl, starTransitImpl).map { (gmtJulDay,progress) ->
        AstroEvent.PlanetRetrograde(planet, gmtJulDay, progress)
      }
    }

    return sequence {
      // 全球星體交角
      yieldAll(globalEvents)
      // 全球 to 個人 , 和諧交角
      yieldAll(searchPersonalEvents(innerStars, tensionAngles).map { aspectData -> AstroEvent.AspectEvent(Type.BAD, aspectData, Impact.PERSONAL) })
      // 全球 to 個人 , 緊張交角
      yieldAll(searchPersonalEvents(innerStars, tensionAngles).map { aspectData -> AstroEvent.AspectEvent(Type.BAD, aspectData, Impact.PERSONAL) })
      // 月亮空亡
      if (config.voc) {
        yieldAll(moonVocSeq)
      }
      if (config.retrograde) {
        // 內行星滯留
        yieldAll(planetStationaries)
        // 星體當日逆行
        yieldAll(planetRetrogrades)
      }
    }
  }

  private val supportedScales = setOf(Scale.DAY, Scale.HOUR)

  private fun matchEwEvents(gmtJulDay: GmtJulDay, outer: IEightWords, inner: IEightWords, config : Config.EwConfig): Sequence<EwEvent> {

    val globalStemCombined: Sequence<EwEvent.EwGlobalEvent.StemCombined> = with(IdentityPatterns.stemCombined) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.StemCombined>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .map { p -> EwEvent.EwGlobalEvent.StemCombined(gmtJulDay, p, outer) }
    }

    val globalBranchCombined = with(IdentityPatterns.branchCombined) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.BranchCombined>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .map { p -> EwEvent.EwGlobalEvent.BranchCombined(gmtJulDay, p, outer) }
    }

    val globalTrilogy = with(IdentityPatterns.trilogy) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.Trilogy>()
      .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
      .map { p -> EwEvent.EwGlobalEvent.Trilogy(gmtJulDay, p, outer) }
    }

    val globalBranchOpposition = with(IdentityPatterns.branchOpposition) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.BranchOpposition>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .map { p -> EwEvent.EwGlobalEvent.BranchOpposition(gmtJulDay, p, outer)}
    }

    val globalStemRooted = with(IdentityPatterns.stemRooted) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.StemRooted>()
        .filter { p -> p.scale in supportedScales }
        .map { p -> EwEvent.EwGlobalEvent.StemRooted(gmtJulDay, p, outer) }
    }

    val auspiciousDays = with(IdentityPatterns.auspiciousPattern) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.AuspiciousPattern>()
        .filter { p ->
          //p.scales == setOf(Scale.DAY) // 只找日柱
          p.scales.contains(Scale.DAY) && !p.scales.contains(Scale.HOUR) // 找日柱、過濾掉時柱
        }
        .map { p -> EwEvent.EwGlobalEvent.AuspiciousDay(gmtJulDay, p, outer) }
    }

    val inauspiciousDays = with(IdentityPatterns.inauspiciousPattern) {
      outer.getPatterns().asSequence().filterIsInstance<IdentityPattern.InauspiciousPattern>()
        .filter { p ->
          p.scales.contains(Scale.DAY) && !p.scales.contains(Scale.HOUR) // 找日柱、過濾掉時柱
        }
        .map { p -> EwEvent.EwGlobalEvent.InauspiciousDay(gmtJulDay, p, outer) }
    }

    val personalAffecting: Sequence<EwEvent.EwPersonalEvent.StemAffecting> = with(affecting) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern ->
        pattern as FlowPattern.Affecting
      }.map { pattern -> EwEvent.EwPersonalEvent.StemAffecting(gmtJulDay, pattern, outer) }
    }

    val personalStemCombined = with(stemCombined) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.StemCombined
      }.map { pattern -> EwEvent.EwPersonalEvent.StemCombined(gmtJulDay, pattern, outer) }
    }

    val personalBranchCombined = with(branchCombined) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.BranchCombined
      }.map { pattern -> EwEvent.EwPersonalEvent.BranchCombined(gmtJulDay, pattern, outer) }
    }

    val personalTrilogyToFlow = with(trilogyToFlow) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.TrilogyToFlow
      }.map { pattern -> EwEvent.EwPersonalEvent.TrilogyToFlow(gmtJulDay, pattern, outer) }
    }

    val personalToFlowTrilogy = with(toFlowTrilogy) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.ToFlowTrilogy
      }.filter { pattern ->
        /**
         * 限制 [FlowPattern.ToFlowTrilogy.flows] 必須至少包含 [FlowScale.DAY] or [FlowScale.HOUR]
         * 這樣找「流日」「流時」才有意義
         */
        val flowScales = pattern.flows.map { it.first }
        flowScales.any { it == FlowScale.DAY || it == FlowScale.HOUR }
      }.map { pattern -> EwEvent.EwPersonalEvent.ToFlowTrilogy(gmtJulDay, pattern, outer) }
    }

    val personalBranchOpposition = with(branchOpposition) {
      inner.getPatterns(outer.day, outer.hour).asSequence().map { pattern: FlowPattern ->
        pattern as FlowPattern.BranchOpposition
      }.map { pattern -> EwEvent.EwPersonalEvent.BranchOpposition(gmtJulDay, pattern, outer) }
    }

    return sequence {
      yieldAll(globalStemCombined)
      yieldAll(globalBranchCombined)
      yieldAll(globalTrilogy)
      yieldAll(globalBranchOpposition)
      yieldAll(globalStemRooted)
      if (config.shanSha) {
        yieldAll(auspiciousDays)
        yieldAll(inauspiciousDays)
      }
      yieldAll(personalAffecting)
      yieldAll(personalStemCombined)
      yieldAll(personalBranchCombined)
      yieldAll(personalTrilogyToFlow)
      yieldAll(personalToFlowTrilogy)
      yieldAll(personalBranchOpposition)
    }
  }

  private fun searchEwEvents(
    bdnp: IBirthDataNamePlace,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation = bdnp.location,
    config: Config.EwConfig
  ): Sequence<EwEvent> {

    val ewPersonPresentConfig = config.personPresentConfig
    val personEw = ewPersonPresentFeature.getPersonModel(bdnp, ewPersonPresentConfig).eightWords
    logger.debug { "本命八字 : $personEw" }
    val fromEw: IEightWords = ewFeature.getModel(fromGmtJulDay, loc, ewPersonPresentConfig)

    return generateSequence(fromEw to fromGmtJulDay) { (outerEw, gmtJulDay) ->

      ewFeature.next(gmtJulDay + 0.01, loc, ewPersonPresentConfig)
    }.takeWhile { (outerEw, gmtJulDay) -> gmtJulDay < toGmtJulDay }
      .flatMap { (outerEw, gmtJulDay) ->
        matchEwEvents(gmtJulDay, outerEw, personEw, config)
      }
  }
}
