/**
 * Created by smallufo on 2025-06-28.
 */
package destiny.core.astrology

import destiny.core.asLocaleString
import destiny.core.astrology.Aspect.Companion.expand
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.IVoidCourseFeature
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.astrology.prediction.ISolarArcModel
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.toLmt
import destiny.core.electional.*
import destiny.tools.*
import jakarta.inject.Named
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs


interface IEventsTraversal {

  fun traverse(
    inner: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: Config.AstrologyConfig
  ): Sequence<AstroEventDto>
}

/**
 * Arc-based
 */
@Named
class EventsTraversalArcImpl(
  private val horoscopeFeature: IHoroscopeFeature,
  private val modernAspectCalculator: IAspectCalculator,
  private val voidCourseFeature: IVoidCourseFeature,
  private val retrogradeImpl: IRetrograde,
  private val starTransitImpl: IStarTransit,
  private val julDayResolver: JulDayResolver,
) : IEventsTraversal {

  override fun traverse(
    inner: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    includeHour: Boolean,
    config: Config.AstrologyConfig
  ): Sequence<AstroEventDto> {

    val hConfig = HoroscopeConfig()

    val threshold = 0.9

    // 太陽換星座
    val signDegrees = (0..<360 step 30).map { it.toDouble().toZodiacDegree() }.toSet()

    starTransitImpl.getRangeTransitGmt(SUN, signDegrees, fromGmtJulDay, toGmtJulDay, true, Coordinate.ECLIPTIC)

    horoscopeFeature.getSolarArc(inner, fromGmtJulDay, includeHour, modernAspectCalculator, threshold, hConfig)

    val astroPoints: Set<AstroPoint> = buildSet {
      addAll(Planet.values)
      addAll(Axis.values)
    }


    /**
     * map of
     * 0 , CONJUNCTION
     * 60 , SEXTILE
     * 90 , SQUARE
     * 120 , TRINE
     * 180 , OPPOSITION
     * 240 , TRINE
     * 300 , SEXTILE
     */
    val degreesMap: Map<Double, Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet().expand()

    val fromSolarArc: ISolarArcModel = horoscopeFeature.getSolarArc(inner, fromGmtJulDay, includeHour, modernAspectCalculator, threshold, hConfig)
    val toSolarArc : ISolarArcModel = horoscopeFeature.getSolarArc(inner, toGmtJulDay, includeHour, modernAspectCalculator, threshold, hConfig)

    // 1. 決定要進行計算的星體與點。這裡我們選取行星、月交點、以及上升/天頂軸點。
    val pointsToConsider = inner.points.filter { it is Planet || it is LunarNode || it is Axis }.toSet()


    return sequence {
      // 3. 遍歷所有「太陽弧點」與「本命點」的組合
      for (pArc in pointsToConsider) {
        for (pNatal in pointsToConsider) {
          // 在此情境下，通常不計算一個點對自身的相位
          if (pArc == pNatal) continue

          // 取得這兩個點在本命盤上的位置
          val natalPosArc = inner.getPosition(pArc) ?: continue
          val natalPosNatal = inner.getPosition(pNatal) ?: continue

          // 4. 計算它們在本命盤上的黃經差 (0-360度)
          val natalSeparation = natalPosArc.lngDeg.getAngle(natalPosNatal.lngDeg)

          // 5. 遍歷所有我們感興趣的目標相位 (0, 60, 90, 120, 180...)
          for ((aspectDegree, aspect) in degreesMap) {

            // 6. 計算要從「本命黃經差」走到「目標相位」，太陽弧需要移動的度數
            var requiredArc = aspectDegree - natalSeparation

            // 將所需弧度標準化到 0-360 範圍內
            if (requiredArc < 0) {
              requiredArc += 360.0
            }

            // 7. 【關鍵優化】: 檢查這個「所需弧度」是否落在我們要搜尋的時間範圍內
            if (requiredArc >= fromSolarArc.degreeMoved && requiredArc <= toSolarArc.degreeMoved) {

              // 8. 確認事件在範圍內後，呼叫精確搜尋函式來找出確切時間
              val eventGmt = findGmtJulDayForArc(inner, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)

              // 9. 如果成功找到時間點，就建立並產出(yield)一個事件 DTO
              if (eventGmt != null) {
                val pattern = PointAspectPattern(listOf(pArc, pNatal), aspectDegree, null, 0.0)
                val aspectData = AspectData(pattern, null, 0.0, null, eventGmt)

                val description = buildString {
                  append("[SA ${pArc.asLocaleString().getTitle(Locale.ENGLISH)}] ")
                  append(aspect.name)
                  append(" [natal ${pNatal.asLocaleString().getTitle(Locale.ENGLISH)}]")
                }

                val eventDto = AstroEventDto(Astro.AspectEvent(description, aspectData), eventGmt, null, Span.INSTANT, Impact.PERSONAL)
                yield(eventDto)
              }
            }
          }
        }
      }
    }
  }

  fun GmtJulDay.describe(location : ILocation): String {
    return (this.toLmt(location, julDayResolver) as LocalDateTime)
      .roundToNearestSecond()
      .truncatedTo(ChronoUnit.MINUTES).toString()
  }

  /**
   * 使用數值搜尋方法，找到產生指定 solar arc 的時間點
   * @param targetArc 需要達成的太陽弧度數 (0-360)
   * @return 如果在時間範圍內找到，則返回 GmtJulDay，否則返回 null
   */
  fun findGmtJulDayForArc(
    inner: IHoroscopeModel,
    targetArc: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    hConfig: HoroscopeConfig
  ): GmtJulDay? {




    // 粗略估算檢查事件是否可能在範圍內
    val degreeMovedFrom = horoscopeFeature.getSolarArc(inner, fromGmt, true, modernAspectCalculator, null, hConfig).degreeMoved
    val degreeMovedTo = horoscopeFeature.getSolarArc(inner, toGmt, true, modernAspectCalculator, null, hConfig).degreeMoved
    if (targetArc < degreeMovedFrom || targetArc > degreeMovedTo) {
      logger.trace { "targetArc = $targetArc , but degreeMovedFrom = $degreeMovedFrom , degreeMovedTo = $degreeMovedTo" }
      return null
    }

    // 實作 Binary Search 或 Iterative Search
    // 搜尋空間是 [fromGmt, toGmt]
    var low = fromGmt
    var high = toGmt

    var round = 0

    while ((high - low) > 0.0001) { // 迭代直到找到足夠精確的時間
      val mid = low + (high - low) / 2
      val solarArcModel = horoscopeFeature.getSolarArc(inner, mid, true, modernAspectCalculator, null, hConfig)
      val currentArc = solarArcModel.degreeMoved

      round++
      logger.trace { "[$round] low = ${low.describe(inner.location)} , high = ${high.describe(inner.location)} , mid = ${mid.describe(inner.location)} , currentArc = $currentArc" }

      if (currentArc < targetArc) {
        low = mid
      } else {
        high = mid
      }
    }

    // 最終檢查 low/high 是否滿足條件且誤差夠小
    val finalModel = horoscopeFeature.getSolarArc(inner, low, true, modernAspectCalculator, null, hConfig)
    if (abs(finalModel.degreeMoved - targetArc) < 0.01) { // 容許的誤差
      return low
    }

    return null // 未找到
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}

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
    val outerStars = setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)
    val innerStars = setOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN)

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
      AstroEventDto(Astro.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL)
    }

    val vocConfig = VoidCourseConfig(MOON, vocImpl = VoidCourseImpl.Medieval)
    val moonVocSeq = voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, vocConfig)
      .map { it: Misc.VoidCourseSpan ->
        val description = buildString {
          append("${it.planet.asLocaleString().getTitle(Locale.ENGLISH)} Void of Course (空亡). ")
          append("From ${it.fromPos.sign.getTitle(Locale.ENGLISH)}/${it.fromPos.signDegree.second.truncate(2)}° ")
          append("to ${it.toPos.sign.getTitle(Locale.ENGLISH)}/${it.toPos.signDegree.second.truncate(2)}°. ")
        }
        AstroEventDto(Astro.MoonVoc(description, it), it.begin, it.end, Span.HOURS, Impact.GLOBAL)
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
          ), s.gmtJulDay, null, Span.INSTANT, Impact.GLOBAL
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
        AstroEventDto(Astro.PlanetRetrograde(description, planet, progress), gmtJulDay, null, Span.DAY, Impact.GLOBAL)
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
        ), eclipse.max, null, Span.HOURS, Impact.GLOBAL
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
          gmtJulDay, null,
          Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 星體換星座
    val signDegrees = (0..<360 step 30).map { it.toDouble().toZodiacDegree() }.toSet()
    val signIngresses = sequenceOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
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
          Astro.SignIngress(description, planet, oldSign, newSign), gmt, null, Span.INSTANT, Impact.GLOBAL
        )
      }
    }

    // 星體換宮位
    val houseIngresses = if (includeHour) {
      // grain 到「時/分」, 宮位可信
      val cuspDegreeMap: Map<ZodiacDegree, Int> = inner.cuspDegreeMap.reverse()
      val cuspDegrees = cuspDegreeMap.keys.toSet()
      sequenceOf(SUN, MOON, MERCURY, VENUS, MARS, JUPITER, SATURN).flatMap { planet ->
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
            Astro.HouseIngress(description, planet, oldHouse, newHouse), gmt, null, Span.INSTANT, Impact.PERSONAL
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
          AstroEventDto(Astro.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.PERSONAL)
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
