/**
 * Created by smallufo at 2008/11/11 下午 8:29:36
 */
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.Aspect;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.classical.RefranationIF;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Optional;

/**
 * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
 */
public final class Refrain_from_Mars_Saturn extends Rule
{
  private RefranationIF refranationImpl;

  public Refrain_from_Mars_Saturn(RefranationIF refranationImpl) {
    this.refranationImpl = refranationImpl;
  }

  @Override
  protected Optional<Pair<String, Object[]>> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    // 太陽 / 月亮不會逆行
    if (planet == Planet.MOON || planet == Planet.SUN)
      return Optional.empty();
    
    Point otherPoint;

    if (planet != Planet.MARS)
    {
      otherPoint = Planet.MARS;
      Tuple3<Boolean , Point, Aspect> t = refranationImpl.resultOf(horoscopeContext, planet, otherPoint);
      if (t.v1())
      {
        //addComment(Locale.TAIWAN, planet + " 逃過了與 " + otherPoint + " 形成 " + bean.getApplyingAspect() + " (Refranation)");
        //return new Tuple<String , Object[]>("comment" , new Object[] {planet , otherPoint , bean.getApplyingAspect()} );
        return Optional.of(ImmutablePair.of("comment", new Object[]{planet, otherPoint, t.v3()}));
      }
    }
    
    if ( planet != Planet.SATURN)
    {
      otherPoint = Planet.SATURN;
      Tuple3<Boolean , Point, Aspect> t = refranationImpl.resultOf(horoscopeContext, planet, otherPoint);
      if (t.v1())
      {
        //addComment(Locale.TAIWAN, planet + " 逃過了與 " + otherPoint + " 形成 " + bean.getApplyingAspect() + " (Refranation)");
        //return new Tuple<String , Object[]>("comment" , new Object[] {planet , otherPoint , bean.getApplyingAspect()} );
        return Optional.of(ImmutablePair.of("comment" , new Object[] {planet , otherPoint , t.v3()}));
      }
    }
    return Optional.empty();
  }

}
