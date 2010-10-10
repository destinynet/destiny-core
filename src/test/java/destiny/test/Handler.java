/**
 * @author smallufo
 * Created on 2010/10/10 at 下午8:07:45
 */
package destiny.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Handler implements InvocationHandler
{

  public Sum trueSum;

  public Handler(Sum sum)
  {
    this.trueSum = sum;
  }

  @Override
  public Object invoke(Object obj, Method method, Object[] args) throws Throwable
  {
    System.out.println("obj class = " + obj.getClass().getName());
    try
    {
      return method.invoke(trueSum, args);
    }
    catch (Exception e)
    {
      //e.printStackTrace();
      return new Integer(0);
    }
  }
}
