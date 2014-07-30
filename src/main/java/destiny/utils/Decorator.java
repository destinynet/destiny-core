/**
 * @author smallufo 
 * Created on 2008/1/17 at 上午 1:20:43
 */ 
package destiny.utils;

import org.jetbrains.annotations.Nullable;

public interface Decorator<T>
{
  @Nullable
  public String getOutputString(T t);
}
