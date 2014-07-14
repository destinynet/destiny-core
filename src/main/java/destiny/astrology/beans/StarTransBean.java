/*
 * @author smallufo
 * @date 2004/11/3
 * @time 下午 04:21:33
 */
package destiny.astrology.beans;

import destiny.astrology.RiseTransIF;
import destiny.astrology.Star;
import destiny.astrology.TransPoint;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 */
public class StarTransBean
{
  @Inject
  private RiseTransIF riseTransImpl;
  
  public StarTransBean(RiseTransIF impl)
  {
    this.riseTransImpl = impl;
  }
  
  public StarTransBean()
  {
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
  public List<Time> getPeriodStarRiseTransTime(Time fromLmtTime , Time toLmtTime , Star star , TransPoint point , Location location ,
      double atmosphericPressure, double atmosphericTemperature, boolean isDiscCenter, boolean hasRefraction)
  {
    Time fromGmtTime = Time.getGMTfromLMT(fromLmtTime, location);
    Time toGmtTime   = Time.getGMTfromLMT(toLmtTime, location);
    
    List<Time> resultList = new ArrayList<Time>();
    
    Time resultGmtTime;
    while (fromGmtTime.isBefore(toGmtTime))
    {
      resultGmtTime = riseTransImpl.getGmtTransTime(fromGmtTime , star , point , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      System.out.println("resultGmtTime = " + resultGmtTime); 
      if (!resultGmtTime.isBefore(toGmtTime))
        break;

      Time resultLMT = Time.getLMTfromGMT(resultGmtTime, location);
      resultList.add(resultLMT);
      fromGmtTime = new Time(resultGmtTime , 1);
    }
    
    return resultList;
  }
}
