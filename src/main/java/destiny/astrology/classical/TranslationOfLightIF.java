/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.utils.Tuple4;

public interface TranslationOfLightIF {
  public Tuple4<Boolean , Planet , Planet , AspectApplySeparateIF.AspectType> getResult(Planet planet, HoroscopeContext horoscopeContext);
}
