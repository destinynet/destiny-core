/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:51:31
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.Dignity;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** In Fall. */
public final class Fall extends EssentialRule {

  public Fall() {
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h) {

    return h.getZodiacSign(planet)
      .filter(sign -> planet == essentialImpl.getPoint(sign, Dignity.FALL).orElse(null))
      .map(sign -> Tuple.tuple("comment", new Object[]{planet, sign}));
  }

}
