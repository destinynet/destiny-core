/**
 * Created by smallufo on 2026-10-18.
 */
package destiny.core.iching.divine

import destiny.core.chinese.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.SimpleBranch.Companion.getFiveElement


/**
 * 六爻盤上**可計算的事實**：各爻對月建、日辰、變爻、世應、其他動爻的生剋沖合刑害、旬空、長生墓絕。
 *
 * 只陳述、不裁決：「旬空、臨月建」照列，不判「空而不空」；「靜爻被日沖」照列，不判暗動或日破；
 * 土的長生墓絕在「隨水」「隨火」兩派不同時兩說並列。裁決交給讀的人（或模型），因為各派判法不同。
 *
 * 由 [LiuYaoFacts.of] 從 [ICombinedWithMetaNameDayMonth] 算出；日辰、月令缺的話，對應欄位為 null。
 *
 * 納甲（[SettingsOfStemBranch] 三種）與伏神（[HiddenEnergy] 兩種）的差異已在 plate 裡決定，事實層照單全收；
 * 兩者一併帶在 [settings]、[hiddenEnergy]，輸出時必須標明，否則讀的人無從得知用的是哪一套。
 */
data class LiuYaoFacts(
  val plate: ICombinedWithMetaNameDayMonth,
  /** 納甲系統 */
  val settings: SettingsOfStemBranch,
  /** 伏神系統 */
  val hiddenEnergy: HiddenEnergy,
  /** 初爻在前 */
  val lines: List<LineFacts>,
  val self: Int,
  val oppo: Int,
  /** 世爻對應爻：五行以「應對世」表述 */
  val selfOppo: Tie,
  val srcPatterns: Set<HexPattern>,
  val dstPatterns: Set<HexPattern>,
  /** 本卦不上卦的六親 */
  val missingRelatives: Set<Relative>,
  /** 動爻與變爻的地支中，三支湊齊的三合局 */
  val trilogies: List<Pair<Set<Branch>, FiveElement>>,
) {

  val movingLines: List<LineFacts> get() = lines.filter { it.moving }

  companion object {

    fun of(plate: ICombinedWithMetaNameDayMonth): LiuYaoFacts {
      val src = plate.srcModel
      val dst = plate.dstModel
      val month = plate.monthBranch
      val day = plate.day?.branch
      val voids = plate.voids
      val moving: Set<Int> = src.getMotivatedLines(dst)

      fun pillar(pillar: Branch?, target: Branch): Tie? = pillar?.let { Tie.of(it, target) }

      val lines = (1..6).map { pos ->
        val sb = src.納甲[pos - 1]
        val change = if (pos in moving) {
          val to = dst.納甲[pos - 1]
          ChangeFacts(
            to = to,
            toRelative = plate.變卦對於本卦的六親[pos - 1],
            tie = Tie.of(to.branch, sb.branch),
            step = Step.of(sb.branch, to.branch),
            stages = KeyStage.of(getFiveElement(sb.branch), to.branch),
            month = pillar(month, to.branch),
            day = pillar(day, to.branch),
            void = to.branch in voids,
          )
        } else null

        val hidden = src.伏神納甲[pos - 1]?.let { h ->
          HiddenFacts(
            sb = h,
            relative = src.伏神六親[pos - 1]!!,
            fly = Tie.of(sb.branch, h.branch),
            month = pillar(month, h.branch),
            day = pillar(day, h.branch),
            void = h.branch in voids,
          )
        }

        LineFacts(
          position = pos,
          yang = src.getBoolean(pos),
          moving = pos in moving,
          isSelf = src.self == pos,
          isOppo = src.oppo == pos,
          animal = plate.sixAnimals.getOrNull(pos - 1),
          relative = src.relatives[pos - 1],
          sb = sb,
          month = pillar(month, sb.branch),
          monthStages = month?.let { KeyStage.of(getFiveElement(sb.branch), it) } ?: emptyList(),
          day = pillar(day, sb.branch),
          dayStages = day?.let { KeyStage.of(getFiveElement(sb.branch), it) } ?: emptyList(),
          void = sb.branch in voids,
          change = change,
          hidden = hidden,
        )
      }

      // 動爻對其他各爻（本卦）的作用：以「動爻對該爻」表述
      val linesWithActs = lines.map { line ->
        if (!line.moving) line
        else line.copy(acts = lines.filter { it.position != line.position }.mapNotNull { target ->
          Tie.of(line.sb.branch, target.sb.branch).takeIf { it.effect != Effect.同 || it.ties.isNotEmpty() }
            ?.let { target.position to it }
        })
      }

      val movingAndChanged: Set<Branch> = lines.filter { it.moving }.flatMap { listOf(it.sb.branch, it.change!!.to.branch) }.toSet()
      val trilogies = movingAndChanged.groupBy { it.trilogy() }
        .filterValues { it.size == 3 }
        .map { (element, branches) -> branches.toSet() to element }

      return LiuYaoFacts(
        plate = plate,
        settings = plate.settings,
        hiddenEnergy = plate.hiddenEnergy,
        lines = linesWithActs,
        self = src.self,
        oppo = src.oppo,
        selfOppo = Tie.of(src.納甲[src.oppo - 1].branch, src.納甲[src.self - 1].branch),
        srcPatterns = HexPattern.of(src.納甲.map { it.branch }),
        dstPatterns = HexPattern.of(dst.納甲.map { it.branch }),
        missingRelatives = Relative.entries.toSet() - src.relatives.toSet(),
        trilogies = trilogies,
      )
    }
  }
}

data class LineFacts(
  /** 1 ~ 6 */
  val position: Int,
  val yang: Boolean,
  val moving: Boolean,
  val isSelf: Boolean,
  val isOppo: Boolean,
  val animal: SixAnimal?,
  val relative: Relative,
  val sb: StemBranch,
  /** 月建對本爻 */
  val month: Tie?,
  /** 本爻五行在月建上的長生墓絕 */
  val monthStages: List<StageFact>,
  /** 日辰對本爻 */
  val day: Tie?,
  /** 本爻五行在日辰上的長生墓絕 */
  val dayStages: List<StageFact>,
  val void: Boolean,
  /** 動爻才有 */
  val change: ChangeFacts?,
  val hidden: HiddenFacts?,
  /** 動爻才有：本爻對其他爻（位置 → 本爻對該爻的關係），只列有生剋沖合刑害者 */
  val acts: List<Pair<Int, Tie>> = emptyList(),
)

data class ChangeFacts(
  val to: StemBranch,
  /** 依本卦的宮五行定的六親 */
  val toRelative: Relative,
  /** 變爻對本爻 */
  val tie: Tie,
  val step: Step?,
  /** 本爻五行在變爻地支上的長生墓絕（化長生、化墓、化絕…） */
  val stages: List<StageFact>,
  /** 月建對變爻 */
  val month: Tie?,
  /** 日辰對變爻 */
  val day: Tie?,
  val void: Boolean,
)

data class HiddenFacts(
  val sb: StemBranch,
  val relative: Relative,
  /** 飛神對伏神 */
  val fly: Tie,
  val month: Tie?,
  val day: Tie?,
  val void: Boolean,
)

/** 五行生剋，以「施方對受方」表述 */
enum class Effect {
  /** 同五行 */
  同,
  生,
  剋,
  /** 受方生施方（施方得受方之洩） */
  被生,
  /** 受方剋施方 */
  被剋;

  companion object {
    fun of(actor: FiveElement, target: FiveElement): Effect = when {
      actor == target              -> 同
      actor.isProducingTo(target)  -> 生
      actor.isDominatorOf(target)  -> 剋
      actor.isProducedBy(target)   -> 被生
      else                         -> 被剋
    }
  }
}

/** 地支之間的沖合刑害；[同支] 為臨（臨月建、臨日辰、伏吟） */
enum class BranchTie {
  同支, 沖, 合, 刑, 害;

  companion object {
    fun of(a: Branch, b: Branch): Set<BranchTie> = buildSet {
      if (a == b) add(同支)
      if (a.opposite == b) add(沖)
      if (a.combined == b) add(合)
      if (a.isPunishedWith(b)) add(刑)
      if (a.harmed == b) add(害)
    }
  }
}

/** 施方地支對受方地支：五行生剋＋沖合刑害 */
data class Tie(val actor: Branch, val target: Branch, val effect: Effect, val ties: Set<BranchTie>) {
  companion object {
    fun of(actor: Branch, target: Branch) =
      Tie(actor, target, Effect.of(getFiveElement(actor), getFiveElement(target)), BranchTie.of(actor, target))
  }
}

/** 化進神、化退神 */
enum class Step {
  進, 退;

  companion object {
    /** 同五行、順行一步為進（寅→卯、丑→辰…），逆行一步為退 */
    fun of(from: Branch, to: Branch): Step? {
      if (getFiveElement(from) != getFiveElement(to)) return null
      val step = if (getFiveElement(from) == 土) 3 else 1
      return when (to) {
        from.next(step)  -> 進
        from.next(-step) -> 退
        else             -> null
      }
    }
  }
}

/** 土的長生起法，兩派 */
enum class EarthRule { 隨水, 隨火 }

/** 長生十二位中，六爻常用的四個 */
enum class KeyStage {
  長生, 帝旺, 墓, 絕;

  companion object {
    private fun startOf(element: FiveElement, rule: EarthRule?): Branch = when (element) {
      木 -> Branch.亥
      火 -> Branch.寅
      金 -> Branch.巳
      水 -> Branch.申
      土 -> if (rule == EarthRule.隨火) Branch.寅 else Branch.申
    }

    private fun stageAt(element: FiveElement, rule: EarthRule?, branch: Branch): KeyStage? =
      when (branch.getAheadOf(startOf(element, rule))) {
        0    -> 長生
        4    -> 帝旺
        8    -> 墓
        9    -> 絕
        else -> null
      }

    /**
     * [element] 在 [branch] 上是否落在 長生／帝旺／墓／絕。
     * 土兩派結果相同時合成一筆（[StageFact.rule] = null），不同時兩派各列一筆。
     */
    fun of(element: FiveElement, branch: Branch): List<StageFact> {
      if (element != 土) return listOfNotNull(stageAt(element, null, branch)?.let { StageFact(it, null) })
      val water = stageAt(土, EarthRule.隨水, branch)
      val fire = stageAt(土, EarthRule.隨火, branch)
      return if (water == fire) listOfNotNull(water?.let { StageFact(it, null) })
      else listOfNotNull(water?.let { StageFact(it, EarthRule.隨水) }, fire?.let { StageFact(it, EarthRule.隨火) })
    }
  }
}

data class StageFact(val stage: KeyStage, val rule: EarthRule?)

/** 卦的整體型態 */
enum class HexPattern {
  /** 初四、二五、三上 三對皆相沖 */
  六沖,
  /** 初四、二五、三上 三對皆相合 */
  六合;

  companion object {
    fun of(branches: List<Branch>): Set<HexPattern> = buildSet {
      val pairs = (0..2).map { branches[it] to branches[it + 3] }
      if (pairs.all { (a, b) -> a.opposite == b }) add(六沖)
      if (pairs.all { (a, b) -> a.combined == b }) add(六合)
    }
  }
}
