/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 11:48:56
 */
package destiny.astrology.beans;

import destiny.astrology.TrueSolarTimeIF;
import destiny.core.calendar.Time;


public class TrueSolarTermsBean
{
  private boolean isPositive;
  private int minute;
  private double second;

  private TrueSolarTimeIF trueSolarTime;
  
  public TrueSolarTermsBean(Time gmtTime , TrueSolarTimeIF impl)
  {
    this.trueSolarTime = impl;
    
    double resultSeconds = trueSolarTime.getTrueSolarTimeInSecond(gmtTime);
    
    double doubleMinute = Math.abs(resultSeconds/60.0);
    
    this.isPositive = ( resultSeconds >=0 ) ? true : false;
    this.second = (doubleMinute - (int)doubleMinute )*60;
    this.minute = Math.abs((int) doubleMinute);
    
  }

  public boolean isPositive() {
    return isPositive;
  }

  public int getMinute() {
    return minute;
  }

  public double getSecond() {
    return second;
  }

}
