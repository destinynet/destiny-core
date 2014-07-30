/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 4:53:40
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import destiny.astrology.classical.Dignity;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Peregrine. */
public final class Peregrine extends EssentialRule
{
  /** 計算白天黑夜的實作 */
  private DayNightDifferentiator dayNightImpl;
  
  public Peregrine(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    double planetDegree = horoscopeContext.getPosition(planet).getLongitude();
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    
    DayNight dayNight = dayNightImpl.getDayNight(horoscopeContext.getLmt(), horoscopeContext.getLocation());
    if (planet != essentialImpl.getPoint(sign, Dignity.RULER) &&
        planet != essentialImpl.getPoint(sign, Dignity.EXALTATION) &&
        planet != essentialImpl.getPoint(sign, Dignity.DETRIMENT) &&
        planet != essentialImpl.getPoint(sign, Dignity.FALL) &&
        planet != essentialImpl.getTermsPoint(sign, planetDegree) &&
        planet != essentialImpl.getFacePoint(planetDegree)
        )
    {
      //判定日夜 Triplicity
      if( !(dayNight == DayNight.DAY   && planet == essentialImpl.getTriplicityPoint(sign, DayNight.DAY )) &&  
          !(dayNight == DayNight.NIGHT && planet == essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT))   )
      {
        //addComment(Locale.TAIWAN , planet + " 處於 Peregrine 狀態.");
        return new Tuple<String , Object[]>("comment" , new Object[]{planet});
      }
    }
    return null;
  }

}
