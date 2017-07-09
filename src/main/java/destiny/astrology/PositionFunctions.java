/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import com.google.common.collect.ImmutableSet;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PositionFunctions {

  private static Logger logger = LoggerFactory.getLogger(PositionFunctions.class);

  public final static IPosition posSun = new PositionStarImpl(Planet.SUN) {} ;
  public final static IPosition posMoon = new PositionStarImpl(Planet.MOON) {} ;
  public final static IPosition posMercury = new PositionStarImpl(Planet.MERCURY) {};
  public final static IPosition posVenus = new PositionStarImpl(Planet.VENUS) {};
  public final static IPosition posMars = new PositionStarImpl(Planet.MARS) {};
  public final static IPosition posJupiter = new PositionStarImpl(Planet.JUPITER) {};
  public final static IPosition posSaturn = new PositionStarImpl(Planet.SATURN) {};
  public final static IPosition posUranus = new PositionStarImpl(Planet.URANUS) {};
  public final static IPosition posNeptune = new PositionStarImpl(Planet.NEPTUNE) {};
  public final static IPosition posPluto = new PositionStarImpl(Planet.PLUTO) {};

  public final static Set<IPosition> posPlanets = new ImmutableSet.Builder<IPosition>()
    .add(posSun , posMoon , posMercury , posVenus , posMars , posJupiter , posSaturn , posUranus , posNeptune , posPluto)
    .build();

  public final static IPosition posLunarNorth = new PositionLunarPointImpl(LunarNode.NORTH_TRUE) {};
  public final static IPosition posLunarSouth = new PositionLunarPointImpl(LunarNode.SOUTH_TRUE) {};

  public final static IPosition posCeres = new PositionAsteroidImpl(Asteroid.CERES) {};
  public final static IPosition posPallas = new PositionAsteroidImpl(Asteroid.PALLAS) {};
  public final static IPosition posJuno = new PositionAsteroidImpl(Asteroid.JUNO) {};
  public final static IPosition posVesta = new PositionAsteroidImpl(Asteroid.VESTA) {};
  public final static IPosition posChiron = new PositionAsteroidImpl(Asteroid.CHIRON) {};
  public final static IPosition posPholus = new PositionAsteroidImpl(Asteroid.PHOLUS) {};

  public final static Set<IPosition> posAsteroids = new ImmutableSet.Builder<IPosition>()
    .add(posCeres , posPallas , posJuno , posVesta , posChiron , posPholus)
    .build();

  public final static IPosition posAlgol = new PositionFixedStarImpl(FixedStar.ALGOL) {};
  public final static IPosition posAldebaran = new PositionFixedStarImpl(FixedStar.ALDEBARAN) {};
  public final static IPosition posRigel = new PositionFixedStarImpl(FixedStar.RIGEL) {};
  public final static IPosition posCapella = new PositionFixedStarImpl(FixedStar.CAPELLA) {};
  public final static IPosition posBetelgeuse = new PositionFixedStarImpl(FixedStar.BETELGEUSE) {};
  public final static IPosition posSirius = new PositionFixedStarImpl(FixedStar.SIRIUS) {};
  public final static IPosition posCanopus = new PositionFixedStarImpl(FixedStar.CANOPUS) {};
  public final static IPosition posPollux = new PositionFixedStarImpl(FixedStar.POLLUX) {};
  public final static IPosition posProcyon = new PositionFixedStarImpl(FixedStar.PROCYON) {};
  public final static IPosition posPraesepe = new PositionFixedStarImpl(FixedStar.PRAESEPE) {};
  public final static IPosition posAlphard = new PositionFixedStarImpl(FixedStar.ALPHARD) {};
  public final static IPosition posRegulus = new PositionFixedStarImpl(FixedStar.REGULUS) {};
  public final static IPosition posSpica = new PositionFixedStarImpl(FixedStar.SPICA) {};
  public final static IPosition posArcturus = new PositionFixedStarImpl(FixedStar.ARCTURUS) {};
  public final static IPosition posAntares = new PositionFixedStarImpl(FixedStar.ANTARES) {};
  public final static IPosition posVega = new PositionFixedStarImpl(FixedStar.VEGA) {};
  public final static IPosition posAltair = new PositionFixedStarImpl(FixedStar.ALTAIR) {};
  public final static IPosition posFomalhaut = new PositionFixedStarImpl(FixedStar.FOMALHAUT) {};
  public final static IPosition posDeneb = new PositionFixedStarImpl(FixedStar.DENEB) {};

  public final static Set<IPosition> posFixedStars = new ImmutableSet.Builder<IPosition>()
    .add(posAlgol , posAldebaran , posRigel , posCapella , posBetelgeuse , posSirius , posCanopus , posPollux , posProcyon ,
      posPraesepe , posAlphard , posRegulus , posSpica , posArcturus , posAntares , posVega , posAltair , posFomalhaut , posDeneb)
    .build();

  public final static IPosition posCupido = new PositionHamburgerImpl(Hamburger.CUPIDO) {};
  public final static IPosition posHades = new PositionHamburgerImpl(Hamburger.HADES) {};
  public final static IPosition posZeus = new PositionHamburgerImpl(Hamburger.ZEUS) {};
  public final static IPosition posKronos = new PositionHamburgerImpl(Hamburger.KRONOS) {};
  public final static IPosition posApollon = new PositionHamburgerImpl(Hamburger.APOLLON) {};
  public final static IPosition posAdmetos = new PositionHamburgerImpl(Hamburger.ADMETOS) {};
  public final static IPosition posVulkanus = new PositionHamburgerImpl(Hamburger.VULKANUS) {};
  public final static IPosition posPoseidon = new PositionHamburgerImpl(Hamburger.POSEIDON) {};

  public final static Set<IPosition> posHamburgers = new ImmutableSet.Builder<IPosition>()
    .add(posCupido , posHades , posZeus , posKronos , posApollon , posAdmetos , posVulkanus , posPoseidon)
    .build();

  public final static Map<Point , IPosition> pointPosMap = new ImmutableSet.Builder<IPosition>()
    .addAll(posPlanets)
    .addAll(posAsteroids)
    .addAll(posFixedStars)
    .addAll(posHamburgers)
    .build().stream()
    .map(iPosition -> Tuple.tuple(iPosition.getPoint() , iPosition))
    .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));




}
