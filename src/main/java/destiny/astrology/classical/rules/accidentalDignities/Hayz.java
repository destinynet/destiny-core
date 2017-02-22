/**
 * @author smallufo 
 * Created on 2007/12/30 at 上午 12:04:45
 */ 
package destiny.astrology.classical.rules.accidentalDignities;

import destiny.astrology.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 判斷得時 (Hayz) : 白天 , 晝星位於地平面上，落入陽性星座；或是晚上，夜星在地平面上，落入陰性星座
 * 晝星 : 日 , 木 , 土
 * 夜星 : 月 , 金 , 火
 */
public final class Hayz extends Rule
{
  /** 計算白天黑夜的實作 */
  private DayNightDifferentiator dayNightImpl;
  
  public Hayz(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Override
  public Optional<Pair<String, Object[]>> getResult(Planet planet, @NotNull HoroscopeContext horoscopeContext)
  {
    DayNight dayNight = dayNightImpl.getDayNight(horoscopeContext.getLmt(), horoscopeContext.getLocation());
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    int planetHouse = horoscopeContext.getHouse(planet);
    if ( dayNight == DayNight.DAY && (planet == Planet.SUN || planet == Planet.JUPITER || planet == Planet.SATURN))
    {
      if (planetHouse >= 7 && sign.getBooleanValue())
      {
        //addComment(Locale.TAIWAN , "晝星 " + planet + " 於白天在地平面上，落入 " + sign.toString(Locale.TAIWAN) + " 座，得時");
        return Optional.of(ImmutablePair.of("commentDay" , new Object[]{planet , sign}));
      }
    } 
    else if (dayNight == DayNight.NIGHT && (planet == Planet.MOON || planet == Planet.VENUS || planet == Planet.MARS))
    {
      if (planetHouse >= 7 && !sign.getBooleanValue())
      {
        //addComment(Locale.TAIWAN , "夜星 " + planet + " 於夜晚在地平面上，落入 " + sign.toString(Locale.TAIWAN) + " 座，得時");
        return Optional.of(ImmutablePair.of("commentNight" , new Object[]{planet , sign}));
      }
    }
    return Optional.empty();
  }

  public DayNightDifferentiator getDayNightImpl()
  {
    return dayNightImpl;
  }

  public void setDayNightImpl(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

}
