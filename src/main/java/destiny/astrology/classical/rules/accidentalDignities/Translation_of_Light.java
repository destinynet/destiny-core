/**
 * @author smallufo 
 * Created on 2008/1/5 at 上午 7:14:47
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.classical.TranslationOfLightIF;
import destiny.utils.Tuple;
import destiny.utils.Tuple4;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Translation_of_Light extends Rule
{

  private TranslationOfLightIF translationOfLightImpl;

  public Translation_of_Light(TranslationOfLightIF translationOfLightImpl) {
    this.translationOfLightImpl = translationOfLightImpl;
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    Tuple4<Boolean , Planet , Planet , AspectApplySeparateIF.AspectType> t = translationOfLightImpl.getResult(planet, horoscopeContext);
    //TranslationOfLightBean bean = new TranslationOfLightBean(planet , horoscopeContext , aspectApplySeparateImpl , besiegedBean);
    if (t.getFirst())
    {
      double deg = horoscopeContext.getHoroscope().getAngle(t.getSecond() , t.getThird());
      //StringBuffer sb = new StringBuffer(" 從 " + bean.getFromPlanet() + " 傳遞光線到 " + bean.getToPlanet() + " , " + bean.getFromPlanet()+" 與 " + bean.getToPlanet() + 
          //" 交角 " + deg + " 度");
      if (t.getFourth() != null)
      {
        //sb.append("(" + (bean.getBesigingPlanetsAspectType() == AspectType.APPLYING ? "入" : "出") + "相位)");
        return new Tuple<String , Object[]>("commentAspect" , new Object[]{planet , t.getSecond() , t.getThird() , deg , t.getFourth()});
      }
      else
      {
        //sb.append("(未形成交角)");
        return new Tuple<String , Object[]>("commentUnaspect" , new Object[]{planet , t.getSecond() , t.getThird() , deg });
      }
        
    }
    return null;
  }

}
