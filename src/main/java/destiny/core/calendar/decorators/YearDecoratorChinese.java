/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:02:50
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

public class YearDecoratorChinese implements Decorator<Integer> , Serializable
{

  @NotNull
  @Override
  public String getOutputString(@NotNull Integer year)
  {
    return "西元" + year.toString() + "年";
  }

}
