/**
 * YearMonth Search — lens preset(SearchProfile)。
 *
 * timing-search-design.md §9 願景的落地(Yearly Peaks B1,
 * root docs/plans/2026-08-24-yearly-peaks-algorithm.md §3.1):
 * 把三 dial(significators / targetHouses / aspectWeights)打包成可命名、可回測的搜尋意圖。
 *
 * ## 命名紀律:以幾何命名,不是生命領域
 *
 * `EventCategory` 的 KDoc 禁令在此仍然生效 —— 固定分組不得作為先驗餵給判讀端。
 * lens 的 id 描述「盯哪些星/宮」(`angles-mars-saturn`),不描述「這是官司還是感情」;
 * 語意歸屬由 Layer 2 的判讀端(LLM/占星師)依提問與脈絡當場決定。
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Arabic
import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Axis
import destiny.core.astrology.Planet

/**
 * @param significators 本命側標的(星體/軸點),直接給定者。
 * @param rulerOfHouses 以「第 n 宮**宮主**」表達的 significator —— 宮主是誰依本命而定,
 *   由 service 於掃描前解出並併入 [significators](此即它不能是 `Set<AstroPoint>` 的原因)。
 * @param targetHouses 目標宮(落宮通道與小限閘門用)。
 * @param aspectWeights 相位偏好 dial;空 map = 中性(全相位等權)。
 */
data class SearchProfile(
  val id: String,
  val significators: Set<AstroPoint> = emptySet(),
  val rulerOfHouses: Set<Int> = emptySet(),
  val targetHouses: Set<Int> = emptySet(),
  val targetLots: Set<Arabic> = emptySet(),
  val aspectWeights: Map<Aspect, Double> = emptyMap(),
) {

  companion object {

    /**
     * Canonical lens 集(B1 定案,6 個;≤8 的預算刻意留空間給實測後的增補)。
     * 前四個直接繼承名人 fixture 的實戰配置(timing-search-design.md §7),
     * 已有真值回測錨點;後兩個是資產/根基兩大常見題,尚無 fixture —— 校準前
     * 屬 UNTESTED tier,B2 落地時如實標示。
     */
    val CANONICAL: List<SearchProfile> = listOf(
      // 衝擊/試煉(Epstein、MalcolmX fixture 的 lens)
      SearchProfile(
        id = "angles-mars-saturn",
        significators = setOf(Axis.RISING, Axis.MERIDIAN, Planet.MARS, Planet.SATURN, Planet.NEPTUNE),
        targetHouses = setOf(1, 7, 8, 12),
        aspectWeights = AspectWeights.HARD,
      ),
      // 結盟/關係(Harry fixture 的 lens)
      SearchProfile(
        id = "venus-descendant",
        significators = setOf(Axis.RISING, Planet.VENUS, Planet.JUPITER, Planet.MOON),
        targetHouses = setOf(5, 7),
        aspectWeights = AspectWeights.SOFT,
      ),
      // 可見度/成就(Sophie Marceau fixture 的 lens)
      SearchProfile(
        id = "mc-sun-jupiter",
        significators = setOf(Axis.MERIDIAN, Planet.SUN, Planet.VENUS, Planet.JUPITER),
        targetHouses = setOf(1, 10, 11),
        aspectWeights = AspectWeights.SOFT,
      ),
      // 契約/攻防(SBF fixture 的 lens)
      SearchProfile(
        id = "saturn-mercury-legal",
        significators = setOf(Axis.RISING, Axis.MERIDIAN, Planet.SATURN, Planet.MARS, Planet.MERCURY, Planet.NEPTUNE),
        targetHouses = setOf(7, 9, 10, 12),
        aspectWeights = AspectWeights.HARD,
      ),
      // 資產流動(2/8 軸;宮主延遲解析;中性 —— 得失方向交判讀端)
      SearchProfile(
        id = "second-eighth",
        significators = setOf(Planet.VENUS, Planet.JUPITER, Planet.PLUTO),
        rulerOfHouses = setOf(2, 8),
        targetHouses = setOf(2, 8),
      ),
      // 根基/居所/家內(4 宮軸;中性)
      SearchProfile(
        id = "fourth-moon",
        significators = setOf(Planet.MOON),
        rulerOfHouses = setOf(4),
        targetHouses = setOf(3, 4),
      ),
    )
  }
}
