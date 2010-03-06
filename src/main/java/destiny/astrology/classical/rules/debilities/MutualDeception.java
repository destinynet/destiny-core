/**
 * @author smallufo 
 * Created on 2008/1/1 at 上午 9:36:03
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;
import destiny.astrology.classical.Dignity;
import destiny.astrology.classical.EssentialUtils;

/** 
 * TODO : Mutual Deception , 互相陷害 <br/>
 * 互容的變形，兩星都處與落陷，又互容→互相扯後腿<br/>
 * 例如： 水星到雙魚 , 木星到處女<br/>
 * 水星在雙魚為 Detriment/Fall , 雙魚的 Ruler 為木星 , 木星到處女為 Detriment
 * 
 */ 
public final class MutualDeception extends EssentialRule implements Applicable
{
  private DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public MutualDeception(DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    super("MutualDeception");
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    EssentialUtils utils = new EssentialUtils(dayNightDifferentiatorImpl);
    utils.setEssentialImpl(essentialImpl);
    
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    
    //Ruler 互陷 或 互落
    Point signRuler = essentialImpl.getPoint(sign, Dignity.RULER);
    ZodiacSign sign2 = horoscopeContext.getZodiacSign(signRuler);
    Point planet2 = essentialImpl.getPoint(sign2, Dignity.RULER);
    if (planet == planet2)
    {
      //已經確定互容，要計算互陷/落
      if (utils.isBothInBadSituation(planet , sign , signRuler , sign2))
      {
        addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Ruler " + signRuler + " 飛至 " + sign2 + " , 形成 Ruler 互陷");
        return true;
      }
    }
    
    //Exaltation 互陷 或 互落
    Point signExaltation = essentialImpl.getPoint(sign, Dignity.EXALTATION);
    if (signExaltation != null)
    {
      sign2 = horoscopeContext.getZodiacSign(signExaltation);
      planet2 = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
      if (planet == planet2)
      {
        //已確定 Exaltation 互容，要確認互陷
        if (utils.isBothInBadSituation(planet , sign , signExaltation , sign2))
        {
          addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + signExaltation + " 飛至 " + sign2 + " , 形成 Exaltation 互陷");
          return true;  
        }
      }
    }
    
    // 「Ruler 到 Detriment , Exaltation 到 Fall 又互容」的互陷
    Point thisSignRuler = essentialImpl.getPoint(sign, Dignity.RULER);
    sign2 = horoscopeContext.getZodiacSign(thisSignRuler);
    Point thatSignExaltation = essentialImpl.getPoint(sign2, Dignity.EXALTATION);
    if (planet == thatSignExaltation )
    {
      //已確定互容，要確定互陷
      if (utils.isBothInBadSituation(planet , sign , thisSignRuler , sign2))
      {
        addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Ruler " + thisSignRuler + " 飛至 " + sign2 + " 形成旺廟互陷");
        return true;
      }
    }
    
    // 「Ruler 到 Fall , Exaltation 到 Detritment 又互容」的互陷
    Point thisSignExaltation = essentialImpl.getPoint(sign, Dignity.EXALTATION);
    if (thisSignExaltation != null) //EXALTATION 可能為 null
    {
      sign2 = horoscopeContext.getZodiacSign(thisSignExaltation);
      Point thatSignRuler = essentialImpl.getPoint(sign2, Dignity.RULER);
      if (planet == thatSignRuler )
      {
        //已確定互容，要確認互陷
        if (utils.isBothInBadSituation(planet , sign , thisSignExaltation , sign2))
        {
          addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 與其 Exaltation " + thisSignExaltation + " 飛至 " + sign2 + " 形成旺廟互陷");
          return true;          
        }
      }
    }
    return false;
  }

}
