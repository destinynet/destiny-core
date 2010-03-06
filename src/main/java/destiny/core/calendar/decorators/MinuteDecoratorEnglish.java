/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:54:41
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class MinuteDecoratorEnglish  implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer minute)
  {
    return minute.toString();
  }
}
