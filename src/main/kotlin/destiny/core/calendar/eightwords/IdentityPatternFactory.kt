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
import destiny.core.chinese.Branch.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
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

  /**
   * 吉祥日 (天赦日、玉堂日 ...）
   */
  val auspiciousDay = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<AuspiciousDay> {
      val monthBranch = this@getPatterns.month.branch
      val day = this@getPatterns.day
      val dayStem = this@getPatterns.day.stem
      val dayBranch = this@getPatterns.day.branch

      fun Branch.天德() : Stem? {
        return when(this) {
          寅   -> 丁
          辰   -> 壬
          巳   -> 辛
          未   -> 甲
          申   -> 癸
          戌   -> 丙
          亥   -> 乙
          丑   -> 庚
          else -> null // 卯(申), 午(亥), 酉(寅), 子(巳) 通常指該日支為天德，較少指日干
        }
      }

      return buildSet {

        // 天赦日
        run {
          val applied = when (monthBranch) {
            in listOf(寅, 卯, 辰) -> day == 戊寅
            in listOf(巳, 午, 未) -> day == 甲午
            in listOf(申, 酉, 戌) -> day == 戊申
            in listOf(亥, 子, 丑) -> day == 甲子
            else                  -> false
          }
          if (applied) {
            add(AuspiciousDay(Auspicious.天赦日))
          }
        }

        // 玉堂日
        run {
          // 黃道十二神煞順序 (玉堂是第8個，索引為7)
          // 青龍、明堂、天刑、朱雀、金匱、天德、白虎、玉堂、天牢、玄武、司命、勾陳
          val YU_TANG_OFFSET = 7 // 玉堂在青龍之後的偏移量 (8 - 1)
          // 確定青龍起始日地支
          val qingLongStartBranch = when (monthBranch) {
            寅, 申 -> 子
            卯, 酉 -> 寅
            辰, 戌 -> 辰
            巳, 亥 -> 午
            子, 午 -> 申
            丑, 未 -> 戌
          }
          // 2. 計算玉堂日的地支
          val expectedYuTangBranch = qingLongStartBranch.next(YU_TANG_OFFSET)
          if (dayBranch == expectedYuTangBranch) {
            add (AuspiciousDay(Auspicious.玉堂日))
          }
        }

        // 天德貴人
        run {
          if (monthBranch.天德() == dayStem) {
            add(AuspiciousDay(Auspicious.天德貴人))
          }
        }

        // 月德貴人
        run {
          // 口訣：寅午戌月在丙，申子辰月在壬，亥卯未月在甲，巳酉丑月在庚。
          val expectedDayStem = when(monthBranch) {
            寅, 午, 戌 -> 丙
            申, 子, 辰 -> 壬
            亥, 卯, 未 -> 甲
            巳, 酉, 丑 -> 庚
          }
          if (expectedDayStem == dayStem) {
            add(AuspiciousDay(Auspicious.月德貴人))
          }
        }

        // 天德合
        run {
          monthBranch.天德()?.combined?.first?.also {
            if (it == dayStem) {
              add(AuspiciousDay(Auspicious.天德合))
            }
          }
        }
      }
    }
  }

  /**
   * 不祥日
   */
  val inauspiciousDay = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<InauspiciousDay> {
      val monthBranch = this@getPatterns.month.branch
      val day = this@getPatterns.day
      val dayStem = this@getPatterns.day.stem
      val dayBranch = this@getPatterns.day.branch


      return buildSet {
        // 受死日
        // 受死日的求法：
        // 正月起戌，然後從戌起單月順排。
        // 如正月起戌，三月為戌的下一位為亥，五月為子，依次類推。（正三五七九十一）
        //
        // 二月起辰，然後從辰起雙月順排。
        // 如二月起辰，四月為辰的下一位為巳，六月為午，依次類推。
        run {

//          val expectedBranch = monthBranch.indexFromOne.let { indexFromOne ->
//            if (indexFromOne % 2 == 1) {
//              // 單月 ,
//              // 寅 = 3 , (3-1)/2 - 1 = 0
//              // 辰 = 5 , (5-1)/2 - 1 = 1
//              // ...
//              // 戌 = 11, (11-1)/2 -1 = 4
//              // 子 = 1 , (1-1)/2 - 1 = -1 ==> change to 5
//              val steps = ((indexFromOne - 1) / 2 - 1).let {
//                if (it < 0)
//                  6 + it
//                else
//                  it
//              }
//              戌.next(steps)
//            } else {
//              // 雙月 ,
//              // 卯 = 4 ,  4/2 - 2 = 0
//              // 巳 = 6 ,  6/2 - 2 = 1
//              // ...
//              // 亥 = 12, 12/2 - 2 = 4
//              // 丑 =  2, 2/2  - 2 =-1 ==> change to 5
//              val steps = (indexFromOne / 2 - 2).let {
//                if (it < 0)
//                  6 + it
//                else
//                  it
//              }
//              辰.next(steps)
//            }
//          }

          val expectedBranch: Branch = when (monthBranch) {
            寅 -> 戌
            卯 -> 辰
            辰 -> 亥
            巳 -> 巳
            午 -> 子
            未 -> 午
            申 -> 丑
            酉 -> 未
            戌 -> 寅
            亥 -> 申
            子 -> 卯
            丑 -> 酉
          }
          if (dayBranch == expectedBranch) {
            add(InauspiciousDay(Inauspicious.受死日))
          }
        }

        // 陰差陽錯日
        run {
          if (setOf(丙子, 丙午, 丁丑, 丁未, 戊寅, 戊申, 辛卯, 辛酉, 壬辰, 壬戌, 癸巳, 癸亥).contains(day)) {
            add(InauspiciousDay(Inauspicious.陰差陽錯))
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
