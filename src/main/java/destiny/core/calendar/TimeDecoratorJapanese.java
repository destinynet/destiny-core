/**
 * @author smallufo 
 * Created on 2009/3/10 at 上午 1:34:25
 */ 
package destiny.core.calendar;

import java.io.Serializable;

import destiny.utils.Decorator;

public class TimeDecoratorJapanese implements Decorator<Time> , Serializable
{
  public TimeDecoratorJapanese()
  {
  }

  @Override
  public String getOutputString(Time time)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("西暦生年");
    if (time.isAd() == false)
      sb.append("前" );
    else
      sb.append("　");
    sb.append(TimeDecoratorChinese.alignRight(time.getYear() , 4) +"年");
    sb.append((time.getMonth() < 10 ? "0" : "" )+ time.getMonth()+"月");
    sb.append((time.getDay() < 10 ? "0" : "")+time.getDay()+"日");
    sb.append("　");
    sb.append((time.getHour() < 10 ? "0" : "") +time.getHour()+"時");
    sb.append((time.getMinute() < 10 ? "0" : "" ) + time.getMinute()+"分");
    if (time.getSecond() - (int)time.getSecond() ==0)
    {
      //整數
      if (time.getSecond() < 10)
        sb.append(" 0" + String.valueOf(time.getSecond()).substring(0,1) + ".00");
      else
        sb.append(" " + String.valueOf(time.getSecond()).substring(0,2)+".00");
    }
    else
    {
      //有小數
      if (time.getSecond() < 10)
      {
        if (String.valueOf(time.getSecond()).length() >= 4)
          sb.append(" 0" + String.valueOf(time.getSecond()).substring(0,4));
        else
          sb.append(" 0" + String.valueOf(time.getSecond()) + "0"); //長度一定等於3
      }
      else
      {
        if (String.valueOf(time.getSecond()).length() >= 5)
          sb.append(" " + String.valueOf(time.getSecond()).substring(0,5));
        else
          sb.append(" " + String.valueOf(time.getSecond()) + "0"); //長度一定等於4
      }
    }
    sb.append("秒");
    return sb.toString();
  }

}
