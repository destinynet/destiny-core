package destiny.core.astrology

import destiny.core.asLocaleString
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.IVoidCourseFeature
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.electional.Impact
import destiny.core.electional.Span
import destiny.tools.getTitle
import destiny.tools.reverse
import destiny.tools.round
import destiny.tools.truncateToString
import jakarta.inject.Named
import java.util.*

/**
 * Progression-based
 */
@Named
class EventsTraversalTransitImpl(
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val relativeTransitImpl: IRelativeTransit,
  private val eclipseImpl: IEclipseFactory,
  private val horoscopeFeature: IHoroscopeFeature,
  private val modernAspectCalculator: IAspectCalculator,
  private val voidCourseFeature: IVoidCourseFeature,
  private val retrogradeImpl: IRetrograde,
) : IEventsTraversal {

  private val Planet.isStationaryPossible: Boolean
    get() = this != Planet.SUN && this != Planet.MOON

  override fun traverse(
    model: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    grain: BirthDataGrain,
    config: AstrologyTraversalConfig,
    transitingPoints: Set<AstroPoint>,
    natalTargetPoints: Set<AstroPoint>,
  ): Sequence<AstroEventDto> {

    // 外圈要考慮的星體 — filterIsInstance<Star> 包含 Planet + LunarNode，排除 Axis（Axis 不是 Star，無法計算行運）
    val transitingStars: Set<Star> = transitingPoints.filterIsInstance<Star>().toSet()

    // 內圈本命星體 (Natal Target Points) — 包含 Planet, LunarNode, Axis
    val natalPoints: Set<AstroPoint> = model.points
      .asSequence()
      .filter { it in natalTargetPoints }
      .filter { it is Planet || it is LunarNode || it is Axis }
      .filter {
        if (grain == BirthDataGrain.MINUTE) true
        else it !in Axis.values
      }.toSet()

    val angles = setOf(0.0, 60.0, 120.0, 240.0, 300.0, 90.0, 180.0)
    // Use model.getPosition() to support all AstroPoint types (Planet, LunarNode, Axis)
    val natalPointsPosMap: Map<AstroPoint, ZodiacDegree> = natalPoints.mapNotNull { point ->
      model.getPosition(point)?.let { point to it.lngDeg }
    }.toMap()


    val houseRelatedPoints = listOf(Axis.values.toList(), Arabic.values.toList()).flatten()

    /**
     * [this] : Outer
     * [chosenPoints] 外圈的某星 針對內圈 的星體，形成哪些交角
     */
    fun IHoroscopeModel.outerToInner(vararg chosenPoints: AstroPoint): List<SynastryAspect> {
      return horoscopeFeature.synastry(this, model, modernAspectCalculator,
                                       threshold = null,
                                       innerIncludeAxis = grain.includeAxis).aspects.filter { aspect: SynastryAspect ->
        aspect.outerPoint in chosenPoints && (
          if (grain.includeAxis)
            true
          else {
            aspect.innerPoint !in houseRelatedPoints
          }
          )
      }
    }

    fun searchPersonalEvents(transitingStars: Set<Star>, natalPoints: Set<AstroPoint>, angles: Set<Double>): Sequence<AspectData> {
      return transitingStars.asSequence().flatMap { outer ->
        natalPoints.asSequence().flatMap { inner ->
          natalPointsPosMap[inner]?.let { innerDeg ->
            val degrees = angles.map { it.toZodiacDegree() }.map { it + innerDeg }.toSet()
            starTransitImpl.getRangeTransitGmt(outer, degrees, fromGmtJulDay, toGmtJulDay, options = config.horoscopeConfig.starTypeOptions).map { (zDeg, gmt) ->
              val angle: Double = zDeg.getAngle(innerDeg).round()
              val pattern = PointAspectPattern(listOf(outer, inner), angle, null, 0.0)
              AspectData(pattern, null, 0.0, null, gmt)
            }
          } ?: emptySequence()
        }
      }
    }


    // Global (sky-to-sky) aspects: only between Planets; LunarNodes are excluded because
    // North/South Node map to the same SwissEph body (True Node), causing TCPlanetPlanet to throw,
    // and their mutual 180° opposition is a constant, not a meaningful event.
    val globalAspectEvents = relativeTransitImpl.mutualAspectingEvents(
      transitingStars.filterIsInstance<Planet>().toSet(), angles,
      fromGmtJulDay, toGmtJulDay, config.horoscopeConfig.starTypeOptions
    ).map { aspectData: AspectData ->
      val (outerStar1, outerStar2) = aspectData.points.let { it[0] to it[1] }
      val description = buildString {
        append("[transiting ${outerStar1.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [transiting ${outerStar2.asLocaleString().getTitle(Locale.ENGLISH)}]")
      }
      AstroEventDto(AstroEvent.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL)
    }

    val vocConfig = VoidCourseConfig(Planet.MOON, vocImpl = VoidCourseImpl.Medieval)
    val moonVocSeq = if (Planet.MOON in transitingStars) {
      voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, vocConfig)
        .map { it: Misc.VoidCourseSpan ->
          val description = buildString {
            append("${it.planet.asLocaleString().getTitle(Locale.ENGLISH)} Void of Course (空亡). ")
            append("From ${it.fromPos.sign.getTitle(Locale.ENGLISH)}/${it.fromPos.signDegree.second.truncateToString(2)}° ")
            append("to ${it.toPos.sign.getTitle(Locale.ENGLISH)}/${it.toPos.signDegree.second.truncateToString(2)}°. ")
          }
          AstroEventDto(AstroEvent.MoonVoc(description, it), it.begin, it.end, Span.HOURS, Impact.GLOBAL)
        }
    } else emptySequence()


    // 滯留
    val planetStationaries = transitingStars.filterIsInstance<Planet>().asSequence().filter { it.isStationaryPossible }.flatMap { planet ->
      retrogradeImpl.getRangeStationaries(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl).map { s: Stationary ->
        val outer = horoscopeFeature.getModel(s.gmtJulDay, loc, config.horoscopeConfig)
        val zodiacDegree = outer.getZodiacDegree(planet)!!
        val transitToNatalAspects = outer.outerToInner(planet)

        val description = buildString {
          append("${s.star.asLocaleString().getTitle(Locale.ENGLISH)} Stationary (滯留). ${s.type.getTitle(Locale.ENGLISH)}")
          append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
          if (config.includeTransitToNatalAspects) {
            if (transitToNatalAspects.isNotEmpty()) {
              appendLine()
              appendLine(transitToNatalAspects.describeAspects(grain))
            }
          }
        }
        AstroEventDto(
          AstroEvent.PlanetStationary(
            description, s, zodiacDegree,
            if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
          ), s.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 當日星體逆行
    val planetRetrogrades = transitingStars.filterIsInstance<Planet>().asSequence().filter { it.isStationaryPossible }.flatMap { planet ->
      retrogradeImpl.getDailyRetrogrades(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl, starTransitImpl).map { (gmtJulDay, progress) ->
        val description = buildString {
          append("${planet.asLocaleString().getTitle(Locale.ENGLISH)} Retrograding (逆行). ")
          append("Progress = ${(progress * 100.0).truncateToString(2)}%")
        }
        AstroEventDto(AstroEvent.PlanetRetrograde(description, planet, progress), gmtJulDay, null, Span.DAY, Impact.GLOBAL)
      }
    }

    // 日食
    val solarEclipses = eclipseImpl.getRangeSolarEclipses(fromGmtJulDay, toGmtJulDay).map { eclipse ->
      val outer = horoscopeFeature.getModel(eclipse.max, loc, config.horoscopeConfig)
      val zodiacDegree = outer.getZodiacDegree(Planet.SUN)!!
      val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.SUN)

      val description = buildString {
        append("Solar Eclipse (日食). ")
        append("Type = ${eclipse.solarType.getTitle(Locale.ENGLISH)}")
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
        if (config.includeTransitToNatalAspects) {
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(grain))
          }
        }
      }
      AstroEventDto(
        AstroEvent.Eclipse(
          description, eclipse,
          if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
        ), eclipse.max, null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月食
    val lunarEclipses = eclipseImpl.getRangeLunarEclipses(fromGmtJulDay, toGmtJulDay).map { eclipse ->
      val outer = horoscopeFeature.getModel(eclipse.max, loc, config.horoscopeConfig)
      val zodiacDegree = outer.getZodiacDegree(Planet.MOON)!!
      val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.MOON)


      val description = buildString {
        append("Lunar Eclipse (月食). ")
        append("Type = ${eclipse.lunarType.getTitle(Locale.ENGLISH)}")
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
        if (config.includeTransitToNatalAspects) {
          if (transitToNatalAspects.isNotEmpty()) {
            appendLine()
            appendLine(transitToNatalAspects.describeAspects(grain))
          }
        }
      }
      AstroEventDto(
        AstroEvent.Eclipse(
          description, eclipse,
          if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
        ), eclipse.max, null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月相 (只在 SUN 和 MOON 都被選中時計算)
    val lunarPhases = if (Planet.SUN in transitingStars && Planet.MOON in transitingStars) {
      sequenceOf(
        0.0 to LunarPhase.NEW,
        90.0 to LunarPhase.FIRST_QUARTER,
        180.0 to LunarPhase.FULL,
        270.0 to LunarPhase.LAST_QUARTER
      ).flatMap { (angle, phase) ->
        relativeTransitImpl.getPeriodRelativeTransitGmtJulDays(Planet.MOON, Planet.SUN, fromGmtJulDay, toGmtJulDay, angle, config.horoscopeConfig.starTypeOptions).map { gmtJulDay ->
          val outer = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
          val zodiacDegree = outer.getZodiacDegree(Planet.MOON)!!
          val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.MOON, Planet.SUN)
          val description = buildString {
            append("${Planet.MOON.asLocaleString().getTitle(Locale.ENGLISH)} ")
            append(
              when (phase) {
                LunarPhase.NEW           -> "🌑"
                LunarPhase.FIRST_QUARTER -> "🌓"
                LunarPhase.FULL          -> "🌕"
                LunarPhase.LAST_QUARTER  -> "🌗"
              }
            )
            append(phase.getTitle(Locale.ENGLISH))
            append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncateToString(2)}°")
            if (config.includeTransitToNatalAspects) {
              if (transitToNatalAspects.isNotEmpty()) {
                appendLine()
                appendLine(transitToNatalAspects.describeAspects(grain))
              }
            }
          }
          AstroEventDto(
            AstroEvent.LunarPhaseEvent(
              description, phase, zodiacDegree,
              if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
            ),
            gmtJulDay, null,
            Span.INSTANT, Impact.GLOBAL
          )
        }
      }
    } else emptySequence()

    // 星體換星座
    val signDegrees = (0..<360 step 30).map { it.toDouble().toZodiacDegree() }.toSet()
    val signIngresses = transitingStars.asSequence().flatMap { planet ->
      starTransitImpl.getRangeTransitGmt(planet, signDegrees, fromGmtJulDay, toGmtJulDay, options = config.horoscopeConfig.starTypeOptions).map { (zDeg, gmt) ->

        val speed = starPositionImpl.calculate(planet, gmt, config.horoscopeConfig.centric, config.horoscopeConfig.coordinate , config.horoscopeConfig.starTypeOptions).speedLng
        val (oldSign, newSign, eventType) = if (speed >= 0) {
          // 順行：進入 zDeg.sign，來自前一個星座
          Triple(zDeg.sign.prev, zDeg.sign, "Ingresses (enters)")
        } else {
          // 逆行：離開 zDeg.sign，進入前一個星座
          Triple(zDeg.sign, zDeg.sign.prev, "Regresses (retrogrades into)")
        }

        val description = buildString {
          append("${planet.asLocaleString().getTitle(Locale.ENGLISH)} $eventType Sign. ")
          append("From ${oldSign.getTitle(Locale.ENGLISH)} to ${newSign.getTitle(Locale.ENGLISH)}")
        }
        AstroEventDto(
          AstroEvent.SignIngress(description, planet, oldSign, newSign), gmt, null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 星體換宮位
    val houseIngresses = if (grain == BirthDataGrain.MINUTE) {
      // grain 到「時/分」, 宮位可信
      val cuspDegreeMap: Map<ZodiacDegree, Int> = model.cuspDegreeMap.reverse()
      val cuspDegrees = cuspDegreeMap.keys.toSet()
      transitingStars.asSequence().flatMap { planet ->
        starTransitImpl.getRangeTransitGmt(planet, cuspDegrees, fromGmtJulDay, toGmtJulDay, options = config.horoscopeConfig.starTypeOptions).map { (zDeg, gmt) ->
          // maybe retrograde
          val speed = starPositionImpl.calculate(planet, gmt, config.horoscopeConfig.centric, config.horoscopeConfig.coordinate , config.horoscopeConfig.starTypeOptions).speedLng
          val cuspHouseNumber = cuspDegreeMap.getValue(zDeg)

          // 根據順行或逆行，決定 old/new house 以及文字描述
          val (oldHouse, newHouse, eventType) = if (speed >= 0) {
            // 順行：進入 cuspHouseNumber，來自前一個宮位
            val fromHouse = if (cuspHouseNumber == 1) 12 else cuspHouseNumber - 1
            Triple(fromHouse, cuspHouseNumber, "Ingresses (enters)")
          } else {
            // 逆行：離開 cuspHouseNumber，退入前一個宮位
            val toHouse = if (cuspHouseNumber == 1) 12 else cuspHouseNumber - 1
            Triple(cuspHouseNumber, toHouse, "Regresses (retrogrades into)")
          }

          // 產生更精確的文字描述
          val description = buildString {
            append("${planet.asLocaleString().getTitle(Locale.ENGLISH)} $eventType House. ")
            append("From House $oldHouse to House $newHouse")
          }
          AstroEventDto(
            AstroEvent.HouseIngress(description, planet, oldHouse, newHouse), gmt, null, Span.INSTANT, Impact.PERSONAL
          )
        }
      }
    } else {
      emptySequence()
    }

    return sequence {

      if (config.globalAspect) {
        // 全球星體交角
        yieldAll(globalAspectEvents)
      }

      if (config.personalAspect) {
        // 全球 to 個人 , 交角
        yieldAll(searchPersonalEvents(transitingStars, natalPoints, angles).map { aspectData ->
          val (outerStar, innerStar) = aspectData.points.let { it[0] to it[1] }
          val description = buildString {
            append("[transiting ${outerStar.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [natal ${innerStar.asLocaleString().getTitle(Locale.ENGLISH)}]")
          }
          AstroEventDto(AstroEvent.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.PERSONAL)
        })
      }

      if (config.voc) {
        // 月亮空亡
        yieldAll(moonVocSeq)
      }
      if (config.stationary) {
        // 內行星滯留
        yieldAll(planetStationaries)
      }
      if (config.retrograde) {
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
        yieldAll(lunarPhases)
      }
      if (config.signIngress) {
        // 星體換星座
        yieldAll(signIngresses)
      }
      if (config.houseIngress && grain == BirthDataGrain.MINUTE) {
        // 星體換宮位
        yieldAll(houseIngresses)
      }
    }
  }

  private fun List<SynastryAspect>.describeAspects(grain: BirthDataGrain): String {
    return this.sortedBy { it.orb }.joinToString("\n") { aspect: SynastryAspect ->
      buildString {
        append("\t")
        append("(p) [transiting ${aspect.outerPoint.asLocaleString().getTitle(Locale.ENGLISH)}")
        if (grain == BirthDataGrain.MINUTE) {
          append(" (H${aspect.outerPointHouse})")
        }
        append("] ")
        append(aspect.aspect)
        append(" [natal ${aspect.innerPoint.asLocaleString().getTitle(Locale.ENGLISH)}")
        if (grain == BirthDataGrain.MINUTE) {
          append(" (H${aspect.innerPointHouse})")
        }
        append("] orb = ${aspect.orb.truncateToString(2)}")
      }
    }
  }
}
