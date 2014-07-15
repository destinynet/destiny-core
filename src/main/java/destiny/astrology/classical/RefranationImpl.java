/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.astrology.classical;

import destiny.astrology.*;
import destiny.core.calendar.Time;
import destiny.utils.Triple;

import java.io.Serializable;

public class RefranationImpl implements RefranationIF , Serializable {

  private final AspectApplySeparateIF aspectApplySeparateImpl;

  private final RelativeTransitIF relativeTransitImpl;

  private final RetrogradeIF retrogradeImpl;

  public RefranationImpl(AspectApplySeparateIF aspectApplySeparateImpl, RelativeTransitIF relativeTransitImpl, RetrogradeIF retrogradeImpl) {
    this.aspectApplySeparateImpl = aspectApplySeparateImpl;
    this.relativeTransitImpl = relativeTransitImpl;
    this.retrogradeImpl = retrogradeImpl;
  }


  @Override
  public Triple<Boolean, Point, Aspect> resultOf(HoroscopeContext context, Planet planet, Point otherPoint) {

    /** 此星 (planet) 與其他星 (otherPoint)，是否會出現 refranation 的情形 */
    boolean refranate = false;

    /** 「先」臨陣脫逃者，是誰，這裡強調「先」，因為有可能在 Perfect 交角之前，雙方都臨陣脫逃。*/
    Point refranator = null;

    /** 此兩星正在 apply 哪個交角 */
    Aspect applyingAspect = null;

    //計算兩星是否 apply 哪個交角
    for(Aspect aspect : Aspect.getAngles(Aspect.Importance.HIGH))
    {
      AspectApplySeparateIF.AspectType aspectType = aspectApplySeparateImpl.getAspectType(context , planet , otherPoint , aspect);
      if (aspectType == AspectApplySeparateIF.AspectType.APPLYING)
      {
        applyingAspect = aspect;
        break;
      }
    }

    //System.out.println("形成逼近的交角 applyingAspect = " + applyingAspect);

    if (applyingAspect == null) //沒有 apply任何交角 , 或是正在 separating
      return Triple.of(false , null , null);

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
      refranate = true;
      refranator = planet;
    }
    else if (nextStationaryGmt2.isBefore(perfectAspectGmt))
    {
      refranate = true;
      refranator = otherPoint;
    }

    return Triple.of(refranate , refranator , applyingAspect);
  } // getResult()
}
