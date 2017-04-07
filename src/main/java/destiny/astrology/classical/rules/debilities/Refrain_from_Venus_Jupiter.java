/**
 * Created by smallufo at 2008/11/11 下午 10:09:47
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.Aspect;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.classical.RefranationIF;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Optional;

public class Refrain_from_Venus_Jupiter extends Rule
{
  private RefranationIF refranationImpl;


  public Refrain_from_Venus_Jupiter(RefranationIF refranationImpl) {
    this.refranationImpl = refranationImpl;
  }

//  /** 判斷入相位，或是出相位 的實作 , 內定採用古典占星 */
//  private final AspectApplySeparateIF aspectApplySeparateImpl;// = new AspectApplySeparateImpl(new AspectEffectiveClassical());
//
//  /** 計算兩星呈現某交角的時間 , 內定是 Swiss ephemeris 的實作 */
//  private final RelativeTransitIF relativeTransitImpl;
//
//  /** 計算星體逆行的介面，目前只支援 Planet , 目前的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！) */
//  private final RetrogradeIF retrogradeImpl;// = new RetrogradeImpl();
//
//  public Refranate_from_Venus_Jupiter(AspectApplySeparateIF aspectApplySeparateImpl , RelativeTransitIF relativeTransitImpl , RetrogradeIF retrogradeImpl)
//  {
//    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
//    this.relativeTransitImpl = relativeTransitImpl;
//    this.retrogradeImpl = retrogradeImpl;
//  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, HoroscopeContext horoscopeContext) {
    //太陽 / 月亮不會逆行
    if (planet == Planet.MOON || planet == Planet.SUN)
      return Optional.empty();
    
    Point otherPoint;

    if (planet != Planet.VENUS) {
      otherPoint = Planet.VENUS;
      Tuple3<Boolean, Point, Aspect> t = refranationImpl.resultOf(horoscopeContext, planet, otherPoint);
      if (t.v1()) {
        //addComment(Locale.TAIWAN, planet + " 在與 " + otherPoint + " 形成 " + bean.getApplyingAspect() + " 之前臨陣退縮(Refranation)");
        //return new Tuple<String , Object[]>("comment" , new Object[]{planet , otherPoint , bean.getApplyingAspect()});
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, t.v3()}));
      }
    }
    
    if ( planet != Planet.JUPITER) {
      otherPoint = Planet.JUPITER;
      Tuple3<Boolean, Point, Aspect> t = refranationImpl.resultOf(horoscopeContext, planet, otherPoint);
      if (t.v1) {
        //addComment(Locale.TAIWAN, planet + " 在與 " + otherPoint + " 形成 " + bean.getApplyingAspect() + " 之前臨陣退縮(Refranation)");
        //return new Tuple<String , Object[]>("comment" , new Object[]{planet , otherPoint , bean.getApplyingAspect()});
        return Optional.of(Tuple.tuple("comment", new Object[]{planet, otherPoint, t.v3()}));
      }
    }
    return Optional.empty();
  }

}
