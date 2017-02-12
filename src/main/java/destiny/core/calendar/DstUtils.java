/** 2009/11/28 下午6:03:01 by smallufo */
package destiny.core.calendar;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DstUtils implements Serializable {

  /** 確認此時刻，是否有DST。不論是否有沒有DST，都傳回與GMT誤差幾秒 */
  @NotNull
  public static Pair<Boolean, Double> getDstSecondOffset(@NotNull Time lmt, @NotNull Location loc) {
    TimeZone tz = loc.getTimeZone();
    GregorianCalendar cal = new GregorianCalendar(lmt.getYear(), lmt.getMonth() - 1, lmt.getDay(), lmt.getHour(), lmt.getMinute(), (int) lmt.getSecond());
    boolean dst = tz.inDaylightTime(cal.getTime());
    double secondOffset = tz.getOffset(cal.getTimeInMillis()) / 1000;
    return ImmutablePair.of(dst, secondOffset);
  }

  @NotNull
  public static Pair<Boolean, Double> getDstSecondOffset(@NotNull LocalDateTime lmt, @NotNull Location loc) {
    TimeZone tz = loc.getTimeZone();
    GregorianCalendar cal = new GregorianCalendar(lmt.getYear(), lmt.getMonthValue() - 1, lmt.getDayOfMonth(), lmt.getHour(), lmt.getMinute(), (int) lmt.getSecond());
    boolean dst = tz.inDaylightTime(cal.getTime());
    double secondOffset = tz.getOffset(cal.getTimeInMillis()) / 1000;
    return ImmutablePair.of(dst, secondOffset);
  }
}

