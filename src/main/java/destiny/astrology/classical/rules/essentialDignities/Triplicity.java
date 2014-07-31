/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:19:56
 */ 
package destiny.astrology.classical.rules.essentialDignities;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities). */
public final class Triplicity extends Rule
{
  /** 計算白天黑夜的實作 */
  private DayNightDifferentiator dayNightImpl;
  
  public Triplicity(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Nullable
  @Override
  protected Tuple<String, Object[]> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    //取得此 Planet 在什麼星座
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);

    DayNight dayNight = dayNightImpl.getDayNight(horoscopeContext.getLmt(), horoscopeContext.getLocation());
    if(  (dayNight == DayNight.DAY   && planet == essentialImpl.getTriplicityPoint(sign, DayNight.DAY )) ||  
         (dayNight == DayNight.NIGHT && planet == essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT))   ) 
    {
      //addComment(Locale.TAIWAN , planet + " 位於 " + sign + " , 為其 "+dayNight.toString()+" 之 Triplicity");
      return new Tuple<>("comment" , new Object[]{planet , sign , dayNight});
    }
    return null;
  }
}
