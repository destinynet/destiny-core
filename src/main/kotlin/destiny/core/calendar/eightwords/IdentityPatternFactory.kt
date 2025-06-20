/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import arrow.core.Either
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
import destiny.core.chinese.impls.TianyiAuthorizedImpl
import destiny.core.chinese.impls.YangBladeRobCashImpl
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
        getScaleMap().map { (scale, sb) -> scale to sb.stem }.toSet(),
        getScaleMap().map { (scale, sb) -> scale to sb.branch }.toSet()
      ).asSequence().filter { (stemPair, branchPair: Pair<Scale, Enum<*>>) ->
        val stem = stemPair.second as Stem
        val branch = branchPair.second as Branch
        hidImpl.getHiddenStems(branch).contains(stem)
      }.map { (stemPair, branchPair: Pair<Scale, Enum<*>>) -> (stemPair as Pair<Scale, Stem>) to (branchPair as Pair<Scale, Branch>) }
        .groupBy({ (stemPair, _) -> stemPair }, { (_, branchPair) -> branchPair })
        .map { (stemPair: Pair<Scale, Stem>, branchPairs: List<Pair<Scale, Branch>>) ->
          val scale = stemPair.first
          val stem = stemPair.second
          StemRooted(scale, stem, branchPairs.toSet())
        }
        .toSet()
    }
  }

  /**
   * 吉祥 (天赦日、玉堂日 ...）
   */
  val auspiciousPattern = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<AuspiciousPattern> {
      val monthBranch = this@getPatterns.month.branch
      val day = this@getPatterns.day
      val dayStem = this@getPatterns.day.stem
      val dayBranch = this@getPatterns.day.branch

      fun Branch.天醫(): Branch {
        return this.prev
      }

      // 正月生者見丁，二月生者見申，三月生者見壬，四月生者見辛，五月生者見亥，六月生者見甲， 七月生者見癸，八月生者見寅， 九月生者見丙，十月生者見乙，十一隻主者見已，十二月生者見庚
      fun Branch.天德(): Either<Stem, Branch> {
        return when (this) {
          寅 -> Either.Left(丁)
          卯 -> Either.Right(申)
          辰 -> Either.Left(壬)
          巳 -> Either.Left(辛)
          午 -> Either.Right(亥)
          未 -> Either.Left(甲)
          申 -> Either.Left(癸)
          酉 -> Either.Right(寅)
          戌 -> Either.Left(丙)
          亥 -> Either.Left(乙)
          丑 -> Either.Left(庚)
          子 -> Either.Right(子)
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
            add(AuspiciousPattern(Auspicious.天赦日, setOf(Scale.DAY)))
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
            add(AuspiciousPattern(Auspicious.玉堂日, setOf(Scale.DAY)))
          }
        }

        // 天德貴人
        run {
          when (val tianDe = monthBranch.天德()) {
            is Either.Left  -> {
              this@getPatterns.getScaleMap().filter { (_, stemBranch) ->
                tianDe.value == stemBranch.stem
              }.map { (scale, _) -> scale }
            }

            is Either.Right -> {
              this@getPatterns.getScaleMap().filter { (_, stemBranch) ->
                tianDe.value == stemBranch.branch
              }.map { (scale, _) -> scale }
            }
          }.toSet()
            .takeIf { it.isNotEmpty() }
            ?.also { add(AuspiciousPattern(Auspicious.天德貴人, it)) }
        }

        // 月德貴人
        run {
          // 口訣：寅午戌月在丙，申子辰月在壬，亥卯未月在甲，巳酉丑月在庚。
          val expectedDayStem = when (monthBranch) {
            寅, 午, 戌 -> 丙
            申, 子, 辰 -> 壬
            亥, 卯, 未 -> 甲
            巳, 酉, 丑 -> 庚
          }

          this@getPatterns.getScaleMap().filter { (scale, stemBranch) ->
            scale in setOf(Scale.DAY, Scale.HOUR) && stemBranch.stem == expectedDayStem
          }.map { (scale, _) -> scale }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.also { add(AuspiciousPattern(Auspicious.月德貴人, it)) }
        }

        // 天德合
        run {
          when (val tianDe = monthBranch.天德()) {
            is Either.Left  -> {
              val tianDeCombined = tianDe.value.combined.first
              this@getPatterns.getScaleMap().filter { (_, stemBranch) ->
                stemBranch.stem == tianDeCombined
              }.map { (scale, _) -> scale }
            }

            is Either.Right -> {
              emptySet()
            }
          }.toSet()
            .takeIf { it.isNotEmpty() }
            ?.also { add(AuspiciousPattern(Auspicious.天德合, it)) }
        }

        // 天乙貴人
        run {
          // 以 Authorized 為主
          val tianyiImpl = TianyiAuthorizedImpl()
          val tianyiBranches = tianyiImpl.getTianyis(this@getPatterns.day.stem)

          this@getPatterns.getScaleMap()
            .filter { (_, stemBranch) ->
              stemBranch.branch in tianyiBranches
            }
            .map { (scale, _) -> scale }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.also {
              add(AuspiciousPattern(Auspicious.天乙貴人, it))
            }
        }

        // 天醫貴人
        run {
          val expectedTianyiBranch = monthBranch.天醫()

          this@getPatterns.getScaleMap()
            .filter { (scale, stemBranch) ->
              scale != Scale.MONTH && stemBranch.branch == expectedTianyiBranch  // 排除月柱
            }
            .map { (scale, _) -> scale }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.also {
              add(AuspiciousPattern(Auspicious.天醫, it))
            }
        }

      }
    }
  }

  /**
   * 不祥
   */
  val inauspiciousPattern = object : IdentityPatternFactory {
    override fun IEightWords.getPatterns(): Set<InauspiciousPattern> {
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
            add(InauspiciousPattern(Inauspicious.受死日, setOf(Scale.DAY)))
          }
        }

        // 陰差陽錯日
        run {
          this@getPatterns.getScaleMap().filter { (_, stemBranch: IStemBranch) ->
            setOf(丙子, 丙午, 丁丑, 丁未, 戊寅, 戊申, 辛卯, 辛酉, 壬辰, 壬戌, 癸巳, 癸亥).any {
              it.stem == stemBranch.stem && it.branch == stemBranch.branch
            }
          }.map { (scale, _) -> scale }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.also {
              add(InauspiciousPattern(Inauspicious.陰差陽錯, it))
            }
        }

        // 十惡大敗
        run {
          val tenEvilsStemBranches = setOf(
            甲辰, 乙巳, 丙申, 丁亥, 戊戌,
            己丑, 庚辰, 辛巳, 壬申, 癸亥
          )

          this@getPatterns.getScaleMap()
            .filter { (_, stemBranch: IStemBranch) ->
              tenEvilsStemBranches.any { evilSB ->
                evilSB.stem == stemBranch.stem && evilSB.branch == stemBranch.branch
              }
            }
            .map { (scale, _) -> scale }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.also {
              add(InauspiciousPattern(Inauspicious.十惡大敗, it))
            }
        }

        // 四廢日
        run {
          val isFourWasteDay = when (monthBranch) {
            // 春季（寅卯辰月）：庚申、辛酉日
            寅, 卯, 辰 -> day in setOf(庚申, 辛酉)
            // 夏季（巳午未月）：壬子、癸亥日
            巳, 午, 未 -> day in setOf(壬子, 癸亥)
            // 秋季（申酉戌月）：甲寅、乙卯日
            申, 酉, 戌 -> day in setOf(甲寅, 乙卯)
            // 冬季（亥子丑月）：丙午、丁巳日
            亥, 子, 丑 -> day in setOf(丙午, 丁巳)
          }

          if (isFourWasteDay) {
            add(InauspiciousPattern(Inauspicious.四廢日, setOf(Scale.DAY)))
          }
        }

        // 羊刃
        run {
          // 羊刃的規則：以日干為主，查其他柱的地支
          val yangRenBranch = YangBladeRobCashImpl.getYangBlade(this@getPatterns.day.stem)
          this@getPatterns.getScaleMap()
            .filter { (scale, stemBranch) ->
              scale != Scale.DAY && stemBranch.branch == yangRenBranch
            }
            .map { (scale, _) -> scale }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.also {
              add(InauspiciousPattern(Inauspicious.羊刃, it))
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
