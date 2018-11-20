/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology

import destiny.astrology.FixedStar.*
import destiny.astrology.Hamburger.*
import destiny.astrology.LunarNode.*
import destiny.astrology.Planet.*

val posSun: IPosition<*> = object : PositionStarImpl(SUN) {}
object PositionFunctions {


  private val posMoon: IPosition<*> = object : PositionStarImpl(MOON) {}
  private val posMercury: IPosition<*> = object : PositionStarImpl(MERCURY) {}
  private val posVenus: IPosition<*> = object : PositionStarImpl(VENUS) {}
  private val posMars: IPosition<*> = object : PositionStarImpl(MARS) {}
  private val posJupiter: IPosition<*> = object : PositionStarImpl(JUPITER) {}
  private val posSaturn: IPosition<*> = object : PositionStarImpl(SATURN) {}
  private val posUranus: IPosition<*> = object : PositionStarImpl(URANUS) {}
  private val posNeptune: IPosition<*> = object : PositionStarImpl(NEPTUNE) {}
  private val posPluto: IPosition<*> = object : PositionStarImpl(PLUTO) {}

  val posPlanets = arrayOf(posSun, posMoon, posMercury, posVenus, posMars, posJupiter, posSaturn, posUranus, posNeptune, posPluto)

  private val posLunarNorth_TRUE: IPosition<*> = object : PositionLunarPointImpl(NORTH_TRUE) {}
  private val posLunarNorth_MEAN: IPosition<*> = object : PositionLunarPointImpl(NORTH_MEAN) {}
  private val posLunarSouth_TRUE: IPosition<*> = object : PositionLunarPointImpl(SOUTH_TRUE) {}
  private val posLunarSouth_MEAN: IPosition<*> = object : PositionLunarPointImpl(SOUTH_MEAN) {}

  private val posLunarNodes_TRUE = arrayOf(posLunarNorth_TRUE, posLunarSouth_TRUE)
  private val posLunarNodes_MEAN = arrayOf(posLunarNorth_MEAN, posLunarSouth_MEAN)


  private val posCeres: IPosition<*> = object : PositionAsteroidImpl(Asteroid.CERES) {}
  private val posPallas: IPosition<*> = object : PositionAsteroidImpl(Asteroid.PALLAS) {}
  private val posJuno: IPosition<*> = object : PositionAsteroidImpl(Asteroid.JUNO) {}
  private val posVesta: IPosition<*> = object : PositionAsteroidImpl(Asteroid.VESTA) {}
  private val posChiron: IPosition<*> = object : PositionAsteroidImpl(Asteroid.CHIRON) {}
  private val posPholus: IPosition<*> = object : PositionAsteroidImpl(Asteroid.PHOLUS) {}

  val posAsteroids = arrayOf(posCeres, posPallas, posJuno, posVesta, posChiron, posPholus)

  private val posAlgol: IPosition<*> = object : PositionFixedStarImpl(ALGOL) {}
  private val posAldebaran: IPosition<*> = object : PositionFixedStarImpl(ALDEBARAN) {}
  private val posRigel: IPosition<*> = object : PositionFixedStarImpl(RIGEL) {}
  private val posCapella: IPosition<*> = object : PositionFixedStarImpl(CAPELLA) {}
  private val posBetelgeuse: IPosition<*> = object : PositionFixedStarImpl(BETELGEUSE) {}
  private val posSirius: IPosition<*> = object : PositionFixedStarImpl(SIRIUS) {}
  private val posCanopus: IPosition<*> = object : PositionFixedStarImpl(CANOPUS) {}
  private val posPollux: IPosition<*> = object : PositionFixedStarImpl(POLLUX) {}
  private val posProcyon: IPosition<*> = object : PositionFixedStarImpl(PROCYON) {}
  private val posPraesepe: IPosition<*> = object : PositionFixedStarImpl(PRAESEPE) {}
  private val posAlphard: IPosition<*> = object : PositionFixedStarImpl(ALPHARD) {}
  private val posRegulus: IPosition<*> = object : PositionFixedStarImpl(REGULUS) {}
  private val posSpica: IPosition<*> = object : PositionFixedStarImpl(SPICA) {}
  private val posArcturus: IPosition<*> = object : PositionFixedStarImpl(ARCTURUS) {}
  private val posAntares: IPosition<*> = object : PositionFixedStarImpl(ANTARES) {}
  private val posVega: IPosition<*> = object : PositionFixedStarImpl(VEGA) {}
  private val posAltair: IPosition<*> = object : PositionFixedStarImpl(ALTAIR) {}
  private val posFomalhaut: IPosition<*> = object : PositionFixedStarImpl(FOMALHAUT) {}
  private val posDeneb: IPosition<*> = object : PositionFixedStarImpl(DENEB) {}

  val posFixedStars = arrayOf(
    posAlgol, posAldebaran, posRigel, posCapella, posBetelgeuse, posSirius, posCanopus, posPollux, posProcyon,
    posPraesepe, posAlphard, posRegulus, posSpica, posArcturus, posAntares, posVega, posAltair, posFomalhaut, posDeneb)


  private val posCupido: IPosition<*> = object : PositionHamburgerImpl(CUPIDO) {}
  private val posHades: IPosition<*> = object : PositionHamburgerImpl(HADES) {}
  private val posZeus: IPosition<*> = object : PositionHamburgerImpl(ZEUS) {}
  private val posKronos: IPosition<*> = object : PositionHamburgerImpl(KRONOS) {}
  private val posApollon: IPosition<*> = object : PositionHamburgerImpl(APOLLON) {}
  private val posAdmetos: IPosition<*> = object : PositionHamburgerImpl(ADMETOS) {}
  private val posVulkanus: IPosition<*> = object : PositionHamburgerImpl(VULKANUS) {}
  private val posPoseidon: IPosition<*> = object : PositionHamburgerImpl(POSEIDON) {}

  val posHamburgers = arrayOf(
    posCupido, posHades, posZeus, posKronos, posApollon, posAdmetos, posVulkanus, posPoseidon
  )

  val sets = setOf(
    *posPlanets,
    *posAsteroids,
    *posFixedStars,
    *posLunarNodes_TRUE,
    *posLunarNodes_MEAN,
    *posHamburgers)

  val pointPosMap: Map<Point, IPosition<*>> =
    sets.map {
      it.point to it
    }.toMap()
}
