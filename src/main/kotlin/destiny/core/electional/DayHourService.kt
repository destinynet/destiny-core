/**
 * Created by smallufo on 2025-05-28.
 */
package destiny.core.electional

import destiny.core.FlowScale
import destiny.core.IBirthDataNamePlace
import destiny.core.Scale
import destiny.core.TimeRange
import destiny.core.astrology.*
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
import destiny.core.chinese.eightwords.EwEvent
import destiny.core.chinese.eightwords.EwEvent.NatalBranches
import destiny.core.chinese.eightwords.EwEvent.NatalStems
import destiny.core.chinese.eightwords.EwEventDto
import destiny.core.chinese.eightwords.EwTraversalConfig
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
import jakarta.inject.Named
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS


@Named
class DayHourService(
  private val ewPersonPresentFeature: PersonPresentFeature,
  private val ewFeature: EightWordsFeature,
  private val horoscopeFeature: IHoroscopeFeature,
  private val julDayResolver: JulDayResolver,
  private val eventsTraversalSolarArcImpl: IEventsTraversal,
  private val eventsTraversalTransitImpl: IEventsTraversal,
) {

  /**
   * @param timeRange : 每日限定時間
   */
  fun aggregate(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config: Config, includeHour: Boolean, timeRange: TimeRange? = null): List<Daily> {

    val daySelector: (IEventDto) -> LocalDate = {
      val beginDate = (it.begin.toLmt(bdnp.location, julDayResolver) as LocalDateTime).toLocalDate()
      when (it.event) {
        is AstroEvent -> beginDate
        is EwEvent    -> {
          val ewEventDto = (it as EwEventDto)
          val outer: IEightWords = ewEventDto.outer

          if (outer.hour.branch == Branch.子) {
            // 當日凌晨的子正之後的(早)子時 , 不需要 +1日

            val loc = model.loc ?: bdnp.location
            val localDate = beginDate

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
            beginDate
          }
        }
        else -> throw IllegalArgumentException("impossible : ${it.event}")
      }
    }

    return traverse(bdnp, model, config, includeHour).groupBy(daySelector).map { (date: LocalDate, events: List<IEventDto>) ->

      val allDayEvents = events
        .sortedBy { it.begin } // 確保先來的在前
        .fold(mutableListOf<IEventDto>()) { acc, event ->
          when (event) {
            is EwEventDto -> {
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
              eventDto.begin >= date.atTime(begin).toGmtJulDay(bdnp.location) && eventDto.begin <= date.atTime(end).toGmtJulDay(bdnp.location)
            } else {
              true
            }
          }
          .sorted()
          .groupBy { (it.begin.toLmt(bdnp.location, julDayResolver) as LocalDateTime).roundAndTruncate() }

      Daily(date, allDayEvents, nonAllDayEvents)
    }.sorted()
  }

  /** 純文字模式 輸出 */
  fun text(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config: Config, includeHour: Boolean, timeRange: TimeRange? = null): String {
    return aggregate(bdnp, model, config, includeHour, timeRange).joinToString("\n") { daily ->
      val date = daily.localDate
      buildString {
        appendLine("$date , ${date.dayOfWeek}")
        if (daily.allDayEvents.isNotEmpty())
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

        daily.hourEvents.forEach { (time: LocalDateTime, eventDtos: List<IEventDto>) ->
          append("\t${time.roundAndTruncate()}")
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

  fun traverse(bdnp: IBirthDataNamePlace, model: Electional.ITraversalModel, config: Config, includeHour: Boolean): Sequence<IEventDto> {
    require(!model.toDate.isBefore(model.fromDate)) { "toDate must be after the fromDate" }

    val loc = model.loc ?: bdnp.location

    val fromGmtJulDay = model.fromDate.atTime(0, 0).toGmtJulDay(loc)
    val toGmtJulDay = model.toDate.plusDays(1).atTime(0, 0).toGmtJulDay(loc)

    return traverse(bdnp, loc, fromGmtJulDay, toGmtJulDay, config, includeHour)
  }

  fun traverse(bdnp: IBirthDataNamePlace, loc: ILocation = bdnp.location, fromGmtJulDay: GmtJulDay, toGmtJulDay: GmtJulDay, config: Config, includeHour: Boolean): Sequence<IEventDto> {

    return sequence {
      config.ewConfig?.also {
        yieldAll(searchEwEvents(bdnp, fromGmtJulDay, toGmtJulDay, loc, includeHour, it))
      }
      config.astrologyConfig?.also {
        yieldAll(traverseAstrologyEvents(bdnp, fromGmtJulDay, toGmtJulDay, loc, includeHour, it, eventsTraversalSolarArcImpl, eventsTraversalTransitImpl))
      }
    }
  }


  fun traverseAstrologyEvents(
    bdnp: IBirthDataNamePlace,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: AstrologyTraversalConfig,
    vararg traversals: IEventsTraversal
  ): Sequence<IEventDto> {
    val inner: IHoroscopeModel = horoscopeFeature.getModel(bdnp.gmtJulDay, loc, config.horoscopeConfig)
    return traversals.asSequence().flatMap { it.traverse(inner, fromGmtJulDay, toGmtJulDay, loc, includeHour, config) }
  }


  private val supportedScales = setOf(Scale.DAY, Scale.HOUR)

  private fun matchEwEvents(gmtJulDay: GmtJulDay, outer: IEightWords, inner: IEightWords, config: EwTraversalConfig, loc: ILocation, includeHour: Boolean): Sequence<IEventDto> {

    val globalStemCombined = with(IdentityPatterns.stemCombined) {
      outer.getPatterns().filterIsInstance<IdentityPattern.StemCombined>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toStemCombinedDtos().map {
          EwEventDto(it, outer, gmtJulDay, null, it.natalStems.findSpanByStems(), Impact.GLOBAL)
        }
    }

    val globalBranchCombined = with(IdentityPatterns.branchCombined) {
      outer.getPatterns().filterIsInstance<IdentityPattern.BranchCombined>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toBranchCombinedDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.natalBranches.findSpanByBranches(), Impact.GLOBAL) }
    }

    val globalTrilogy = with(IdentityPatterns.trilogy) {
      outer.getPatterns().filterIsInstance<IdentityPattern.Trilogy>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toTrilogyDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.natalBranches.findSpanByBranches(), Impact.GLOBAL) }
    }

    val globalBranchOpposition = with(IdentityPatterns.branchOpposition) {
      outer.getPatterns().filterIsInstance<IdentityPattern.BranchOpposition>()
        .filter { p -> p.pillars.map { it.first }.any { s -> s in supportedScales } }
        .toBranchOppositionDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.natalBranches.findSpanByBranches(), Impact.GLOBAL) }
    }

    val globalStemRooted = with(IdentityPatterns.stemRooted) {
      outer.getPatterns().filterIsInstance<IdentityPattern.StemRooted>()
        .filter { p -> p.pillar in supportedScales }
        .toStemRootedDtos().map { event ->
          val span = setOf(
            event.natalStems.maxBy { it.pillars.max() }.pillars.max(),
            event.natalBranches.maxBy { it.pillars.max() }.pillars.max()
          ).max().toSpan()
          EwEventDto(event, outer, gmtJulDay, null, span, Impact.GLOBAL)
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
          EwEventDto(event, outer, gmtJulDay, null, span, Impact.GLOBAL)
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
          EwEventDto(event, outer, gmtJulDay, null, span, Impact.GLOBAL)
        }
    }

    val personalAffecting = with(affecting) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.Affecting
      }.toAffectingDtos().map {
        EwEventDto(it, outer, gmtJulDay, null, it.flowScales.max().toSpan(), Impact.PERSONAL)
      }
    }

    val personalStemCombined = with(stemCombined) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.StemCombined
      }.toStemCombinedDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.flowStems.scales.max().toSpan() , Impact.PERSONAL) }
    }

    val personalBranchCombined = with(branchCombined) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.BranchCombined
      }.toBranchCombinedDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.flowBranches.scales.max().toSpan(), Impact.PERSONAL) }
    }

    val personalTrilogyToFlow = with(trilogyToFlow) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.TrilogyToFlow
      }.toTrilogyToFlowDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.flowBranches.scales.max().toSpan(), Impact.PERSONAL) }
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
        .map { event -> EwEventDto(event, outer, gmtJulDay, null, event.flowBranches.maxBy { it.scales.max() }.scales.max().toSpan(), Impact.PERSONAL) }
    }

    val personalBranchOpposition = with(branchOpposition) {
      inner.getPatterns(outer.day, outer.hour).map { pattern ->
        pattern as FlowPattern.BranchOpposition
      }.toBranchOppositionDtos().map { EwEventDto(it, outer, gmtJulDay, null, it.flowBranches.scales.max().toSpan() , Impact.PERSONAL) }
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
            is EwEvent.EwFlow     -> !(it.event as EwEvent.EwFlow).hourRelated
            is EwEvent.EwIdentity -> true
            is AstroEvent         -> true
            else                  -> throw IllegalArgumentException(NOT_SUPPORTED)
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
    config: EwTraversalConfig,
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
