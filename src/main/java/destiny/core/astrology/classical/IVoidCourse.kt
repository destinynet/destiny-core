/**
 * Created by smallufo on 2021-05-11.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.classical.rules.Misc
import destiny.tools.CircleTools.normalize
import mu.KotlinLogging
import java.io.Serializable


interface IVoidCourse {

  fun getVoidCourse(h: IHoroscopeModel, planet: Planet = Planet.MOON): Misc.VoidCourse?
}

/**
 * The Moon does not complete an exact Ptolemaic aspect with any planet within the next 30 degrees.
 */
class VoidCourseHellenistic(private val besiegedImpl: IBesieged,
                            private val starPositionImpl: IStarPosition<*>) : IVoidCourse, Serializable {
  override fun getVoidCourse(h: IHoroscopeModel, planet: Planet): Misc.VoidCourse? {

    return besiegedImpl.getBesiegingPlanetsByAspects(planet, h.gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
      .let { (prior, after) ->
        prior!! to after!!
      }.let { (exactAspectPrior, exactAspectAfter) ->
        val p1 = exactAspectPrior.points.first { it != planet } as Planet
        val p2 = exactAspectAfter.points.first { it != planet } as Planet

        val pos1 = starPositionImpl.getPosition(planet, exactAspectPrior.gmtJulDay!!, h.location).lngDeg
        val pos2 = starPositionImpl.getPosition(planet, exactAspectAfter.gmtJulDay!!, h.location).lngDeg
        val posPlanet: IPos = h.positionMap[planet]!!

        planet.takeIf {
          pos1.getAngle(pos2) > 30
        }?.let {
          logger.trace { """
            ${planet}目前在 ${posPlanet.lng} 度.
            之前運行到 ${pos1.value} 時，曾與 $p1 形成 ${exactAspectPrior.aspect} , 
            之後運行到 ${pos2.value} 時，將與 $p2 形成 ${exactAspectAfter.aspect} ,
            橫跨共 ${pos1.getAngle(pos2)} 度 , 超過 30度。
          """.trimIndent() }

          Misc.VoidCourse(planet , exactAspectPrior.gmtJulDay!! , pos1 , exactAspectAfter.gmtJulDay!! , pos2 , exactAspectPrior , exactAspectAfter)
        }
      }
  }
  companion object {
    val logger = KotlinLogging.logger {  }
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
 */
class VoidCourseWilliamLilly(private val besiegedImpl: IBesieged,
                             private val starPositionImpl: IStarPosition<*>,
                             private val starTransitImpl: IStarTransit) : IVoidCourse, Serializable {

  private val pointDiameter: IPointDiameter = PointDiameterLillyImpl()

  override fun getVoidCourse(h: IHoroscopeModel, planet: Planet): Misc.VoidCourse? {
    return besiegedImpl.getBesiegingPlanetsByAspects(planet, h.gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
      .let { (prior, after) ->
        prior!! to after!!
      }.let { (exactAspectPrior, exactAspectAfter) ->
        val p1 = exactAspectPrior.points.first { it != planet } as Planet
        val p2 = exactAspectAfter.points.first { it != planet } as Planet

        val posPlanet = h.positionMap[planet]!!.lngDeg

        val planetExactPosPrior = starPositionImpl.getPosition(planet, exactAspectPrior.gmtJulDay!!, h.location)
        val planetExactPosAfter = starPositionImpl.getPosition(planet, exactAspectAfter.gmtJulDay!!, h.location)


        val combinedMoiety =  (pointDiameter.getDiameter(planet) + pointDiameter.getDiameter(p2)) / 2

        val beginDegree = ZodiacDegree((planetExactPosPrior.lng + 6/60.0).normalize())
        val endDegree = ZodiacDegree((planetExactPosAfter.lng - combinedMoiety).normalize())

        planet.takeIf {
          val angle1 = planetExactPosPrior.lngDeg.getAngle(planetExactPosAfter.lngDeg)
          val angle2 = beginDegree.getAngle(endDegree)
          logger.trace { "angle1 = $angle1 , angle2 = $angle2" }
          angle1 > angle2
        }?.takeIf {
          posPlanet.isOccidental(beginDegree)
        }?.takeIf {
          posPlanet.isOriental(endDegree)
        }?.let {
          logger.trace {
            """$planet 之前曾與 $p1 形成 ${exactAspectPrior.aspect} , 發生於黃道 ${planetExactPosPrior.lng} , 星座 = ${planetExactPosPrior.lngDeg.signDegree}
              |加上 6 分之後 , 
              |VOC 開始於 黃道 $beginDegree , 星座 = ${beginDegree.sign}
              |$planet 目前位於黃道 ${posPlanet.value} , 星座 = ${posPlanet.sign}
              |VOC 結束於 黃道 $endDegree , 星座 = ${endDegree.sign}
              |加上 $combinedMoiety 度之後
              |$planet 之後將與 $p2 形成 ${exactAspectAfter.aspect} , 發生於黃道 ${planetExactPosAfter.lng} , 星座 = ${planetExactPosAfter.lngDeg.signDegree}
            """.trimMargin()
          }

          val beginGmt = starTransitImpl.getNextTransitGmt(planet, beginDegree.value, h.coordinate, h.gmtJulDay, false)
          val endGmt = starTransitImpl.getNextTransitGmt(planet, endDegree.value, h.coordinate, h.gmtJulDay, true)
          Misc.VoidCourse(planet, beginGmt, beginDegree, endGmt, endDegree, exactAspectPrior, exactAspectAfter)
        }
      }
  }

  companion object {
    val logger = KotlinLogging.logger {  }
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

        val pos2 = starPositionImpl.getPosition(p2, exactAspectAfter.gmtJulDay!!, h.location).lngDeg
        val posPlanet = h.positionMap[planet]!!.lngDeg

        planet.takeIf {
          // 此星與 p2 ，並未在同一個星座
          posPlanet.sign != pos2.sign
        }?.let {
          // 計算進入下一個星座的時間
          val nextSign = posPlanet.sign.next
          val beginGmt = exactAspectPrior.gmtJulDay!!
          val beginDegree = starPositionImpl.getPosition(planet, beginGmt, h.location).lngDeg
          val endGmt = starTransitImpl.getNextTransitGmt(planet, nextSign.degree.toDouble(), Coordinate.ECLIPTIC, h.gmtJulDay, true)
          val endDegree = ZodiacDegree(nextSign.degree.toDouble())
          Misc.VoidCourse(
            planet, beginGmt, beginDegree, endGmt, endDegree, exactAspectPrior, exactAspectAfter
          )
        }
      }
  }
}
