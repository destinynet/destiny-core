/*
 * @author smallufo
 * @date 2004/10/22
 * @time 下午 10:28:37
 */
package destiny.astrology;

import destiny.core.calendar.JulianDateTime;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

import java.time.LocalDateTime;

/**
 * 取得星體的位置。支援 Planet , Asteroid , Hamburger <br/>
 * Swiss Ephemeris 的實作是 StarPositionImpl <br/>
 * 原廠還支援 月亮的 Apsis , 但我使用 ApsisIF 來實作
 */
public interface StarPositionIF {

  /** 設定觀測地點，對於 {@link Centric#TOPO} 有用 . 2017-07-03 註記 : 此 method 無法移除*/
  void setLocation(Location location);

  Position getPosition(Star star, double gmtJulDay , Centric centric , Coordinate coordinate);

  /** 同樣是求 Position , 但多傳入地點、溫度、壓力 等資料 , 在此直接 discard 掉 */
  default Position getPosition(Star star, double gmtJulDay, Location location, double temperature, double pressure, Centric centric, Coordinate coordinate) {
    return getPosition(star , gmtJulDay , centric , coordinate);
  }

  /**
   * @param gmt GMT 的 Gregorian 時刻
   */
  default Position getPosition(Star star, LocalDateTime gmt , Centric centric , Coordinate coordinate){
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getPosition(star , gmtJulDay , centric , coordinate);
  }

  default Position getPosition(Star star, LocalDateTime gmt , Centric centric , Coordinate coordinate , Location location , double temperature , double pressure){
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getPosition(star , gmtJulDay , location , temperature , pressure , centric , coordinate);
  }


  /**
   * @param gmt GMT 的 Julian 時刻
   */
  default Position getPosition(Star star, JulianDateTime gmt , Centric centric , Coordinate coordinate){
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getPosition(star , gmtJulDay , centric , coordinate);
  }

  /** 取得星體的位置 , 包含當地時間 (LMT) 以及座標 */
  default Position getPosition(Star star, LocalDateTime lmt, Location location , Centric centric , Coordinate coordinate) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    return getPosition(star , gmt , centric , coordinate , location , 0 , 0);
  }

}
