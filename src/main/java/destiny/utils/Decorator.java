/**
 * @author smallufo 
 * Created on 2008/1/17 at 上午 1:20:43
 */ 
package destiny.utils;

import org.jetbrains.annotations.NotNull;

public interface Decorator<T>
{
  @NotNull
  public String getOutputString(T t);
}
