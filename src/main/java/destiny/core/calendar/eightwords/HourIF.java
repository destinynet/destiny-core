/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:49:08
 */
package destiny.core.calendar.eightwords;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.Branch;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/** 時辰的分界點實作 , SwissEph 的實作是 HourSolarTransImpl */
public interface HourIF extends Descriptive {

  @NotNull
  Branch getHour(double gmtJulDay , Location location);

  @NotNull
  default Branch getHour(LocalDateTime lmt , Location location) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getHour(gmtJulDay, location);
  }

  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @return 時辰（只有地支）
   */
  @NotNull
  default Branch getHour(Time lmt , Location location) {
    double gmtJulDay = Time.getGMTfromLMT(lmt , location).getGmtJulDay();
    return getHour(gmtJulDay , location);
  }


  /**
   * @param gmtJulDay GMT 時間
   * @param location 地點
   * @param eb 下一個地支
   * @return 下一個地支的開始時刻
   */
  double getGmtNextStartOf(double gmtJulDay , Location location , Branch eb);

  /**
   * @return 回傳 LMT 時刻
   */
  default LocalDateTime getLmtNextStartOf(LocalDateTime lmt , Location location , Branch eb) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double gmtJulDay = Time.getGmtJulDay(gmt);
    double resultGmtJulDay = getGmtNextStartOf(gmtJulDay , location , eb);
    Time resultGmtTime = new Time(resultGmtJulDay);
    return Time.getLMTfromGMT(resultGmtTime , location).toLocalDateTime();
  }

  /**
   * @param lmt 傳入當地手錶時間
   * @param location 當地的經緯度等資料
   * @param eb 欲求之下一個地支開始時刻
   * @return 下一個時辰開始的時刻
   */
  @NotNull
  default Time getLmtNextStartOf(Time lmt , Location location , Branch eb) {
    double gmtJulDay = Time.getGMTfromLMT(lmt , location).getGmtJulDay();
    double gmtResult = getGmtNextStartOf(gmtJulDay , location , eb);
    Time gmtTime = new Time(gmtResult);
    return Time.getLMTfromGMT(gmtTime , location);
  }
  
}
