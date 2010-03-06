/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 10:38:03
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class YearDecoratorEnglish implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer year)
  {
    return year.toString();
  }

}
