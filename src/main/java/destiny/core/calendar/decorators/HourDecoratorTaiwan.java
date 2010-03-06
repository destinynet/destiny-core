/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:48:03
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class HourDecoratorTaiwan implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer hour)
  {
    return hour.toString()+"時";
  }

}
