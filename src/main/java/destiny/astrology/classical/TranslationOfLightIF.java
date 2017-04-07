/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import org.jooq.lambda.tuple.Tuple4;

import java.util.Optional;

public interface TranslationOfLightIF {

  Tuple4<Boolean, Planet, Planet, Optional<AspectApplySeparateIF.AspectType>> getResult(Planet planet, HoroscopeContext horoscopeContext);
}
