/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IZodiacDegree


interface IPrimaryDirection {
  /**
   * 計算一張本命盤，在推進了指定的赤經弧角後，所有星體在黃道上的新位置
   * @param natalChart 本命盤模型
   * @param directionArc 要推進的赤經弧角 (來自時間鑰匙)
   * @param houseSystem 主限法計算所依賴的宮位系統
   * @return 一個 Map，包含所有星體推進後的新黃道位置
   */
  fun getDirectedPositions(
    natalChart: IHoroscopeModel,
    directionArc: Double,
    houseSystem: HouseSystem
  ): Map<AstroPoint, IZodiacDegree>
}
