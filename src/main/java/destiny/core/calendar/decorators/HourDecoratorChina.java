/**
 * @author smallufo 
 * Created on 2008/4/9 at �W�� 11:48:49
 */ 
package destiny.core.calendar.decorators;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class HourDecoratorChina implements Decorator<Integer> , Serializable
{
  @NotNull
  @Override
  public String getOutputString(@NotNull Integer hour)
  {
    return hour.toString()+"时";
  }
}
