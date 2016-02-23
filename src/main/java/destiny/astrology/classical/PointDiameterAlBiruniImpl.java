/**
 * @author smallufo 
 * Created on 2007/11/25 at 上午 12:19:49
 */ 
package destiny.astrology.classical;

import com.google.common.collect.ImmutableMap;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * 古典占星術，Al-Biruni 的交角 <br/>
 * 參考資料 http://www.skyscript.co.uk/aspects.html
 * 尚有 PointDiameterLillyImpl 的實作可供比對
 */
public class PointDiameterAlBiruniImpl implements PointDiameterIF , Serializable
{
  private final static ImmutableMap<Planet,Double> planetOrbsMap = new ImmutableMap.Builder<Planet , Double>()
    .put(Planet.SUN, 15.0)
    .put(Planet.MOON, 12.0)
    .put(Planet.MERCURY, 7.0)
    .put(Planet.VENUS, 7.0)
    .put(Planet.MARS, 8.0)
    .put(Planet.JUPITER , 9.0)
    .put(Planet.SATURN , 9.0)
    .put(Planet.URANUS, 5.0)
    .put(Planet.NEPTUNE, 5.0)
    .put(Planet.PLUTO, 5.0)
    .build();

  public PointDiameterAlBiruniImpl() {
  }

  /*
  private final static Map<Point , Double> planetOrbsMap = Collections.synchronizedMap(new HashMap<Point , Double>());
  static
  {
    planetOrbsMap.put(Planet.SUN, 15.0);
    planetOrbsMap.put(Planet.MOON, 12.0);
    planetOrbsMap.put(Planet.MERCURY, 7.0);
    planetOrbsMap.put(Planet.VENUS, 7.0);
    planetOrbsMap.put(Planet.MARS, 8.0);
    planetOrbsMap.put(Planet.JUPITER , 9.0);
    planetOrbsMap.put(Planet.SATURN , 9.0);
    planetOrbsMap.put(Planet.URANUS, 5.0);
    planetOrbsMap.put(Planet.NEPTUNE, 5.0);
    planetOrbsMap.put(Planet.PLUTO, 5.0);
  }
  */
  
  @NotNull
  @Override
  public String getTitle(Locale locale)
  {
    return "Al-Biruni";
  }
  
  @NotNull
  @Override
  public String getDescription(Locale locale)
  {
    return "Al-Biruni";
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
