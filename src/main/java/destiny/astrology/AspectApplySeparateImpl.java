/**
 * Created by smallufo at 2008/11/6 下午 6:21:59
 */
package destiny.astrology;

import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public class AspectApplySeparateImpl implements AspectApplySeparateIF , Serializable
{
  /** 可以注入現代占星 ( AspectEffectiveModern ) 或是古典占星 ( AspectEffectiveClassical ) 的實作 */
  private AspectEffectiveIF aspectEffectiveImpl;
  
  public AspectApplySeparateImpl(AspectEffectiveIF aspectEffectiveImpl)
  {
    this.aspectEffectiveImpl = aspectEffectiveImpl;
  }
  
  /**
   * 判斷兩顆星體是否形成某交角 , 如果是的話 , 傳回 入相位或是出相位 ; 如果沒有形成交角 , 傳回 empty
   * 計算方式：這兩顆星的交角，與 Aspect 的誤差，是否越來越少
   */
  @Override
  public Optional<AspectType> getAspectType(@NotNull HoroscopeContext horoscopeContext, Point p1, Point p2, @NotNull Aspect aspect)
  {
    double deg1 = horoscopeContext.getHoroscope().getPositionWithAzimuth(p1).getLongitude();
    double deg2 = horoscopeContext.getHoroscope().getPositionWithAzimuth(p2).getLongitude();
    
    if (aspectEffectiveImpl.isEffective(p1, deg1, p2, deg2, aspect))
    {
      double planetsAngle = Horoscope.getAngle(deg1, deg2);
      double error = Math.abs(planetsAngle - aspect.getDegree()); //目前與 aspect 的誤差
      //System.out.println(p1 + " 與 " + p2 + " 形成 " + aspect + " , 誤差 " + error + " 度");
      
      Time lmt = horoscopeContext.getLmt(); //目前時間
      Time oneSecondLater = new Time(lmt , 1); //一秒之後
      
      HoroscopeContext hc2 = HoroscopeContext.getNewLmtHoroscope(oneSecondLater,  horoscopeContext);
      
      double deg1_next = hc2.getHoroscope().getPositionWithAzimuth(p1).getLongitude();
      double deg2_next = hc2.getHoroscope().getPositionWithAzimuth(p2).getLongitude();
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
  public Optional<AspectType> getAspectType(@NotNull HoroscopeContext horoscopeContext, Point p1, Point p2, @NotNull Collection<Aspect> aspects)
  {
    Optional<AspectType> aspectType = Optional.empty();
    for(Aspect aspect : aspects)
    {
      aspectType = getAspectType(horoscopeContext, p1, p2, aspect); 
      if ( aspectType.isPresent())
        break;
    }
    //System.out.println(p1 + " 與 " + p2 +" 的 AspectType 為 " + aspectType);
    return aspectType;
  }

}
