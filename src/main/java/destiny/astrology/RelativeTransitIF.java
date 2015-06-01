/*
 * @author smallufo
 * @date 2004/10/29
 * @time 下午 09:57:05
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * 計算兩星呈現某交角的時間
 * Swiss ephemeris 的實作是 RelativeTransitImpl
 * </pre> 
 */
public interface RelativeTransitIF
{
  /**
   * <pre>
   * 計算兩星下一個/上一個交角。
   * 注意！angle 有方向性，如果算相刑的角度，別忘了加上 270度
   * TODO : ，目前 RelativeTransitImpl 僅支援 Planet 以及 Asteroid
   * 傳回的 Time 是 GMT
   * </pre>
   */
  Time getRelativeTransit(Star transitStar , Star relativeStar , double angle , Time startGmtTime , boolean isForward);

  /**
   * 從 fromGmt 到 toGmt 之間，transitStar 對 relativeStar 形成 angle 交角的時間
   * @return List < Map < angle , Time > >
   * 傳回的是 GMT 時刻
   */
  @NotNull
  default List<Time> getPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , @NotNull Time fromGmt, @NotNull Time toGmt, double angle) {
    List<Time> resultList = new ArrayList<>();
    while (fromGmt.isBefore(toGmt))
    {
      fromGmt = getRelativeTransit( transitStar , relativeStar , angle , fromGmt, true );
      if (!fromGmt.isBefore(toGmt))
        break;
      resultList.add(fromGmt);
      fromGmt = new Time(  (fromGmt.getGmtJulDay() + 0.000001 ) );
    }
    return resultList;
  }

  /** 承上，此為計算 LMT , 回傳也是 LMT */
  @NotNull
  default List<Time> getLocalPeriodRelativeTransitTimes(Star transitStar , Star relativeStar , @NotNull Time fromLmt, @NotNull Time toLmt, Location location , double angle) {
    Time fromGmt = Time.getGMTfromLMT(fromLmt , location );
    Time   toGmt = Time.getGMTfromLMT(toLmt , location);
    return getPeriodRelativeTransitTimes(transitStar , relativeStar , fromGmt , toGmt , angle).stream().map(
      gmt -> Time.getLMTfromGMT(gmt , location)
    ).collect(Collectors.toList());
  }


  /**
   * 求出 fromStar 下一次/上一次 與 relativeStar 形成 angles[] 的角度 , 最近的是哪一次
   */
  default Time getNearestRelativeTransitTime(Star transitStar , Star relativeStar , Time fromGmtTime , Collection<Double> angles , boolean isForward ) {
    Time resultTime = null;
    /**
     * 相交 270 度也算 90 度
     * 相交 240 度也是 120 度
     * 所以要重算一次角度
     */
    Set<Double> realAngles = new HashSet<>();
    for (double angle : angles) {
      realAngles.add(angle);
      if (angle != 0)
        realAngles.add(360 - angle);
    }
    for(double angle : realAngles)
    {
      Time temp = null;
      try
      {
        temp = getRelativeTransit(transitStar, relativeStar, angle, fromGmtTime, isForward);
        //System.out.println("推算" + transitStar + " 與 " + relativeStar + " 的角度 : " + angle + " 得到 " + result);
      }
      catch(RuntimeException ignored)
      {
      }

      if (resultTime == null)
        resultTime = temp;
      else
      {
        if (temp != null)
        {
          //目前已經有一個結果，比較看看現在算出的，和之前的，哪個比較近
          if (isForward)
          {
            //順推
            if (temp.isBefore(resultTime))
              resultTime = temp;
          }
          else
          {
            //逆推
            if (temp.isAfter(resultTime))
              resultTime = temp;
          }
        }
      }
    }
    //System.out.println("計算 " + transitStar + " 與 " + relativeStar + (isForward ? "正推":"逆推") + " 形成 " + realAngles + " 的時間，得到 " + resultTime);
    return resultTime;
  }



}
