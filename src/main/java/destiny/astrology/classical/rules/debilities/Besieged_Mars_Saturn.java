/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:33:43
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.beans.BesiegedBean;
import destiny.core.calendar.Time;
import destiny.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Besieged between Mars and Saturn. 
 * 被火土夾制，只有日月水金，這四星有可能發生
 * 前一個角度與火土之一形成 0/90/180 , 後一個角度又與火土另一顆形成 0/90/180
 * 中間不能與其他行星形成角度
 */
public final class Besieged_Mars_Saturn extends Rule
{
  /** 計算兩星夾角的工具箱 */
  private final BesiegedBean besiegedBean;
  
  public Besieged_Mars_Saturn(BesiegedBean besiegedBean)
  {
    this.besiegedBean = besiegedBean;
  }


  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    if (planet == Planet.SUN || planet == Planet.MOON || planet == Planet.MERCURY || planet == Planet.VENUS)
    {
      //火土夾制，只考量「硬」角度 , 所以最後一個參數設成 true
      if (besiegedBean.isBesieged(planet, Planet.MARS , Planet.SATURN , Time.getGMTfromLMT(horoscopeContext.getLmt() , horoscopeContext.getLocation())  , true , true))
      {
        //addComment(Locale.TAIWAN , planet + " 被 " + Planet.MARS + " 以及 " + Planet.SATURN +" 夾制 (Besieged)");
        return new Tuple<String , Object[]>("comment" , new Object[] {planet , Planet.MARS , Planet.SATURN});
      }
    }
    return null;
  }

}
