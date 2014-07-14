/**
 * @author smallufo 
 * Created on 2008/1/8 at 下午 12:26:42
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import destiny.astrology.beans.BesiegedBean;
import destiny.astrology.beans.CollectionOfLightBean;
import destiny.astrology.classical.AspectEffectiveClassical;
import destiny.utils.Tuple;

import java.util.List;

public final class Collection_of_Light extends Rule
{
  private RelativeTransitIF relativeTransitImpl;
  
  private AspectEffectiveClassical aspectEffectiveClassicalImpl;
  private AspectApplySeparateIF aspectApplySeparateImpl;
  private BesiegedBean besiegedBean;
  
  /** 判斷日夜 */
  private DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public Collection_of_Light(RelativeTransitIF relativeTransitImpl , AspectEffectiveClassical aspectEffectiveClassicalImpl , AspectApplySeparateIF aspectApplySeparateImpl , DayNightDifferentiator dayNightDifferentiatorImpl , BesiegedBean besiegedBean)
  {
    this.relativeTransitImpl = relativeTransitImpl;
    this.aspectEffectiveClassicalImpl = aspectEffectiveClassicalImpl;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
    this.besiegedBean = besiegedBean;
  }

  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, HoroscopeContext horoscopeContext)
  {
    //目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮 
    CollectionOfLightBean bean = new CollectionOfLightBean(planet , horoscopeContext , CollectionOfLightBean.CollectType.DIGNITIES , relativeTransitImpl, aspectEffectiveClassicalImpl , aspectApplySeparateImpl , dayNightDifferentiatorImpl , besiegedBean);
    if (bean.isCollectionLight())
    {
      List<Planet> twoPlanets = bean.getFromPlanets();
      // planet + " 從 " + twoPlanets.get(0) + " 以及 " + twoPlanets.get(1) + " 收集光線" + " , " + twoPlanets.get(0) +" 與 " + twoPlanets.get(1) + " 交角 " +  horoscopeContext.getHoroscope().getAngle(twoPlanets.get(0) , twoPlanets.get(1)) + " 度."
      return new Tuple<String , Object[]>("comment" , 
          new Object[] {planet , twoPlanets.get(0) , twoPlanets.get(1) , 
          horoscopeContext.getHoroscope().getAngle(twoPlanets.get(0) , twoPlanets.get(1))});
    }
    return null;
  }

}
