/**
 * @author smallufo 
 * Created on 2008/4/9 at 上午 11:09:42
 */ 
package destiny.core.calendar.decorators;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class MonthDecoratorEnglish implements Decorator<Integer> , Serializable
{
  @NotNull
  @Override
  public String getOutputString(@NotNull Integer month)
  {
    switch(month)
    {
      case  1 : return "January";
      case  2 : return "February";
      case  3 : return "March";
      case  4 : return "April";
      case  5 : return "May";
      case  6 : return "June";
      case  7 : return "July";
      case  8 : return "August";
      case  9 : return "September";
      case 10 : return "October";
      case 11 : return "November";
      case 12 : return "December";
      default : throw new RuntimeException("impossible");
    }
  }

}
