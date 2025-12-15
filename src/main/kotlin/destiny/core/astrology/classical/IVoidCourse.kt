/**
 * Created by smallufo on 2021-05-11.
 */
package destiny.core.astrology.classical

import destiny.core.Descriptive
import destiny.core.astrology.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.Misc
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.tools.KotlinLogging
import java.io.Serializable
import java.util.*
import kotlin.math.min


sealed interface IVoidCourse : Descriptive {

  fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<AstroPoint, IPosition<*>>,
    planet: Planet = Planet.MOON, centric: Centric = Centric.GEO, starTypeOptions: StarTypeOptions = StarTypeOptions.DEFAULT
  ): Misc.VoidCourseSpan?

  fun getVoidCourses(
    fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<AstroPoint, IPosition<*>>,
    relativeTransitImpl: IRelativeTransit, centric: Centric = Centric.GEO, planet: Planet = Planet.MOON, starTypeOptions: StarTypeOptions = StarTypeOptions.DEFAULT
  ): Sequence<Misc.VoidCourseSpan> {

    val planets = Planet.classicalSet
    val aspects = Aspect.getAspects(Aspect.Importance.HIGH).toSet()

    fun getNextVoc(gmt: GmtJulDay): Misc.VoidCourseSpan? {

      return relativeTransitImpl.getNearestRelativeTransitGmtJulDay(planet, planets, gmt, aspects, true)
        ?.takeIf { nextAspectData: IAspectData -> nextAspectData.gmtJulDay < toGmt }
        ?.takeIf { nextAspectData -> nextAspectData.gmtJulDay > fromGmt }
        ?.let { nextAspectData ->
          logger.trace { "接下來將在 ${julDayResolver.getLocalDateTime(nextAspectData.gmtJulDay)} 與 ${nextAspectData.points} 形成 ${nextAspectData.aspect}" }

          val nextTime: GmtJulDay = nextAspectData.gmtJulDay + 0.01
          logger.trace { "推進計算時刻 ${julDayResolver.getLocalDateTime(nextTime)}" }
          getVoidCourse(nextTime, loc, pointPosFuncMap, planet, centric, starTypeOptions) ?: getNextVoc(nextTime)
        }
    }

    fun getVoc(gmt: GmtJulDay): Misc.VoidCourseSpan? {
      val gmtVoc: Misc.VoidCourseSpan? = getVoidCourse(gmt, loc, pointPosFuncMap, planet, centric)

      return if (gmtVoc == null) {
        logger.trace { "沒有 VOC : ${julDayResolver.getLocalDateTime(gmt)} " }

        getNextVoc(gmt)?.takeIf { nextVoc ->
          nextVoc.begin < toGmt
        }
      } else {
        logger.trace { "免進下一步，直接得到 VOC , 開始於 ${gmtVoc.begin.let { julDayResolver.getLocalDateTime(it) }}" }
        gmtVoc
      }
    }

    return generateSequence(getVoc(fromGmt)) {
      val newGmt = (min(it.end.value, it.exactAspectAfter.gmtJulDay.value) + 0.01).toGmtJulDay()
      if (newGmt < toGmt) {
        getVoc(newGmt)?.takeIf { voc -> voc.begin in fromGmt..toGmt || voc.end in fromGmt..toGmt }
      } else
        null
    }
  }

  fun getVocMap(gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<AstroPoint, IPosition<*>>, points: Set<AstroPoint>): Map<Planet, Misc.VoidCourseSpan> {
    return points.filterIsInstance<Planet>()
      .map { planet -> planet to getVoidCourse(gmtJulDay, loc, pointPosFuncMap, planet) }
      .filter { (_, voc) -> voc != null }
      .associate { (planet, voc) -> planet to voc!! }
  }

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IVoidCourse::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      logger.trace { "missing resource : $locale , 傳回 simple name : ${javaClass.simpleName}" }
      javaClass.simpleName
    }
  }

  companion object {
    val logger = KotlinLogging.logger { }
    val julDayResolver = JulDayResolver1582CutoverImpl()
  }
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with any planet within the next 30 degrees.
 */
class VoidCourseHellenistic(
  private val besiegedImpl: IBesieged,
  private val starPositionImpl: IStarPosition<*>
) : IVoidCourse, Serializable {
  override fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<AstroPoint, IPosition<*>>, planet: Planet, centric: Centric, starTypeOptions: StarTypeOptions
  ): Misc.VoidCourseSpan? {

    return besiegedImpl.getBesiegingPlanetsByAspects(planet, gmtJulDay, Planet.classicalSet, Aspect.getAspects(Aspect.Importance.HIGH).toSet())
      .let { (prior, after) ->
        prior!! to after!!
      }.let { (exactAspectPrior, exactAspectAfter) ->
        val p1 = exactAspectPrior.points.first { it != planet } as Planet
        val p2 = exactAspectAfter.points.first { it != planet } as Planet

        val pos1 = starPositionImpl.calculate(planet, exactAspectPrior.gmtJulDay, loc, centric, Coordinate.ECLIPTIC, starTypeOptions)
        val pos2 = starPositionImpl.calculate(planet, exactAspectAfter.gmtJulDay, loc, centric, Coordinate.ECLIPTIC, starTypeOptions)


        pointPosFuncMap[planet]?.getPosition(gmtJulDay, loc, centric, starTypeOptions = StarTypeOptions.DEFAULT)?.lngDeg?.let { planetDeg: ZodiacDegree ->
          planet.takeIf {
            pos1.lngDeg.getAngle(pos2.lngDeg) > 30
          }?.let {
            logger.trace {
              """
            ${planet}目前在 ${planetDeg.value} 度.
            之前運行到 ${pos1.lngDeg.value} 時，曾與 $p1 形成 ${exactAspectPrior.aspect} , 
            之後運行到 ${pos2.lngDeg.value} 時，將與 $p2 形成 ${exactAspectAfter.aspect} ,
            橫跨共 ${pos1.lngDeg.getAngle(pos2.lngDeg)} 度 , 超過 30度。
          """.trimIndent()
            }
            Misc.VoidCourseSpan(planet, exactAspectPrior.gmtJulDay, pos1, exactAspectAfter.gmtJulDay, pos2, exactAspectPrior as AspectData, exactAspectAfter as AspectData)
          }
        }
      }
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}

/**
 * This interpretation of Lilly’s definition of void of course is similar to the original Hellenistic definition
 * in that it ignores sign boundaries, although it happens much more frequently than the Hellenistic definition
 * because Lilly’s orb for applying aspects tends to be less than 10 degrees.
 *
 * Unfortunately since this interpretation of Lilly’s definition of void of course is largely based on
 * inferences made from his chart examples, it is still somewhat controversial. Some astrologers agree that
 * this is a correct interpretation of what Lilly meant, and thus that there is a third definition of void of course,
 * while others do not agree that this is a correct interpretation of the text,
 * and thus they believe that there are only two potential definitions.
 *
 * 月亮先後被兩星 (p1,p2) 包夾，
 * 月亮先離開與 p1交角+6分之後 , VOC 開始
 * 直到碰到 p2 - (月半徑/2 + p2半徑/2) 點，就會進入 p2 交角勢力範圍 , VOC 結束
 *
 */
class VoidCourseWilliamLilly(private val besiegedImpl: IBesieged,
                             private val starPositionImpl: IStarPosition<*>,
                             private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {

  private val pointDiameter: IPointDiameter = PointDiameterLillyImpl()

  override fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<AstroPoint, IPosition<*>>, planet: Planet, centric: Centric, starTypeOptions: StarTypeOptions
  ): Misc.VoidCourseSpan? {
    return besiegedImpl.getBesiegingPlanetsByAspects(planet, gmtJulDay, Planet.classicalSet, Aspect.getAspects(Aspect.Importance.HIGH).toSet())
      .let { (prior, after) ->
        prior!! to after!!
      }.let { (exactAspectPrior, exactAspectAfter) ->
        val p1 = exactAspectPrior.points.first { it != planet } as Planet
        val p2 = exactAspectAfter.points.first { it != planet } as Planet


        pointPosFuncMap[planet]?.getPosition(gmtJulDay, loc, centric, starTypeOptions = StarTypeOptions.DEFAULT)?.lngDeg?.let { planetDeg: ZodiacDegree ->
          val planetExactPosPrior = starPositionImpl.calculate(planet, exactAspectPrior.gmtJulDay, loc, centric, Coordinate.ECLIPTIC, starTypeOptions)
          val planetExactPosAfter = starPositionImpl.calculate(planet, exactAspectAfter.gmtJulDay, loc, centric, Coordinate.ECLIPTIC, starTypeOptions)

          val combinedMoiety = (pointDiameter.getDiameter(planet) + pointDiameter.getDiameter(p2)) / 2

          val beginDegree = planetExactPosPrior.lngDeg + 6 / 60.0
          val endDegree = planetExactPosAfter.lngDeg - combinedMoiety

          planet.takeIf {
            val angle1 = planetExactPosPrior.lngDeg.getAngle(planetExactPosAfter.lngDeg)
            val angle2 = beginDegree.getAngle(endDegree)
            logger.trace { "angle1 = $angle1 , angle2 = $angle2" }
            angle1 > angle2
          }?.takeIf {
            planetDeg.isOccidental(beginDegree)
          }?.takeIf {
            planetDeg.isOriental(endDegree)
          }?.let {
            logger.trace {
              """$planet 之前曾與 $p1 形成 ${exactAspectPrior.aspect} , 發生於黃道 ${planetExactPosPrior.lng} , 星座 = ${planetExactPosPrior.lngDeg.signDegree}
              |加上 6 分之後 , 
              |VOC 開始於 黃道 $beginDegree , 星座 = ${beginDegree.sign}
              |$planet 目前位於黃道 ${planetDeg.value} , 星座 = ${planetDeg.sign}
              |VOC 結束於 黃道 $endDegree , 星座 = ${endDegree.sign}
              |加上 $combinedMoiety 度之後
              |$planet 之後將與 $p2 形成 ${exactAspectAfter.aspect} , 發生於黃道 ${planetExactPosAfter.lng} , 星座 = ${planetExactPosAfter.lngDeg.signDegree}
            """.trimMargin()
            }

            val beginGmt = starTransitImpl.getNextTransitGmt(planet, beginDegree, gmtJulDay, false)
            val endGmt = starTransitImpl.getNextTransitGmt(planet, endDegree, gmtJulDay, true)
            Misc.VoidCourseSpan(planet, beginGmt, beginDegree, endGmt, endDegree,
                                exactAspectPrior as AspectData, exactAspectAfter as AspectData)
          }
        }
      }
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with another planet
 * until it moves into the following sign of the zodiac.
 *
 * 月亮(或其他)剛離開與其他星體的「準確」交角，直到進入下一個星座時，都還沒與其他星體形成準確交角
 *
 * (最常出現)
 */
class VoidCourseMedieval(private val besiegedImpl: IBesieged,
                         private val starPositionImpl: IStarPosition<*>,
                         private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<AstroPoint, IPosition<*>>, planet: Planet, centric: Centric, starTypeOptions: StarTypeOptions
  ): Misc.VoidCourseSpan? {

    return besiegedImpl.getBesiegingPlanetsByAspects(planet, gmtJulDay, Planet.classicalSet, Aspect.getAspects(Aspect.Importance.HIGH).toSet())
      .let { (prior, after) ->
        prior!! to after!!
      }.let { (exactAspectPrior, exactAspectAfter) ->

        val pos2 = starPositionImpl.calculate(planet, exactAspectAfter.gmtJulDay, loc, centric, Coordinate.ECLIPTIC, starTypeOptions).lngDeg

        pointPosFuncMap[planet]?.getPosition(gmtJulDay, loc, centric, starTypeOptions = StarTypeOptions.DEFAULT)?.lngDeg?.let { planetDeg: ZodiacDegree ->
          planet.takeIf {
            // 此星與 此星運行到 與 p2 形成交角時 (此星的)位置 ，並未在同一個星座
            planetDeg.sign != pos2.sign
          }?.let {
            // 計算進入下一個星座的時間
            val nextSign = planetDeg.sign.next
            val beginGmt = exactAspectPrior.gmtJulDay
            val beginDegree = starPositionImpl.calculate(planet, beginGmt, loc, centric, Coordinate.ECLIPTIC, starTypeOptions).lngDeg
            val endGmt = starTransitImpl.getNextTransitGmt(planet, nextSign.degree.toZodiacDegree(), gmtJulDay, true)
            val endDegree = nextSign.degree.toZodiacDegree()
            Misc.VoidCourseSpan(
              planet, beginGmt, beginDegree, endGmt, endDegree, exactAspectPrior as AspectData, exactAspectAfter as AspectData
            )
          }
        }
      }
  }
}
