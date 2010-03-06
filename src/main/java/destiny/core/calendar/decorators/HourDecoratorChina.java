/**
 * @author smallufo 
 * Created on 2008/4/9 at �W�� 11:48:49
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class HourDecoratorChina implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer hour)
  {
    return hour.toString()+"时";
  }
}
