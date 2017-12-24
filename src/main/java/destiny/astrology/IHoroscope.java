/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:29:42
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.NotNull;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 2015-06-11 重寫此介面，讓此介面成為 immutable
 */
public interface IHoroscope {

  default Set<Point> getDefaultPoints() {
    Set<Point> pointSet = new HashSet<>();
    pointSet.addAll(Arrays.asList(Planet.SUN , Planet.MOON , Planet.MERCURY , Planet.VENUS , Planet.MARS , Planet.JUPITER , Planet.SATURN));
    //pointSet.addAll(Arrays.asList(Planet.values));
//    pointSet.addAll(Arrays.asList(Asteroid.values));
//    pointSet.addAll(Arrays.asList(Hamburger.values));
//    pointSet.addAll(Arrays.asList(FixedStar.values));
//    pointSet.addAll(Arrays.asList(LunarNode.mean_values));
    return pointSet;
  }

  default Set<Point> getDefaultPoints(NodeType nodeType) {
    Set<Point> pointSet = new HashSet<>();
    pointSet.addAll(Arrays.asList(Planet.values));
    pointSet.addAll(Arrays.asList(Asteroid.values));
    pointSet.addAll(Arrays.asList(Hamburger.values));
    pointSet.addAll(Arrays.asList(FixedStar.values));
    switch (nodeType) {
      case MEAN: pointSet.addAll(Arrays.asList(LunarNode.mean_values)); break;
      case TRUE: pointSet.addAll(Arrays.asList(LunarNode.true_values)); break;
    }
    return pointSet;
  }

  Horoscope getHoroscope(ChronoLocalDateTime lmt , Location loc ,
                         @NotNull Collection<Point> points ,
                         @NotNull HouseSystem houseSystem ,
                         @NotNull Centric centric ,
                         @NotNull Coordinate coordinate ,
                         double temperature , double pressure) ;


  default Horoscope getHoroscope(ChronoLocalDateTime lmt , Location loc ,
                                 @NotNull HouseSystem houseSystem ,
                                 @NotNull Centric centric ,
                                 @NotNull Coordinate coordinate ) {
    return getHoroscope(lmt , loc , getDefaultPoints() , houseSystem , centric , coordinate , 0 , 1013.25);
  }

  default Horoscope getHoroscope(ChronoLocalDateTime lmt , Location loc ,
                                 @NotNull HouseSystem houseSystem ,
                                 @NotNull Centric centric ,
                                 @NotNull Coordinate coordinate ,
                                 @NotNull NodeType nodeType) {
    return getHoroscope(lmt , loc , getDefaultPoints(nodeType) , houseSystem , centric , coordinate , 0 , 1013.25);
  }
}
