/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 3:56:55
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.Horoscope;
import destiny.astrology.Planet;
import destiny.astrology.classical.Dignity;
import destiny.astrology.classical.EssentialUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/**
 * 廟旺互容 <br/>
 * 舉例：水星到摩羯，火星到雙子 <br/>
 * 摩羯為火星 Exaltation 之星座，雙子為水星 Ruler 之星座
 */
public final class MixedReception extends Rule
{
  private final EssentialUtils utils;

  public MixedReception(DayNightDifferentiator dayNightDifferentiatorImpl) {
    utils = new EssentialUtils(dayNightDifferentiatorImpl);
  }

  @Override
  public Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    utils.setEssentialImpl(essentialImpl);

    Optional<Tuple2<String, Object[]>> result;

    result = rulerExaltMutualReception(h , planet);
    if (result.isPresent())
      return result;

    result = exaltRulerMutualReception(h , planet);
    return result;
  }

  /**
   * 旺廟互容
   * RULER / EXALTATION 互容
   * planet 在此 horoscope 中，座落到 sign1 星座
   * 而 sign1 星座的 RULER           星，飛到 sign2 星座
   * 而 sign2 星座的 EXALTATION (planet2) 剛好等於 planet
   */
  private Optional<Tuple2<String, Object[]>> rulerExaltMutualReception(Horoscope h , Planet planet) {

    return h.getZodiacSignOpt(planet).flatMap(sign1 ->
      essentialImpl.getPoint(sign1, Dignity.RULER).flatMap(signRuler ->
        h.getZodiacSignOpt(signRuler).flatMap(sign2 ->
          essentialImpl.getPoint(sign2 , Dignity.EXALTATION).flatMap(planet2 -> {
            if (planet == planet2 && !utils.isBothInBadSituation(planet , sign1 , signRuler , sign2)) {
              getLogger().debug("{} 位於 {} , 與其 {} {} 飛至 {} , 形成 廟旺互容" , planet , sign1 , Dignity.RULER , signRuler ,sign2);
              return Optional.of(Tuple.tuple("commentRuler", new Object[]{planet, sign1, signRuler, sign2}));
            }
            else
              return Optional.empty();
          })
        )
      )
    );

//    return h.getZodiacSign(planet).flatMap(sign1 -> {
//      // planet 在 sign1 , 計算 sign1 的 RULER :
//      Point signRuler = essentialImpl.getPoint(sign1, Dignity.RULER);
//      return h.getZodiacSign(signRuler).flatMap(sign2 -> {
//        // sign1 的 ruler 飛到 sign2 , 計算 sign2 的 EXALTATION
//        Point planet2 = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
//        if (planet == planet2) {
//          //已確定互容，要排除互陷
//          //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
//          if (!utils.isBothInBadSituation(planet , sign1 , signRuler , sign2))
//          {
//            //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Ruler " + thisSignRuler + " 飛至 " + sign2 + " 形成旺廟互容");
//            return Optional.of(Tuple.tuple("commentRuler", new Object[]{planet, sign1, signRuler, sign2}));
//          }
//        }
//        return empty();
//      });
//    });
  }

  /**
   * 廟旺互容
   * EXALTATION / RULER 互容
   * planet 在此 horoscope 中，座落到 sign1 星座
   * 而 sign1 星座的 EXALTATION   星，飛到 sign2 星座
   * 而 sign2 星座的 RULER (planet2) 剛好等於 planet
   */
  private Optional<Tuple2<String, Object[]>> exaltRulerMutualReception(Horoscope h , Planet planet) {

    return h.getZodiacSignOpt(planet).flatMap(sign1 ->
      essentialImpl.getPoint(sign1, Dignity.EXALTATION).flatMap(thisSignExaltation ->
        h.getZodiacSignOpt(thisSignExaltation).flatMap(sign2 ->
          essentialImpl.getPoint(sign2 , Dignity.RULER).flatMap(planet2 -> {
            if (planet == planet2) {
              //已確定互容，要排除互陷
              //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
              if (!utils.isBothInBadSituation(planet , sign1 , thisSignExaltation , sign2)) {
                //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + thisSignExaltation + " 飛至 " + sign2 + " 形成旺廟互容");
                getLogger().debug("{} 位於 {} , 與其 {} {} 飛至 {} , 形成 廟旺互容" , planet , sign1 , Dignity.EXALTATION , thisSignExaltation , sign2);
                return Optional.of(Tuple.tuple("commentExaltation" , new Object[] {planet , sign1 , thisSignExaltation , sign2}));
              }
            }
            return Optional.empty();
          })
        )
      )
    );


//    return h.getZodiacSign(planet).flatMap(sign1 -> {
//      // planet 在 sign1 , 計算 sign1 的 EXALTATION
//      Point thisSignExaltation = essentialImpl.getPoint(sign1, Dignity.EXALTATION);
//      // EXALTATION 可能為 null
//      if (thisSignExaltation != null ) {
//        return h.getZodiacSign(thisSignExaltation).flatMap(sign2 -> {
//          Point planet2 = essentialImpl.getPoint(sign2, Dignity.RULER);
//          if (planet == planet2) {
//            //已確定互容，要排除互陷
//            //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
//            if (!utils.isBothInBadSituation(planet , sign1 , thisSignExaltation , sign2))
//            {
//              //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + thisSignExaltation + " 飛至 " + sign2 + " 形成旺廟互容");
//              return Optional.of(Tuple.tuple("commentExaltation" , new Object[] {planet , sign1 , thisSignExaltation , sign2}));
//            }
//          }
//          return empty();
//        });
//      }
//      return empty();
//    });
  }
}
