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
import destiny.core.electional.Config
import destiny.core.electional.Impact
import destiny.core.electional.Span
import destiny.tools.getTitle
import destiny.tools.reverse
import destiny.tools.round
import destiny.tools.truncate
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
  override fun traverse(
    inner: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: Config.AstrologyConfig
  ): Sequence<AstroEventDto> {
    val outerStars = setOf(Planet.SUN, Planet.MOON, Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN)
    val innerStars = setOf(Planet.SUN, Planet.MOON, Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN)

    val angles = setOf(0.0, 60.0, 120.0, 240.0, 300.0, 90.0, 180.0)
    val innerStarPosMap: Map<Planet, ZodiacDegree> = innerStars.associateWith { planet ->
      starPositionImpl.getPosition(planet, inner.gmtJulDay, inner.location).lngDeg
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


    val globalAspectEvents = relativeTransitImpl.mutualAspectingEvents(
      outerStars, angles,
      fromGmtJulDay, toGmtJulDay
    ).map { aspectData: AspectData ->
      val (outerStar1, outerStar2) = aspectData.points.let { it[0] to it[1] }
      val description = buildString {
        append("[transit ${outerStar1.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [transit ${outerStar2.asLocaleString().getTitle(Locale.ENGLISH)}]")
      }
      AstroEventDto(AstroEvent.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL)
    }

    val vocConfig = VoidCourseConfig(Planet.MOON, vocImpl = VoidCourseImpl.Medieval)
    val moonVocSeq = voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, vocConfig)
      .map { it: Misc.VoidCourseSpan ->
        val description = buildString {
          append("${it.planet.asLocaleString().getTitle(Locale.ENGLISH)} Void of Course (空亡). ")
          append("From ${it.fromPos.sign.getTitle(Locale.ENGLISH)}/${it.fromPos.signDegree.second.truncate(2)}° ")
          append("to ${it.toPos.sign.getTitle(Locale.ENGLISH)}/${it.toPos.signDegree.second.truncate(2)}°. ")
        }
        AstroEventDto(AstroEvent.MoonVoc(description, it), it.begin, it.end, Span.HOURS, Impact.GLOBAL)
      }


    // 滯留
    val planetStationaries = sequenceOf(Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN).flatMap { planet ->
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
          AstroEvent.PlanetStationary(
            description, s, zodiacDegree,
            if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
          ), s.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 當日星體逆行
    val planetRetrogrades = sequenceOf(Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN).flatMap { planet ->
      retrogradeImpl.getDailyRetrogrades(planet, fromGmtJulDay, toGmtJulDay, starPositionImpl, starTransitImpl).map { (gmtJulDay, progress) ->
        val description = buildString {
          append("${planet.asLocaleString().getTitle(Locale.ENGLISH)} Retrograding (逆行). ")
          append("Progress = ${(progress * 100.0).truncate(2)}%")
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
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncate(2)}°")
        if (transitToNatalAspects.isNotEmpty()) {
          appendLine()
          appendLine(transitToNatalAspects.describeAspects(includeHour))
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
        append(" at ${zodiacDegree.sign.getTitle(Locale.ENGLISH)}/${zodiacDegree.signDegree.second.truncate(2)}°")
        if (transitToNatalAspects.isNotEmpty()) {
          appendLine()
          appendLine(transitToNatalAspects.describeAspects(includeHour))
        }
      }
      AstroEventDto(
        AstroEvent.Eclipse(
          description, eclipse,
          if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
        ), eclipse.max, null, Span.HOURS, Impact.GLOBAL
      )
    }

    // 月相
    val lunarPhases = sequenceOf(
      0.0 to LunarPhase.NEW,
      90.0 to LunarPhase.FIRST_QUARTER,
      180.0 to LunarPhase.FULL,
      270.0 to LunarPhase.LAST_QUARTER
    ).flatMap { (angle, phase) ->
      relativeTransitImpl.getPeriodRelativeTransitGmtJulDays(Planet.MOON, Planet.SUN, fromGmtJulDay, toGmtJulDay, angle).map { gmtJulDay ->
        val outer = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
        val zodiacDegree = outer.getZodiacDegree(Planet.MOON)!!
        val transitToNatalAspects: List<SynastryAspect> = outer.outerToInner(Planet.MOON, Planet.SUN)

        val description = buildString {
          append("${Planet.MOON.asLocaleString().getTitle(Locale.ENGLISH)} ")
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
          AstroEvent.LunarPhaseEvent(
            description, phase, zodiacDegree,
            if (config.includeTransitToNatalAspects) transitToNatalAspects else emptyList()
          ),
          gmtJulDay, null,
          Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 星體換星座
    val signDegrees = (0..<360 step 30).map { it.toDouble().toZodiacDegree() }.toSet()
    val signIngresses = sequenceOf(Planet.SUN, Planet.MOON, Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN).flatMap { planet ->
      starTransitImpl.getRangeTransitGmt(planet, signDegrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC).map { (zDeg, gmt) ->

        val speed = starPositionImpl.getPosition(planet, gmt, loc).speedLng
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
    val houseIngresses = if (includeHour) {
      // grain 到「時/分」, 宮位可信
      val cuspDegreeMap: Map<ZodiacDegree, Int> = inner.cuspDegreeMap.reverse()
      val cuspDegrees = cuspDegreeMap.keys.toSet()
      sequenceOf(Planet.SUN, Planet.MOON, Planet.MERCURY, Planet.VENUS, Planet.MARS, Planet.JUPITER, Planet.SATURN).flatMap { planet ->
        starTransitImpl.getRangeTransitGmt(planet, cuspDegrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC).map { (zDeg, gmt) ->
          // maybe retrograde
          val speed = starPositionImpl.getPosition(planet, gmt, loc).speedLng

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

      if (config.aspect) {
        // 全球星體交角
        yieldAll(globalAspectEvents)

        // 全球 to 個人 , 交角
        yieldAll(searchPersonalEvents(innerStars, angles).map { aspectData ->
          val (outerStar, innerStar) = aspectData.points.let { it[0] to it[1] }
          val description = buildString {
            append("[transit ${outerStar.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [natal ${innerStar.asLocaleString().getTitle(Locale.ENGLISH)}]")
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
      if (config.houseIngress && includeHour) {
        // 星體換宮位
        yieldAll(houseIngresses)
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
}
