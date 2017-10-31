/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.astrology;

import destiny.core.calendar.Location;

import java.io.Serializable;

import static destiny.astrology.Planet.SUN;
import static destiny.astrology.TransPoint.*;

/**
 * TODO 實作 http://www.astrology.com.tr/planetary-hours.asp
 * 晝夜、分別劃分 12等分
 */
public class PlanetaryHourImpl implements IPlanetaryHour , Serializable {

  private final IRiseTrans riseTransImpl;

  public PlanetaryHourImpl(IRiseTrans riseTransImpl) {this.riseTransImpl = riseTransImpl;}

  @Override
  public Planet getPlanetaryHour(double gmtJulDay, Location loc) {
    double nextRising = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, RISING, loc);
    double nextSetting = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, SETTING, loc);
    if (nextRising < nextSetting) {
      // 目前是黑夜
      // 先計算「接近上一個中午」的時刻，這裡不用算得很精準
      double nearPrevMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, MERIDIAN, loc) - 1; // 記得減一
      // 接著，計算「上一個」日落時刻
      double prevSetting = riseTransImpl.getGmtTransJulDay(nearPrevMeridian, SUN, SETTING, loc);

      double avgHour = (nextRising - prevSetting) / 12.0;
    }
    else {
      // 目前是白天
      // 先計算「接近上一個子正的時刻」，這禮不用算得很經準
      double nearPrevMidNight = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, NADIR, loc) - 1; // 記得減一
      // 接著，計算「上一個」日出時刻
      double prevRising = riseTransImpl.getGmtTransJulDay(nearPrevMidNight , SUN , RISING , loc);

      double avgHour = (nextSetting - prevRising) / 12.0;
    }
    return null;
  }
}
