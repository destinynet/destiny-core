/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import com.google.common.collect.Sets
import destiny.core.Scale
import destiny.core.calendar.eightwords.IdentityPattern.*
import destiny.core.calendar.eightwords.IdentityPatterns.branchCombined
import destiny.core.calendar.eightwords.IdentityPatterns.branchOpposition
import destiny.core.calendar.eightwords.IdentityPatterns.stemCombined
import destiny.core.calendar.eightwords.IdentityPatterns.stemRooted
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.Stem
import destiny.core.chinese.eightwords.HiddenStemsStandardImpl
import destiny.core.chinese.eightwords.IHiddenStems
import destiny.core.chinese.trilogy

interface IdentityPatternFactory {
  fun IEightWords.getPatterns(): Set<IdentityPattern>
}

object IdentityPatterns {

  /** 天干五合 */
  val stemCombined = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<StemCombined> {
      return Sets.combinations(getScaleMap().map { (scale, v) -> scale to v.stem }.toSet(), 2)
        .filter { pillars: Set<Pair<Scale, Stem>> ->
          val pairList = pillars.toList()
          val p1 = pairList[0]
          val p2 = pairList[1]
          p1.second.combined.first == p2.second
        }.map { pillars -> StemCombined(pillars) }
        .toSet()
    }
  }

  /** 地支六合 */
  val branchCombined = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<BranchCombined> {
      return Sets.combinations(getScaleMap().entries.map { (scale: Scale, sb: IStemBranch) -> scale to sb.branch }.toSet(), 2).filter { pairs: Set<Pair<Scale, Branch>> ->
        val pairList = pairs.toList()
        val p1 = pairList[0]
        val p2 = pairList[1]
        p1.second.combined == p2.second
      }.map { pillars ->
        BranchCombined(pillars)
      }.toSet()
    }
  }

  /** 地支三合 */
  val trilogy = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<Trilogy> {
      return Sets.combinations(getScaleMap().entries.map { (scale: Scale, sb: IStemBranch) -> scale to sb.branch }.toSet(), 3).filter { triples: Set<Pair<Scale, Branch>> ->
        val tripleList = triples.toList()
        val pair1 = tripleList[0]
        val pair2 = tripleList[1]
        val pair3 = tripleList[2]
        trilogy(pair1.second, pair2.second, pair3.second) != null
      }.map { triples: Set<Pair<Scale, Branch>> ->
        Trilogy(triples)
      }.toSet()
    }
  }

  /** 地支對沖 */
  val branchOpposition = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<BranchOpposition> {
      return Sets.combinations(getScaleMap().entries.map { (scale: Scale, sb: IStemBranch) -> scale to sb.branch }.toSet(), 2).filter { pairs: Set<Pair<Scale, Branch>> ->
        val pairList = pairs.toList()
        val p1 = pairList[0]
        val p2 = pairList[1]
        p1.second.opposite == p2.second
      }.map { pillars ->
        BranchOpposition(pillars)
      }.toSet()
    }
  }

  val stemRooted = object : IdentityPatternFactory {
    val hidImpl: IHiddenStems = HiddenStemsStandardImpl()

    @Suppress("UNCHECKED_CAST")
    override fun IEightWords.getPatterns(): Set<StemRooted> {
      return Sets.cartesianProduct(
        getScaleMap().map { (scale, sb) -> scale to sb.stem  }.toSet(),
        getScaleMap ().map { (scale, sb) -> scale to sb.branch  }.toSet()
      ).asSequence().filter { (stemPair , branchPair: Pair<Scale, Enum<*>>) ->
        val stem  = stemPair.second as Stem
        val branch = branchPair.second as Branch
        hidImpl.getHiddenStems(branch).contains(stem)
      }.map { (stemPair, branchPair: Pair<Scale, Enum<*>>) -> (stemPair as Pair<Scale, Stem>) to (branchPair as Pair<Scale, Branch>) }
        .groupBy ({ (stemPair, _) -> stemPair } , {(_ , branchPair) -> branchPair})
        .map { (stemPair: Pair<Scale, Stem>, branchPairs: List<Pair<Scale, Branch>>) ->
          val scale = stemPair.first
          val stem = stemPair.second
          StemRooted(scale , stem , branchPairs.toSet())
        }
        .toSet()
    }
  }

}

fun IEightWords.getIdentityPatterns(): Set<IdentityPattern> {
  return setOf(
    stemCombined, branchCombined, IdentityPatterns.trilogy, branchOpposition, stemRooted
  ).flatMap { factory: IdentityPatternFactory ->
    with(factory) {
      this@getIdentityPatterns.getPatterns()
    }
  }.toSet()
}
