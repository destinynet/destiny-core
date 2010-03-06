/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:27:42
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class DayDecoratorChinese implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer day)
  {
    return day.toString()+"日";
  }

}
