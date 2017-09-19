/**
 * @author smallufo
 * Created on 2008/1/8 at 下午 12:26:42
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.HoroscopeContextIF;
import destiny.astrology.Planet;
import destiny.astrology.classical.CollectionOfLightIF;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.List;
import java.util.Optional;

public final class Collection_of_Light extends Rule
{
  private final CollectionOfLightIF collectionOfLightImpl;

  public Collection_of_Light(CollectionOfLightIF collectionOfLightImpl) {
    this.collectionOfLightImpl = collectionOfLightImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContextIF horoscopeContext)
  {
    //目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮
    //CollectionOfLightBean bean = new CollectionOfLightBean(planet , horoscopeContext , CollectionOfLightBean.CollectType.DIGNITIES , relativeTransitImpl, aspectEffectiveClassicalImpl , aspectApplySeparateImpl , dayNightDifferentiatorImpl , besiegedBean);
    Tuple2<Boolean, List<Planet>> t = collectionOfLightImpl.getResult(planet , horoscopeContext , CollectionOfLightIF.CollectType.DIGNITIES);
    if (t.v1())
    {
      List<Planet> twoPlanets = t.v2();
      // planet + " 從 " + twoPlanets.get(0) + " 以及 " + twoPlanets.get(1) + " 收集光線" + " , " + twoPlanets.get(0) +" 與 " + twoPlanets.get(1) + " 交角 " +  horoscopeContext.getHoroscope().getAngle(twoPlanets.get(0) , twoPlanets.get(1)) + " 度."
      return Optional.of(Tuple.tuple("comment", new Object[]{planet, twoPlanets.get(0), twoPlanets.get(1), horoscopeContext.getHoroscope().getAngle(twoPlanets.get(0), twoPlanets.get(1))}));
    }
    return Optional.empty();
  }

}
