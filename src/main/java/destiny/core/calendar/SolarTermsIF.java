/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:22:50
 */
package destiny.core.calendar;


import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 計算節氣的介面
 */
public interface SolarTermsIF {
  
  /** 計算某時刻當下的節氣 */
  SolarTerms getSolarTermsFromGMT(Time gmt);

  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   * @return List <SolarTermsTime>
   */
  List<SolarTermsTime> getPeriodSolarTerms(@NotNull Time fromGmtTime , @NotNull Time toGmtTime );

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
      Time gmt = stt.getTime();
      return new SolarTermsTime(stt.getSolarTerms() , Time.getLMTfromGMT(gmt , location));
    }).collect(Collectors.toList());
  }
}
