/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.Scale
import destiny.core.calendar.eightwords.Auspicious
import destiny.core.calendar.eightwords.IdentityPattern
import destiny.core.calendar.eightwords.IdentityPattern.*
import destiny.core.calendar.eightwords.Inauspicious
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem
import destiny.core.chinese.trilogy
import destiny.core.electional.Dtos
import destiny.core.electional.Dtos.EwEvent.NatalBranches
import destiny.core.electional.Dtos.EwEvent.NatalStems
import destiny.tools.getTitle
import java.util.*


/**
 * DTOs transformer for [IdentityPattern]
 */
object IdentityDtoTransformer {

  private val locale = Locale.TAIWAN

  /**
   * 天干五合
   * ex : 年干(丁) 與 月干(壬) 合木
   * ex : 年干(丁) 與 月干、時干(均為壬) 合木
   * ex : 年干、日干(均為丁) 與 月干、時干(均為壬) 合木
   */
  private fun Pair<FiveElement , List<StemCombined>>.translateStemCombined(): String {
    val groupedPillars: List<Pair<Stem, List<Pair<Scale, Stem>>>> = this.second.flatMap { it.pillars }.distinct().groupBy { (_, stem) -> stem }.toList()

    return buildString {
      append(
        groupedPillars.joinToString(" 與 ") { (stem: Stem, list: List<Pair<Scale, Stem>>) ->
          buildString {
            append(
              list.joinToString("、") {
                it.first.getTitle(locale) + "干"
              }
            )
            append("(")
            if (list.size > 1)
              append("均為")
            append(stem)
            append(")")
          }
        }
      )
      append(" 合").append(this@translateStemCombined.first)
    }
  }

  fun Iterable<StemCombined>.toStemCombinedDtos(): Set<Dtos.EwEvent.EwIdentity.StemCombinedDto> {
    return this.groupBy { p -> p.pillars.first().second.combined.second }
      .map { (five: FiveElement, patterns) ->
        val description = (five to patterns).translateStemCombined()

        val natalStems = patterns.flatMap { it.pillars }.groupBy { it.second }.map { (stem, scales) ->
          NatalStems(scales.map { it.first }.toSet(), stem)
        }.toSet()

        Dtos.EwEvent.EwIdentity.StemCombinedDto(description, natalStems, five)
      }.toSet()
  }

  /**
   * 地支六合
   * ex : 年支(子) 六合 月支(丑)
   * ex : 年支(子) 六合 月支、日支、時支(均為 丑)
   * ex : 年支、月支、時支(均為 子) 六合 日支(丑)
   * ex : 年支、月支(均為 子) 六合 日支、時支(均為 丑)
   */

  private fun List<BranchCombined>.translateBranchCombined(): String {
    return this.flatMap { it.pillars }.groupBy({ it.second }, { it.first }).map { (branch, scales) ->
      buildString {
        append(
          scales.distinct().joinToString("、") { s ->
            s.getTitle(locale) + "支"
          }
        )
        append("(")
        if (scales.distinct().size > 1)
          append("均為 ")
        append(branch)
        append(")")
      }
    }.joinToString(" 六合 ")
  }

  fun Iterable<BranchCombined>.toBranchCombinedDtos(): Set<Dtos.EwEvent.EwIdentity.BranchCombinedDto> {
    return this.groupBy { p -> p.pillars.map { it.second }.toSet() }
      .map { (_: Set<Branch>, patterns: List<BranchCombined>) ->
        val description = patterns.translateBranchCombined()

        val natalBranches = patterns.flatMap { it.pillars }
          .groupBy { it.second } // Group by Branch
          .map { (branch, pairs) ->
            NatalBranches(pairs.map { it.first }.toSet(), branch)
          }.toSet()

        Dtos.EwEvent.EwIdentity.BranchCombinedDto(description, natalBranches)
      }.toSet()
  }

  /**
   * 地支三合
   * ex : 年支、月支、日支三合木局
   */
  private fun Trilogy.translateTrilogy() : String {
    return buildString {
      append(
        pillars.joinToString("、") {
          it.first.getTitle(locale) + "支"
        }
      )
      append("三合")
      append(pillars.first().second.trilogy().getTitle(locale)).append("局")
    }
  }

  fun Iterable<Trilogy>.toTrilogyDtos(): Set<Dtos.EwEvent.EwIdentity.TrilogyDto> {
    return this.map { pattern ->
      val description = pattern.translateTrilogy()

      val natalBranches = pattern.pillars.map { (scale, branch) ->
        NatalBranches(setOf(scale), branch)
      }.toSet()

      val trilogyElement = pattern.pillars.first().second.trilogy()

      Dtos.EwEvent.EwIdentity.TrilogyDto(description, natalBranches, trilogyElement)
    }.toSet()
  }

  /**
   * ex : 年支(丑) 正沖 日支(未)
   * ex : 年支(未) 正沖 月支、日支、時支(均為 丑)
   * ex : 年支、時支(均為 未) 正沖 月支、日支(均為 丑)
   */
  private fun List<BranchOpposition>.translateBranchOpposition() : String {
    return this.flatMap { it.pillars }.groupBy({ it.second }, { it.first }).map { (branch: Branch, scales: List<Scale>) ->
      buildString {
        append(
          scales.distinct().joinToString("、") { s ->
            s.getTitle(locale) + "支"
          }
        )
        append("(")
        if (scales.distinct().size > 1)
          append("均為 ")
        append(branch)
        append(")")
      }
    }.joinToString(" 正沖 ")
  }

  fun Iterable<BranchOpposition>.toBranchOppositionDtos(): Set<Dtos.EwEvent.EwIdentity.BranchOppositionDto> {
    return this.groupBy { p -> p.pillars.map { it.second }.toSet() }
      .map { (_, patterns: List<BranchOpposition>) ->
        val description = patterns.translateBranchOpposition()

        val natalBranches = patterns.flatMap { it.pillars }
          .groupBy { it.second } // Group by Branch
          .map { (branch, pairs) ->
            NatalBranches(pairs.map { it.first }.toSet(), branch)
          }.toSet()

        Dtos.EwEvent.EwIdentity.BranchOppositionDto(description, natalBranches)
      }.toSet()
  }

  /**
   * 天干通根
   * ex : 年干(丁) 通根 日支(未)、時支(午)
   * ex : 年干、時干(均為 丁) 通根 日支(未)
   */

  private fun Pair<Set<Pair<Scale, Branch>> , List<StemRooted>>.translateStemRooted() : String {
    return buildString {
      append(
        second.joinToString("、") {
          it.scale.getTitle(locale) + "干"
        }
      )
      append("(")
      if (second.size > 1)
        append("均為 ")
      append(second.first().stem)
      append(")")

      append(" 通根 ")
      append(first.joinToString("、") {
        it.first.getTitle(locale) + "支" + "(" + it.second + ")"
      })
    }
  }

  fun Iterable<StemRooted>.toStemRootedDtos(): Set<Dtos.EwEvent.EwIdentity.StemRootedDto> {
    return this.groupBy { it.roots }
      .map { (roots, patterns) ->
        val description = (roots to patterns).translateStemRooted()

        val natalStems = patterns.groupBy { it.stem }
          .map { (stem, stemPatterns) ->
            NatalStems(stemPatterns.map { it.scale }.toSet(), stem)
          }.toSet()

        val natalBranches = roots.map { (scale, branch) ->
          NatalBranches(setOf(scale), branch)
        }.toSet()

        Dtos.EwEvent.EwIdentity.StemRootedDto(description, natalStems, natalBranches)
      }.toSet()
  }

  fun Iterable<AuspiciousPattern>.toAuspiciousDto(): Dtos.EwEvent.EwIdentity.AuspiciousDto {
    val scaleMap: Map<Scale, Set<Auspicious>> = Scale.entries.associateWith { scale ->
      this.filter { it.scales.contains(scale) }.map { it.value }.toSet()
    }

    val description = scaleMap.mapNotNull {  (scale, auspiciousSet) ->
      if (auspiciousSet.isEmpty()) {
        null
      } else {
        scale.getTitle(locale) + "柱 : " + auspiciousSet.joinToString("、") { it.name }
      }
    }.joinToString("；")

    return Dtos.EwEvent.EwIdentity.AuspiciousDto(description, scaleMap)
  }


  fun Iterable<InauspiciousPattern>.toInauspiciousDto(): Dtos.EwEvent.EwIdentity.InauspiciousDto {
    val scaleMap: Map<Scale, Set<Inauspicious>> = Scale.entries.associateWith { scale ->
      this.filter { it.scales.contains(scale) }.map { it.value }.toSet()
    }

    val description = scaleMap.mapNotNull {  (scale, inauspiciousSet) ->
      if (inauspiciousSet.isEmpty()) {
        null
      } else {
        scale.getTitle(locale) + "柱 : " + inauspiciousSet.joinToString("、") { it.name }
      }
    }.joinToString("；")

    return Dtos.EwEvent.EwIdentity.InauspiciousDto(description, scaleMap)
  }


}
