/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 * SwissEph 的實作，是 destiny.astrology.swissephImpl.RiseTransImpl
 */
public interface IRiseTrans {

  Logger logger = LoggerFactory.getLogger(IRiseTrans.class);

  /**
   * 來源、目標時間都是 GMT
   *
   * @param atmosphericTemperature 攝氏溫度
   * @param atmosphericPressure    壓力 , 例如 1013.25
   *                               <p>
   *                               根據測試資料 , 美國海軍天文台的計算結果，「似乎」傾向 center = false , refraction = true. 亦即： 計算「邊緣」以及「考量折射」
   *                               </p>
   */
  double getGmtTransJulDay(double fromGmtJulDay, Star star, TransPoint point, Location location, boolean isDiscCenter, boolean hasRefraction, double atmosphericTemperature, double atmosphericPressure);

  /**
   * 內定 溫度0度 , 壓力 1013.25
   */
  default double getGmtTransJulDay(double fromGmtJulDay, Star star, TransPoint point, Location location , boolean isDiscCenter , boolean hasRefraction) {
    return getGmtTransJulDay(fromGmtJulDay , star , point , location , isDiscCenter, hasRefraction, 0 , 1013.25);
  }

  /**
   * 內定 center = false , 並且 考量大氣折射
   */
  default double getGmtTransJulDay(double fromGmtJulDay, Star star, TransPoint point, Location location) {
    return getGmtTransJulDay(fromGmtJulDay , star , point , location , false, true);
  }


  /**
   * 來源、目標時間都是 GMT
   */
  default ChronoLocalDateTime getGmtTrans(ChronoLocalDateTime fromGmt, Star star, TransPoint point, Location location, double atmosphericTemperature, double atmosphericPressure, boolean isDiscCenter, boolean hasRefraction , Function<Double , ChronoLocalDateTime> revJulDayFunc ) {
    double fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt);
    double resultGmt = getGmtTransJulDay(fromGmtJulDay , star , point , location , isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure);
    return revJulDayFunc.apply(resultGmt);
  }

  /**
   * 來源、目標時間都是 LMT
   */
  default ChronoLocalDateTime getLmtTrans(ChronoLocalDateTime fromLmtTime, Star star, TransPoint point, Location location, boolean isDiscCenter, boolean hasRefraction, double atmosphericTemperature, double atmosphericPressure, Function<Double, ChronoLocalDateTime> revJulDayFunc) {
    ChronoLocalDateTime fromGmtTime = TimeTools.getGmtFromLmt(fromLmtTime , location);

    ChronoLocalDateTime resultGmt = getGmtTrans(fromGmtTime , star , point , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction , revJulDayFunc);
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
      resultGmtJulDay = getGmtTransJulDay(fromGmtJulDay , star , point , location , isDiscCenter, hasRefraction, atmosphericTemperature, atmosphericPressure);
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
  default List<ChronoLocalDateTime> getPeriodStarRiseTransTime(ChronoLocalDateTime fromLmtTime, ChronoLocalDateTime toLmtTime, Star star, TransPoint point, Location location, double atmosphericTemperature, double atmosphericPressure, boolean isDiscCenter, boolean hasRefraction , Function<Double , ChronoLocalDateTime> revJulDayFunc ) {
    return getPeriodStarRiseTransGmtJulDay(fromLmtTime , toLmtTime , star , point , location , atmosphericTemperature, atmosphericPressure , isDiscCenter , hasRefraction)
      .stream()
      .map(gmtJulDay -> {
        ChronoLocalDateTime gmt = revJulDayFunc.apply(gmtJulDay);
        return TimeTools.getLmtFromGmt(gmt , location);
      }).collect(Collectors.toList());
  }
}
