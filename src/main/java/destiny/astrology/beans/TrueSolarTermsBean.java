/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 11:48:56
 */
package destiny.astrology.beans;

import destiny.astrology.TrueSolarTimeIF;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;


public class TrueSolarTermsBean
{
  private boolean isPositive;
  private int minute;
  private double second;

  public TrueSolarTermsBean(Time gmtTime , @NotNull TrueSolarTimeIF trueSolarTimeImpl)
  {
    double resultSeconds = trueSolarTimeImpl.getTrueSolarTimeInSecond(gmtTime);
    
    double doubleMinute = Math.abs(resultSeconds/60.0);
    
    this.isPositive = (resultSeconds >= 0);
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
