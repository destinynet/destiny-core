/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.electional

import destiny.core.*
import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.IVoidCourseFeature
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.eightwords.*
import destiny.core.calendar.eightwords.FlowDayHourPatterns.affecting
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowDayHourPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowDayHourPatterns.trilogyToFlow
import destiny.core.calendar.toLmt
import destiny.core.chinese.Branch
import destiny.core.chinese.eightwords.FlowDtoTransformer.toAffectingDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toToFlowTrilogyDtos
import destiny.core.chinese.eightwords.FlowDtoTransformer.toTrilogyToFlowDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toAuspiciousDto
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toBranchCombinedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toBranchOppositionDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toInauspiciousDto
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toStemCombinedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toStemRootedDtos
import destiny.core.chinese.eightwords.IdentityDtoTransformer.toTrilogyDtos
import destiny.core.chinese.eightwords.PersonPresentFeature
import destiny.core.electional.Ew.NatalBranches
import destiny.core.electional.Ew.NatalStems
import destiny.tools.getTitle
import destiny.tools.round
import destiny.tools.truncate
import jakarta.inject.Named
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS
import java.util.*


@Named
class DayHourService(
  private val ewPersonPresentFeature: PersonPresentFeature,
  private val ewFeature: EightWordsFeature,
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val relativeTransitImpl: IRelativeTransit,
  private val voidCourseFeature: IVoidCourseFeature,
  private val retrogradeImpl: IRetrograde,
  private val eclipseImpl: IEclipseFactory,
  private val horoscopeFeature: IHoroscopeFeature,
  private val modernAspectCalculator: IAspectCalculator,
  private val julDayResolver: JulDayResolver
) {


  /**
   * @param timeRange : 每日限定時間
   */
  fun aggregate(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config: Config, includeHour: Boolean, timeRange: TimeRange? = null): List<Daily> {

    val daySelector: (IEventDto) -> LocalDate = {
      when (it.event) {
        is Astro -> it.begin.toLocalDate()
        is Ew    -> {
          val ewEventDto = (it as EwEventDto)
          val outer: IEightWords = ewEventDto.outer

          if (outer.hour.branch == Branch.子) {
            // 當日凌晨的子正之後的(早)子時 , 不需要 +1日

            val loc = model.loc ?: bdnp.location
            val localDate = it.begin.toLocalDate()

            val noon = localDate.atTime(LocalTime.NOON)
            val noonGmt = noon.toGmtJulDay(loc)
            val noonEw = ewFeature.getModel(noonGmt, loc, config.ewConfig?.personPresentConfig ?: EightWordsConfig())

            // 中午的日柱 , 與 流日日柱不同的話，意味這應該屬於隔天的子時
            if (noonEw.day != it.outer.day) {
              localDate.plus(1, DAYS)
            } else {
              localDate
            }

          } else {
            it.begin.toLocalDate()
          }
        }
      }
    }

    return traverse(bdnp, model, config, includeHour).groupBy(daySelector).map { (date, events: List<IEventDto>) ->

      val allDayEvents = events
        .sortedBy { it.begin } // 確保先來的在前
        .fold(mutableListOf<IEventDto>()) { acc, event ->
          when (event) {
            is EwEventDto    -> {
              if (event.span == Span.DAY &&
                acc.none { it is EwEventDto && it.event == event.event }
              ) {
                logger.trace { "adding event (${event.span}) : $event" }
                acc.add(event)
              }
            }

            is AstroEventDto -> {
              if (event.span == Span.DAY &&
                acc.none { it is AstroEventDto && it.event == event.event }
              ) {
                logger.trace { "adding event (${event.span}) : $event" }
                acc.add(event)
              }
            }
          }
          acc
        }

      val nonAllDayEvents: Map<LocalDateTime, List<IEventDto>> =
        events.filter { it.span != Span.DAY }
          .filter { eventDto: IEventDto ->
            if (timeRange != null) {
              val (begin: LocalTime, end: LocalTime) = timeRange
              eventDto.begin >= date.atTime(begin) && eventDto.begin <= date.atTime(end)
            } else {
              true
            }
          }
          .sorted()
          .groupBy { it.begin }

      Daily(date, allDayEvents, nonAllDayEvents)
    }.sorted()
  }

  /** 純文字模式 輸出 */
  fun text(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config: Config, includeHour: Boolean, timeRange: TimeRange? = null): String {
    return aggregate(bdnp, model, config, includeHour, timeRange).joinToString("\n") { daily ->
      val date = daily.localDate
      buildString {
        appendLine("$date , ${date.dayOfWeek}")
        appendLine("\t全天")

        daily.allDayEvents.forEach { eventDto ->
          append("\t\t")
          append(
            when (eventDto.impact) {
              Impact.GLOBAL   -> " (g) "
              Impact.PERSONAL -> " (p) "
            }
          )
          appendLine(eventDto.event.description)
        }

        daily.hourEvents.forEach { (time, eventDtos: List<IEventDto>) ->
          append("\t$time")
          eventDtos.filterIsInstance<EwEventDto>().firstOrNull()?.also { ewEventDto ->
            append(" (${ewEventDto.outer.hour}時)")
          }
          appendLine()

          eventDtos.forEach { eventDto ->
            appendLine(
              buildString {
                append("\t\t")
                append(
                  when (eventDto.impact) {
                    Impact.GLOBAL   -> " (g) "
                    Impact.PERSONAL -> " (p) "
                  }
                )
                append(eventDto.event.description)
              }
            )
          }

        }
      }
    }
  }

  private fun traverse(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config: Config, includeHour: Boolean): Sequence<IEventDto> {
    require(!model.toDate.isBefore(model.fromDate)) { "toDate must be after the fromDate" }

    val loc = model.loc ?: bdnp.location

    val fromGmtJulDay = model.fromDate.atTime(0, 0).toGmtJulDay(loc)
    val toGmtJulDay = model.toDate.plusDays(1).atTime(0, 0).toGmtJulDay(loc)

    return sequence {
      config.ewConfig?.also {
        yieldAll(searchEwEvents(bdnp, fromGmtJulDay, toGmtJulDay, loc, includeHour, it))
      }
      config.astrologyConfig?.also {
        yieldAll(searchAstrologyEvents(bdnp, fromGmtJulDay, toGmtJulDay, loc, includeHour, it))
      }
    }
  }

  private fun List<SynastryAspect>.describeAspects(includeHour: Boolean): String {
    return this.sortedBy { it.orb }.joinToString("\n") { aspect: SynastryAspect ->
      buildString {
        append("\t")
        append("(p) [transit ${aspect.outerPoint.asLocaleString().getTitle(Locale.ENGLISH)}")
        if (includeHour) {
          append(" (H${aspect.outerPointHouse})")
        }
        append("] ")
        append(aspect.aspect)
        append(" [natal ${aspect.innerPoint.asLocaleString().getTitle(Locale.ENGLISH)}")
        if (includeHour) {
          append(" (H${aspect.innerPointHouse})")
        }
        append("] orb = ${aspect.orb.truncate(2)}")
      }
    }
  }

  private fun searchAstrologyEvents(
    bdnp: IBirthDataNamePlace,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: Config.AstrologyConfig
  ): Sequence<IEventDto> {

    fun GmtJulDay.toLmt(): LocalDateTime {
      return (this.toLmt(loc, julDayResolver) as LocalDateTime).roundAndTruncate()
    }

    val outerStars = setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
    val innerStars = setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)

    val angles = setOf(0.0, 60.0, 120.0, 240.0, 300.0, 90.0, 180.0)

    val inner: IHoroscopeModel = horoscopeFeature.getModel(bdnp.gmtJulDay, loc, config.horoscopeConfig)

    val innerStarPosMap: Map<Planet, ZodiacDegree> = innerStars.associateWith { planet ->
      starPositionImpl.getPosition(planet, bdnp.gmtJulDay, bdnp.location).lngDeg
    }

    val houseRelatedPoints = listOf(Axis.values.toList(), Arabic.values.toList()).flatten()

    /**
     * [chosenPoints] 外圈的某星 針對內圈 的星體，形成哪些交角
     */
    fun IHoroscopeModel.outerToInner(vararg chosenPoints: AstroPoint): List<SynastryAspect> {
      return horoscopeFeature.synastry(this, inner, modernAspectCalculator, threshold = null).aspects.filter { aspect ->
        aspect.outerPoint in chosenPoints && (
          if (includeHour)
            true
          else {
            aspect.innerPoint !in houseRelatedPoints
          }
          )
      }
    }

    fun searchPersonalEvents(outerStars: Set<Planet>, angles: Set<Double>): Sequence<AspectData> {
      return outerStars.asSequence().flatMap { outer ->
        innerStars.flatMap { inner ->
          innerStarPosMap[inner]?.let { innerDeg ->
            val degrees = angles.map { it.toZodiacDegree() }.map { it + innerDeg }.toSet()
            starTransitImpl.getRangeTransitGmt(outer, degrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC).map { (zDeg, gmt) ->
              val angle: Double = zDeg.getAngle(innerDeg).round()
              val pattern = PointAspectPattern(listOf(outer, inner), angle, null, 0.0)
              AspectData(pattern, null, 0.0, null, gmt)
            }
          } ?: emptyList()
        }
      }
    }

    val globalEvents = relativeTransitImpl.mutualAspectingEvents(
      outerStars, angles,
      fromGmtJulDay, toGmtJulDay
    ).map { aspectData: AspectData ->
      val (outerStar1, outerStar2) = aspectData.points.let { it[0] to it[1] }
      val description = buildString {
        append("[transit ${outerStar1.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [transit ${outerStar2.asLocaleString().getTitle(Locale.ENGLISH)}]")
      }
      AstroEventDto(Astro.AspectEvent(description, aspectData), aspectData.gmtJulDay.toLmt(), null, Span.INSTANT, Impact.GLOBAL)
    }

    val vocConfig = VoidCourseConfig(MOON, vocImpl = VoidCourseImpl.Medieval)
    val moonVocSeq = voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, vocConfig)
      .map { it: Misc.VoidCourseSpan ->
        val description = buildString {
          append("${it.planet.asLocaleString().getTitle(Locale.ENGLISH)} Void of Course (空亡). ")
          append("From ${it.fromPos.sign.getTitle(Locale.ENGLISH)}/${it.fromPos.signDegree.second.truncate(2)}° ")
          append("to ${it.toPos.sign.getTitle(Locale.ENGLISH)}/${it.toPos.signDegree.second.truncate(2)}°. ")
        }
        AstroEventDto(Astro.MoonVoc(description, it), it.begin.toLmt(), it.end.toLmt(), Span.HOURS, Impact.GLOBAL)
      }


    // 滯留
    val planetStationaries = sequenceOf(MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
      retrogradeImpl.getRangeStationaries(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl).map { s: Stationary ->
        val outer = horoscopeFeature.getModel(s.gmtJulDay, loc, config.horoscopeConfig)
        val zodiacDegree = outer.getZodiacDegree(planet)!!
        val transitToNatalAspects = outer.outerToInner(planet)

        val description = buildString {
          append("${s.star.asLocaleString().getTitle(Locale.ENGLISH)} Stationary (滯留). ${s.type.getTitle(Locale.ENGLISH)}")
          append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncate(2)}°")
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(includeHour))
          }
        }
        AstroEventDto(
          Astro.PlanetStationary(
            description, s, zodiacDegree,
            if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
          ), s.gmtJulDay.toLmt(), null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 當日星體逆行
    val planetRetrogrades = sequenceOf(MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
      retrogradeImpl.getDailyRetrogrades(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl, starTransitImpl).map { (gmtJulDay, progress) ->
        val description = buildString {
          append("${planet.asLocaleString().getTitle(Locale.ENGLISH)} Retrograding (逆行). ")
          append("Progress = ${(progress * 100.0).truncate(2)}%")
        }
        AstroEventDto(Astro.PlanetRetrograde(description, planet, progress), gmtJulDay.toLmt(), null, Span.DAY, Impact.GLOBAL)
      }
    }

    // 日食
    val solarEclipses = eclipseImpl.getRangeSolarEclipses(fromGmtJulDay, toGmtJulDay).map { eclipse ->
      val outer = horoscopeFeature.getModel(eclipse.max, loc, config.horoscopeConfig)
      val zodiacDegree = outer.getZodiacDegree(SUN)!!
      val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(SUN)

      val description = buildString {
        append("Solar Eclipse (日食). ")
        append("Type = ${eclipse.solarType.getTitle(Locale.ENGLISH)}")
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncate(2)}°")
        if (transitToNatalAspects.isNotEmpty()) {
          appendLine()
          appendLine(transitToNatalAspects.describeAspects(includeHour))
        }
      }
      AstroEventDto(
        Astro.Eclipse(
          description, eclipse,
          if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
        ), eclipse.max.toLmt(), null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月食
    val lunarEclipses = eclipseImpl.getRangeLunarEclipses(fromGmtJulDay, toGmtJulDay).map { eclipse ->
      val outer = horoscopeFeature.getModel(eclipse.max, loc, config.horoscopeConfig)
      val zodiacDegree = outer.getZodiacDegree(MOON)!!
      val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(MOON)


      val description = buildString {
        append("Lunar Eclipse (月食). ")
        append("Type = ${eclipse.lunarType.getTitle(Locale.ENGLISH)}")
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncate(2)}°")
        if (transitToNatalAspects.isNotEmpty()) {
          appendLine()
          appendLine(transitToNatalAspects.describeAspects(includeHour))
        }
      }
      AstroEventDto(
        Astro.Eclipse(
          description, eclipse,
          if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
        ), eclipse.max.toLmt(), null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月相
    val lunarPhaseEvents = sequenceOf(
      0.0 to LunarPhase.NEW,
      90.0 to LunarPhase.FIRST_QUARTER,
      180.0 to LunarPhase.FULL,
      270.0 to LunarPhase.LAST_QUARTER
    ).flatMap { (angle, phase) ->
      relativeTransitImpl.getPeriodRelativeTransitGmtJulDays(MOON, SUN, fromGmtJulDay, toGmtJulDay, angle).map { gmtJulDay ->
        val outer = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
        val zodiacDegree = outer.getZodiacDegree(MOON)!!
        val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(MOON, SUN)

        val description = buildString {
          append("${MOON.asLocaleString().getTitle(Locale.ENGLISH)} ")
          append(
            when (phase) {
              LunarPhase.NEW           -> "\uD83C\uDF11"
              LunarPhase.FIRST_QUARTER -> "\uD83C\uDF13"
              LunarPhase.FULL          -> "\uD83C\uDF15"
              LunarPhase.LAST_QUARTER  -> "\uD83C\uDF17"
            }
          )
          append(phase.getTitle(Locale.ENGLISH))
          append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncate(2)}°")
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(includeHour))
          }
        }
        AstroEventDto(
          Astro.LunarPhaseEvent(
            description, phase, zodiacDegree,
            if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
          ),
          gmtJulDay.toLmt(), null,
          Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 星體換星座
    val degrees = (0..<360 step 30).map { it.toDouble().toZodiacDegree() }.toSet()
    val starChangeSigns = sequenceOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
      starTransitImpl.getRangeTransitGmt(planet, degrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC).map { (zDeg, gmt) ->
        val newSign = zDeg.sign
        val description = buildString {
          append("${planet.asLocaleString().getTitle(Locale.ENGLISH)} Change Sign (星座換位). ")
          append("From ${zDeg.sign.prev.getTitle(Locale.ENGLISH)} to ${newSign.getTitle(Locale.ENGLISH)}")
        }
        AstroEventDto(
          Astro.StarChangeSign(description, planet, newSign), gmt.toLmt(), null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }


    return sequence {

      if (config.aspect) {
        // 全球星體交角
        yieldAll(globalEvents)

        // 全球 to 個人 , 交角
        yieldAll(searchPersonalEvents(innerStars, angles).map { aspectData ->
          val (outerStar, innerStar) = aspectData.points.let { it[0] to it[1] }
          val description = buildString {
            append("[transit ${outerStar.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [natal ${innerStar.asLocaleString().getTitle(Locale.ENGLISH)}]")
          }
          AstroEventDto(Astro.AspectEvent(description, aspectData), aspectData.gmtJulDay.toLmt(), null, Span.INSTANT, Impact.PERSONAL)
        })
      }

      if (config.voc) {
        // 月亮空亡
        yieldAll(moonVocSeq)
      }
      if (config.retrograde) {
        // 內行星滯留
        yieldAll(planetStationaries)
        // 星體當日逆行
        yieldAll(planetRetrogrades)
      }
      if (config.eclipse) {
        // 日食
        yieldAll(solarEclipses)
        // 月食
        yieldAll(lunarEclipses)
      }
      if (config.lunarPhase) {
        // 月相
        yieldAll(lunarPhaseEvents)
      }
      if (config.starChangeSign){
        // 星體換星座
        yieldAll(starChangeSigns)
      }
    }
  }


  private val supportedScales = setOf(Scale.DAY, Scale.HOUR)

  private fun matchEwEvents(gmtJulDay: GmtJulDay, outer: IEightWords, inner: IEightWords, config: Config.EwConfig, loc: ILocation, includeHour: Boolean): Sequence<IEventDto> {

    fun GmtJulDay.toLmt(): LocalDateTime {
      return (this.toLmt(loc, julDayResolver) as LocalDateTime).roundAndTruncate()
    }

    val globalStemCombined = with(IdentityPatterns.stemCombined) {
      outer.getPatterns().filterIsInstance<IdentityPattern.StemCombined>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toStemCombinedDtos().map {
          EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.natalStems.findSpanByStems(), Impact.GLOBAL)
        }
    }

    val globalBranchCombined = with(IdentityPatterns.branchCombined) {
      outer.getPatterns().filterIsInstance<IdentityPattern.BranchCombined>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toBranchCombinedDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.natalBranches.findSpanByBranches(), Impact.GLOBAL) }
    }

    val globalTrilogy = with(IdentityPatterns.trilogy) {
      outer.getPatterns().filterIsInstance<IdentityPattern.Trilogy>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toTrilogyDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.natalBranches.findSpanByBranches(), Impact.GLOBAL) }
    }

    val globalBranchOpposition = with(IdentityPatterns.branchOpposition) {
      outer.getPatterns().filterIsInstance<IdentityPattern.BranchOpposition>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toBranchOppositionDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.natalBranches.findSpanByBranches(), Impact.GLOBAL) }
    }

    val globalStemRooted = with(IdentityPatterns.stemRooted) {
      outer.getPatterns().filterIsInstance<IdentityPattern.StemRooted>()
        .filter { p -> p.pillar in supportedScales }
        .toStemRootedDtos().map { event ->
          val span = setOf(
            event.natalStems.maxBy { it.pillars.max() }.pillars.max(),
            event.natalBranches.maxBy { it.pillars.max() }.pillars.max()
          ).max().toSpan()
          EwEventDto(event, outer, gmtJulDay.toLmt(), null, span, Impact.GLOBAL)
        }
    }

    val auspiciousDays: EwEventDto? = with(IdentityPatterns.auspiciousPattern) {
      outer.getPatterns().filterIsInstance<IdentityPattern.AuspiciousPattern>()
        .filter { p ->
          // 找日柱
          p.pillars.contains(Scale.DAY)
        }
        .toAuspiciousDto()?.let { event ->
          val span = event.pillars.filter { it.value.isNotEmpty() }.keys.max().toSpan()
          EwEventDto(event, outer, gmtJulDay.toLmt(), null, span, Impact.GLOBAL)
        }
    }

    val inauspiciousDays: EwEventDto? = with(IdentityPatterns.inauspiciousPattern) {
      outer.getPatterns().filterIsInstance<IdentityPattern.InauspiciousPattern>()
        .filter { p ->
          // 找日柱
          p.pillars.contains(Scale.DAY)
        }
        .toInauspiciousDto()?.let { event ->
          val span = event.pillars.filter { it.value.isNotEmpty() }.keys.max().toSpan()
          EwEventDto(event, outer, gmtJulDay.toLmt(), null, span, Impact.GLOBAL)
        }
    }

    val personalAffecting = with(affecting) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.Affecting
      }.toAffectingDtos().map {
        EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.flowScales.max().toSpan(), Impact.PERSONAL)
      }
    }

    val personalStemCombined = with(stemCombined) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.StemCombined
      }.toStemCombinedDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.flowStems.scales.max().toSpan() , Impact.PERSONAL) }
    }

    val personalBranchCombined = with(branchCombined) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.BranchCombined
      }.toBranchCombinedDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.flowBranches.scales.max().toSpan(), Impact.PERSONAL) }
    }

    val personalTrilogyToFlow = with(trilogyToFlow) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.TrilogyToFlow
      }.toTrilogyToFlowDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.flowBranches.scales.max().toSpan(), Impact.PERSONAL) }
    }

    val personalToFlowTrilogy = with(toFlowTrilogy) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.ToFlowTrilogy
      }.filter { pattern ->
        /**
         * 限制 [FlowPattern.ToFlowTrilogy.flows] 必須至少包含 [FlowScale.DAY] or [FlowScale.HOUR]
         * 這樣找「流日」「流時」才有意義
         */
        val flowScales = pattern.flows.map { it.first }
        flowScales.any { it == FlowScale.DAY || it == FlowScale.HOUR }
      }.toToFlowTrilogyDtos()
        .map { event -> EwEventDto(event, outer, gmtJulDay.toLmt(), null, event.flowBranches.maxBy { it.scales.max() }.scales.max().toSpan(), Impact.PERSONAL) }
    }

    val personalBranchOpposition = with(branchOpposition) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.BranchOpposition
      }.toBranchOppositionDtos().map { EwEventDto(it, outer, gmtJulDay.toLmt(), null, it.flowBranches.scales.max().toSpan() , Impact.PERSONAL) }
    }

    return sequence {
      yieldAll(globalStemCombined)
      yieldAll(globalBranchCombined)
      yieldAll(globalTrilogy)
      yieldAll(globalBranchOpposition)
      yieldAll(globalStemRooted)
      if (config.shanSha) {
        auspiciousDays?.also { yield(it) }
        inauspiciousDays?.also { yield(it) }
      }

      yieldAll(personalAffecting)
      yieldAll(personalStemCombined)
      yieldAll(personalBranchCombined)
      yieldAll(personalTrilogyToFlow)
      yieldAll(personalToFlowTrilogy)
      yieldAll(personalBranchOpposition)
    }
      .filter { it: IEventDto ->
        if (includeHour)
          true
        else {
          when (it.event) {
            is Ew.EwFlow     -> !(it.event as Ew.EwFlow).hourRelated
            is Ew.EwIdentity -> true
            is Astro         -> true
          }
        }
      }
  }


  private fun searchEwEvents(
    bdnp: IBirthDataNamePlace,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation = bdnp.location,
    includeHour: Boolean,
    config: Config.EwConfig,
  ): Sequence<IEventDto> {

    val ewPersonPresentConfig = config.personPresentConfig
    val personEw = ewPersonPresentFeature.getPersonModel(bdnp, ewPersonPresentConfig).eightWords
    logger.debug { "本命八字 : $personEw" }
    val fromEw: IEightWords = ewFeature.getModel(fromGmtJulDay, loc, ewPersonPresentConfig)

    return generateSequence(fromEw to fromGmtJulDay) { (outerEw, gmtJulDay) ->
      ewFeature.next(gmtJulDay + 0.01, loc, ewPersonPresentConfig)
    }.takeWhile { (outerEw, gmtJulDay) -> gmtJulDay < toGmtJulDay }
      .flatMap { (outerEw, gmtJulDay) ->
        matchEwEvents(gmtJulDay, outerEw, personEw, config, loc, includeHour)
      }
  }


  companion object {
    private fun LocalDateTime.roundAndTruncate(): LocalDateTime {
      return (
        if (this.nano >= 500_000_000) {
          this.plusSeconds(1).truncatedTo(ChronoUnit.SECONDS)
        } else {
          this.truncatedTo(ChronoUnit.SECONDS)
        }
        ).truncatedTo(ChronoUnit.MINUTES)
    }

    private const val NOT_SUPPORTED = "not supported"
    private const val IMPOSSIBLE_FLOW_SCALE = "impossible flowScale"

    fun Set<NatalStems>.findSpanByStems(): Span {
      return this.maxBy { it.pillars.max() }.pillars.max().toSpan()
    }

    fun Set<NatalBranches>.findSpanByBranches(): Span {
      return this.maxBy { it.pillars.max() }.pillars.max().toSpan()
    }

    fun Scale.toSpan(): Span {
      return when (this) {
        Scale.DAY               -> Span.DAY
        Scale.HOUR              -> Span.HOURS
        Scale.YEAR, Scale.MONTH -> throw IllegalArgumentException(NOT_SUPPORTED)
      }
    }

    fun FlowScale.toSpan(): Span {
      return when (this) {
        FlowScale.DAY  -> Span.DAY
        FlowScale.HOUR -> Span.HOURS
        else           -> throw IllegalArgumentException(IMPOSSIBLE_FLOW_SCALE)
      }
    }
  }

}
