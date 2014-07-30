/**
 * @author smallufo
 * Created on 2010/10/10 at 下午8:08:15
 */
package destiny.test;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Proxy;

/**
 * 參考資料
 * http://www.javalobby.org/java/forums/t18631.html
 */
public class SumFactory
{
  @NotNull
  @SuppressWarnings("rawtypes")
  public ISum createSum(Integer val1, Integer val2)
  {
    Sum sum = new Sum(val1, val2);
    Handler handler = new Handler(sum);
    Class[] interfacesArray = new Class[] { ISum.class };

    return (ISum) Proxy.newProxyInstance(Sum.class.getClassLoader(), interfacesArray, handler);
  }
}