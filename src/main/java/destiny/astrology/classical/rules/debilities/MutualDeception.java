/**
 * @author smallufo
 * Created on 2008/1/1 at 上午 9:36:03
 */
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import destiny.astrology.classical.Dignity;
import destiny.astrology.classical.EssentialUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * TODO : Mutual Deception , 互相陷害 <br/>
 * 互容的變形，兩星都處與落陷，又互容→互相扯後腿<br/>
 * 例如： 水星到雙魚 , 木星到處女<br/>
 * 水星在雙魚為 Detriment/Fall , 雙魚的 Ruler 為木星 , 木星到處女為 Detriment
 */
public final class MutualDeception extends EssentialRule implements Applicable {

  private final EssentialUtils utils;

  public MutualDeception(DayNightDifferentiator dayNightDifferentiatorImpl) {
    this.utils = new EssentialUtils(dayNightDifferentiatorImpl);
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {

    Optional<Tuple2<String, Object[]>> result;


    utils.setEssentialImpl(essentialImpl);

    result = rulerMutualDeception(h , planet);
    if (result.isPresent())
      return result;

    // Ruler 的 互陷 或 互落
    result = rulerMutualDeception(h , planet);
    if (result.isPresent())
      return result;

    //Exaltation 互陷 或 互落
    result = exaltationMutualDeception(h , planet);
    if (result.isPresent())
      return result;


    // 「Ruler 到 Detriment , Exaltation 到 Fall 又互容」的互陷
    result = detrimentExaltationMutualDeception(h , planet);
    if (result.isPresent())
      return result;

    // 「Ruler 到 Fall , Exaltation 到 Detriment 又互容」的互陷
    result = fallExaltationMutualDeception(h , planet);
    return result;
  } // getResult()

  /**
   * @return Ruler 的 互陷 或 互落
   * planet 在此 horoscope 中，座落到 sign1 星座
   * 而 sign1 星座的 ruler 星，飛到 sign2 星座
   * 而 sign2 星座的 ruler (planet2) 剛好等於 planet
   */
  private Optional<Tuple2<String, Object[]>> rulerMutualDeception(Horoscope h , Planet planet) {
    //取得此 Planet 在什麼星座
    return h.getZodiacSign(planet).flatMap(sign1 -> {
      Point signRuler = essentialImpl.getPoint(sign1, Dignity.RULER);
      return h.getZodiacSign(signRuler).flatMap(sign2 -> {
        Point planet2 = essentialImpl.getPoint(sign2, Dignity.RULER);
        if (planet == planet2) {
          //已經確定互容，要計算互陷/落
          if (utils.isBothInBadSituation(planet, sign1, signRuler, sign2)) {
            //addComment(Locale.TAIWAN , planet + " 位於 " + sign1 + " , 與其 Ruler " + signRuler + " 飛至 " + sign2 + " , 形成 Ruler 互陷");
            return Optional.of(Tuple.tuple("comment1", new Object[]{planet, sign1, signRuler, sign2}));
          }
        }
        return empty();
      });
    });
  } // ruler 互陷 或 互落


  /**
   * @return Exaltation 的 互陷 或 互落
   */
  private Optional<Tuple2<String, Object[]>> exaltationMutualDeception(Horoscope h , Planet planet) {
    return h.getZodiacSign(planet).flatMap(sign -> {
      Point signExaltation = essentialImpl.getPoint(sign, Dignity.EXALTATION);
      if (signExaltation != null) {
        return
          h.getZodiacSign(signExaltation).flatMap(sign2 -> {
          Point planet2 = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
          if (planet == planet2) {
            //已確定 Exaltation 互容，要確認互陷
            if (utils.isBothInBadSituation(planet, sign, signExaltation, sign2)) {
              //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + signExaltation + " 飛至 " + sign2 + " , 形成 Exaltation 互陷");
              return Optional.of(Tuple.tuple("comment2", new Object[]{planet, sign, signExaltation, sign2}));
            }
          }
          return empty();
        });
      }
      return empty();
    });

  }

  /**
   * 旺廟互陷
   * 「Ruler 到 Detriment , Exaltation 到 Fall 又互容」的互陷
   */
  private Optional<Tuple2<String, Object[]>> detrimentExaltationMutualDeception(Horoscope h , Planet planet) {
    return h.getZodiacSign(planet).flatMap(sign -> {
      Point thisSignRuler = essentialImpl.getPoint(sign, Dignity.RULER);
      return h.getZodiacSign(thisSignRuler).flatMap(sign2 -> {
        Point thatSignExaltation = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
        if (planet == thatSignExaltation) {
          //已確定互容，要確定互陷
          if (utils.isBothInBadSituation(planet, sign, thisSignRuler, sign2)) {
            //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Ruler " + thisSignRuler + " 飛至 " + sign3 + " 形成旺廟互陷");
            return Optional.of(Tuple.tuple("comment3", new Object[]{planet, sign, thisSignRuler, sign2}));
          }
        }
        return empty();
      });
    });
  }

  /**
   * 旺廟互陷
   * 「Ruler 到 Fall , Exaltation 到 Detriment 又互容」的互陷
   */
  private Optional<Tuple2<String, Object[]>> fallExaltationMutualDeception(Horoscope h , Planet planet) {
    return h.getZodiacSign(planet).flatMap(sign -> {
      Point thisSignExaltation = essentialImpl.getPoint(sign, Dignity.EXALTATION);
      if (thisSignExaltation != null) //EXALTATION 可能為 null
      {
        return h.getZodiacSign(thisSignExaltation).flatMap(sign3 -> {
          Point thatSignRuler = essentialImpl.getPoint(sign3, Dignity.RULER);
          if (planet == thatSignRuler) {
            //已確定互容，要確認互陷
            if (utils.isBothInBadSituation(planet, sign, thisSignExaltation, sign3)) {
              //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + thisSignExaltation + " 飛至 " + sign3 + " 形成旺廟互陷");
              return Optional.of(Tuple.tuple("comment4", new Object[]{planet, sign, thisSignExaltation, sign3}));
            }
          }
          return empty();
        });

      }
      return empty();
    });

  }


}
