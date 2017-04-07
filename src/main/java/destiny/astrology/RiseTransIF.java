/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 * SwissEph 的實作，是 destiny.astrology.swissephImpl.RiseTransImpl
 */
public interface RiseTransIF {

  Logger logger = LoggerFactory.getLogger(RiseTransIF.class);

  /**
   * 來源、目標時間都是 GMT
   */
  double getGmtTransJulDay(double fromGmtJulDay , Star star , TransPoint point , Location location ,
      double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction);

  default LocalDateTime getGmtTrans(double fromGmtJulDay , Star star , TransPoint point , Location location ,
                                    double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction) {
    double resultGmt = getGmtTransJulDay(fromGmtJulDay , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    return new Time(resultGmt).toLocalDateTime();
  }

  default LocalDateTime getGmtTrans(LocalDateTime fromGmt , Star star , TransPoint point , Location location ,
                                    double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction) {
    double fromGmtJulDay = Time.getGmtJulDay(fromGmt);
    double resultGmt = getGmtTransJulDay(fromGmtJulDay , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    return new Time(resultGmt).toLocalDateTime();
  }

  /**
   * 來源、目標時間都是 LMT
   */
  default LocalDateTime getLmtTrans(LocalDateTime fromLmtTime , Star star , TransPoint point , Location location ,
                                    double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction) {
    LocalDateTime fromGmtTime = Time.getGmtFromLmt(fromLmtTime , location);
    LocalDateTime resultGmt = getGmtTrans(fromGmtTime , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    return Time.getLmtFromGmt(resultGmt , location);
  }

  /**
   * 取得某段時間（LMT）之內，某星體的通過某 Point 的時刻（LMT）
   * @param fromLmtTime 開始時間
   * @param toLmtTime 結束時間
   * @param star 星體
   * @param point 接觸點 : RISING , MERIDIAN , SETTING , NADIR
   * @param location 地點
   * @param atmosphericPressure 大氣壓力
   * @param atmosphericTemperature 溫度
   * @param isDiscCenter 是否是星體中心（只影響 日、月），通常設為 false
   * @param hasRefraction 是否考量濛氣差 , 通常設為 true
   * @return List <Time> in LMT
   */
  default List<LocalDateTime> getPeriodStarRiseTransTime(LocalDateTime fromLmtTime ,
                                                         LocalDateTime toLmtTime ,
                                                         Star star , TransPoint point ,
                                                         Location location ,
                                                         double atmosphericPressure,
                                                         double atmosphericTemperature,
                                                         boolean isDiscCenter,
                                                         boolean hasRefraction) {
    LocalDateTime fromGmtTime = Time.getGmtFromLmt(fromLmtTime, location);
    LocalDateTime toGmtTime   = Time.getGmtFromLmt(toLmtTime, location);

    List<LocalDateTime> resultList = new ArrayList<>();

    LocalDateTime resultGmtTime;
    while (fromGmtTime.isBefore(toGmtTime)) {
      resultGmtTime = getGmtTrans(fromGmtTime , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      logger.debug("resultGmtTime = {}" , resultGmtTime);

      if (!resultGmtTime.isBefore(toGmtTime))
        break;

      LocalDateTime resultLmtLDT = Time.getLmtFromGmt(resultGmtTime , location);
      resultList.add(resultLmtLDT);
      fromGmtTime = LocalDateTime.from(resultGmtTime).plus(1, ChronoUnit.SECONDS);
    }

    return resultList;
  }
}
