/**
 * @author smallufo 
 * Created on 2008/1/17 at 上午 1:20:43
 */ 
package destiny.utils;

public interface Decorator<T>
{
  public String getOutputString(T t);
}
