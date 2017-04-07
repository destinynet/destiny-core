/**
 * @author smallufo
 * Created on 2007/3/13 at 上午 5:15:04
 */
package destiny.core.calendar;

import org.apache.commons.lang3.tuple.Pair;

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
    double absLng = Math.abs(location.getLongitude());
    double secondsOffset = Time.getDstSecondOffset(lmt, location).v2();
    double zoneSecondOffset = Math.abs(secondsOffset);
    double longitudeSecondOffset = absLng * 4 * 60; // 經度與GMT的時差 (秒) , 一分鐘四度

    if (location.isEast()) {
      double seconds = longitudeSecondOffset - zoneSecondOffset;
      Pair<Long , Long> pair = Time.splitSecond(seconds);
      return LocalDateTime.from(lmt).plusSeconds(pair.getLeft()).plusNanos(pair.getRight());
    } else {
      double seconds = zoneSecondOffset - longitudeSecondOffset;
      Pair<Long , Long> pair = Time.splitSecond(seconds);
      return LocalDateTime.from(lmt).plusSeconds(pair.getLeft()).plusNanos(pair.getRight());
    }
  }

}
