/**
 * @author smallufo 
 * Created on 2008/1/8 at 下午 12:26:42
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import java.util.List;
import java.util.Locale;

import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.beans.CollectionOfLightBean;
import destiny.astrology.classical.AspectEffectiveClassical;

public final class Collection_of_Light extends Rule
{
  private RelativeTransitIF relativeTransitImpl;
  
  private AspectEffectiveClassical aspectEffectiveClassicalImpl;
  private AspectApplySeparateIF aspectApplySeparateImpl;
  
  /** 判斷日夜 */
  private DayNightDifferentiator dayNightDifferentiatorImpl;
  
  public Collection_of_Light(RelativeTransitIF relativeTransitImpl , AspectEffectiveClassical aspectEffectiveClassicalImpl , AspectApplySeparateIF aspectApplySeparateImpl , DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    super("Collection_of_Light");
    this.relativeTransitImpl = relativeTransitImpl;
    this.aspectEffectiveClassicalImpl = aspectEffectiveClassicalImpl;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    //目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮 
    CollectionOfLightBean bean = new CollectionOfLightBean(planet , horoscopeContext , CollectionOfLightBean.CollectType.DIGNITIES , relativeTransitImpl, aspectEffectiveClassicalImpl , aspectApplySeparateImpl , dayNightDifferentiatorImpl);
    if (bean.isCollectionLight())
    {
      List<Planet> twoPlanets = bean.getFromPlanets();
      addComment(Locale.TAIWAN , planet + " 從 " + twoPlanets.get(0) + " 以及 " + twoPlanets.get(1) + " 收集光線"
          + " , " + twoPlanets.get(0) +" 與 " + twoPlanets.get(1) + " 交角 " + 
          horoscopeContext.getHoroscope().getAngle(twoPlanets.get(0) , twoPlanets.get(1)) + " 度.");
      return true;
    }
    return false;
  }

}
