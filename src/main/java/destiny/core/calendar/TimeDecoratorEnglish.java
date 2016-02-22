/**
 * @author smallufo 
 * Created on 2008/1/16 at 下午 11:24:26
 */ 
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class TimeDecoratorEnglish implements Decorator<Time> , Serializable
{

  @NotNull
  @Override
  public String getOutputString(@NotNull Time time)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(time.getYear() );
    if(time.isAd())
      sb.append("AD");
    else
      sb.append("BC");
    sb.append(" ");
    
    sb.append(time.getMonth() < 10 ? "0" : "").append(time.getMonth());
    sb.append("/");
    sb.append(time.getDay() < 10 ? "0" : "").append(time.getDay());
    sb.append(" ");
    
    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour());
    sb.append(":");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute());
    sb.append(":");
    if (time.getSecond() - (int)time.getSecond() ==0)
    {
      //整數
      if (time.getSecond() < 10)
        sb.append("0").append(String.valueOf(time.getSecond()).substring(0, 1)).append(".00");
      else
        sb.append(String.valueOf(time.getSecond()).substring(0, 2)).append(".00");
    }
    else
    {
      //有小數
      if (time.getSecond() < 10)
      {
        if (String.valueOf(time.getSecond()).length() >= 4)
          sb.append("0").append(String.valueOf(time.getSecond()).substring(0, 4));
        else
          sb.append("0").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於3
      }
      else
      {
        if (String.valueOf(time.getSecond()).length() >= 5)
          sb.append(String.valueOf(time.getSecond()).substring(0,5));
        else
          sb.append(String.valueOf(time.getSecond())).append("0"); //長度一定等於4
      }
    }
    
    
    return sb.toString();
  }

}
