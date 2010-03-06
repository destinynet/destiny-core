/**
 * @author smallufo 
 * Created on 2009/3/10 at 上午 1:42:35
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class YearDecoratorJapanese implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer year)
  {
    return "西暦年" + year + "年";
  }
}
