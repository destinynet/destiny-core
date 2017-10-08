/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.astrology;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 * SwissEph 的實作，是 destiny.astrology.swissephImpl.RiseTransImpl
 */
public interface RiseTransIF {

  Logger logger = LoggerFactory.getLogger(RiseTransIF.class);

  /**
   * 來源、目標時間都是 GMT
   *
   * @param atmosphericTemperature 攝氏溫度
   * @param atmosphericPressure    壓力 , 例如 1013.25
   */
  double getGmtTransJulDay(double fromGmtJulDay, Star star, TransPoint point, Location location,
                           double atmosphericTemperature, double atmosphericPressure,
                           boolean isDiscCenter, boolean hasRefraction);


  /**
   * 來源、目標時間都是 GMT
   */
  default ChronoLocalDateTime getGmtTrans(ChronoLocalDateTime fromGmt, Star star, TransPoint point, Location location, double atmosphericTemperature, double atmosphericPressure, boolean isDiscCenter, boolean hasRefraction) {
    double fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    double resultGmt = getGmtTransJulDay(fromGmtJulDay , star , point , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction);
    return JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(resultGmt);
  }

  /**
   * 來源、目標時間都是 LMT
   */
  default ChronoLocalDateTime getLmtTrans(ChronoLocalDateTime fromLmtTime, Star star, TransPoint point, Location location, double atmosphericTemperature, double atmosphericPressure, boolean isDiscCenter, boolean hasRefraction) {
    ChronoLocalDateTime fromGmtTime = TimeTools.getGmtFromLmt(fromLmtTime , location);

    ChronoLocalDateTime resultGmt = getGmtTrans(fromGmtTime , star , point , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction);
    return TimeTools.getLmtFromGmt(resultGmt , location);
  }


  /**
   * 取得某段時間（LMT）之內，某星體的通過某 Point 的時刻（GMT）
   */
  default List<Double> getPeriodStarRiseTransGmtJulDay(ChronoLocalDateTime fromLmtTime, ChronoLocalDateTime toLmtTime, Star star, TransPoint point, Location location, double atmosphericTemperature, double atmosphericPressure, boolean isDiscCenter, boolean hasRefraction) {
    double fromGmtJulDay = TimeTools.getGmtJulDay(TimeTools.getGmtFromLmt(fromLmtTime, location));
    double   toGmtJulDay = TimeTools.getGmtJulDay(TimeTools.getGmtFromLmt(toLmtTime, location));

    List<Double> resultList = new ArrayList<>();

    double resultGmtJulDay;

    while (fromGmtJulDay < toGmtJulDay) {
      resultGmtJulDay = getGmtTransJulDay(fromGmtJulDay , star , point , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction);
      logger.debug("resultGmtJulDay = {}" , resultGmtJulDay);

      if (resultGmtJulDay > toGmtJulDay)
        break;

      resultList.add(resultGmtJulDay);
      fromGmtJulDay = resultGmtJulDay + 0.01;
    }
    return resultList;
  }

  /**
   * 取得某段時間（LMT）之內，某星體的通過某 Point 的時刻（LMT）
   * @param fromLmtTime 開始時間
   * @param toLmtTime 結束時間
   * @param star 星體
   * @param point 接觸點 : RISING , MERIDIAN , SETTING , NADIR
   * @param location 地點
   * @param atmosphericTemperature 溫度
   * @param atmosphericPressure 大氣壓力
   * @param isDiscCenter 是否是星體中心（只影響 日、月），通常設為 false
   * @param hasRefraction 是否考量濛氣差 , 通常設為 true
   * @return List <Time> in LMT
   */
  default List<ChronoLocalDateTime> getPeriodStarRiseTransTime(ChronoLocalDateTime fromLmtTime, ChronoLocalDateTime toLmtTime, Star star, TransPoint point, Location location, double atmosphericTemperature, double atmosphericPressure, boolean isDiscCenter, boolean hasRefraction) {
    return getPeriodStarRiseTransGmtJulDay(fromLmtTime , toLmtTime , star , point , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction)
      .stream()
      .map(gmtJulDay -> {
        ChronoLocalDateTime gmt = JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(gmtJulDay);
        return TimeTools.getLmtFromGmt(gmt , location);
      }).collect(Collectors.toList());
  }
}
