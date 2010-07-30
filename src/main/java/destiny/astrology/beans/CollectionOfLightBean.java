/**
 * @author smallufo 
 * Created on 2008/1/8 at 上午 9:49:01
 */ 
package destiny.astrology.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import destiny.astrology.Aspect;
import destiny.astrology.Aspect.Importance;
import destiny.astrology.AspectApplySeparateIF;
import destiny.astrology.DayNightDifferentiator;
import destiny.astrology.HoroscopeContext;
import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.classical.AspectEffectiveClassical;
import destiny.astrology.classical.EssentialUtils;

/**
 * <pre>
 * Collection of Light 之定義以及演算法 : 
 * TODO : (2008-11-07 修正 新增考慮：) 根據此文的定義
 * http://destiny.xfiles.to/ubbthreads/ubbthreads.php/posts/6999
 * (1) 當兩個徵象星彼此沒有交角,
 * (2) 但這兩個行星都與第三個行星有入相位
 *     (即第三個行星是三個中運行最慢的). 
 *     
 * 表第三個行星使得兩個徵象星能互相接觸.
 * </pre>
 */
public class CollectionOfLightBean implements Serializable
{
  private BesiegedBean besiegedBean;
  
  /** 計算此行星是否收集光線，如果有的話，是以何種形式 ( CollectType ) 收集光線 */
  private Planet planet;
  
  /** 收集光線的形式 */
  public enum CollectType {DIGNITIES , DEBILITIES , ALL};
  
  private HoroscopeContext horoscopeContext;
  
  /** 古典占星的交角判定 */
  @SuppressWarnings("unused")
  private AspectEffectiveClassical aspectEffectiveClassical;
  
  /** 判斷入相位，或是出相位 */
  @SuppressWarnings("unused")
  private AspectApplySeparateIF aspectApplySeparateImpl;
  
  /** 判斷日夜 */
  private DayNightDifferentiator dayNightDifferentiatorImpl;
  
  /** 蒐集光線的演算法 */
  private CollectType collectType = null;
  
  /** 是否蒐集光線 */
  private boolean collectionLight = false;
  /** 從哪兩顆星蒐集光線 */
  private Planet[] fromPlanets = new Planet[2];
  
  /** 已經指明詢問是否符合某種 「光線蒐集模式 : CollectType 」*/
  public CollectionOfLightBean(Planet planet , 
      HoroscopeContext horoscopeContext , 
      CollectType collectType , RelativeTransitIF relativeTransitImpl, 
      AspectEffectiveClassical aspectEffectiveClassical ,  
      AspectApplySeparateIF aspectApplySeparateImpl ,
      DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.planet = planet;
    this.horoscopeContext = horoscopeContext;
    this.collectType = collectType;
    this.besiegedBean = new BesiegedBean(relativeTransitImpl);
    this.aspectEffectiveClassical = aspectEffectiveClassical;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
    
    this.calculate();
  }
  
  /** 並未指明是否符合某種「光線收集模式 : CollectType」 */
  public CollectionOfLightBean(Planet planet , 
      HoroscopeContext horoscopeContext , 
      RelativeTransitIF relativeTransitImpl, 
      AspectEffectiveClassical aspectEffectiveClassical , 
      AspectApplySeparateIF aspectApplySeparateImpl , 
      DayNightDifferentiator dayNightDifferentiatorImpl)
  {
    this.planet = planet;
    this.horoscopeContext = horoscopeContext;
    this.besiegedBean = new BesiegedBean(relativeTransitImpl);
    this.aspectEffectiveClassical = aspectEffectiveClassical;
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.dayNightDifferentiatorImpl = dayNightDifferentiatorImpl;
    
    this.calculate();
  }
  
  
  private void calculate()
  {
    List<Planet> twoPlanets = besiegedBean.getBesiegingPlanets(planet, horoscopeContext.getGmt() , true , Aspect.getAngles(Importance.HIGH));
    if (twoPlanets.get(0) == null || twoPlanets.get(1) == null)
      return;
    Planet p1 = twoPlanets.get(0);
    Planet p2 = twoPlanets.get(1);
    double planetSpeed = horoscopeContext.getPosition(planet).getSpeedLongitude();
    double p1Speed = horoscopeContext.getPosition(p1).getSpeedLongitude();
    double p2Speed = horoscopeContext.getPosition(p2).getSpeedLongitude();
    
    //System.out.println(planet + " 被 " + p1 + " 以及 " + p2 + " 夾");
    
    //中間被夾的星，速度比較慢
    if (Math.abs(planetSpeed) < Math.abs(p1Speed) && Math.abs(planetSpeed) < Math.abs(p2Speed))
    {
      //System.out.println(planet + " 速度最慢");
      /*
       * 夾 planet 的兩顆星 , 還必須形成交角
       * if (aspectEffectiveClassical.isEffective(p1, horoscopeContext.getPosition(p1).getLongitude(), p2, horoscopeContext.getPosition(p2).getLongitude(), Aspect.getAngles(Aspect.Importance.HIGH)))
       *  
       * 2008-11-08 標注：也有學派認為「該兩顆星是否形成交角」不需要。
       *   但既然都與第三顆星形成主交角，這兩顆星必然也(「將會」/「曾經」)形成主交角 , 所以移除此演算法
       *   改以「被夾的星，需同時與兩顆星，APPLYING 交角」（這會嚴格許多，待驗證）
       * 
       */
      /*
      if (AspectType.APPLYING == aspectApplySeparateImpl.getAspectType(horoscopeContext, planet , p1, Aspect.getAngles(Importance.HIGH)) &&
          AspectType.APPLYING == aspectApplySeparateImpl.getAspectType(horoscopeContext, planet , p2, Aspect.getAngles(Importance.HIGH))
          )
      */
      {
        //System.out.println(p1 + " 以及 " + p2 + " 也形成交角");
        
        EssentialUtils utils = new EssentialUtils(dayNightDifferentiatorImpl);
        //System.out.println(p1 + " 是否接納 " + planet + " : " + utils.isReceivingFromDignities(p1, planet , horoscopeContext));
        //System.out.println(p2 + " 是否接納 " + planet + " : " + utils.isReceivingFromDignities(p2, planet , horoscopeContext));
        
        if (this.collectType == null) //沒有指定蒐集光線的演算法
        {
          this.collectionLight = true;
          this.fromPlanets[0] = p1;
          this.fromPlanets[1] = p2;
          if (utils.isReceivingFromDignities(p1, planet , horoscopeContext) && 
              utils.isReceivingFromDignities(p2, planet , horoscopeContext))
            this.collectType = CollectType.DIGNITIES;
          else if (utils.isReceivingFromDebilities(p1 , planet , horoscopeContext) &&
                    utils.isReceivingFromDebilities(p2 , planet , horoscopeContext))
            this.collectType = CollectType.DEBILITIES;
          else
            this.collectType = CollectType.ALL;
        }
        else //有指定蒐集光線的演算法
        {
          if (this.collectType == CollectType.DIGNITIES)
          {
            //兩顆快星都必須透過 essential dignities 接納慢星
            if (utils.isReceivingFromDignities(p1, planet , horoscopeContext) && 
                utils.isReceivingFromDignities(p2, planet , horoscopeContext))
            {
              this.collectionLight = true;
              this.fromPlanets[0] = p1;
              this.fromPlanets[1] = p2;
            }
            else
              return;
          }
          else if (this.collectType == CollectType.DEBILITIES)
          {
            if (utils.isReceivingFromDebilities(p1 , planet , horoscopeContext) &&
                utils.isReceivingFromDebilities(p2 , planet , horoscopeContext))
            {
              this.collectionLight = true;
              this.fromPlanets[0] = p1;
              this.fromPlanets[1] = p2;
            }
            else
              return;
          }
          else if (this.collectType == CollectType.ALL)
          {
            //this.collectType == null
            this.collectionLight = true;
            this.fromPlanets[0] = p1;
            this.fromPlanets[1] = p2;          
          }          
        } // collectType != null
      }
    }
  }

  public boolean isCollectionLight()
  {
    return collectionLight;
  }

  /** 如果沒有收集光線，其值將為 null */
  public List<Planet> getFromPlanets()
  {
    if (fromPlanets[0] == null || fromPlanets[1] == null)
      return null;
    return Arrays.asList(fromPlanets);
  }

  /** 收集光線的形式 */
  public CollectType getCollectType()
  {
    return collectType;
  }

}
