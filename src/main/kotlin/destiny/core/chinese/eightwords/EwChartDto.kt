/**
 * Created by smallufo on 2026-08-13.
 *
 * 八字盤的**呈現層 DTO** —— 專供 client 端 renderer 使用，與 [IPersonContextModel] 刻意分離。
 *
 * 存在的理由與 [destiny.core.chinese.ziwei.ZiweiPlateDto] 相同：
 *
 * 1. **去識別化的型別保證**：[IPersonContextModel] 同時是 `IBirthDataNamePlace`，帶有
 *    `name` / `place` / `time` / `location` / `chineseDate`（農曆日期等同精確生日）/
 *    `dst` / `gmtMinuteOffset`（可反推時區與經度）。本 DTO **結構上就沒有宣告這些欄位**
 *    ——不是序列化後被過濾掉，而是壓根沒有可填之處，故由編譯器把關而非輸出白名單的維護紀律。
 *    需要顯示身分時，由呼叫端另外組一個並列的身分物件。
 * 2. **時刻要換算成歲數**：`FortuneData` 的起訖是 `GmtJulDay`，其
 *    `startFortuneAgeNotes` 的內容更直接就是「西元 2035 年」這類年份字串。
 *    大運只留歲數，年份一概不出現。節氣位置同理：只給名稱與已算好的進度，不給時刻。
 * 3. **十神與藏干要在伺服器算完**：十神是「與日主的關係」，藏干還牽涉流派實作，
 *    都不該讓 client 自備對照表。
 *
 * 干支、十神、節氣名一律為**繁體中文**（八字不做多語系）。
 *
 * 另外刻意**不含**：小運（依需求排除）、`ageMap`（每歲起訖時刻）、
 * `starPosMap` / `houseMap` / `rsmiMap` / `aspectsDataSet`（黃道度數，且占星另有 lens）、
 * 排盤設定（真太陽時／換日／換年…屬 calcConfig，不是盤面內容）。
 *
 * ⚠️ 命名規則：本檔案內**不得出現名為 `name` / `time` / `place` / `location` 的欄位**。
 * 這讓「DTO 不含身分欄位」可以用欄位名精確比對來自動驗證（見對應測試）。
 */
package destiny.core.chinese.eightwords

import destiny.core.ChartDensity
import destiny.core.Gender
import destiny.core.Scale
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.Reaction
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.Stem
import destiny.tools.Lang
import destiny.tools.getTitle
import kotlinx.serialization.Serializable

/** 查表語系 —— 八字固定繁中，不對外開放為參數 */
private val ZH = Lang.ZH_TW

/** 一個天干與它對日主的十神。用於地支藏干 */
@Serializable
data class EwReactionDto(
  /** 天干 */
  val stem: String,
  /** 十神 */
  val reaction: String,
  /** 十神縮寫（手機窄欄用） */
  val abbr: String,
)

@Serializable
data class EwPillarDto(
  /** YEAR | MONTH | DAY | HOUR */
  val scale: String,
  val stem: String,
  val branch: String,
  /**
   * 天干十神。**日柱恆為 null** —— 日柱天干就是日主，對自己談十神沒有意義；
   * 那一格由 renderer 印「日主」或性別。[ChartDensity.COMPACT] 下一律 null。
   */
  val stemReaction: String?,
  val stemReactionAbbr: String?,
  /** 地支藏干（含十神）。[ChartDensity.COMPACT] 為空 */
  val hiddenStems: List<EwReactionDto>,
  /** 納音，僅 [ChartDensity.ALL]；非標準干支組合亦可能為 null */
  val naYin: String?,
)

/** 一柱大運。只有歲數，沒有年份 */
@Serializable
data class EwFortuneDto(
  val stem: String,
  val branch: String,
  /** 起運歲數（可能虛歲，依設定） */
  val fromAge: Int,
  /** 終運歲數 */
  val toAge: Int,
  val stemReaction: String,
  val stemReactionAbbr: String,
  /** 大運地支藏干，僅 [ChartDensity.ALL] */
  val hiddenStems: List<EwReactionDto>,
)

/**
 * 出生時刻在前後兩「節」之間的位置 —— renderer 據此畫進度條。
 * 只給名稱與算好的進度，不給時刻（時刻可反推年份到日）。
 */
@Serializable
data class EwSolarTermsDto(
  /** 前一個（也是所處的）「節」 */
  val prevMajor: String,
  /** 該節所屬月支 */
  val prevMajorBranch: String,
  /** 兩節之間的「中氣」 */
  val middle: String,
  /** 該中氣起始的星座 */
  val middleSign: String,
  /** 下一個「節」 */
  val nextMajor: String,
  val nextMajorBranch: String,
  /** 距前一個「節」幾天（取整） */
  val daysFromPrev: Int,
  /** 兩節之間的進度 0–1 */
  val ratio: Double,
)

@Serializable
data class EwMetaDto(
  /** M | F */
  val gender: String,
  /** 命宮干支，[ChartDensity.COMPACT] 為 null */
  val mingStemBranch: String?,
  /** 命宮地支對應的星座 */
  val mingSign: String?,
  /** 日柱空亡的兩個地支，僅 [ChartDensity.ALL] */
  val dayEmpties: List<String>,
  /** 得分，[ChartDensity.COMPACT] 為 null。分法依排盤設定 */
  val score: Double?,
  /**
   * 此盤只有四柱、無出生時刻（古書命例）。
   *
   * 此時 [EwChartDto.fortunes] / [EwChartDto.solarTerms] / [mingStemBranch] / [score]
   * 在**任何密度下都是空的**（不是被 density 過濾掉），renderer 據此省略那幾塊區域，
   * 而不是畫出空的大運列與跑不動的節氣進度條。
   */
  val pillarsOnly: Boolean,
)

@Serializable
data class EwChartDto(
  val meta: EwMetaDto,
  /** 四柱，固定 年→月→日→時。左右方向（中式／西式）由 renderer 決定 */
  val pillars: List<EwPillarDto>,
  /** 大運，[ChartDensity.COMPACT] 為空 */
  val fortunes: List<EwFortuneDto>,
  /** 節氣位置，[ChartDensity.COMPACT] 為 null */
  val solarTerms: EwSolarTermsDto?,
)

private fun Reaction.toDto(stem: Stem) = EwReactionDto(stem.name, name, getAbbreviation(ZH))

/**
 * 只需要八個字就算得出來的部分 —— 四柱干支、十神、藏干、納音。
 * 有時刻的盤與古書命例共用這段。
 */
private class PillarMapper(private val eightWords: IEightWords, private val hiddenStemsImpl: IHiddenStems) {

  private val dayStem = eightWords.day.stem

  fun reactionOf(stem: Stem): Reaction = ReactionUtil.getReaction(stem, dayStem)

  /** 地支藏干對日主的十神 */
  fun hiddenStemsOf(branch: Branch): List<EwReactionDto> = hiddenStemsImpl.getHiddenStems(branch)
    .map { hiddenStem -> reactionOf(hiddenStem).toDto(hiddenStem) }

  fun pillars(density: ChartDensity): List<EwPillarDto> {
    val compact = density == ChartDensity.COMPACT
    return listOf<Pair<Scale, IStemBranch>>(
      Scale.YEAR to eightWords.year,
      Scale.MONTH to eightWords.month,
      Scale.DAY to eightWords.day,
      Scale.HOUR to eightWords.hour,
    ).map { (scale, sb) ->
      // 日主不對自己談十神
      val reaction = if (compact || scale == Scale.DAY) null else reactionOf(sb.stem)
      EwPillarDto(
        scale = scale.name,
        stem = sb.stem.name,
        branch = sb.branch.name,
        stemReaction = reaction?.name,
        stemReactionAbbr = reaction?.getAbbreviation(ZH),
        hiddenStems = if (compact) emptyList() else hiddenStemsOf(sb.branch),
        naYin = if (density == ChartDensity.ALL) sb.naYin?.name else null,
      )
    }
  }

  /** 日柱空亡的兩個地支 */
  fun dayEmpties(density: ChartDensity): List<String> =
    if (density == ChartDensity.ALL) eightWords.day.empties.map { it.name } else emptyList()
}

/**
 * 古書命例（三命通會、滴天髓…）的排盤：**只有四柱與性別**。
 *
 * 沒有出生時刻 ⇒ 大運（起運歲數要算到節氣的距離）、節氣位置、命宮（要太陽位置）、
 * 八分法得分（月支分數取決於節氣深淺）全都算不出來，[EwMetaDto.pillarsOnly] 因此為 true。
 * 也因為沒有排盤這一步，換日／換年／真太陽時等設定在此完全不參與 ——
 * 同一筆古書命例對所有使用者都是同一張盤。
 *
 * 密度階梯仍然有效：十神、藏干、納音、空亡都只需要八個字。
 */
fun IEightWords.toEwChartDto(
  gender: Gender,
  density: ChartDensity,
  hiddenStemsImpl: IHiddenStems = HiddenStemsStandardImpl(),
): EwChartDto {
  val mapper = PillarMapper(this, hiddenStemsImpl)
  return EwChartDto(
    meta = EwMetaDto(
      gender = gender.name,
      mingStemBranch = null,
      mingSign = null,
      dayEmpties = mapper.dayEmpties(density),
      score = null,
      pillarsOnly = true,
    ),
    pillars = mapper.pillars(density),
    fortunes = emptyList(),
    solarTerms = null,
  )
}

fun IPersonContextModel.toEwChartDto(
  density: ChartDensity,
  hiddenStemsImpl: IHiddenStems = HiddenStemsStandardImpl(),
): EwChartDto {
  val compact = density == ChartDensity.COMPACT
  val all = density == ChartDensity.ALL
  val mapper = PillarMapper(eightWords, hiddenStemsImpl)

  fun reactionOf(stem: Stem) = mapper.reactionOf(stem)
  fun hiddenStemsOf(branch: Branch) = mapper.hiddenStemsOf(branch)

  val fortunes = if (compact) emptyList() else fortuneDataLarges.map { fortuneData ->
    val reaction = reactionOf(fortuneData.stemBranch.stem)
    EwFortuneDto(
      stem = fortuneData.stemBranch.stem.name,
      branch = fortuneData.stemBranch.branch.name,
      fromAge = fortuneData.startFortuneAge,
      toAge = fortuneData.endFortuneAge,
      stemReaction = reaction.name,
      stemReactionAbbr = reaction.getAbbreviation(ZH),
      hiddenStems = if (all) hiddenStemsOf(fortuneData.stemBranch.branch) else emptyList(),
    )
  }

  val solarTerms = if (compact) null else solarTermsTimePos.let { pos ->
    val prev = pos.prevMajor.solarTerms
    val toLeft = pos.gmtJulDay - pos.prevMajor.begin
    val span = pos.nextMajor.begin - pos.prevMajor.begin
    EwSolarTermsDto(
      prevMajor = prev.name,
      prevMajorBranch = prev.branch.name,
      // 兩「節」之間的中氣，不是 pos.middle —— 過了中氣之後，pos.middle 已指向下個月的中氣
      middle = prev.next().name,
      // 中氣即星座起點：前半段時中氣還沒到，那個星座是「下一個」
      middleSign = (if (pos.firstHalf) nextSolarSign.first else prevSolarSign.first).getTitle<ZodiacSign>(ZH),
      nextMajor = pos.nextMajor.solarTerms.name,
      nextMajorBranch = pos.nextMajor.solarTerms.branch.name,
      daysFromPrev = toLeft.toInt(),
      ratio = if (span > 0) toLeft / span else 0.0,
    )
  }

  return EwChartDto(
    meta = EwMetaDto(
      gender = gender.name,
      mingStemBranch = if (compact) null else risingStemBranch.toString(),
      mingSign = if (compact) null else ZodiacSign.of(risingStemBranch.branch).getTitle<ZodiacSign>(ZH),
      dayEmpties = mapper.dayEmpties(density),
      score = if (compact) null else score,
      pillarsOnly = false,
    ),
    pillars = mapper.pillars(density),
    fortunes = fortunes,
    solarTerms = solarTerms,
  )
}
