/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.chinese.eightwords

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowPattern
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.Branch
import destiny.core.chinese.trilogy
import destiny.tools.getTitle
import java.util.*


/**
 * 八字Flow (大運、流年、流月) 與本命四柱 特徵翻譯
 */
object FlowTranslator {

  private val locale = Locale.TAIWAN

  /**
   * 大運、流年同時影響本命天干
   */
  fun Set<FlowPattern>.translateAffecting(): List<String> {
    return this.filterIsInstance<Affecting>().groupBy { it.reacting }.map { (reacting, p: List<Affecting>) ->
      buildString {
        append("本命")
        append(p.joinToString("、") { it.scale.getTitle(locale) + "干" })
        append("(")
        if (p.size > 1)
          append("均為 ")
        append(p.first().stem)
        append(") ")
        val flowScales = p.flatMap { it.flowScales }.distinct().joinToString("、") { it.getTitle(locale) }
        append(
          when (reacting) {
            SAME       -> "與 ${flowScales}五行相同"
            PRODUCING  -> "生/洩 出${flowScales}"
            PRODUCED   -> "得到 $flowScales 五行 所生"
            DOMINATING -> "同時剋制 $flowScales (消耗能量)"
            BEATEN     -> "同時被 $flowScales 所剋制 (消耗能量)"
          }
        )
      }
    }
  }

  /**
   * 大運、流年合住本命天干
   *
   * ex : 本命時干(己) 被大運(甲)合住，甲己合化土
   * ex : 本命日干、時干(均為 己) 被大運(甲)合住，甲己合化土
   *
   * ex : 本命時干(己) 被大運、流年(均為 甲)合住，甲己合化土
   * ex : 本命日干、時干(均為 己) 被大運、流年(均為 甲)合住，甲己合化土
   *
   * ex : 本命時干(己) 被流年、流月(均為 甲)合住，甲己合化土
   * ex : 本命日干、時干(均為 己) 被流年、流月(均為 甲)合住，甲己合化土
   */
  fun Set<FlowPattern>.translateStemCombined(): List<String> {
    return this.filterIsInstance<StemCombined>().groupBy { it.stem }.map { (stem, p: List<StemCombined>) ->
      buildString {
        append("本命")
        val distinctPillars = p.map { it.scale }.distinct()
        append(
          distinctPillars.joinToString("、") {
            it.getTitle(locale) + "干"
          }
        )
        append("(")
        if (distinctPillars.size > 1)
          append("均為 ")
        append(stem)
        append(") ")

        val distinctFlowScales = p.map { it.flowScale }.distinct()
        append("被")
        append(distinctFlowScales.joinToString("、") {
          it.getTitle(locale)
        })
        append("(")
        if (distinctFlowScales.size > 1)
          append("均為 ")
        append(stem.combined.first)
        append(")")
        append("合住")
        append("，")
        append(setOf(stem, stem.combined.first).sorted().joinToString("")).append("合化").append(stem.combined.second)
      }
    }
  }

  /**
   * 大運流年合住本命地支
   * ex : 大運地支(戌) 合住 本命時支(卯)
   * ex : 大運地支(午) 合住 本命月支、日支、時支(均為 未)
   *
   * ex : 流年地支(戌) 合住 本命時支(卯)
   * ex : 流年地支(午) 合住 本命月支、日支(均為 未)
   * ex : 大運、流年地支(均為 午) 合住 本命月支、日支(均為 未)
   *
   * ex : 流月地支(戌) 合住 本命時支(卯)
   * ex : 流月地支(午) 合住 本命月支、日支(均為 未)
   * ex : 流年、流月地支(均為 午) 合住 本命月支、日支(均為 未)
   */
  fun Set<FlowPattern>.translateBranchCombined(): List<String> {
    return this.filterIsInstance<BranchCombined>().groupBy { it.branch }.map { (branch, p: List<BranchCombined>) ->
      buildString {
        val distinctFlowScales = p.map { it.flowScale }.distinct()
        append(
          distinctFlowScales.joinToString("、") {
            it.getTitle(locale)
          }
        )
        append("地支")
        append("(")
        if (distinctFlowScales.size > 1)
          append("均為 ")
        append(branch.combined)
        append(")")

        append(" 合住 ")

        append("本命")
        val distinctPillars = p.map { it.scale }.distinct()
        append(
          distinctPillars.joinToString("、") {
            it.getTitle(locale) + "支"
          }
        )
        append("(")
        if (distinctPillars.size > 1)
          append("均為 ")
        append(branch)
        append(")")
      }
    }
  }

  /**
   * 本命已經三合取二_再拱大運或流年
   * ex : 大運(辰)與本命年柱(子)、時柱(申)三合水局
   * ex : 大運(辰)與本命年柱(子)、時柱(申)三合水局
   *
   * ex : 大運(辰)與本命年柱(子)、時柱(申)三合水局
   * ex : 大運(辰)、流年(辰)與本命年柱(子)、時柱(申)三合水局
   *
   * ex : 流年(辰)與本命年柱(子)、時柱(申)三合水局
   * ex : 流年(辰)、流月(辰)與本命年柱(子)、時柱(申)三合水局
   */
  fun Set<FlowPattern>.translateTrilogyToFlow(): List<String> {
    return this.filterIsInstance<TrilogyToFlow>().groupBy { it.pairs }.map { (pairs: Set<Pair<Scale, Branch>>, patterns: List<TrilogyToFlow>) ->
      buildString {
        append(
          patterns.joinToString("、") {
            it.flow.first.getTitle(locale) + "(" + it.flow.second.getTitle(locale) + ")"
          }
        )

        append("與本命")
        append(
          pairs.joinToString("、") {
            it.first.getTitle(locale) + "柱(" + it.second + ")"
          }
        )
        append("三合")
        append(pairs.first().second.trilogy()).append("局")
      }
    }
  }

  /**
   * 大運流年三合_再拱本命地支
   * ex : 大運(辰)、流年(子) 與本命時支(申) 三合 水局
   * ex : 大運(辰)、流年(申) 與本命年支、時支(均為子) 三合 水局
   * ex : 流年(辰)、流月(子) 與本命時支(申) 三合 水局
   * ex : 流年(辰)、流月(申) 與本命年支、時支(均為子) 三合 水局
   */
  fun Set<FlowPattern>.translateToFlowTrilogy(): List<String> {
    return this.filterIsInstance<ToFlowTrilogy>().groupBy { it.flows }.map { (flows: Set<Pair<FlowScale, Branch>>, p: List<ToFlowTrilogy>) ->
      buildString {
        append(
          flows.joinToString("、") {
            it.first.getTitle(locale) + "(" + it.second + ")"
          }
        )
        append(" 與本命")
        append(
          p.joinToString("、") {
            it.scale.getTitle(locale) + "支"
          }
        )
        append("(")
        if (p.size > 1)
          append("均為")
        append(p.first().branch)
        append(")")
        append(" 三合 ")
        append(p.first().branch.trilogy())
        append("局")
      }
    }
  }

  /**
   * 大運、流年、流月 正沖本命地支
   * ex : 大運地支(酉) 正沖 本命時支(卯)
   * ex : 大運地支(丑) 正沖 本命月支、日支(均為 未)
   *
   * ex : 流年地支(酉) 正沖 本命時支(卯)
   * ex : 大運、流年地支(均為 丑) 正沖 本命月支、日支(均為 未)
   *
   * ex : 流月地支(酉) 正沖 本命時支(卯)
   * ex : 流年、流月地支(均為 丑) 正沖 本命月支、日支(均為 未)
   */
  fun Set<FlowPattern>.translateBranchOpposition(): List<String> {
    return this.filterIsInstance<BranchOpposition>().groupBy({ p -> p.branch }, { p -> p.scale to p.flowScale }).map { (branch, scalePairs: List<Pair<Scale, FlowScale>>) ->
      val ewScales = scalePairs.map { it.first }.toSet()
      val flowScales = scalePairs.map { it.second }.toSet()
      buildString {
        append(
          flowScales.joinToString("、") {
            it.getTitle(locale)
          }
        )
        append("地支")
        append("(")
        if (flowScales.size > 1)
          append("均為 ")
        append(branch.opposite)
        append(")")

        append(" 正沖 ")

        append("本命")
        append(
          ewScales.joinToString("、") {
            it.getTitle(locale) + "支"
          }
        )

        append("(")
        if (ewScales.size > 1)
          append("均為 ")
        append(branch)
        append(")")
      }
    }
  }
}
