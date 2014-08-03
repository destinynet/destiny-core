/**
 * @author smallufo 
 * Created on 2007/6/12 at 上午 5:28:19
 */
package destiny.astrology;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;


public final class Planet extends Star implements Comparable<Planet>
{
  private final static String resource = "destiny.astrology.Star";

  public final static Planet SUN     = new Planet("Planet.SUN"    , "Planet.SUN_ABBR");
  public final static Planet MOON    = new Planet("Planet.MOON"   , "Planet.MOON_ABBR");
  public final static Planet MERCURY = new Planet("Planet.MERCURY", "Planet.MERCURY_ABBR"); 
  public final static Planet VENUS   = new Planet("Planet.VENUS"  , "Planet.VENUS_ABBR");
  public final static Planet MARS    = new Planet("Planet.MARS"   , "Planet.MARS_ABBR");
  public final static Planet JUPITER = new Planet("Planet.JUPITER", "Planet.JUPITER_ABBR"); 
  public final static Planet SATURN  = new Planet("Planet.SATURN" , "Planet.SATURN_ABBR");
  public final static Planet URANUS  = new Planet("Planet.URANUS" , "Planet.URANUS_ABBR");
  public final static Planet NEPTUNE = new Planet("Planet.NEPTUNE", "Planet.NEPTUNE_ABBR");
  public final static Planet PLUTO   = new Planet("Planet.PLUTO"  , "Planet.PLUTO_ABBR"); 

  public final static Planet[] values = {SUN , MOON , MERCURY , VENUS , MARS , JUPITER , SATURN , URANUS , NEPTUNE , PLUTO};
  public final static Planet[] classicalValues = {SUN , MOON , MERCURY , VENUS , MARS , JUPITER , SATURN};
  
  
  public Planet(String nameKey , String abbrKey)
  {
    super(nameKey , abbrKey , resource);
  }
  
  /** 從 "sun" 取得 Planet.SUN  ... , 限英文 */
  public static Optional<Planet> get(String value)
  {
    return Stream.of(values).filter(planet -> planet.getName(Locale.ENGLISH).equalsIgnoreCase(value)).findFirst();
//    for(Planet planet : values)
//    {
//      if (planet.getName(Locale.ENGLISH).equalsIgnoreCase(value))
//        return planet;
//    }
//    return null;
  }

  @Override
  public int compareTo(Planet o)
  {
    if (this.equals(o))
      return 0;
    
    List<Planet> list = Arrays.asList(values);
    return list.indexOf(this) - list.indexOf(o);
  }

}
