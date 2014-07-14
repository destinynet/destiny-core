/**
 * @author smallufo 
 * Created on 2008/1/5 at 上午 7:14:47
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.beans.BesiegedBean;
import destiny.astrology.beans.TranslationOfLightBean;
import destiny.utils.Tuple;

public class Translation_of_Light extends Rule
{
  private RelativeTransitIF relativeTransitImpl;
  
  private AspectApplySeparateIF aspectApplySeparateImpl;

  private BesiegedBean besiegedBean;
  
  public Translation_of_Light(RelativeTransitIF relativeTransitImpl , AspectApplySeparateIF aspectApplySeparateImpl , BesiegedBean besiegedBean)
  {
    this.relativeTransitImpl = relativeTransitImpl;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.besiegedBean = besiegedBean;
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    TranslationOfLightBean bean = new TranslationOfLightBean(planet , horoscopeContext , aspectApplySeparateImpl , besiegedBean);
    if (bean.isTranslatingLight())
    {
      double deg = horoscopeContext.getHoroscope().getAngle(bean.getFromPlanet(), bean.getToPlanet());
      //StringBuffer sb = new StringBuffer(" 從 " + bean.getFromPlanet() + " 傳遞光線到 " + bean.getToPlanet() + " , " + bean.getFromPlanet()+" 與 " + bean.getToPlanet() + 
          //" 交角 " + deg + " 度");
      if (bean.getBesigingPlanetsAspectType() != null)
      {
        //sb.append("(" + (bean.getBesigingPlanetsAspectType() == AspectType.APPLYING ? "入" : "出") + "相位)");
        return new Tuple<String , Object[]>("commentAspect" , new Object[]{planet , bean.getFromPlanet() , bean.getToPlanet() , deg , bean.getBesigingPlanetsAspectType()});
      }
      else
      {
        //sb.append("(未形成交角)");
        return new Tuple<String , Object[]>("commentUnaspect" , new Object[]{planet , bean.getFromPlanet() , bean.getToPlanet() , deg });
      }
        
    }
    return null;
  }

}
