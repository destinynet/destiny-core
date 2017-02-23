/**
 * Created by smallufo on 2017-02-23.
 */
package destiny.core.calendar;

import destiny.tools.ColorCanvas.AlignUtil;
import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LdtDecoratorChinese implements Decorator<LocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuffer sb = new StringBuffer();
    sb.append("西元");
    if (time.getYear() <= 0)
      sb.append("前" );
    else
      sb.append("　");
    sb.append(alignRight(time.getYear(), 4)).append("年");
    sb.append(time.getMonthValue() < 10 ? "0" : "").append(time.getMonthValue()).append("月");
    sb.append(time.getDayOfMonth() < 10 ? "0" : "").append(time.getDayOfMonth()).append("日");
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

  public static String alignRight(int value , int width) {
    StringBuffer sb = new StringBuffer(String.valueOf(value));
    int valueLength = sb.length();

    return AlignUtil.outputStringBuffer(valueLength , width , sb);
  }
}
