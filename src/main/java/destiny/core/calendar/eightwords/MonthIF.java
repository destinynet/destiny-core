/*
 * @author smallufo
 * @date 2004/11/25
 * @time 下午 07:45:47
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.chinese.StemBranch;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 取得月干支的介面
 */
public interface MonthIF {


  StemBranch getMonth(double gmtJulDay, Location location);

  default StemBranch getMonth(ChronoLocalDateTime lmt, Location loc) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , loc);
    return getMonth(gmtJulDay , loc);
  }

  /**
   * 南半球月支是否對沖 , 內定是 '否'
   */
  void setSouthernHemisphereOpposition(boolean value);
}
