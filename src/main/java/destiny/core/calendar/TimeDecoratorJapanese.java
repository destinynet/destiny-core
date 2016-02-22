/**
 * @author smallufo 
 * Created on 2009/3/10 at 上午 1:34:25
 */ 
package destiny.core.calendar;

import java.io.Serializable;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

public class TimeDecoratorJapanese implements Decorator<Time> , Serializable
{
  public TimeDecoratorJapanese()
  {
  }

  @NotNull
  @Override
  public String getOutputString(@NotNull Time time)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("西暦生年");
    if (!time.isAd())
      sb.append("前" );
    else
      sb.append("　");
    sb.append(TimeDecoratorChinese.alignRight(time.getYear(), 4)).append("年");
    sb.append(time.getMonth() < 10 ? "0" : "").append(time.getMonth()).append("月");
    sb.append(time.getDay() < 10 ? "0" : "").append(time.getDay()).append("日");
    sb.append("　");
    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour()).append("時");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute()).append("分");
    if (time.getSecond() - (int)time.getSecond() ==0)
    {
      //整數
      if (time.getSecond() < 10)
        sb.append(" 0").append(String.valueOf(time.getSecond()).substring(0, 1)).append(".00");
      else
        sb.append(" ").append(String.valueOf(time.getSecond()).substring(0, 2)).append(".00");
    }
    else
    {
      //有小數
      if (time.getSecond() < 10)
      {
        if (String.valueOf(time.getSecond()).length() >= 4)
          sb.append(" 0").append(String.valueOf(time.getSecond()).substring(0, 4));
        else
          sb.append(" 0").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於3
      }
      else
      {
        if (String.valueOf(time.getSecond()).length() >= 5)
          sb.append(" ").append(String.valueOf(time.getSecond()).substring(0, 5));
        else
          sb.append(" ").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於4
      }
    }
    sb.append("秒");
    return sb.toString();
  }

}
