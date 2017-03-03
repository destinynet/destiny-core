/*
 * @author smallufo
 * @date 2004/12/6
 * @time 上午 11:23:37
 */
package destiny.core.calendar.eightwords;

import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/** 定義「子正」的介面，是要以當地手錶 0時 為子正，亦或是太陽過當地天底 ... 或是其他實作 */
public interface MidnightIF extends Descriptive {

  /** 取得下一個「子正」的 GMT 時刻 */
  double getNextMidnight(double gmtJulDay , @NotNull Location loc);

  /** 取得下一個「子正」的 LMT 時刻 */
  default LocalDateTime getNextMidnight(LocalDateTime lmt , Location loc) {
    LocalDateTime gmtLdt = Time.getGmtFromLmt(lmt , loc);
    double gmtJulDay = Time.getGmtJulDay(gmtLdt);
    double gmtResult = getNextMidnight(gmtJulDay , loc);
    LocalDateTime gmt = new Time(gmtResult).toLocalDateTime();
    return Time.getLmtFromGmt(gmt , loc);
  }
}
