/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:22:50
 */
package destiny.core.calendar;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 計算節氣的介面
 */
public interface SolarTermsIF {

  Logger logger = LoggerFactory.getLogger(SolarTermsIF.class);

  /** 計算某時刻當下的節氣 */
  SolarTerms getSolarTermsFromGMT(double gmtJulDay);

  /** 承上， ChronoLocalDateTime 版本 */
  default SolarTerms getSolarTermsFromGMT(ChronoLocalDateTime gmt) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getSolarTermsFromGMT(gmtJulDay);
  }

  /**
   * 承上 , LMT + Location 版本
   */
  default SolarTerms getSolarTerms(ChronoLocalDateTime lmt , Location location) {
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt , location);
    return getSolarTermsFromGMT(gmt);
  }


  /**
   * @return 計算，從 某時刻開始，的下一個（或上一個）節氣的時間點為何
   */
  double getSolarTermsTime(SolarTerms solarTerms , double fromGmtJulDay , boolean isForward);



  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   * @return List <SolarTermsTime>
   */
  List<SolarTermsTime> getPeriodSolarTerms(double fromGmt , double toGmt );



  /**
   * @return 傳回某段時間內的節氣列表， GMT 時刻
   */
  default List<SolarTermsTime> getPeriodSolarTerms(@NotNull ChronoLocalDateTime fromGmtTime , @NotNull ChronoLocalDateTime toGmtTime ) {
    return getPeriodSolarTerms(TimeTools.getGmtJulDay(fromGmtTime) , TimeTools.getGmtJulDay(toGmtTime));
  }

  /**
   * 計算從某時(fromLmtTime) 到某時(toLmtTime) 之間的節氣 , in LMT
   * @return List of <b>LMT</b> Time , 傳回 LMT 表示的節氣列表
   * 注意，此方法因為經過 Julian Day 的轉換，精確度比 GMT 差了 約萬分之一秒
   * List < SolarTermsTime >
   */
  default List<SolarTermsTime> getLocalPeriodSolarTerms(@NotNull ChronoLocalDateTime fromLmt , @NotNull ChronoLocalDateTime toLmt , @NotNull Location location) {
    double fromGmt = TimeTools.getGmtJulDay(fromLmt , location);
    double   toGmt = TimeTools.getGmtJulDay(  toLmt , location);

    return getPeriodSolarTerms(fromGmt, toGmt).stream().map( stt -> {
      ChronoLocalDateTime gmt = stt.getTime();
      logger.trace("節氣 : {} , GMT時間 : {} . nano = {} " , stt.getSolarTerms() , gmt , gmt.get(ChronoField.NANO_OF_SECOND));
      return new SolarTermsTime(stt.getSolarTerms() , TimeTools.getLmtFromGmt(gmt , location));
    }).collect(Collectors.toList());
  }

}
