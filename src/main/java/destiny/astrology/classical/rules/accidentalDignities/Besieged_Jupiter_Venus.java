/**
 * @author smallufo 
 * Created on 2008/1/3 at 上午 8:55:07
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.beans.BesiegedBean;
import destiny.core.calendar.Time;

/**
 * 夾輔 : 被金星木星包夾 , 是很幸運的情形<br/>
 * 角度考量 0/60/90/120/180 <br/>
 * 中間不能與其他行星形成角度
 */
public class Besieged_Jupiter_Venus extends Rule
{
  /** 計算兩星交角的介面*/
  private RelativeTransitIF relativeTransitImpl;
  
  /** 計算兩星夾角的工具箱 */
  BesiegedBean besiegedBean;
  
  public Besieged_Jupiter_Venus(RelativeTransitIF relativeTransitImpl)
  {
    super("Besieged_Jupiter_Venus");
    this.relativeTransitImpl = relativeTransitImpl;
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.SUN || planet == Planet.MOON || planet == Planet.MERCURY || planet == Planet.MARS || planet == Planet.SATURN)
    {
      besiegedBean = new BesiegedBean(relativeTransitImpl);
      if (besiegedBean.isBesieged(planet, Planet.VENUS , Planet.JUPITER , Time.getGMTfromLMT(horoscopeContext.getLmt() , horoscopeContext.getLocation())  , true , false))
      {
        addComment(Locale.TAIWAN , planet + " 被 " + Planet.VENUS + " 以及 " + Planet.JUPITER + " 夾輔 (善意 Besieged)");
        return true;
      }
    }
    return false;
  }

  public void setRelativeTransitImpl(RelativeTransitIF relativeTransitImpl)
  {
    this.relativeTransitImpl = relativeTransitImpl;
  }

}
