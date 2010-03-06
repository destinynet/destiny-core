/**
 * @author smallufo 
 * Created on 2007/12/21 at 上午 1:29:52
 */ 
package destiny.astrology.classical;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import destiny.astrology.Planet;

public class AverageDailyMotionMap
{
  /** 這個網址有平均速度的列表 http://mithras93.tripod.com/lessons/lesson7/index.html 
   * 我則另外參考 Horary Astrology Plain and Simple , page 82 */
  private static Map<Planet , Double> averageDailyMotionMap = Collections.synchronizedMap(new HashMap<Planet , Double>());
  static
  {
    averageDailyMotionMap.put(Planet.SUN     ,  0.0 + 59.0/60 + 8.0/3600);
    averageDailyMotionMap.put(Planet.MOON    , 13.0 + 10.0/60 + 36.0/3600);
    averageDailyMotionMap.put(Planet.MERCURY ,  1.0 + 23.0/60);
    averageDailyMotionMap.put(Planet.VENUS   ,  1.0 + 12.0/60);
    averageDailyMotionMap.put(Planet.MARS    ,  0.0 + 31.0/60 + 27.0/3600);
    averageDailyMotionMap.put(Planet.JUPITER ,  0.0 +  5.0/60);
    averageDailyMotionMap.put(Planet.SATURN  ,  0.0 +  2.0/60 + 1.0/3600);
  }
  
  
  public static Double get(Planet planet)
  {
    return averageDailyMotionMap.get(planet);
  }

}
