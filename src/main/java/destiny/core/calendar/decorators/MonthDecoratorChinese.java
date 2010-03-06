/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:13:38
 */ 
package destiny.core.calendar.decorators;

import java.io.Serializable;

import destiny.utils.Decorator;

public class MonthDecoratorChinese implements Decorator<Integer> , Serializable
{
  @Override
  public String getOutputString(Integer month)
  {
    switch(month.intValue())
    {
      case  1 : return "一月";
      case  2 : return "二月";
      case  3 : return "三月";
      case  4 : return "四月";
      case  5 : return "五月";
      case  6 : return "六月";
      case  7 : return "七月";
      case  8 : return "八月";
      case  9 : return "九月";
      case 10 : return "十月";
      case 11 : return "十一月";
      case 12 : return "十二月";
    }
    return null;
  }

}
