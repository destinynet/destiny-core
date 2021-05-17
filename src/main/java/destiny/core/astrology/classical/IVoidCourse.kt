/**
 * Created by smallufo on 2021-05-11.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
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
 * This interpretation of Lilly’s definition of void of course is similar to the original Hellenistic definition
 * in that it ignores sign boundaries, although it happens much more frequently than the Hellenistic definition
 * because Lilly’s orb for applying aspects tends to be less than 10 degrees.
 *
 * Unfortunately since this interpretation of Lilly’s definition of void of course is largely based on
 * inferences made from his chart examples, it is still somewhat controversial. Some astrologers agree that
 * this is a correct interpretation of what Lilly meant, and thus that there is a third definition of void of course,
 * while others do not agree that this is a correct interpretation of the text,
 * and thus they believe that there are only two potential definitions.
 */
class VoidCourseWilliamLilly() : IVoidCourse, Serializable {
  override fun getVoidCourse(h: IHoroscopeModel, planet: Planet): Misc.VoidCourse? {
    TODO("Not yet implemented")
  }

}

/**
 * The Moon does not complete an exact Ptolemaic aspect with another planet
 * until it moves into the following sign of the zodiac.
 *
 * 月亮(或其他)剛離開與其他星體的「準確」交角，直到進入下一個星座時，都還沒與其他星體形成準確交角
 */
class VoidCourseMedieval(private val besiegedImpl: IBesieged,
                         private val starPositionImpl: IStarPosition<*>,
                         private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {
  override fun getVoidCourse(h: IHoroscopeModel, planet: Planet): Misc.VoidCourse? {

    return besiegedImpl.getBesiegingPlanetsByAspects(planet, h.gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
      .let { (prior, after) ->
        prior!! to after!!
      }.let { (exactAspectPrior, exactAspectAfter) ->
        val p2 = exactAspectAfter.points.first { it != planet } as Planet

        val pos2: IPos = starPositionImpl.getPosition(p2, exactAspectAfter.gmtJulDay!!, h.location)
        val posPlanet: IPos = h.positionMap[planet]!!

        planet.takeIf {
          // 此星與 p2 ，並未在同一個星座
          posPlanet.sign != pos2.sign
        }?.let {
          // 計算進入下一個星座的時間
          val nextSign = posPlanet.sign.next
          val beginGmt = exactAspectPrior.gmtJulDay!!
          val beginDegree = starPositionImpl.getPosition(planet, beginGmt, h.location).lng
          val endGmt = starTransitImpl.getNextTransitGmt(planet, nextSign.degree.toDouble(), Coordinate.ECLIPTIC, h.gmtJulDay, true)
          val endDegree = nextSign.degree.toDouble()
          Misc.VoidCourse(
            planet, beginGmt, beginDegree, endGmt, endDegree, exactAspectPrior, exactAspectAfter
          )
        }
      }
  }
}
