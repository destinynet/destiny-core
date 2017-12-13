/**
 * @author smallufo 
 * Created on 2008/1/5 at 上午 7:14:47
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.IAspectApplySeparate;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.TranslationOfLightIF;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple4;

import java.util.Optional;

public class Translation_of_Light extends Rule
{

  private final TranslationOfLightIF translationOfLightImpl;

  public Translation_of_Light(TranslationOfLightIF translationOfLightImpl) {
    this.translationOfLightImpl = translationOfLightImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h)
  {
    Tuple4<Boolean , Planet , Planet , Optional<IAspectApplySeparate.AspectType>> t = translationOfLightImpl.getResult(planet, h);
    if (t.v1()) {
      double deg = h.getAngle(t.v2() , t.v3());
      if (t.v4().isPresent()) {
        return Optional.of(Tuple.tuple("commentAspect", new Object[]{planet, t.v2(), t.v3(), deg, t.v4().get()}));
      }
      else {
        //sb.append("(未形成交角)");
        return Optional.of(Tuple.tuple("commentUnaspect" , new Object[]{planet , t.v2() , t.v3() , deg }));
      }
        
    }
    return Optional.empty();
  }

}
