/**
 * Created by smallufo on 2021-05-11.
 */
package destiny.core.astrology.classical

import destiny.core.Descriptive
import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import mu.KotlinLogging
import java.io.Serializable
import java.util.*
import kotlin.math.min


sealed interface IVoidCourse : Descriptive {

  fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<Point, IPosition<*>>,
    planet: Planet = Planet.MOON, centric: Centric = Centric.GEO
  ): Misc.VoidCourse?

  fun getVoidCourses(
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    loc: ILocation,
    pointPosFuncMap: Map<Point, IPosition<*>>,
    relativeTransitImpl: IRelativeTransit,
    centric: Centric = Centric.GEO,
    planet: Planet = Planet.MOON
  ): List<Misc.VoidCourse> {

    val planets = Planet.classicalList
    val aspects = Aspect.getAspects(Aspect.Importance.HIGH)

    fun getNextVoc(gmt : GmtJulDay): Misc.VoidCourse? {

      return relativeTransitImpl.getNearestRelativeTransitGmtJulDay(planet, planets, gmt, aspects, true)
        ?.takeIf { nextAspectData -> nextAspectData.gmtJulDay!! < toGmt }
        ?.takeIf { nextAspectData -> nextAspectData.gmtJulDay!! > fromGmt }
        ?.let { nextAspectData ->
          logger.trace { "接下來將在 ${julDayResolver.getLocalDateTime(nextAspectData.gmtJulDay!!)} 與 ${nextAspectData.points} 形成 ${nextAspectData.aspect}" }

          val nextTime: GmtJulDay = nextAspectData.gmtJulDay!! + 0.01
          logger.trace { "推進計算時刻 ${julDayResolver.getLocalDateTime(nextTime)}" }
          getVoidCourse(nextTime, loc, pointPosFuncMap, planet, centric) ?: getNextVoc(nextTime)
        }
    }

    fun getVoc(gmt: GmtJulDay) : Misc.VoidCourse? {
      val gmtVoc: Misc.VoidCourse? = getVoidCourse(gmt, loc, pointPosFuncMap, planet, centric)

      return if (gmtVoc == null) {
        logger.trace { "沒有 VOC : ${julDayResolver.getLocalDateTime(gmt)} " }

        getNextVoc(gmt)?.takeIf { nextVoc ->
          nextVoc.beginGmt < toGmt
        }
      } else {
        logger.trace { "免進下一步，直接得到 VOC , 開始於 ${gmtVoc.beginGmt.let { julDayResolver.getLocalDateTime(it) }}" }
        gmtVoc
      }
    }

    return generateSequence(getVoc(fromGmt)) {
      val newGmt = (min(it.endGmt.value , it.exactAspectAfter.gmtJulDay!!.value) + 0.01).toGmtJulDay()
      if (newGmt < toGmt) {
        getVoc(newGmt)?.takeIf { voc -> voc.beginGmt < toGmt }
      } else
        null
    }.toList()
  }

  fun getVocMap(gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<Point, IPosition<*>> , points: Collection<Point>): Map<Planet, Misc.VoidCourse> {
    return points.filterIsInstance<Planet>()
      .map { planet -> planet to getVoidCourse(gmtJulDay, loc, pointPosFuncMap, planet) }
      .filter { (_, voc) -> voc != null }
      .associate { (planet, voc) -> planet to voc!! }
  }

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IVoidCourse::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      logger.trace { "missing resource : $locale , 傳回 simple name : ${javaClass.simpleName}" }
      javaClass.simpleName
    }
  }

  companion object {
    val logger = KotlinLogging.logger {  }
    val julDayResolver = JulDayResolver1582CutoverImpl()
  }
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with any planet within the next 30 degrees.
 *
 * may be replaced with [VoidCourseFeature.VocHellenistic]
 */
@Impl([Domain(Domains.Astrology.KEY_VOC, VoidCourseHellenistic.VALUE)])
class VoidCourseHellenistic(private val besiegedImpl: IBesieged,
                            private val starPositionImpl: IStarPosition<*>,
                            private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<Point, IPosition<*>>, planet: Planet, centric: Centric
  ): Misc.VoidCourse? {

    val config = VoidCourseConfig(planet, centric, VoidCourseConfig.VoidCourseImpl.Hellenistic)
    val feature = VoidCourseFeature(besiegedImpl, starPositionImpl, starTransitImpl, pointPosFuncMap)
    return feature.getModel(gmtJulDay, loc, config)
  }
  companion object {
    val logger = KotlinLogging.logger {  }
    const val VALUE = "Hellenistic"
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
 * may be replaced with [VoidCourseFeature.VocWilliamWilly]
 */
@Impl([Domain(Domains.Astrology.KEY_VOC, VoidCourseWilliamLilly.VALUE)])
class VoidCourseWilliamLilly(private val besiegedImpl: IBesieged,
                             private val starPositionImpl: IStarPosition<*>,
                             private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {

  override fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<Point, IPosition<*>>, planet: Planet, centric: Centric
  ): Misc.VoidCourse? {

    val config = VoidCourseConfig(planet, centric, VoidCourseConfig.VoidCourseImpl.WilliamLilly)
    val feature = VoidCourseFeature(besiegedImpl, starPositionImpl, starTransitImpl, pointPosFuncMap)
    return feature.getModel(gmtJulDay, loc, config)
  }

  companion object {
    val logger = KotlinLogging.logger {  }
    const val VALUE = "WilliamLilly"
  }
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with another planet
 * until it moves into the following sign of the zodiac.
 *
 * 月亮(或其他)剛離開與其他星體的「準確」交角，直到進入下一個星座時，都還沒與其他星體形成準確交角
 *
 * may be replaced with [VoidCourseFeature.VocMedieval]
 */
@Impl([Domain(Domains.Astrology.KEY_VOC, VoidCourseMedieval.VALUE , default = true)])
class VoidCourseMedieval(private val besiegedImpl: IBesieged,
                         private val starPositionImpl: IStarPosition<*>,
                         private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(
    gmtJulDay: GmtJulDay, loc: ILocation, pointPosFuncMap: Map<Point, IPosition<*>>, planet: Planet, centric: Centric
  ): Misc.VoidCourse? {

    val config = VoidCourseConfig(planet, centric, VoidCourseConfig.VoidCourseImpl.Medieval)
    val feature = VoidCourseFeature(besiegedImpl, starPositionImpl, starTransitImpl, pointPosFuncMap)
    return feature.getModel(gmtJulDay, loc, config)
  }

  companion object {
    const val VALUE = "Medieval"
  }
}
