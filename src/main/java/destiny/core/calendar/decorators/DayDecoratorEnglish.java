/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:26:11
 */
package destiny.core.calendar.decorators;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class DayDecoratorEnglish implements Decorator<Integer>, Serializable {

  @NotNull
  @Override
  public String getOutputString(@NotNull Integer day) {
    return day.toString();
  }

}
