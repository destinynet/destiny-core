/*
 * @author smallufo
 * @date 2004/10/30
 * @time 上午 12:37:50
 */
package destiny.astrology.beans;

import destiny.astrology.Aspect;
import destiny.astrology.RelativeTransitIF;
import destiny.astrology.Star;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 計算兩星交角的 Bean
 */
public class RelativeTransitBean implements Serializable
{
  /** 內定採用 SwissEph 的 RelativeTransitIF 實作 */
  @Inject
  private RelativeTransitIF relativeTransitImpl;// = new RelativeTransitImpl();
  
  protected RelativeTransitBean()
  {
  }
  
  public RelativeTransitBean(RelativeTransitIF relativeTransitImpl)
  {
    this.relativeTransitImpl = relativeTransitImpl;
  }
  

  

  /** 
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   */
  @Nullable
  public Time getNearestRelativeTransitTime(Star transitStar , Star relativeStar , Time fromGmtTime , @NotNull List<Aspect> aspects , boolean isForward )
  {
    double[] doubleAngles = new double[aspects.size()];
    for(int i=0 ; i < aspects.size() ; i++)
      doubleAngles[i] = aspects.get(i).getDegree();
    return getNearestRelativeTransitTime(transitStar, relativeStar, fromGmtTime, doubleAngles, isForward);
  }
  
  /** 
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   */
  @Nullable
  public Time getNearestRelativeTransitTime(Star transitStar , Star relativeStar , Time fromGmtTime , @NotNull double[] angles , boolean isForward )
  {
    Time resultTime = null;
    /** 
     * 相交 270 度也算 90 度 
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */
    Set<Double> realAngles = new HashSet<>();
    for (double angle : angles)
    {
      realAngles.add(angle);
      if (angle != 0)
        realAngles.add(360-angle);
    }
    
    for(double angle : realAngles)
    {
      Time result = null;
      try
      {
        result = relativeTransitImpl.getRelativeTransit(transitStar, relativeStar, angle, fromGmtTime, isForward);
        //System.out.println("推算" + transitStar + " 與 " + relativeStar + " 的角度 : " + angle + " 得到 " + result);
      }
      catch(RuntimeException ignored)
      {
      }
      
      if (resultTime == null)
        resultTime = result;
      else
      {
        if (result != null)
        {
          //目前已經有一個結果，比較看看現在算出的，和之前的，哪個比較近
          if (isForward)
          {
            //順推
            if (result.isBefore(resultTime))
              resultTime = result;
          }
          else
          {
            //逆推
            if (result.isAfter(resultTime))
              resultTime = result;
          }          
        }
      }
    }
    //System.out.println("計算 " + transitStar + " 與 " + relativeStar + (isForward ? "正推":"逆推") + " 形成 " + realAngles + " 的時間，得到 " + resultTime);
    return resultTime;
  }
  
  
  /**
   * 從 fromGmtTime 到 toGmtTime 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return List < Map < angle , Time > >
   */
  @NotNull
  public List<Time> getPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , @NotNull Time fromGmtTime , @NotNull Time toGmtTime , double angle)
  {
    List<Time> resultList = new ArrayList<>();
    while (fromGmtTime.isBefore(toGmtTime))
    {
      fromGmtTime = relativeTransitImpl.getRelativeTransit( transitStar , relativeStar , angle , fromGmtTime , true );
      if (!fromGmtTime.isBefore(toGmtTime))
        break;
      resultList.add(fromGmtTime);
      fromGmtTime = new Time(  (fromGmtTime.getGmtJulDay() + 0.000001 ) );
    }
    return resultList;
  }// getPeriodRelativeTransitTimes

}
