/**
 * @author smallufo 
 * Created on 2007/11/25 at 上午 12:21:59
 */ 
package destiny.astrology.classical;

import java.io.Serializable;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import destiny.astrology.Planet;
import destiny.astrology.Point;
import org.jetbrains.annotations.NotNull;

/**
 * 古典占星術，William Lilly 的交角 <br/>
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 比較少用，對比於內定的 PointDiameterAlBiruniImpl
 */
public class PointDiameterLillyImpl implements PointDiameterIF , Serializable
{
  private final static ImmutableMap<Point , Double> planetOrbsMap = new ImmutableMap.Builder<Point , Double>()
    .put(Planet.SUN, 17.0)
    .put(Planet.MOON, 12.5)
    .put(Planet.MERCURY, 7.0)
    .put(Planet.VENUS, 8.0)
    .put(Planet.MARS, 7.5)
    .put(Planet.JUPITER , 12.0)
    .put(Planet.SATURN , 10.0)
    .put(Planet.URANUS, 5.0)
    .put(Planet.NEPTUNE, 5.0)
    .put(Planet.PLUTO, 5.0)
    .build();
  
  /*
  private final static Map<Point , Double> planetOrbsMap = Collections.synchronizedMap(new HashMap<Point , Double>());
  static
  {
    planetOrbsMap.put(Planet.SUN, 17.0);
    planetOrbsMap.put(Planet.MOON, 12.5);
    planetOrbsMap.put(Planet.MERCURY, 7.0);
    planetOrbsMap.put(Planet.VENUS, 8.0);
    planetOrbsMap.put(Planet.MARS, 7.5);
    planetOrbsMap.put(Planet.JUPITER , 12.0);
    planetOrbsMap.put(Planet.SATURN , 10.0);
    planetOrbsMap.put(Planet.URANUS, 5.0);
    planetOrbsMap.put(Planet.NEPTUNE, 5.0);
    planetOrbsMap.put(Planet.PLUTO, 5.0);
  }
  */

  @NotNull
  @Override
  public String getTitle(Locale locale)
  {
    return "William Lilly";
  }

  @NotNull
  @Override
  public String getDescription(Locale locale)
  {
    return "William Lilly";
  }

  @Override
  public double getDiameter(Point point)
  {
    if (planetOrbsMap.get(point) != null)
      return planetOrbsMap.get(point);
    else
      return 2.0;
  }
}
