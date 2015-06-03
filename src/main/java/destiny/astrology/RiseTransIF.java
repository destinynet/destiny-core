/*
 * @author smallufo
 * @date 2004/11/2
 * @time 下午 02:56:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  Time getGmtTransTime(Time fromGmtTime , Star star , TransPoint point , Location location ,
      double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction);

  /**
   * 承上 , 來源、目標時間都是 LMG
   */
  default Time getLmtTransTime(Time fromLmtTime , Star star , TransPoint point , Location location ,
      double atmosphericPressure , double atmosphericTemperature , boolean isDiscCenter , boolean hasRefraction) {
    Time fromGmtTime = Time.getGMTfromLMT(fromLmtTime, location);
    Time resultGmt = getGmtTransTime(fromGmtTime , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    return Time.getLMTfromGMT(resultGmt , location);
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
  default List<Time> getPeriodStarRiseTransTime(@NotNull Time fromLmtTime , @NotNull Time toLmtTime , Star star , TransPoint point , @NotNull Location location ,
      double atmosphericPressure, double atmosphericTemperature, boolean isDiscCenter, boolean hasRefraction) {
    Time fromGmtTime = Time.getGMTfromLMT(fromLmtTime, location);
    Time toGmtTime   = Time.getGMTfromLMT(toLmtTime, location);

    List<Time> resultList = new ArrayList<>();

    Time resultGmtTime;
    while (fromGmtTime.isBefore(toGmtTime))
    {
      resultGmtTime = getGmtTransTime(fromGmtTime , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      logger.debug("resultGmtTime = {}" , resultGmtTime);

      if (!resultGmtTime.isBefore(toGmtTime))
        break;

      Time resultLMT = Time.getLMTfromGMT(resultGmtTime, location);
      resultList.add(resultLMT);
      fromGmtTime = new Time(resultGmtTime , 1);
    }

    return resultList;
  }
}
