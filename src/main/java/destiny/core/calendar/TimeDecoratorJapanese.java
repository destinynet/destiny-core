/**
 * Created by smallufo on 2017-03-01.
 */
package destiny.core.calendar;

import destiny.tools.Decorator;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.chrono.IsoEra;

import static java.time.temporal.ChronoField.YEAR_OF_ERA;

public class TimeDecoratorJapanese implements Decorator<LocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    sb.append("西暦生年");
    if (time.toLocalDate().getEra() == IsoEra.BCE)
      sb.append("前" );
    else
      sb.append("　");
    sb.append(TimeDecoratorChinese.alignRight(time.get(YEAR_OF_ERA), 4)).append("年");
    sb.append(time.getMonthValue() < 10 ? "0" : "").append(time.getMonthValue()).append("月");
    sb.append(time.getDayOfMonth() < 10 ? "0" : "").append(time.getDayOfMonth()).append("日");
    sb.append("　");
    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour()).append("時");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute()).append("分");

    sb.append(' ');
    if (time.getSecond() < 10) {
      sb.append("0");
    }
    sb.append(time.getSecond());

    if (time.getNano() == 0) {
      sb.append(".00");
    } else {
      sb.append(".");
      sb.append(String.valueOf(time.getNano()).substring(0,2));
    }
    sb.append("秒");
//    if (time.getSecond() - (int)time.getSecond() ==0)
//    {
//      //整數
//      if (time.getSecond() < 10)
//        sb.append(" 0").append(String.valueOf(time.getSecond()).substring(0, 1)).append(".00");
//      else
//        sb.append(" ").append(String.valueOf(time.getSecond()).substring(0, 2)).append(".00");
//    }
//    else
//    {
//      //有小數
//      if (time.getSecond() < 10)
//      {
//        if (String.valueOf(time.getSecond()).length() >= 4)
//          sb.append(" 0").append(String.valueOf(time.getSecond()).substring(0, 4));
//        else
//          sb.append(" 0").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於3
//      }
//      else
//      {
//        if (String.valueOf(time.getSecond()).length() >= 5)
//          sb.append(" ").append(String.valueOf(time.getSecond()).substring(0, 5));
//        else
//          sb.append(" ").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於4
//      }
//    }

    return sb.toString();
  }
}
