/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:55:39
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

public class MinuteDecoratorChinese implements Decorator<Integer> , Serializable
{
  @NotNull
  @Override
  public String getOutputString(@NotNull Integer minute)
  {
    return minute.toString()+"分";
  }

}
