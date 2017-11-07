/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Optional;

/** 計算日食、月食的介面 */
public interface IEclipse {


  /** 從此時之後，全球各地的日食資料 */
  AbstractEclipse nextSolarEclipse(double fromGmtJulDay , boolean forward);

  /** 從此之後 , 此地點下次發生日食的資訊為何 */
  void nextSolarEclipse(double fromGmtJulDay , double lng , double lat , double alt , boolean forward);

  default void nextSolarEclipse(double fromGmtJulDay , Location loc , boolean forward) {
    nextSolarEclipse(fromGmtJulDay , loc.getLongitude() , loc.getLatitude() , loc.getAltitudeMeter() , forward);
  }

  /** 此時此刻，哪裡有發生日食，其「中線」經過哪裡 . 此 method 專門計算「中線在哪裡」 */
  Optional<EclipseCenterInfo> getEclipseCenterInfo(double gmtJulDay);

  /** 若當下 gmtJulDay 有日食，傳出此地點觀測此日食的相關資料 */
  Optional<EclipseObservation> getEclipseObservation(double gmtJulDay , double lng , double lat , double alt);

  default Optional<EclipseObservation> getEclipseObservation(ChronoLocalDateTime gmt, double lng , double lat , double alt) {
    return getEclipseObservation(TimeTools.getGmtJulDay(gmt) , lng , lat , alt);
  }
}
