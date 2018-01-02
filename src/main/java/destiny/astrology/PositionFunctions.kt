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


  val posMoon: IPosition<*> = object : PositionStarImpl(MOON) {}
  val posMercury: IPosition<*> = object : PositionStarImpl(MERCURY) {}
  val posVenus: IPosition<*> = object : PositionStarImpl(VENUS) {}
  val posMars: IPosition<*> = object : PositionStarImpl(MARS) {}
  val posJupiter: IPosition<*> = object : PositionStarImpl(JUPITER) {}
  val posSaturn: IPosition<*> = object : PositionStarImpl(SATURN) {}
  val posUranus: IPosition<*> = object : PositionStarImpl(URANUS) {}
  val posNeptune: IPosition<*> = object : PositionStarImpl(NEPTUNE) {}
  val posPluto: IPosition<*> = object : PositionStarImpl(PLUTO) {}

  val posPlanets = arrayOf(posSun, posMoon, posMercury, posVenus, posMars, posJupiter, posSaturn, posUranus, posNeptune, posPluto)

  val posLunarNorth_TRUE: IPosition<*> = object : PositionLunarPointImpl(NORTH_TRUE) {}
  val posLunarNorth_MEAN: IPosition<*> = object : PositionLunarPointImpl(NORTH_MEAN) {}
  val posLunarSouth_TRUE: IPosition<*> = object : PositionLunarPointImpl(SOUTH_TRUE) {}
  val posLunarSouth_MEAN: IPosition<*> = object : PositionLunarPointImpl(SOUTH_MEAN) {}

  val posLunarNodes_TRUE = arrayOf(posLunarNorth_TRUE, posLunarSouth_TRUE)
  val posLunarNodes_MEAN = arrayOf(posLunarNorth_MEAN, posLunarSouth_MEAN)


  val posCeres: IPosition<*> = object : PositionAsteroidImpl(Asteroid.CERES) {}
  val posPallas: IPosition<*> = object : PositionAsteroidImpl(Asteroid.PALLAS) {}
  val posJuno: IPosition<*> = object : PositionAsteroidImpl(Asteroid.JUNO) {}
  val posVesta: IPosition<*> = object : PositionAsteroidImpl(Asteroid.VESTA) {}
  val posChiron: IPosition<*> = object : PositionAsteroidImpl(Asteroid.CHIRON) {}
  val posPholus: IPosition<*> = object : PositionAsteroidImpl(Asteroid.PHOLUS) {}

  val posAsteroids = arrayOf(posCeres, posPallas, posJuno, posVesta, posChiron, posPholus)

  val posAlgol: IPosition<*> = object : PositionFixedStarImpl(ALGOL) {}
  val posAldebaran: IPosition<*> = object : PositionFixedStarImpl(ALDEBARAN) {}
  val posRigel: IPosition<*> = object : PositionFixedStarImpl(RIGEL) {}
  val posCapella: IPosition<*> = object : PositionFixedStarImpl(CAPELLA) {}
  val posBetelgeuse: IPosition<*> = object : PositionFixedStarImpl(BETELGEUSE) {}
  val posSirius: IPosition<*> = object : PositionFixedStarImpl(SIRIUS) {}
  val posCanopus: IPosition<*> = object : PositionFixedStarImpl(CANOPUS) {}
  val posPollux: IPosition<*> = object : PositionFixedStarImpl(POLLUX) {}
  val posProcyon: IPosition<*> = object : PositionFixedStarImpl(PROCYON) {}
  val posPraesepe: IPosition<*> = object : PositionFixedStarImpl(PRAESEPE) {}
  val posAlphard: IPosition<*> = object : PositionFixedStarImpl(ALPHARD) {}
  val posRegulus: IPosition<*> = object : PositionFixedStarImpl(REGULUS) {}
  val posSpica: IPosition<*> = object : PositionFixedStarImpl(SPICA) {}
  val posArcturus: IPosition<*> = object : PositionFixedStarImpl(ARCTURUS) {}
  val posAntares: IPosition<*> = object : PositionFixedStarImpl(ANTARES) {}
  val posVega: IPosition<*> = object : PositionFixedStarImpl(VEGA) {}
  val posAltair: IPosition<*> = object : PositionFixedStarImpl(ALTAIR) {}
  val posFomalhaut: IPosition<*> = object : PositionFixedStarImpl(FOMALHAUT) {}
  val posDeneb: IPosition<*> = object : PositionFixedStarImpl(DENEB) {}

  val posFixedStars = arrayOf(
    posAlgol, posAldebaran, posRigel, posCapella, posBetelgeuse, posSirius, posCanopus, posPollux, posProcyon,
    posPraesepe, posAlphard, posRegulus, posSpica, posArcturus, posAntares, posVega, posAltair, posFomalhaut, posDeneb)


  val posCupido: IPosition<*> = object : PositionHamburgerImpl(CUPIDO) {}
  val posHades: IPosition<*> = object : PositionHamburgerImpl(HADES) {}
  val posZeus: IPosition<*> = object : PositionHamburgerImpl(ZEUS) {}
  val posKronos: IPosition<*> = object : PositionHamburgerImpl(KRONOS) {}
  val posApollon: IPosition<*> = object : PositionHamburgerImpl(APOLLON) {}
  val posAdmetos: IPosition<*> = object : PositionHamburgerImpl(ADMETOS) {}
  val posVulkanus: IPosition<*> = object : PositionHamburgerImpl(VULKANUS) {}
  val posPoseidon: IPosition<*> = object : PositionHamburgerImpl(POSEIDON) {}

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
