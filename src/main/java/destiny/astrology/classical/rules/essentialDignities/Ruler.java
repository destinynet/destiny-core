/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:18:14
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.*;
import destiny.astrology.classical.Dignity;
import destiny.astrology.classical.EssentialUtils;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

/** A planet in its own sign , or mutual reception with another planet by sign */
public final class Ruler extends Rule
{
  private final DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public Ruler(DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }
  
  @Override
  public Optional<Tuple2<String, Object[]>> getResult(@NotNull Planet planet, @NotNull Horoscope h) {

    return h.getZodiacSign(planet)
      .flatMap(sign -> {
        // Ruler (旺)
        if (planet == essentialImpl.getPointOptional(sign , Dignity.RULER).orElse(null)) {
          logger.debug("{} 位於 {} , 為其 {}" , planet , sign , Dignity.RULER);
          return Optional.of(Tuple.tuple("commentBasic", new Object[]{planet, sign}));
        } else {
          logger.debug("檢查旺旺互容 of {}" , planet);
          return rulerMutualReception(h , planet);
        }
      });
  }

  /**
   * 旺旺互容 (mutual reception)
   * RULER / RULER 互容
   * planet 在此 horoscope 中，座落到 sign1 星座
   * 而 sign1 星座的 RULER 星，飛到 sign2 星座
   * 而 sign2 星座的 RULER 星 (planet2) 剛好等於 planet
   *
   * 例如： 火星在射手 , 木星在牡羊 , 兩個星座的 Ruler 互訪<br/>
     * 「而且都沒有落陷」 (否則變成互陷)
   */
  private Optional<Tuple2<String, Object[]>> rulerMutualReception(Horoscope h , Planet planet) {
    EssentialUtils utils = new EssentialUtils(dayNightDifferentiatorImpl);
    utils.setEssentialImpl(essentialImpl);

    //取得此 Planet 在什麼星座
    return h.getZodiacSign(planet).flatMap(sign1 ->
      essentialImpl.getPointOptional(sign1 , Dignity.RULER).flatMap(signRuler -> {
        // planet 在 sign1 , 計算 sign1 的 RULER , 為 signRuler
        return h.getZodiacSign(signRuler)
          .filter(sign2 ->
            planet == essentialImpl.getPointOptional(sign2, Dignity.RULER).orElse(null)   // 已經確定 Ruler 互容，要排除互陷
            && !utils.isBothInBadSituation(planet , sign1 , signRuler , sign2)            // 只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
          ).flatMap(sign2 -> {
            logger.debug("{} 位於 {} , 與其 Ruler {} 飛至 {} , 形成 旺旺互容" , planet , sign1 , signRuler , sign2);
            return Optional.of(Tuple.tuple("commentReception", new Object[]{planet, sign1, signRuler, sign2}));
          });
      })
    );
  }
}
