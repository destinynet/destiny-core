/**
 * Created by smallufo on 2026-01-27.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.eightwords.Reaction
import destiny.core.chinese.FiveElement

interface IEwBdnpEvaluation {

  /**
   * 計算四柱十神分佈
   * @return Map<Reaction, 分數> score: 0 to 1
   */
  fun EwBdnp.calculateTenGodDistribution(): Map<Reaction, Double>

  /**
   * 計算四柱五行分佈
   * @return Map<FiveElement, 分數> score: 0 to 1
   */
  fun EwBdnp.calculateFiveElementDistribution(): Map<FiveElement, Double>

  /**
   * 計算單一柱（大運/流年）的五行分佈
   * 用於計算每條大運的五行強度
   *
   * @param stemAndBranch 單柱的天干、地支、藏干資訊
   * @return Map<FiveElement, 分數> (未正規化的原始分數)
   */
  fun calculateSinglePillarFiveElement(stemAndBranch: EwBdnp.StemAndBranch): Map<FiveElement, Double>

  /**
   * 計算大運五行走勢
   * @return List of (大運標籤, 五行分佈)
   */
  fun EwBdnp.calculateFortuneLargeFiveElementTrend(): List<Pair<String, Map<FiveElement, Double>>>
}
