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
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.eightwords.HiddenStemsStandardImpl
import destiny.core.chinese.eightwords.IHiddenStems

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

  /**
   * 吉祥日 (天赦日、玉堂日 ...）
   */
  val auspiciousDay = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<AuspiciousDay> {
      return buildSet {

        // 天赦日
        run {
          val applied = when (this@getPatterns.month.branch) {
            in listOf(寅, 卯, 辰) -> this@getPatterns.day == StemBranch.戊寅
            in listOf(巳, 午, 未) -> this@getPatterns.day == StemBranch.甲午
            in listOf(申, 酉, 戌) -> this@getPatterns.day == StemBranch.戊申
            in listOf(亥, 子, 丑) -> this@getPatterns.day == StemBranch.甲子
            else                  -> false
          }
          if (applied) {
            add(AuspiciousDay("天赦日"))
          }
        }

        // 玉堂日
        run {
          // 黃道十二神煞順序 (玉堂是第8個，索引為7)
          // 青龍、明堂、天刑、朱雀、金匱、天德、白虎、玉堂、天牢、玄武、司命、勾陳
          val YU_TANG_OFFSET = 7 // 玉堂在青龍之後的偏移量 (8 - 1)
          // 確定青龍起始日地支
          val qingLongStartBranch = when (this@getPatterns.month.branch) {
            寅, 申 -> 子
            卯, 酉 -> 寅
            辰, 戌 -> 辰
            巳, 亥 -> 午
            子, 午 -> 申
            丑, 未 -> 戌
          }
          // 2. 計算玉堂日的地支
          val expectedYuTangBranch = qingLongStartBranch.next(YU_TANG_OFFSET)
          if (this@getPatterns.day.branch == expectedYuTangBranch) {
            add (AuspiciousDay("玉堂日"))
          }
        }
      }
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
