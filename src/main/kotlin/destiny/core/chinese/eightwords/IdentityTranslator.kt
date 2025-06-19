/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.Scale
import destiny.core.calendar.eightwords.IdentityPattern
import destiny.core.calendar.eightwords.IdentityPattern.*
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem
import destiny.core.chinese.trilogy
import destiny.tools.getTitle
import java.util.*


object IdentityTranslator {

  private val locale = Locale.TAIWAN

  /**
   * 天干五合
   * ex : 年干(丁) 與 月干(壬) 合木
   * ex : 年干(丁) 與 月干、時干(均為壬) 合木
   * ex : 年干、日干(均為丁) 與 月干、時干(均為壬) 合木
   */
  fun Set<IdentityPattern>.translateStemCombined(): List<String> {
    return this.filterIsInstance<StemCombined>().groupBy { p -> p.pillars.first().second.combined.second }
      .map { (five: FiveElement, p: List<StemCombined>) ->
        val groupedPillars: List<Pair<Stem, List<Pair<Scale, Stem>>>> = p.flatMap { it.pillars }.distinct().groupBy { (_, stem) -> stem }.toList()
        buildString {
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
          append(" 合").append(five)
        }
      }.toList()
  }

  /**
   * 地支六合
   * ex : 年支(子) 六合 月支(丑)
   * ex : 年支(子) 六合 月支、日支、時支(均為 丑)
   * ex : 年支、月支、時支(均為 子) 六合 日支(丑)
   * ex : 年支、月支(均為 子) 六合 日支、時支(均為 丑)
   */
  fun Set<IdentityPattern>.translateBranchCombined(): List<String> {
    return this.filterIsInstance<BranchCombined>().groupBy { p -> p.pillars.map { it.second }.toSet() }.map { (k,v) ->
      v.flatMap { it.pillars }.groupBy ({ it.second } , {it.first}).map { (branch,scales) ->
        buildString {
          append(
            scales.distinct().joinToString("、") { s ->
              s.getTitle(locale)+"支"
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
  }

  /**
   * 地支三合
   * ex : 年支、月支、日支三合木局
   */
  fun Set<IdentityPattern>.translateTrilogy(): List<String> {
    return this.filterIsInstance<Trilogy>().map { trilogy ->
      buildString {
        append(
          trilogy.pillars.joinToString("、") {
            it.first.getTitle(locale) + "支"
          }
        )
        append("三合")
        append(trilogy.pillars.first().second.trilogy().getTitle(locale)).append("局")
      }
    }
  }

  /**
   * 地支六沖
   * ex : 年支(丑) 正沖 日支(未)
   * ex : 年支(未) 正沖 月支、日支、時支(均為 丑)
   * ex : 年支、時支(均為 未) 正沖 月支、日支(均為 丑)
   */
  fun Set<IdentityPattern>.translateBranchOpposition() : List<String> {
    return this.filterIsInstance<BranchOpposition>().groupBy { p -> p.pillars.map { it.second }.toSet() }.map { (_, v) ->
      v.flatMap { it.pillars }.groupBy ({ it.second } , {it.first}).map { (branch, scales) ->
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
  }

  /**
   * 天干通根
   * ex : 年干(丁) 通根 日支(未)、時支(午)
   * ex : 年干、時干(均為 丁) 通根 日支(未)
   */
  fun Set<IdentityPattern>.translateStemRooted() : List<String> {
    return this.filterIsInstance<StemRooted>().groupBy { p -> p.roots }.map { (roots: Set<Pair<Scale, Branch>>, patterns: List<StemRooted>) ->
      buildString {
        append(
          patterns.joinToString("、") {
            it.scale.getTitle(locale) + "干"
          }
        )
        append("(")
        if (patterns.size > 1)
          append("均為 ")
        append(patterns.first().stem)
        append(")")

        append(" 通根 ")
        append(roots.joinToString("、") {
          it.first.getTitle(locale)+"支"+"("+it.second+")"
        })
      }

    }
  }

  fun Set<IdentityPattern>.translateAuspiciousDays() : List<String> {
    return listOf(
      this.filterIsInstance<AuspiciousPattern>().joinToString("、") {
        it.value.name
      }
    )
  }

  fun Set<IdentityPattern>.translateInauspiciousDays(): List<String> {
    return listOf(
      this.filterIsInstance<InauspiciousPattern>().joinToString("、") {
        it.value.name
      }
    )
  }


}
