/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:29:49
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.*;
import destiny.astrology.classical.Dignity;
import destiny.astrology.classical.EssentialUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** A planet in its exaltation , or mutial reception with another planet by exaltation */
public final class Exaltation extends Rule
{
  private DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public Exaltation(DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }
  
  @Override
  public Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    
    //Exaltation (廟)
    if (planet == essentialImpl.getPoint(sign, Dignity.EXALTATION))
    {
      //addComment(Locale.TAIWAN , planet + " 位於其 Exaltation 的星座 " + sign);
      return Optional.of(ImmutablePair.of("commentBasic", new Object[]{planet, sign}));
    }
    // Exaltation 互容 , mutual reception
    else 
    {
      Point signExaltation = essentialImpl.getPoint(sign, Dignity.EXALTATION);
      if (signExaltation != null)
      {
        EssentialUtils utils = new EssentialUtils(dayNightDifferentiatorImpl);
        utils.setEssentialImpl(essentialImpl);
        
        ZodiacSign sign2 = horoscopeContext.getZodiacSign(signExaltation);
        Point planet2 = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
        if (planet == planet2)
        {
          //已確定 Exaltation 互容，要排除互陷
          //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
          if (!utils.isBothInBadSituation(planet , sign , signExaltation , sign2))
          {
            //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + signExaltation + " 飛至 " + sign2 + " , 形成互容");
            return Optional.of(ImmutablePair.of("commentReception" , new Object[]{planet , sign , signExaltation , sign2}));
          }
        }        
      }
    }

    return Optional.empty();
  }

}
