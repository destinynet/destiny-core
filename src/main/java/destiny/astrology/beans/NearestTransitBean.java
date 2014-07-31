/**
 * @author smallufo 
 * Created on 2008/12/23 at 上午 12:19:30
 */ 
package destiny.astrology.beans;

import destiny.astrology.RelativeTransitIF;
import destiny.astrology.Star;
import destiny.core.calendar.Time;
import destiny.utils.Tuple;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近 (可能 backward , 也可能 forward) 的是哪一次
 */
public class NearestTransitBean implements Serializable
{
  /** 內定採用 SwissEph 的 RelativeTransitIF 實作 */
  @Inject
  private RelativeTransitIF relativeTransitImpl;

//  private Star transitStar;
//  private Star relativeStar;
//  private Time fromGmtTime;
//  private double[] angles;
//  private boolean isForward;
//
//  //下一個/或上一個 形成交角的時刻 (GMT)
//  private Time resultTime;
//
//  //下一個/或上一個 形成的交角
//  private double resultAngle;

  public NearestTransitBean(RelativeTransitIF relativeTransitImpl) {
    this.relativeTransitImpl = relativeTransitImpl;
  }
  
//  public NearestTransitBean(RelativeTransitIF relativeTransitImpl , Star transitStar , Star relativeStar , Time fromGmtTime , double[] angles , boolean isForward)
//  {
//    this.relativeTransitImpl = relativeTransitImpl;
//    this.transitStar = transitStar;
//    this.relativeStar = relativeStar;
//    this.fromGmtTime = fromGmtTime;
//    this.angles = angles;
//    this.isForward = isForward;
//    this.calculate();
//  }
//
//  private void calculate()
//  {
//    /**
//     * 相交 270 度也算 90 度
//     * 相交 240 度也是 120 度
//     * 所以要重算一次角度
//     */
//    Set<Double> realAngles = Collections.synchronizedSet(new HashSet<Double>());
//    for (double angle : angles)
//    {
//      realAngles.add(angle);
//      if (angle != 0)
//        realAngles.add(360-angle);
//    }
//
//    for(double angle : realAngles)
//    {
//      Time result = null;
//      try
//      {
//        result = relativeTransitImpl.getRelativeTransit(transitStar, relativeStar, angle, fromGmtTime, isForward);
//        //System.out.println("推算" + transitStar + " 與 " + relativeStar + " 的角度 : " + angle + " 得到 " + result);
//      }
//      catch(RuntimeException ignored)
//      {
//      }
//
//      if (resultTime == null)
//        resultTime = result;
//      else
//      {
//        if (result != null)
//        {
//          //目前已經有一個結果，比較看看現在算出的，和之前的，哪個比較近
//          if (isForward)
//          {
//            //順推
//            if (result.isBefore(resultTime))
//            {
//              resultTime = result;
//              resultAngle = angle;
//            }
//          }
//          else
//          {
//            //逆推
//            if (result.isAfter(resultTime))
//            {
//              resultTime = result;
//              resultAngle = angle;
//            }
//          }
//        }
//      }
//    }
//    //System.out.println("計算 " + transitStar + " 與 " + relativeStar + (isForward ? "正推":"逆推") + " 形成 " + resultAngle + " 的時間，得到 " + resultTime);
//  } // calculate()

  // 取得 下一個(Forward)/或上一個(Backward) 形成交角的時刻 (GMT)
//  public Time getResultTime()
//  {
//    return resultTime;
//  }
  
  /** 取得所形成的交角度數 (可能大於 180) */
//  public double getResultAngle()
//  {
//    return resultAngle;
//  }

  /**
   * @return
   * A : 取得 下一個(Forward)/或上一個(Backward) 形成交角的時刻 (GMT)
   * B : /** 取得所形成的交角度數 (可能大於 180)
   */
  @NotNull
  public Tuple<Time , Double> getResult( Star transitStar , Star relativeStar , Time fromGmtTime , @NotNull double[] angles , boolean isForward) {


    //下一個/或上一個 形成交角的時刻 (GMT)
    Time resultTime = null;

    //下一個/或上一個 形成的交角
    double resultAngle = 0;

     /**
     * 相交 270 度也算 90 度
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */
    Set<Double> realAngles = Collections.synchronizedSet(new HashSet<>());
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
            {
              resultTime = result;
              resultAngle = angle;
            }
          }
          else
          {
            //逆推
            if (result.isAfter(resultTime))
            {
              resultTime = result;
              resultAngle = angle;
            }
          }
        }
      }
    }

    return Tuple.of(resultTime , resultAngle);
  }
  
}
