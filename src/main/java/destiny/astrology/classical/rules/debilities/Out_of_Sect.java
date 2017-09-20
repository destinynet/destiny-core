/**
 * @author smallufo 
 * Created on 2007/12/31 at 上午 3:46:51
 */ 
package destiny.astrology.classical.rules.debilities;

import destiny.astrology.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

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
    this.dayNightImpl = dayNightImpl;
  }

  public DayNightDifferentiator getDayNightImpl()
  {
    return dayNightImpl;
  }

  public void setDayNightImpl(DayNightDifferentiator dayNightImpl)
  {
    this.dayNightImpl = dayNightImpl;
  }

  @Override
  protected Optional<Tuple2<String, Object[]>> getResult(Planet planet, @NotNull Horoscope h) {
    ZodiacSign sign = h.getZodiacSign(planet);
    DayNight dayNight = dayNightImpl.getDayNight(h.getLmt(), h.getLocation());
    
    if ( dayNight == DayNight.DAY && (planet == Planet.MOON || planet == Planet.VENUS || planet == Planet.MARS)) {
      if (h.getHouse(planet) >= 7 && sign.getBooleanValue()) {
        //addComment(Locale.TAIWAN , "夜星 " + planet + " 於白天在地平面上，落入陽性星座 " + sign.toString(Locale.TAIWAN) + " 座，不得時");
        return Optional.of(Tuple.tuple("commentNight", new Object[]{planet, sign}));
      }
    }
    else if (dayNight == DayNight.NIGHT && (planet == Planet.SUN || planet == Planet.JUPITER || planet == Planet.SATURN)) {
      if (h.getHouse(planet) >= 7 && !sign.getBooleanValue()) {
        //addComment(Locale.TAIWAN , "晝星 " + planet + " 於夜晚在地平面上，落入陰性星座 " + sign.toString(Locale.TAIWAN) + " 座，不得時");
        return Optional.of(Tuple.tuple("commentDay" , new Object[]{planet , sign}));
      }
    }
    return Optional.empty();
  }

}
