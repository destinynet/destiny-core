/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical;

import destiny.astrology.IAspectApplySeparate;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import org.jooq.lambda.tuple.Tuple4;

import java.util.Optional;

public interface TranslationOfLightIF {

  Tuple4<Boolean, Planet, Planet, Optional<IAspectApplySeparate.AspectType>> getResult(Planet planet, Horoscope h);
}
