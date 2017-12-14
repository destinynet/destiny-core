/**
 * @author smallufo
 * Created on 2007/12/21 at 上午 1:29:52
 */
package destiny.astrology.classical

import destiny.astrology.Planet
import java.util.*

object AverageDailyMotionMap {

  /** 這個網址有平均速度的列表 http://mithras93.tripod.com/lessons/lesson7/index.html
   * 我則另外參考 Horary Astrology Plain and Simple , page 82  */
  private val averageDailyMotionMap = mapOf<Planet, Double>(
    Planet.SUN to 0.0 + 59.0 / 60 + 8.0 / 3600.0,
    Planet.MOON to 13.0 + 10.0 / 60 + 36.0 / 3600.0,
    Planet.MERCURY to 1.0 + 23.0 / 60.0,
    Planet.VENUS to 1.0 + 12.0 / 60.0 ,
    Planet.MARS to 0.0 + 31.0 / 60 + 27.0 / 3600.0,
    Planet.JUPITER to 0.0 + 5.0 / 60.0,
    Planet.SATURN to 0.0 + 2.0 / 60 + 1.0 / 3600.0
  )


  /**
   * 三王星 沒「每日速度」資料
   */
  fun getAvgDailySpeed(planet: Planet): Double ? {
    return averageDailyMotionMap[planet]
  }

  fun getDailySpeedOpt(planet: Planet): Optional<Double> {
    return Optional.ofNullable(averageDailyMotionMap[planet])
  }


}
