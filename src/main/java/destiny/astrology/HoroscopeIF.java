/**
 * Created by smallufo on 2017-09-18.
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HoroscopeIF {

  Set<Point> getPoints();

  double getCuspDegree(int cusp);

  double getAngle(Point fromPoint, Point toPoint);

  double getAspectError(Point p1, Point p2, @NotNull Aspect aspect);

  PositionWithAzimuth getPositionWithAzimuth(Point point);

  Map<Star , PositionWithAzimuth> getPositionWithAzimuth(@NotNull List<Star> stars);

  Map<Planet , PositionWithAzimuth> getPlanetPositionWithAzimuth();

  Map<Asteroid , PositionWithAzimuth> getAsteroidPositionWithAzimuth();

  Map<Hamburger , PositionWithAzimuth> getHamburgerPositionWithAzimuth();

  int getHouse(double degree) //getHouse()
  ;
}
