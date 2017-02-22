/**
 * @author smallufo
 * Created on 2007/3/13 at 上午 5:15:04
 */
package destiny.core.calendar;

import java.time.LocalDateTime;

/**
 * 將 LMT 以及經度 轉換為當地真正的時間 , 不包含真太陽時(均時差) 的校正
 */
public class LongitudeTimeBean {

  /**
   * 將 LMT 以及經度 轉換為當地真正的時間 , 不包含真太陽時(均時差) 的校正
   * @param lmt
   * @param location
   * @return 經度時間
   */
  public static LocalDateTime getLocalTime(LocalDateTime lmt, Location location) {
    double absLong = Math.abs(location.getLongitude());
    double secondsOffset = DstUtils.getDstSecondOffset(lmt, location).getRight();
    double zoneSecondOffset = Math.abs(secondsOffset);
    double longitudeSecondOffset = absLong * 4 * 60; // 經度與GMT的時差 (秒) , 一分鐘四度

    if (location.isEast()) {
      double seconds = longitudeSecondOffset - zoneSecondOffset;
      long secsPart = (long) (seconds);
      long nanoPart = (long) ((seconds - secsPart) * 1_000_000_000);
      return LocalDateTime.from(lmt).plusSeconds(secsPart).plusNanos(nanoPart);
    } else {
      double seconds = zoneSecondOffset - longitudeSecondOffset;
      long secsPart = (long) (seconds);
      long nanoPart = (long) ((seconds - secsPart) * 1_000_000_000);
      return LocalDateTime.from(lmt).plusSeconds(secsPart).plusNanos(nanoPart);
    }
  }

}
