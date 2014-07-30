/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:26:11
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;
import org.jetbrains.annotations.NotNull;

public class DayDecoratorEnglish implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(@NotNull Integer day)
  {
    return day.toString();
  }

}
