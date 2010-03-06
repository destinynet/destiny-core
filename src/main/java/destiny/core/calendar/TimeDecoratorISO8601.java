/**
 * @author smallufo 
 * Created on 2008/8/12 at 上午 5:20:07
 */ 
package destiny.core.calendar;

import java.io.Serializable;

import destiny.utils.Decorator;

public class TimeDecoratorISO8601 implements Decorator<Time> , Serializable
{
  public TimeDecoratorISO8601()
  {
  }

  /** 
   * 格式 : 2001-01-02T01:02:03
   */
  @Override
  public String getOutputString(Time time)
  {
    return time.getYear()+"-"
    +(time.getMonth()<10?"0":"")+time.getMonth() +"-"
    +(time.getDay()<10?"0":"")+time.getDay()
    +"T"
    +(time.getHour()<10?"0":"")+time.getHour()+":"
    +(time.getMinute()<10?"0":"")+time.getMinute()+":"
    +(time.getSecond()<10?"0":"")+time.getSecond();
  }

}
