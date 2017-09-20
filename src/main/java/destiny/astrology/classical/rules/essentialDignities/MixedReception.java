/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 3:56:55
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.*;
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
  private final DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public MixedReception(DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }

  @Override
  public Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h)
  {
    EssentialUtils utils = new EssentialUtils(dayNightDifferentiatorImpl);
    utils.setEssentialImpl(essentialImpl);
    
    //取得此 Planet 在什麼星座
    ZodiacSign sign = h.getZodiacSign(planet);
    
    Point thisSignRuler = essentialImpl.getPoint(sign, Dignity.RULER);
    ZodiacSign sign2 = h.getZodiacSign(thisSignRuler);
    Point thatSignExaltation = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
    if (planet == thatSignExaltation )
    {
      //已確定互容，要排除互陷
      //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
      if (!utils.isBothInBadSituation(planet , sign , thisSignRuler , sign2))
      {
        //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Ruler " + thisSignRuler + " 飛至 " + sign2 + " 形成旺廟互容");
        return Optional.of(Tuple.tuple("commentRuler", new Object[]{planet, sign, thisSignRuler, sign2}));
      }
    }
    
    Point thisSignExaltation = essentialImpl.getPoint(sign, Dignity.EXALTATION);
    if (thisSignExaltation != null) //EXALTATION 可能為 null
    {
      sign2 = h.getZodiacSign(thisSignExaltation);
      Point thatSignRuler = essentialImpl.getPoint(sign2, Dignity.RULER);
      if (planet == thatSignRuler )
      {
        //已確定互容，要排除互陷
        //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
        if (!utils.isBothInBadSituation(planet , sign , thisSignExaltation , sign2))
        {
          //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + thisSignExaltation + " 飛至 " + sign2 + " 形成旺廟互容");
          return Optional.of(Tuple.tuple("commentExaltation" , new Object[] {planet , sign , thisSignExaltation , sign2}));
        }
      }  
    }
    return Optional.empty();
  }

}
