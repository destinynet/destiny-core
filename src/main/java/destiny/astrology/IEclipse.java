/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

import destiny.astrology.AbstractSolarEclipse.SolarType;
import destiny.core.calendar.Location;
import org.jooq.lambda.tuple.Tuple2;

import java.time.chrono.ChronoLocalDateTime;
import java.util.*;

import static destiny.core.calendar.TimeTools.getGmtJulDay;

/** 計算日食、月食的介面 */
public interface IEclipse {

  /** 從此時之後，全球各地的「一場」日食資料 (型態、開始、最大、結束...） */
  AbstractSolarEclipse getNextSolarEclipse(double fromGmtJulDay, boolean forward, Collection<SolarType> types);

  default AbstractSolarEclipse getNextSolarEclipse(double fromGmtJulDay, boolean forward) {
    return getNextSolarEclipse(fromGmtJulDay , forward , Arrays.asList(SolarType.values()));
  }

  /** 同上，月食 */
  AbstractLunarEclipse getNextLunarEclipse(double fromGmtJulDay , boolean forward);


  // ========================================================================================

  /** 從此之後 , 此地點下次發生日食的資訊為何 (tuple.v1) , 以及， 日食最大化的時間，該地的觀測資訊為何 (tuple.v2) */
  Tuple2<EclipseSpan , SolarEclipseObservation> getNextSolarEclipse(double fromGmtJulDay , double lng , double lat , double alt , boolean forward);

  default Tuple2<EclipseSpan , SolarEclipseObservation> getNextSolarEclipse(double fromGmtJulDay , Location loc , boolean forward) {
    return getNextSolarEclipse(fromGmtJulDay , loc.getLongitude() , loc.getLatitude() , loc.getAltitudeMeter() , forward);
  }

  /** 從此之後 , 此地點下次發生月食的資訊為何 (tuple.v1) , 以及， 日食最大化的時間，該地的觀測資訊為何 (tuple.v2) */
  Tuple2<EclipseSpan , LunarEclipseObservation> getNextLunarEclipse(double fromGmtJulDay , double lng , double lat , double alt , boolean forward);


  // ========================================================================================

  /** 此時此刻，哪裡有發生日食，其「中線」的地點為何 , 以及其相關日食觀測結果 . 此 method 專門計算「中線在哪裡」 */
  Optional<SolarEclipseObservation> getEclipseCenterInfo(double gmtJulDay);



  /** 若當下 gmtJulDay 有日食，傳出此地點觀測此日食的相關資料 */
  Optional<SolarEclipseObservation> getSolarEclipseObservation(double gmtJulDay , double lng , double lat , double alt);

  /** 若當下 gmtJulDay 有月食，傳出此地點觀測此日食的相關資料 */
  Optional<LunarEclipseObservation> getLunarEclipseObservation(double gmtJulDay , double lng , double lat , double alt);


  default Optional<SolarEclipseObservation> getSolarEclipseObservation(ChronoLocalDateTime gmt, double lng , double lat , double alt) {
    return getSolarEclipseObservation(getGmtJulDay(gmt) , lng , lat , alt);
  }

  // ========================================================================================

  /** 全球，某時間範圍內的日食記錄 */
  default List<AbstractSolarEclipse> getRangeSolarEclipses(double fromGmt , double toGmt , Collection<SolarType> types) {
    if (fromGmt >= toGmt)
      throw new RuntimeException("fromGmt : " + fromGmt + " must less than toGmt : " + toGmt);

    List<AbstractSolarEclipse> list = new ArrayList<>();
    double gmt = fromGmt;

    while (gmt < toGmt) {
      AbstractSolarEclipse e = getNextSolarEclipse(gmt , true , types);
      list.add(e);

      gmt = e.getEnd();
    }
    return list;
  }

  /** 搜尋 全部 種類的日食 */
  default List<AbstractSolarEclipse> getRangeSolarEclipses(double fromGmt , double toGmt) {
    return getRangeSolarEclipses(fromGmt , toGmt , Arrays.asList(SolarType.values()));
  }

  /** 承上 , ChronoLocalDateTime 版本 , 搜尋 全部 種類的日食 */
  default List<AbstractSolarEclipse> getRangeSolarEclipses(ChronoLocalDateTime fromGmt , ChronoLocalDateTime toGmt) {
    return getRangeSolarEclipses(getGmtJulDay(fromGmt) , getGmtJulDay(toGmt));
  }

  /** 承上 , ChronoLocalDateTime 版本 , 搜尋 單一 種類的日食 */
  default List<AbstractSolarEclipse> getRangeSolarEclipses(ChronoLocalDateTime fromGmt , ChronoLocalDateTime toGmt , SolarType type) {
    return getRangeSolarEclipses(getGmtJulDay(fromGmt) , getGmtJulDay(toGmt) , Collections.singletonList(type));
  }


  /** 全球，某時間範圍內的月食記錄 */
  default List<AbstractLunarEclipse> getRangeLunarEclipses(double fromGmt , double toGmt , Collection<AbstractLunarEclipse.LunarType> types) {
    if (fromGmt >= toGmt)
      throw new RuntimeException("fromGmt : " + fromGmt + " must less than toGmt : " + toGmt);

    List<AbstractLunarEclipse> list = new ArrayList<>();
    double gmt = fromGmt;

    while (gmt < toGmt) {
      AbstractLunarEclipse e = getNextLunarEclipse(gmt , true);
      list.add(e);

      gmt = e.getEnd();
    }
    return list;
  }

    /** 承上 , ChronoLocalDateTime 版本 , 搜尋 全部 種類的日食 */
  default List<AbstractLunarEclipse> getRangeLunarEclipses(ChronoLocalDateTime fromGmt , ChronoLocalDateTime toGmt) {
    return getRangeLunarEclipses(getGmtJulDay(fromGmt) , getGmtJulDay(toGmt), null);
  }


}
