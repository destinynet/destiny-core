/**
 * @author smallufo
 * Created on 2010/10/10 at 下午8:07:02
 */
package destiny.test;

import org.jetbrains.annotations.NotNull;

public class Sum implements ISum
{
  private Integer value1;
  private Integer value2;

  public Sum(Integer val1, Integer val2)
  {
    value1 = val1;
    value2 = val2;
  }

  @NotNull
  public Integer sum()
  {
    return value1.intValue() + value2.intValue();
  }
}
