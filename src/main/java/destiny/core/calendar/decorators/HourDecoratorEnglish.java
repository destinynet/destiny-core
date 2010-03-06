/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:47:02
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class HourDecoratorEnglish implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer hour)
  {
    return hour.toString();
  }

}
