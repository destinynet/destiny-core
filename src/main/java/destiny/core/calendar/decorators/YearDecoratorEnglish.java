/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 10:38:03
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

public class YearDecoratorEnglish implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(@NotNull Integer year)
  {
    return year.toString();
  }

}
