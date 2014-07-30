/**
 * @author smallufo 
 * Created on 2008/4/9 at �W�� 11:48:49
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;
import org.jetbrains.annotations.NotNull;

public class HourDecoratorChina implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(@NotNull Integer hour)
  {
    return hour.toString()+"时";
  }
}
