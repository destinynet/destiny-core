/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 10:10:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.LongitudeTimeBean;
import destiny.core.calendar.Time;

/**
 * 真太陽時計算介面 <br/>
 * Swiss Ephemeris 實作是 TrueSolarTimeImpl
 */
public interface TrueSolarTimeIF {

  /**
   * E : Equation of Time
   * E = LAT - LMT
   * 均時差 = 真太陽時 - LMT
   * 真太陽時 = LMT + 均時差
   */
  double getEquationSecs(Time gmtTime);

  /** 取得 LMT 時刻所對應的 真太陽時 */
  default Time getTrueSolarTime(Time lmt , Location location) {
    Time gmt = Time.getGMTfromLMT(lmt , location);
    double e = getEquationSecs(gmt);

    return LongitudeTimeBean.getLocalTime(Time.getLMTfromGMT(new Time(gmt , e) , location) , location);
  }

}
