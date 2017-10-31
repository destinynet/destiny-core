/**
 * Created by smallufo at 2008/11/6 下午 6:21:59
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

public class AspectApplySeparateImpl implements IAspectApplySeparate, Serializable
{
  /** 可以注入現代占星 ( AspectEffectiveModern ) 或是古典占星 ( AspectEffectiveClassical ) 的實作 */
  private final IAspectEffective aspectEffectiveImpl;

  private final IHoroscope horoscopeImpl;
  
  public AspectApplySeparateImpl(IAspectEffective aspectEffectiveImpl, IHoroscope horoscopeImpl)
  {
    this.aspectEffectiveImpl = aspectEffectiveImpl;
    this.horoscopeImpl = horoscopeImpl;
  }
  
  /**
   * 判斷兩顆星體是否形成某交角 , 如果是的話 , 傳回 入相位或是出相位 ; 如果沒有形成交角 , 傳回 empty
   * 計算方式：這兩顆星的交角，與 Aspect 的誤差，是否越來越少
   */
  @Override
  public Optional<AspectType> getAspectType(@NotNull Horoscope h, Point p1, Point p2, @NotNull Aspect aspect)
  {
    double deg1 = h.getPositionWithAzimuth(p1).getLng();
    double deg2 = h.getPositionWithAzimuth(p2).getLng();
    
    if (aspectEffectiveImpl.isEffective(p1, deg1, p2, deg2, aspect))
    {
      double planetsAngle = Horoscope.getAngle(deg1, deg2);
      double error = Math.abs(planetsAngle - aspect.getDegree()); //目前與 aspect 的誤差

      ChronoLocalDateTime lmt = h.getLmt(); //目前時間
      ChronoLocalDateTime oneSecondLater = lmt.plus(1 , ChronoUnit.SECONDS); // 一秒之後

      Horoscope hc2 = horoscopeImpl.getHoroscope(oneSecondLater , h.getLocation() , h.getPoints() , h.getHouseSystem() , h.getCentric() , h.getCoordinate() , h.getTemperature() , h.getPressure());

      double deg1_next = hc2.getPositionWithAzimuth(p1).getLng();
      double deg2_next = hc2.getPositionWithAzimuth(p2).getLng();
      double planetsAngle_next = Horoscope.getAngle(deg1_next , deg2_next);
      double error_next = Math.abs(planetsAngle_next - aspect.getDegree());
      
      //System.out.println(p1 + " 與 " + p2 + " 形成 " + aspect + " , 誤差 " + error_next + " 度");
      
      if (error_next <= error)
        return Optional.of(AspectType.APPLYING);
      else
        return Optional.of(AspectType.SEPARATING);
    }
    else
      return Optional.empty(); //這兩顆星沒有形成交角
  }

  @Override
  public Optional<AspectType> getAspectType(@NotNull Horoscope h, Point p1, Point p2, @NotNull Collection<Aspect> aspects)
  {
    Optional<AspectType> aspectType = Optional.empty();
    for(Aspect aspect : aspects)
    {
      aspectType = getAspectType(h, p1, p2, aspect);
      if ( aspectType.isPresent())
        break;
    }
    //System.out.println(p1 + " 與 " + p2 +" 的 AspectType 為 " + aspectType);
    return aspectType;
  }

}
