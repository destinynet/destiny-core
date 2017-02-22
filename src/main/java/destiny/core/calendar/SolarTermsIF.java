/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:22:50
 */
package destiny.core.calendar;


import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 計算節氣的介面
 */
public interface SolarTermsIF {
  
  /** 計算某時刻當下的節氣 */
  SolarTerms getSolarTermsFromGMT(double gmtJulDay);

  /** 承上， LocalDateTime 版本 */
  default SolarTerms getSolarTermsFromGMT(LocalDateTime gmt) {
    double gmtJulDay = Time.getGmtJulDay(gmt);
    return getSolarTermsFromGMT(gmtJulDay);
  }

  /** 承上， Time 版本 */
  default SolarTerms getSolarTermsFromGMT(Time gmt) {
    return getSolarTermsFromGMT(gmt.getGmtJulDay());
  }

  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   * @return List <SolarTermsTime>
   */
  List<SolarTermsTime> getPeriodSolarTerms(double fromGmt , double toGmt );

  /**
   * @return 傳回某段時間內的節氣列表， GMT 時刻
   */
  default List<SolarTermsTime> getPeriodSolarTerms(@NotNull LocalDateTime fromGmtTime , @NotNull LocalDateTime toGmtTime ) {
    return getPeriodSolarTerms(Time.getGmtJulDay(fromGmtTime) , Time.getGmtJulDay(toGmtTime));
  }

  /**
   * @return 傳回某段時間內的節氣列表， GMT 時刻
   */
  default List<SolarTermsTime> getPeriodSolarTerms(@NotNull Time fromGmtTime , @NotNull Time toGmtTime ) {
    return getPeriodSolarTerms(fromGmtTime.getGmtJulDay() , toGmtTime.getGmtJulDay());
  }

  /**
   * 計算從某時(fromLmtTime) 到某時(toLmtTime) 之間的節氣 , in LMT
   * @return List of <b>LMT</b> Time , 傳回 LMT 表示的節氣列表
   * 注意，此方法因為經過 Julian Day 的轉換，精確度比 GMT 差了 約萬分之一秒
   * List < SolarTermsTime >
   */
  default List<SolarTermsTime> getLocalPeriodSolarTerms(@NotNull LocalDateTime fromLmt , @NotNull LocalDateTime toLmt , @NotNull Location location) {
    LocalDateTime fromGmt = Time.getGmtFromLmt(fromLmt , location);
    LocalDateTime   toGmt = Time.getGmtFromLmt(  toLmt , location);

    return getPeriodSolarTerms(fromGmt, toGmt).stream().map( stt -> {
      LocalDateTime gmt = stt.getTime();
      return new SolarTermsTime(stt.getSolarTerms() , Time.getLmtFromGmt(gmt , location));
    }).collect(Collectors.toList());
  }

  /**
   * 計算從某時(fromLmtTime) 到某時(toLmtTime) 之間的節氣 , in LMT
   * @return List of <b>LMT</b> Time , 傳回 LMT 表示的節氣列表
   * 注意，此方法因為經過 Julian Day 的轉換，精確度比 GMT 差了 約萬分之一秒
   * List < SolarTermsTime >
   */
  default List<SolarTermsTime> getLocalPeriodSolarTerms(@NotNull Time fromLmt , @NotNull Time toLmt , @NotNull Location location) {
    Time fromGmtTime = Time.getGMTfromLMT(fromLmt, location);
    Time   toGmtTime = Time.getGMTfromLMT(toLmt , location);
    return getPeriodSolarTerms(fromGmtTime, toGmtTime).stream().map( stt -> {
      LocalDateTime gmt = stt.getTime();
      return new SolarTermsTime(stt.getSolarTerms() , Time.getLmtFromGmt(gmt , location));
    }).collect(Collectors.toList());
  }
}
