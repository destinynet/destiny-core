/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.chinese.eightwords

import destiny.core.FlowScale
import destiny.core.Scale
import destiny.core.calendar.eightwords.FlowPattern
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.calendar.eightwords.Reacting
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.trilogy
import destiny.core.electional.Dtos
import destiny.core.electional.Dtos.EwEvent.NatalBranches
import destiny.core.electional.Dtos.EwEvent.NatalStems
import destiny.tools.getTitle
import java.util.*


/**
 * 八字Flow (大運、流年、流月) 與本命四柱 特徵翻譯
 * DTOs transformer for [FlowPattern]
 */
object FlowDtoTransformer {

  private val locale = Locale.TAIWAN

  /**
   * 大運、流年同時影響本命天干
   */
  private fun Pair<Reacting, List<Affecting>>.translateAffecting(): String {
    return buildString {
      append("本命")
      append(second.joinToString("、") { it.scale.getTitle(locale) + "干" })
      append("(")
      if (second.size > 1)
        append("均為 ")
      append(second.first().stem)
      append(") ")
      val flowScales = second.flatMap { it.flowScales }.distinct().joinToString("、") { it.getTitle(locale) }
      append(
        when (first) {
          SAME       -> "與 ${flowScales}五行相同"
          PRODUCING  -> "生/洩 出${flowScales}"
          PRODUCED   -> "得到 $flowScales 五行 所生"
          DOMINATING -> "同時剋制 $flowScales (消耗能量)"
          BEATEN     -> "同時被 $flowScales 所剋制 (消耗能量)"
        }
      )
    }
  }

  fun Iterable<Affecting>.toAffectingDtos() : Set<Dtos.EwEvent.EwFlow.AffectingDto> {
    return this.groupBy { it.reacting }.map { (reacting, patterns) ->
      val description = (reacting to patterns).translateAffecting()
      val natalStems = NatalStems(patterns.map { it.scale }.toSet(), patterns.first().stem)
      val flowScales: Set<FlowScale> = patterns.flatMap { it.flowScales }.toSet()
      Dtos.EwEvent.EwFlow.AffectingDto(description, natalStems, reacting, flowScales)
    }.toSet()
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
  private fun Pair<Stem, List<StemCombined>>.translateStemCombined(): String {
    return buildString {
      append("本命")
      val distinctPillars = second.map { it.scale }.distinct()
      append(
        distinctPillars.joinToString("、") {
          it.getTitle(locale) + "干"
        }
      )
      append("(")
      if (distinctPillars.size > 1)
        append("均為 ")
      append(first)
      append(") ")

      val distinctFlowScales = second.map { it.flowScale }.distinct()
      append("被")
      append(distinctFlowScales.joinToString("、") {
        it.getTitle(locale)
      })
      append("(")
      if (distinctFlowScales.size > 1)
        append("均為 ")
      append(first.combined.first)
      append(")")
      append("合住")
      append("，")
      append(setOf(first, first.combined.first).sorted().joinToString("")).append("合化").append(first.combined.second)
    }
  }

  fun Iterable<StemCombined>.toStemCombinedDtos() : Set<Dtos.EwEvent.EwFlow.StemCombinedDto> {
    return this.groupBy { it.stem }.map { (stem, patterns: List<StemCombined>) ->
      val description = (stem to patterns).translateStemCombined()
      val natalStems = NatalStems(patterns.map { it.scale }.toSet() , stem)
      val flowStems: Dtos.EwEvent.EwFlow.FlowStems = Dtos.EwEvent.EwFlow.FlowStems(patterns.map { it.flowScale }.toSet(), stem.combined.first)
      Dtos.EwEvent.EwFlow.StemCombinedDto(description, natalStems, flowStems, stem.combined.second)
    }.toSet()
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
  private fun Pair<Branch, List<BranchCombined>>.translateBranchCombined(): String {
    return buildString {
      val distinctFlowScales = second.map { it.flowScale }.distinct()
      append(
        distinctFlowScales.joinToString("、") {
          it.getTitle(locale)
        }
      )
      append("地支")
      append("(")
      if (distinctFlowScales.size > 1)
        append("均為 ")
      append(first.combined)
      append(")")

      append(" 合住 ")

      append("本命")
      val distinctPillars = second.map { it.scale }.distinct()
      append(
        distinctPillars.joinToString("、") {
          it.getTitle(locale) + "支"
        }
      )
      append("(")
      if (distinctPillars.size > 1)
        append("均為 ")
      append(first)
      append(")")
    }
  }

  fun Iterable<BranchCombined>.toBranchCombinedDtos() : Set<Dtos.EwEvent.EwFlow.BranchCombinedDto> {
    return this.groupBy { it.branch }.map { (branch, patterns) ->
      val description = (branch to patterns).translateBranchCombined()
      val natalBranches = NatalBranches(patterns.map { it.scale }.toSet(), branch)
      val flowBranches = Dtos.EwEvent.EwFlow.FlowBranches(patterns.map { it.flowScale }.toSet(), branch.combined)
      Dtos.EwEvent.EwFlow.BranchCombinedDto(description, natalBranches, flowBranches)
    }.toSet()
  }


  /**
   * 本命已經三合取二_再拱大運或流年
   * ex : 大運(辰)與本命年柱(子)、時柱(申)三合水局
   *
   * ex : 大運(辰)與本命年柱(子)、時柱(申)三合水局
   * ex : 大運、流年(均為 辰)與本命年柱(子)、時柱(申)三合水局
   *
   * ex : 流年(辰)與本命年柱(子)、時柱(申)三合水局
   * ex : 流年、流月(均為 辰)與本命年柱(子)、時柱(申)三合水局
   */
  private fun Pair<Set<Pair<Scale, Branch>>, List<TrilogyToFlow>>.translateTrilogyToFlow(): String {
    return buildString {

      val flowsSize = second.map { it.flow.first }.size

      append(
        second.joinToString("、") {
          it.flow.first.getTitle(locale) //+ "(" + it.flow.second.getTitle(locale) + ")"
        }
      )
      append("(")
      if (flowsSize > 1)
        append("均為 ")
      append(second.first().flow.second)
      append(")")

      append("與本命")
      append(
        first.joinToString("、") {
          it.first.getTitle(locale) + "柱(" + it.second + ")"
        }
      )
      append("三合")
      append(first.first().second.trilogy()).append("局")
    }
  }

  fun Iterable<TrilogyToFlow>.toTrilogyToFlowDtos() : Set<Dtos.EwEvent.EwFlow.TrilogyToFlowDto> {
    return this.groupBy { it.pairs }.map { (pairs, patterns) ->
      val description = (pairs to patterns).translateTrilogyToFlow()

      val natalBranches = pairs.map { (scale, branch) ->
        NatalBranches(setOf(scale), branch)
      }.toSet()

      val flowBranches = Dtos.EwEvent.EwFlow.FlowBranches(patterns.map { it.flow.first }.toSet(), patterns.first().flow.second)
      Dtos.EwEvent.EwFlow.TrilogyToFlowDto(description, natalBranches, flowBranches)
    }.toSet()
  }

  /**
   * 大運流年三合_再拱本命地支
   * ex : 大運(辰)、流年(子) 與本命時支(申) 三合 水局
   * ex : 大運(辰)、流年(申) 與本命年支、時支(均為子) 三合 水局
   * ex : 流年(辰)、流月(子) 與本命時支(申) 三合 水局
   * ex : 流年(辰)、流月(申) 與本命年支、時支(均為子) 三合 水局
   */
  private fun Pair<Set<Pair<FlowScale, Branch>>, List<ToFlowTrilogy>>.translateToFlowTrilogy(): String {
    return buildString {
      append(
        first.joinToString("、") {
          it.first.getTitle(locale) + "(" + it.second + ")"
        }
      )
      append(" 與本命")
      append(
        second.joinToString("、") {
          it.scale.getTitle(locale) + "支"
        }
      )
      append("(")
      if (second.size > 1)
        append("均為")
      append(second.first().branch)
      append(")")
      append(" 三合 ")
      append(second.first().branch.trilogy())
      append("局")
    }
  }

  fun Iterable<ToFlowTrilogy>.toToFlowTrilogyDtos() : Set<Dtos.EwEvent.EwFlow.ToFlowTrilogyDto> {
    return this.groupBy { it.flows }.map { (flows: Set<Pair<FlowScale, Branch>>, patterns: List<ToFlowTrilogy>) ->
      val description = (flows to patterns).translateToFlowTrilogy()

      val natalBranches: NatalBranches = patterns.map { it.scale }.toSet().let { scales ->
        NatalBranches(scales, patterns.first().branch)
      }

      val flowBranches: Set<Dtos.EwEvent.EwFlow.FlowBranches> = flows.map { (flowScale, branch) ->
        Dtos.EwEvent.EwFlow.FlowBranches(setOf(flowScale), branch)
      }.toSet()

      Dtos.EwEvent.EwFlow.ToFlowTrilogyDto(description, natalBranches, flowBranches)
    }.toSet()
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
  private fun Pair<Branch, List<Pair<Scale, FlowScale>>>.translateBranchOpposition(): String {
    val ewScales = second.map { it.first }.toSet()
    val flowScales = second.map { it.second }.toSet()
    return buildString {
      append(
        flowScales.joinToString("、") {
          it.getTitle(locale)
        }
      )
      append("地支")
      append("(")
      if (flowScales.size > 1)
        append("均為 ")
      append(first.opposite)
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
      append(first)
      append(")")
    }
  }

  fun Iterable<BranchOpposition>.toBranchOppositionDtos() : Set<Dtos.EwEvent.EwFlow.BranchOppositionDto> {
    return this.groupBy { it.branch }.map { (branch, patterns) ->
      val description = (branch to patterns.map { it.scale to it.flowScale }).translateBranchOpposition()

      val natalBranches = NatalBranches(patterns.map { it.scale }.toSet(), branch)
      val flowBranches = Dtos.EwEvent.EwFlow.FlowBranches(patterns.map { it.flowScale }.toSet(), branch.opposite)

      Dtos.EwEvent.EwFlow.BranchOppositionDto(description, natalBranches, flowBranches)
    }.toSet()
  }
}
