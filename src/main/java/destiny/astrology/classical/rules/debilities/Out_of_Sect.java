/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:46:51
 */ 
package destiny.astrology.classical.rules.debilities;

import java.util.Locale;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.ZodiacSign;
import destiny.core.chinese.YinYang;

/** 
 * 判斷不得時 (Out of sect) : 白天 , 夜星位於地平面上，落入陽性星座；或是晚上，晝星在地平面上，落入陰性星座
 * 晝星 : 日 , 木 , 土
 * 夜星 : 月 , 金 , 火 
 */
public final class Out_of_Sect extends Rule
{
  /** 計算白天黑夜的實作 */
  private DayNightDifferentiator dayNightImpl;
  
  public Out_of_Sect(DayNightDifferentiator dayNightImpl)
  {
    super("Out_of_Sect");
    this.dayNightImpl = dayNightImpl;
  }

  @Override
  public boolean isApplicable(Planet planet, HoroscopeContext horoscopeContext)
  {
    ZodiacSign sign = horoscopeContext.getZodiacSign(planet);
    DayNight dayNight = dayNightImpl.getDayNight(horoscopeContext.getLmt(), horoscopeContext.getLocation());
    
    if ( dayNight == DayNight.DAY && (planet == Planet.MOON || planet == Planet.VENUS || planet == Planet.MARS))
    {
      if (horoscopeContext.getHouse(planet) >= 7 && sign.getYinYang() == YinYang.陽)
      {
        addComment(Locale.TAIWAN , "夜星 " + planet + " 於白天在地平面上，落入 " + sign.toString(Locale.TAIWAN) + " 座，不得時");
        return true;
      }
    }
    else if (dayNight == DayNight.NIGHT && (planet == Planet.SUN || planet == Planet.JUPITER || planet == Planet.SATURN))
    {
      if (horoscopeContext.getHouse(planet) >= 7 && sign.getYinYang() == YinYang.陰)
      {
        addComment(Locale.TAIWAN , "晝星 " + planet + " 於夜晚在地平面上，落入 " + sign.toString(Locale.TAIWAN) + " 座，不得時");
        return true;
      }
    }
    return false;
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
