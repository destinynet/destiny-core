/**
 * @author smallufo 
 * Created on 2008/1/5 at 上午 7:14:47
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.Locale;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.AspectApplySeparateIF.AspectType;
import destiny.astrology.beans.TranslationOfLightBean;

public class Translation_of_Light extends Rule
{
  private RelativeTransitIF relativeTransitImpl;
  
  private AspectApplySeparateIF aspectApplySeparateImpl;
  
  public Translation_of_Light(RelativeTransitIF relativeTransitImpl , AspectApplySeparateIF aspectApplySeparateImpl)
  {
    super("Translation_of_Light");
    this.relativeTransitImpl = relativeTransitImpl;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    TranslationOfLightBean bean = new TranslationOfLightBean(planet , horoscopeContext , relativeTransitImpl , aspectApplySeparateImpl);
    if (bean.isTranslatingLight())
    {
      StringBuffer sb = new StringBuffer(" 從 " + bean.getFromPlanet() + " 傳遞光線到 " + bean.getToPlanet() + " , " + bean.getFromPlanet()+" 與 " + bean.getToPlanet() + " 交角 " + horoscopeContext.getHoroscope().getAngle(bean.getFromPlanet(), bean.getToPlanet()) + " 度");
      if (bean.getBesigingPlanetsAspectType() != null)
        sb.append("(" + (bean.getBesigingPlanetsAspectType() == AspectType.APPLYING ? "入" : "出") + "相位)");
      else
        sb.append("(未形成交角)");
      
      addComment(Locale.TAIWAN, planet + sb.toString());
      return true;
    }
    return false;
  }

}
