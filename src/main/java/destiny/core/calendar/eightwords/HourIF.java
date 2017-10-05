/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:49:08
 */
package destiny.core.calendar.eightwords;

import destiny.core.Descriptive;
import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.chinese.Branch;
import org.jetbrains.annotations.NotNull;

import java.time.chrono.ChronoLocalDateTime;

/** 時辰的分界點實作 , SwissEph 的實作是 HourSolarTransImpl */
public interface HourIF extends Descriptive {

  @NotNull
  Branch getHour(double gmtJulDay , Location location);


  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @return 時辰（只有地支）
   */
  @NotNull
  default Branch getHour(ChronoLocalDateTime lmt , Location location) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , location);
    return getHour(gmtJulDay, location);
  }

  /**
   * @param gmtJulDay GMT 時間
   * @param location 地點
   * @param eb 下一個地支
   * @return 下一個地支的開始時刻
   */
  double getGmtNextStartOf(double gmtJulDay , Location location , Branch eb);


  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @param eb 欲求之下一個地支開始時刻
   * @return 回傳 LMT 時刻
   */
  default ChronoLocalDateTime getLmtNextStartOf(ChronoLocalDateTime lmt , Location location , Branch eb) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , location);
    double resultGmtJulDay = getGmtNextStartOf(gmtJulDay , location , eb);
    ChronoLocalDateTime resultGmt =JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(resultGmtJulDay);
    return TimeTools.getLmtFromGmt(resultGmt , location);
  }


}
