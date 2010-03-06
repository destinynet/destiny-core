/**
 * @author smallufo 
 * Created on 2008/4/9 at 下午 12:20:57
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class YearDecoratorTaiwan implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer year)
  {
    if (year-1911 >=1)
      return "西元" + year + "年 (民國" + (year-1911)+"年)";
    else
      //民國前
      return "西元" + year + "年 (民國前" + (1912-year)+"年)";
  }
}
