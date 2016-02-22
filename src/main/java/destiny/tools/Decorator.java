/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:20:43
 */
package destiny.tools;

import org.jetbrains.annotations.NotNull;

public interface Decorator<T> {

  @NotNull
  String getOutputString(T t);
}
