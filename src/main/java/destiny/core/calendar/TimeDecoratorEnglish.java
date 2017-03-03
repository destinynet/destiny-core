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

public class TimeDecoratorEnglish implements Decorator<LocalDateTime>, Serializable {

  @NotNull
  @Override
  public String getOutputString(LocalDateTime time) {
    StringBuilder sb = new StringBuilder();
    sb.append(time.get(YEAR_OF_ERA) );
    if(time.toLocalDate().getEra() == IsoEra.CE)
      sb.append("AD");
    else
      sb.append("BC");
    sb.append(" ");

    sb.append(time.getMonthValue() < 10 ? "0" : "").append(time.getMonthValue());
    sb.append("/");
    sb.append(time.getDayOfMonth() < 10 ? "0" : "").append(time.getDayOfMonth());
    sb.append(" ");

    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour());
    sb.append(":");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute());
    sb.append(":");

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


//    if (time.getSecond() - (int)time.getSecond() ==0)
//    {
//      //整數
//      if (time.getSecond() < 10)
//        sb.append("0").append(String.valueOf(time.getSecond()).substring(0, 1)).append(".00");
//      else
//        sb.append(String.valueOf(time.getSecond()).substring(0, 2)).append(".00");
//    }
//    else
//    {
//      //有小數
//      if (time.getSecond() < 10)
//      {
//        if (String.valueOf(time.getSecond()).length() >= 4)
//          sb.append("0").append(String.valueOf(time.getSecond()).substring(0, 4));
//        else
//          sb.append("0").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於3
//      }
//      else
//      {
//        if (String.valueOf(time.getSecond()).length() >= 5)
//          sb.append(String.valueOf(time.getSecond()).substring(0,5));
//        else
//          sb.append(String.valueOf(time.getSecond())).append("0"); //長度一定等於4
//      }
//    }


    return sb.toString();
  }
}
