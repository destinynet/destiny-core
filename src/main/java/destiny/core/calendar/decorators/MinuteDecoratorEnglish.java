/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:54:41
 */ 
package destiny.core.calendar.decorators;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class MinuteDecoratorEnglish  implements Decorator<Integer> , Serializable
{
  @NotNull
  @Override
  public String getOutputString(@NotNull Integer minute)
  {
    return minute.toString();
  }
}
