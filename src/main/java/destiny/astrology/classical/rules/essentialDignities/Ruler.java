/**
 * @author smallufo 
 * Created on 2007/12/29 at 上午 4:18:14
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;
import destiny.astrology.classical.Dignity;
import destiny.astrology.classical.EssentialUtils;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A planet in its own sign , or mutual reception with another planet by sign */
public final class Ruler extends Rule
{
  private DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public Ruler(DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }
  
  @Nullable
  @Override
  public Tuple<String , Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    
    // Ruler (旺)
    if (planet == essentialImpl.getPoint(sign, Dignity.RULER) )
    {
      //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 Ruler");
      return new Tuple<String , Object[]>("commentBasic" , new Object[]{planet , sign});
    }
    /**
     * Ruler 互容 , mutual reception <br/> 
     * 例如： 火星在射手 , 木星在牡羊 , 兩個星座的 Ruler 互訪<br/>
     * 「而且都沒有落陷」 (否則變成互陷)
     */
    else 
    {
      EssentialUtils utils = new EssentialUtils(dayNightDifferentiatorImpl);
      utils.setEssentialImpl(essentialImpl);

      Point signRuler = essentialImpl.getPoint(sign, Dignity.RULER);
      ZodiacSign sign2 = horoscopeContext.getZodiacSign(signRuler);
      Point planet2 = essentialImpl.getPoint(sign2, Dignity.RULER);
      if (planet == planet2)
      {
        //已經確定 Ruler 互容，要排除互陷
        //只要兩顆星都不是陷落，就算互容。其中一顆星陷落無妨
        if (!utils.isBothInBadSituation(planet , sign , signRuler , sign2))
        {
          //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Ruler " + signRuler + " 飛至 " + sign2 + " , 形成互容");
          return new Tuple<String , Object[]>("commentReception" , new Object[]{planet , sign , signRuler , sign2});  
        }
      }
    }
    return null;
  }
}
