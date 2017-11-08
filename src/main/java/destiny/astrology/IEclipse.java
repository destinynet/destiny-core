/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.jooq.lambda.tuple.Tuple2;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Optional;

/** 計算日食、月食的介面 */
public interface IEclipse {


  /** 從此時之後，全球各地的「一場」日食資料 (型態、開始、最大、結束...） */
  AbstractEclipse getNextSolarEclipse(double fromGmtJulDay , boolean forward);

  /** 從此之後 , 此地點下次發生日食的資訊為何 (tuple.v1) , 以及， 日食最大化的時間，該地的觀測資訊為何 (tuple.v2) */
  Tuple2<EclipseSpan , EclipseObservation> getNextSolarEclipse(double fromGmtJulDay , double lng , double lat , double alt , boolean forward);

  default Tuple2<EclipseSpan , EclipseObservation> getNextSolarEclipse(double fromGmtJulDay , Location loc , boolean forward) {
    return getNextSolarEclipse(fromGmtJulDay , loc.getLongitude() , loc.getLatitude() , loc.getAltitudeMeter() , forward);
  }

  /** 此時此刻，哪裡有發生日食，其「中線」的地點為何 , 以及其相關日食觀測結果 . 此 method 專門計算「中線在哪裡」 */
  Optional<EclipseObservation> getEclipseCenterInfo(double gmtJulDay);

  /** 若當下 gmtJulDay 有日食，傳出此地點觀測此日食的相關資料 */
  Optional<EclipseObservation> getEclipseObservation(double gmtJulDay , double lng , double lat , double alt);

  default Optional<EclipseObservation> getEclipseObservation(ChronoLocalDateTime gmt, double lng , double lat , double alt) {
    return getEclipseObservation(TimeTools.getGmtJulDay(gmt) , lng , lat , alt);
  }
}
