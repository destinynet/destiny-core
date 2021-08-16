/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.Misc.VoidCourse
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.GmtJulDay.Companion.toGmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import kotlin.math.min


@Serializable
data class VoidCourseConfig(@Serializable(with = PointSerializer::class)
                            val planet: Planet = Planet.MOON,
                            val centric: Centric = Centric.GEO,
                            val vocImpl: VoidCourseImpl = VoidCourseImpl.Medieval) {
  enum class VoidCourseImpl {
    Hellenistic,
    Medieval,
    WilliamLilly
  }
}

class VoidCourseConfigBuilder : Builder<VoidCourseConfig> {
  var planet: Planet = Planet.MOON
  var centric: Centric = Centric.GEO
  var impl: VoidCourseConfig.VoidCourseImpl = VoidCourseConfig.VoidCourseImpl.Medieval

  override fun build(): VoidCourseConfig {
    return VoidCourseConfig(planet, centric, impl)
  }

  companion object {
    fun voidCourse(block : VoidCourseConfigBuilder.() -> Unit = {}) : VoidCourseConfig {
      return VoidCourseConfigBuilder().apply(block).build()
    }
  }

}

class VoidCourseFeature(private val besiegedImpl: IBesieged,
                        private val starPositionImpl: IStarPosition<*>,
                        private val starTransitImpl: IStarTransit,
                        private val pointPosFuncMap: Map<Point, IPosition<*>>) : Feature<VoidCourseConfig , VoidCourse?>{

  private val logger = KotlinLogging.logger { }

  override val key: String = "voidCourse"

  override val defaultConfig: VoidCourseConfig = VoidCourseConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: VoidCourseConfig): VoidCourse? {
    val voidCourseImpl: Voc = when(config.vocImpl) {
      VoidCourseConfig.VoidCourseImpl.Hellenistic -> VocHellenistic()//  VoidCourseHellenistic(besiegedImpl, starPositionImpl)
      VoidCourseConfig.VoidCourseImpl.Medieval -> VocMedieval() // VoidCourseMedieval(besiegedImpl, starPositionImpl, starTransitImpl)
      VoidCourseConfig.VoidCourseImpl.WilliamLilly -> VocWilliamWilly() //VoidCourseWilliamLilly(besiegedImpl, starPositionImpl, starTransitImpl)
    }
    return voidCourseImpl.getVoidCourse(gmtJulDay, loc, config.planet, config.centric)
  }

  fun getVocMap(gmtJulDay: GmtJulDay, loc: ILocation , points: Collection<Point> , config: VoidCourseConfig): Map<Planet, VoidCourse> {
    return points.filterIsInstance<Planet>()
      .map { planet ->

        planet to getModel(gmtJulDay, loc, config.copy(planet = planet))
      }
      .filter { (_, voc) -> voc != null }
      .associate { (planet, voc) -> planet to voc!! }
  }

  fun getVoidCourses(
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    loc: ILocation,
    relativeTransitImpl: IRelativeTransit,
    config: VoidCourseConfig
  ): List<VoidCourse> {

    val planets = Planet.classicalList
    val aspects = Aspect.getAspects(Aspect.Importance.HIGH)

    fun getNextVoc(gmt : GmtJulDay): VoidCourse? {

      return relativeTransitImpl.getNearestRelativeTransitGmtJulDay(config.planet, planets, gmt, aspects, true)
        ?.takeIf { nextAspectData -> nextAspectData.gmtJulDay!! < toGmt }
        ?.takeIf { nextAspectData -> nextAspectData.gmtJulDay!! > fromGmt }
        ?.let { nextAspectData ->
          logger.trace { "接下來將在 ${IVoidCourse.julDayResolver.getLocalDateTime(nextAspectData.gmtJulDay!!)} 與 ${nextAspectData.points} 形成 ${nextAspectData.aspect}" }

          val nextTime: GmtJulDay = nextAspectData.gmtJulDay!! + 0.01
          logger.trace { "推進計算時刻 ${IVoidCourse.julDayResolver.getLocalDateTime(nextTime)}" }
          getModel(nextTime , loc , config)?:getNextVoc(nextTime)
          //getVoidCourse(nextTime, loc, pointPosFuncMap, planet, centric) ?: getNextVoc(nextTime)
        }
    }

    fun getVoc(gmt: GmtJulDay , config: VoidCourseConfig) : VoidCourse? {
      val gmtVoc: VoidCourse? = getModel(gmt, loc , config)

      return if (gmtVoc == null) {
        logger.trace { "沒有 VOC : ${IVoidCourse.julDayResolver.getLocalDateTime(gmt)} " }

        getNextVoc(gmt)?.takeIf { nextVoc ->
          nextVoc.beginGmt < toGmt
        }
      } else {
        logger.trace { "免進下一步，直接得到 VOC , 開始於 ${gmtVoc.beginGmt.let { IVoidCourse.julDayResolver.getLocalDateTime(it) }}" }
        gmtVoc
      }
    }

    return generateSequence(getVoc(fromGmt , config)) {
      val newGmt = (min(it.endGmt.value, it.exactAspectAfter.gmtJulDay!!.value) + 0.01).toGmtJulDay()
      if (newGmt < toGmt) {
        getVoc(newGmt, config)?.takeIf { voc -> voc.beginGmt < toGmt }
      } else
        null
    }.toList()
  }

  private sealed interface Voc {
    fun getVoidCourse(gmtJulDay: GmtJulDay, loc: ILocation, planet: Planet = Planet.MOON, centric: Centric = Centric.GEO): VoidCourse?
  }

  /**
   * The Moon does not complete an exact Ptolemaic aspect with any planet within the next 30 degrees.
   */
  private inner class VocHellenistic : Voc {
    override fun getVoidCourse(gmtJulDay: GmtJulDay, loc: ILocation, planet: Planet, centric: Centric): VoidCourse? {

      return besiegedImpl.getBesiegingPlanetsByAspects(planet, gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
        .let { (prior, after) ->
          prior!! to after!!
        }.let { (exactAspectPrior, exactAspectAfter) ->
          val p1 = exactAspectPrior.points.first { it != planet } as Planet
          val p2 = exactAspectAfter.points.first { it != planet } as Planet

          val pos1 = starPositionImpl.getPosition(planet, exactAspectPrior.gmtJulDay!!, loc).lngDeg
          val pos2 = starPositionImpl.getPosition(planet, exactAspectAfter.gmtJulDay!!, loc).lngDeg

          pointPosFuncMap[planet]?.getPosition(gmtJulDay, loc, centric)?.lngDeg?.let { planetDeg: ZodiacDegree ->
            planet.takeIf {
              pos1.getAngle(pos2) > 30
            }?.let {
              VoidCourseHellenistic.logger.trace {
                """
            ${planet}目前在 ${planetDeg.value} 度.
            之前運行到 ${pos1.value} 時，曾與 $p1 形成 ${exactAspectPrior.aspect} , 
            之後運行到 ${pos2.value} 時，將與 $p2 形成 ${exactAspectAfter.aspect} ,
            橫跨共 ${pos1.getAngle(pos2)} 度 , 超過 30度。
          """.trimIndent()
              }
              VoidCourse(planet, exactAspectPrior.gmtJulDay!!, pos1, exactAspectAfter.gmtJulDay!!, pos2, exactAspectPrior, exactAspectAfter)
            }
          }
        }
    }
  }

  /**
   * The Moon does not complete an exact Ptolemaic aspect with another planet
   * until it moves into the following sign of the zodiac.
   *
   * 月亮(或其他)剛離開與其他星體的「準確」交角，直到進入下一個星座時，都還沒與其他星體形成準確交角
   */
  private inner class VocMedieval : Voc {
    override fun getVoidCourse(gmtJulDay: GmtJulDay, loc: ILocation, planet: Planet, centric: Centric): VoidCourse? {
      return besiegedImpl.getBesiegingPlanetsByAspects(planet, gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
        .let { (prior, after) ->
          prior!! to after!!
        }.let { (exactAspectPrior, exactAspectAfter) ->

          val pos2 = starPositionImpl.getPosition(planet, exactAspectAfter.gmtJulDay!!, loc).lngDeg

          pointPosFuncMap[planet]?.getPosition(gmtJulDay, loc, centric)?.lngDeg?.let { planetDeg: ZodiacDegree ->
            planet.takeIf {
              // 此星與 此星運行到 與 p2 形成交角時 (此星的)位置 ，並未在同一個星座
              planetDeg.sign != pos2.sign
            }?.let {
              // 計算進入下一個星座的時間
              val nextSign = planetDeg.sign.next
              val beginGmt = exactAspectPrior.gmtJulDay!!
              val beginDegree = starPositionImpl.getPosition(planet, beginGmt, loc).lngDeg
              val endGmt = starTransitImpl.getNextTransitGmt(planet, nextSign.degree.toZodiacDegree(), gmtJulDay, true)
              val endDegree = nextSign.degree.toZodiacDegree()
              Misc.VoidCourse(
                planet, beginGmt, beginDegree, endGmt, endDegree, exactAspectPrior, exactAspectAfter
              )
            }
          }
        }
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
  private inner class VocWilliamWilly : Voc {
    private val pointDiameter: IPointDiameter = PointDiameterLillyImpl()

    override fun getVoidCourse(gmtJulDay: GmtJulDay, loc: ILocation, planet: Planet, centric: Centric): VoidCourse? {
      return besiegedImpl.getBesiegingPlanetsByAspects(planet, gmtJulDay, Planet.classicalList, Aspect.getAspects(Aspect.Importance.HIGH))
        .let { (prior, after) ->
          prior!! to after!!
        }.let { (exactAspectPrior, exactAspectAfter) ->
          val p1 = exactAspectPrior.points.first { it != planet } as Planet
          val p2 = exactAspectAfter.points.first { it != planet } as Planet


          pointPosFuncMap[planet]?.getPosition(gmtJulDay, loc, centric)?.lngDeg?.let { planetDeg: ZodiacDegree ->
            val planetExactPosPrior = starPositionImpl.getPosition(planet, exactAspectPrior.gmtJulDay!!, loc)
            val planetExactPosAfter = starPositionImpl.getPosition(planet, exactAspectAfter.gmtJulDay!!, loc)

            val combinedMoiety = (pointDiameter.getDiameter(planet) + pointDiameter.getDiameter(p2)) / 2

            val beginDegree = planetExactPosPrior.lngDeg + 6 / 60.0
            val endDegree = planetExactPosAfter.lngDeg - combinedMoiety

            planet.takeIf {
              val angle1 = planetExactPosPrior.lngDeg.getAngle(planetExactPosAfter.lngDeg)
              val angle2 = beginDegree.getAngle(endDegree)
              VoidCourseWilliamLilly.logger.trace { "angle1 = $angle1 , angle2 = $angle2" }
              angle1 > angle2
            }?.takeIf {
              planetDeg.isOccidental(beginDegree)
            }?.takeIf {
              planetDeg.isOriental(endDegree)
            }?.let {
              VoidCourseWilliamLilly.logger.trace {
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
              Misc.VoidCourse(planet, beginGmt, beginDegree, endGmt, endDegree, exactAspectPrior, exactAspectAfter)
            }
          }
        }
    }
  }


}
