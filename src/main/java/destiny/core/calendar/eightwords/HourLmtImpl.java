/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:51:18
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.chinese.Branch;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.function.Function;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * 最簡單 , 以當地平均時間來區隔時辰 , 兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時
 */
public class HourLmtImpl implements HourIF, Serializable {

  private final Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  @NotNull
  @Override
  public Branch getHour(double gmtJulDay, Location location) {
    ChronoLocalDateTime gmt = revJulDayFunc.apply(gmtJulDay);
    int lmtHour = TimeTools.getLmtFromGmt(gmt , location).get(ChronoField.HOUR_OF_DAY);
    return getHour(lmtHour);
  }

  private Branch getHour(int lmtHour) {
    switch (lmtHour) {
      case 23:
      case 0:
        return Branch.子;
      case 1:
      case 2:
        return Branch.丑;
      case 3:
      case 4:
        return Branch.寅;
      case 5:
      case 6:
        return Branch.卯;
      case 7:
      case 8:
        return Branch.辰;
      case 9:
      case 10:
        return Branch.巳;
      case 11:
      case 12:
        return Branch.午;
      case 13:
      case 14:
        return Branch.未;
      case 15:
      case 16:
        return Branch.申;
      case 17:
      case 18:
        return Branch.酉;
      case 19:
      case 20:
        return Branch.戌;
      case 21:
      case 22:
        return Branch.亥;
    }
    throw new RuntimeException("HourLmtImpl : Cannot find EarthlyBranches for this LMT : " + lmtHour);
  }

  @Override
  public double getGmtNextStartOf(double gmtJulDay, Location location, Branch eb) {

    ChronoLocalDateTime gmt = revJulDayFunc.apply(gmtJulDay);
    ChronoLocalDateTime lmt = TimeTools.getLmtFromGmt(gmt, location);
    ChronoLocalDateTime lmtResult = getLmtNextStartOf(lmt, location, eb , revJulDayFunc);
    ChronoLocalDateTime gmtResult = TimeTools.getGmtFromLmt(lmtResult, location);
    return TimeTools.getGmtJulDay(gmtResult);
  }


  /**
   * 要實作，不然會有一些 round-off 的問題
   */
  @Override
  public ChronoLocalDateTime getLmtNextStartOf(ChronoLocalDateTime lmt, Location location, Branch eb , Function<Double , ChronoLocalDateTime> revJulDayFunc) {

    switch (eb.getIndex()) {
      case 0: //欲求下一個子時時刻
        if (lmt.get(HOUR_OF_DAY) >= 23)
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 23).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.with(HOUR_OF_DAY, 23).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 1: //欲求下一個丑時的時刻
        if (lmt.get(HOUR_OF_DAY) < 1)
          return lmt.with(HOUR_OF_DAY, 1).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 1).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 2: //欲求下一個寅時的時刻
        if (lmt.get(HOUR_OF_DAY) < 3)
          return lmt.with(HOUR_OF_DAY, 3).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 3).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 3: //欲求下一個卯時的時刻
        if (lmt.get(HOUR_OF_DAY) < 5)
          return lmt.with(HOUR_OF_DAY, 5).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 5).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 4: //欲求下一個辰時的時刻
        if (lmt.get(HOUR_OF_DAY) < 7)
          return lmt.with(HOUR_OF_DAY, 7).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 7).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 5: //欲求下一個巳時的時刻
        if (lmt.get(HOUR_OF_DAY) < 9)
          return lmt.with(HOUR_OF_DAY, 9).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 9).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 6: //欲求下一個午時的時刻
        if (lmt.get(HOUR_OF_DAY) < 11)
          return lmt.with(HOUR_OF_DAY, 11).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 11).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 7: //欲求下一個未時的時刻
        if (lmt.get(HOUR_OF_DAY) < 13)
          return lmt.with(HOUR_OF_DAY, 13).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 13).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 8: //欲求下一個申時的時刻
        if (lmt.get(HOUR_OF_DAY) < 15)
          return lmt.with(HOUR_OF_DAY, 15).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 15).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 9: //欲求下一個酉時的時刻
        if (lmt.get(HOUR_OF_DAY) < 17)
          return lmt.with(HOUR_OF_DAY, 17).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 17).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 10: //欲求下一個戌時的時刻
        if (lmt.get(HOUR_OF_DAY) < 19)
          return lmt.with(HOUR_OF_DAY, 19).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 19).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      case 11: //欲求下一個亥時的時刻
        if (lmt.get(HOUR_OF_DAY) < 21)
          return lmt.with(HOUR_OF_DAY, 21).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
        else
          return lmt.plus(1, DAYS).with(HOUR_OF_DAY, 21).with(MINUTE_OF_HOUR, 0).with(SECOND_OF_MINUTE, 0).with(NANO_OF_SECOND, 0);
      default:
        throw new RuntimeException("Cannot get next start time of " + eb + " , LMT = " + lmt);
    }
  }


  @NotNull
  public String getTitle(Locale locale) {
    return "以當地標準鐘錶時間區隔時辰";
  }


  @NotNull
  public String getDescription(Locale locale) {
    return "兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時";
  }

}
