/**
 * Created by smallufo on 2021-05-11.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import mu.KotlinLogging
import java.io.Serializable


interface IVoidCourse {

  fun getVoidCourse(h: IHoroscopeModel, planet: Planet = Planet.MOON): Misc.VoidCourse?
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with any planet within the next 30 degrees.
 */
class VoidCourseHellenistic(private val relativeTransit: IRelativeTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(h: IHoroscopeModel, planet: Planet): Misc.VoidCourse? {
    TODO("Not yet implemented")
  }
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with another planet
 * until it moves into the following sign of the zodiac.
 */
class VoidCourseMedieval(private val besiegedImpl: IBesieged,
                         private val starPositionImpl: IStarPosition<*>,
                         private val aspectEffectiveImpl: IAspectEffective,
                         private val starTransitImpl : IStarTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(h: IHoroscopeModel, planet: Planet): Misc.VoidCourse? {


    return besiegedImpl.getBesiegingPlanetsByAspects(planet, h.gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
      .let { (prior, after) ->
        prior!! to after!!
      }
      .let { (exactAspectPrior, exactAspectAfter) ->
        val p1 = exactAspectPrior.points.first { it != planet } as Planet
        val p2 = exactAspectAfter.points.first { it != planet } as Planet

        val pos2: IPos = starPositionImpl.getPosition(p2, exactAspectAfter.gmtJulDay!!, h.location)
        val posPlanet: IPos = h.positionMap[planet]!!

        planet.takeIf {
          // 目前此星(maybe moon) 已經與 p1 離開交角
          !aspectEffectiveImpl.isEffective(planet , posPlanet.lng , p1 , h.getPosition(p1)!!.lng , exactAspectPrior.aspect)
        }?.takeIf {
          // 此星與 p2 ，並未在同一個星座
          (posPlanet.sign != pos2.sign)
        }?.let {
          // 計算進入下一個星座的時間
          val nextSign = posPlanet.sign.next
          val beginGmt = exactAspectPrior.gmtJulDay!!
          val beginDegree = starPositionImpl.getPosition(planet , beginGmt , h.location).lng
          val endGmt = starTransitImpl.getNextTransitGmt(planet, nextSign.degree.toDouble(), Coordinate.ECLIPTIC, h.gmtJulDay, true)
          val endDegree = nextSign.degree.toDouble()
          Misc.VoidCourse(planet,
                          beginGmt, beginDegree,
                          endGmt, endDegree,
                          exactAspectPrior, exactAspectAfter)
        }
      }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
