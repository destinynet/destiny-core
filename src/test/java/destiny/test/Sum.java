/**
 * @author smallufo
 * Created on 2010/10/10 at 下午8:07:02
 */
package destiny.test;

public class Sum implements ISum
{
  public Integer value1;
  public Integer value2;

  public Sum(Integer val1, Integer val2)
  {
    value1 = val1;
    value2 = val2;
  }

  public Integer sum()
  {
    return new Integer(value1.intValue() + value2.intValue());
  }
}
