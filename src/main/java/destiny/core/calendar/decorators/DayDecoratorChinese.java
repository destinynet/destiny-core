/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:27:42
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;
import org.jetbrains.annotations.NotNull;

public class DayDecoratorChinese implements Decorator<Integer> , Serializable
{
  @NotNull
  @Override
  public String getOutputString(@NotNull Integer day)
  {
    return day.toString()+"日";
  }

}
