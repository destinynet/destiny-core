/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 取得當下、當地的「行星時」 Planetary Hour
 * 參考資料
 *
 * http://pansci.asia/archives/126644
 *
 * http://www.astrology.com.tr/planetary-hours.asp
 * 晝夜、分別劃分 12等分
 */
public interface IPlanetaryHour {

  Planet getPlanetaryHour(double gmtJulDay , Location loc);

  default Planet getPlanetaryHour(ChronoLocalDateTime lmt, Location loc) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , loc);
    return getPlanetaryHour(gmtJulDay , loc);
  }

}
