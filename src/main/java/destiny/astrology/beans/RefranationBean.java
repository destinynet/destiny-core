/**
 * @author smallufo 
 * Created on 2008/11/11 at 上午 2:29:44
 */ 
package destiny.astrology.beans;

import java.io.Serializable;

import destiny.astrology.Aspect;
import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.Point;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.RetrogradeIF;
import destiny.astrology.Star;
import destiny.astrology.Aspect.Importance;
import destiny.astrology.AspectApplySeparateIF.AspectType;
import destiny.core.calendar.Time;

/**
 * <pre>
 * Refranation 定義：返回、臨陣脫逃
 * 這是六種 Denials of Perfection 之一 ，定義於此： 
 * http://www.skyscript.co.uk/tobyn2.html#ref
 * 當兩顆星正在 apply 某交角，在形成 Perfect 之前，其中一顆轉為逆行
 * 
 * 此程式必須能正確判斷以下兩種情形
 * 1. 本星即將 apply 他星，而在 perfect 前，本星逆行，代表自我退縮。
 * 2. 本星即將 apply 他星，而在 perfect 前，他星逆行，代表對方退縮。
 * 
 * TODO : 應該加上演算法：如果星體順轉逆（或逆轉順），並且逃離了 aspect 的有效範圍，才是真的「臨陣脫逃」
 * </pre>
 */
public class RefranationBean implements Serializable
{
  /** 本星 */
  private Planet planet;
  
  /** 另外一顆星 */
  private Point otherPoint;
  
  /** 命盤 */
  private HoroscopeContext context;
  
  /** 判斷入相位，或是出相位 的實作 */
  private AspectApplySeparateIF aspectApplySeparateImpl ;
  
  /** 計算兩星呈現某交角的時間 , 內定是 Swiss ephemeris 的實作 */
  private final RelativeTransitIF relativeTransitImpl;// = new RelativeTransitImpl();
  
  /** 計算星體逆行的介面，目前只支援 Planet , 目前的實作只支援 Planet , Asteroid , Moon's Node (只有 True Node。 Mean 不會逆行！) */
  private final RetrogradeIF retrogradeImpl;// = new RetrogradeImpl();
  
  //============= 以下是計算的結果 ============================
  
  
  /** 此星 (planet) 與其他星 (otherPoint)，是否會出現 refranation 的情形 */
  private boolean refranate;
  
  /** 「先」臨陣脫逃者，是誰，這裡強調「先」，因為有可能在 Perfect 交角之前，雙方都臨陣脫逃。*/
  private Point refranator = null;
  
  /** 此兩星正在 apply 哪個交角 */
  private Aspect applyingAspect = null;
  
  /**
   * <pre>
   * Refranation : 返回；臨陣脫逃
   * 兩顆星正在 Apply 某交角，但是其中一顆星（或是兩顆星都），突然由順轉逆（或是逆轉順），而無法 perfect 其交角
   * 根據此文定義： http://www.thespiritconnect.com/article-lilly4.html
   * TODO : 任一顆星在 perfect 其交角之前，換了星座，也是 Refranation !!! 此演算法還沒實作
   * 該文提及：由逆轉順，代表事情會好轉。由順轉逆，事情會被放棄
   * </pre> 
   */
  public RefranationBean(HoroscopeContext context , Planet planet , Point otherPoint , 
                          AspectApplySeparateIF aspectApplySeparateImpl , RelativeTransitIF relativeTransitImpl , RetrogradeIF retrogradeImpl)
  {
    this.context = context;
    this.planet = planet;
    this.otherPoint = otherPoint;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.relativeTransitImpl = relativeTransitImpl;
    this.retrogradeImpl = retrogradeImpl;
    
    this.calculate();
  }
  
  private void calculate()
  {
    //計算兩星是否 apply 哪個交角
    for(Aspect aspect : Aspect.getAngles(Importance.HIGH))
    {
      AspectType aspectType = aspectApplySeparateImpl.getAspectType(context , planet , otherPoint , aspect);
      if (aspectType == AspectType.APPLYING)
      {
        applyingAspect = aspect;
        break;
      }
    }
    
    //System.out.println("形成逼近的交角 applyingAspect = " + applyingAspect);
    
    if (applyingAspect == null) //沒有 apply任何交角 , 或是正在 separating
      return;

    //兩星目前正在接近 applyingAspect 此角度

    Time perfectAspectGmt = null;
    Time perfectAspectGmt1 = relativeTransitImpl.getRelativeTransit(planet , (Star)otherPoint , applyingAspect.getDegree() , context.getGmt() , true);
    if (applyingAspect.getDegree() != 0 && applyingAspect.getDegree() != 180) //額外計算 「補角」（360-degree）的時刻
    {
      Time perfectAspectGmt2 = relativeTransitImpl.getRelativeTransit(planet , (Star)otherPoint , 360-applyingAspect.getDegree() , context.getGmt() , true);
      if (perfectAspectGmt1.isAfter(perfectAspectGmt2))
        perfectAspectGmt = perfectAspectGmt2;
      else
        perfectAspectGmt = perfectAspectGmt1;
    }
    else
      perfectAspectGmt = perfectAspectGmt1; // 衝/合 只會有一個時刻，不用算「補角」 
      
    //System.out.println("準確形成交角("+applyingAspect.getDegree()+"度)於 GMT  " + perfectAspectGmt);
    
    Time nextStationaryGmt1 = retrogradeImpl.getNextStationary(planet, context.getGmt() , true);
    Time nextStationaryGmt2 = retrogradeImpl.getNextStationary((Star)otherPoint, context.getGmt() , true);
    
    //System.out.println(planet + "轉折時間 (GMT) : " + nextStationaryGmt1);
    //System.out.println(otherPoint + "轉折時間 (GMT) : " + nextStationaryGmt2);
    
    //如果這兩個 GMT 時間，有任何一個早於 (prior to) perfectAspectGmt，則代表有一顆星臨陣脫逃
    if (nextStationaryGmt1.isBefore(perfectAspectGmt))
    {
      this.refranate = true;
      this.refranator = planet;
    }
    else if (nextStationaryGmt2.isBefore(perfectAspectGmt))
    {
      this.refranate = true;
      this.refranator = otherPoint;
    }
  }

  
  /** 此兩星是否有任何一顆星發生 「臨陣脫逃」的情形 */
  public boolean isRefranate()
  {
    return refranate;
  }

  /** 如果有臨陣脫逃，是哪顆星「先」逃 (並不代表另一顆星一定不會逃) */
  public Point getRefranator()
  {
    return refranator;
  }
  
  /** @return 此兩星正在 applying 哪個交角，如果沒有，則為 null */
  public Aspect getApplyingAspect()
  {
    return applyingAspect;
  }
}
