/**
 * Created by smallufo on 2018-02-17.
 */
package destiny.iching.divine

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.Location
import destiny.core.calendar.TimeSecDecorator
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.EightWordsNullable
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.*
import destiny.core.chinese.impls.TianyiAuthorizedImpl
import destiny.core.chinese.impls.YangBladeNextBlissImpl
import destiny.iching.Hexagram
import destiny.iching.HexagramText
import destiny.iching.IChing
import destiny.iching.IHexagram
import destiny.iching.contentProviders.*
import java.time.chrono.ChronoLocalDateTime
import java.util.*


interface IResult<out T : ICombined> {
  fun getResult(): T
}

open class CombinedContext(val src: IHexagram, val dst: IHexagram) : IResult<ICombined> {
  override fun getResult(): ICombined {
    val s = Hexagram.getHexagram(src)
    val d = Hexagram.getHexagram(dst)
    return Combined(s, d)
  }
}


class CombinedWithMetaContext(val src: IHexagram,
                              val dst: IHexagram,
                              val locale: Locale = Locale.TAIWAN,
                              val 納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                              val 伏神系統: IHiddenEnergy = HiddenEnergyWangImpl()) : IResult<ICombinedWithMeta> {
  override fun getResult(): ICombinedWithMeta {
    val meta = Meta(納甲系統.getTitle(locale), 伏神系統.getTitle(locale))

    val srcPlate = Divines.getSinglePlate(src, 納甲系統, 伏神系統)
    val dstPlate = Divines.getSinglePlate(dst, 納甲系統, 伏神系統)

    val 變卦對於本卦的六親: List<Relative> =
      (0..5).map {
        Divines.getRelative(SimpleBranch.getFiveElement(dstPlate.納甲[it].branch), srcPlate.symbol.fiveElement)
      }.toList()

    return CombinedWithMeta(srcPlate, dstPlate, 變卦對於本卦的六親, meta)
  }
}


/** 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 */
class CombinedWithMetaNameContext(val src: IHexagram,
                                  val dst: IHexagram,
                                  val locale: Locale = Locale.TAIWAN,
                                  val 納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                                  val 伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                                  val nameShortImpl: IHexagramNameShort,
                                  val nameFullImpl: IHexagramNameFull) : IResult<ICombinedWithMetaName> {
  override fun getResult(): ICombinedWithMetaName {

    val prevCtx = CombinedWithMetaContext(src, dst, locale, 納甲系統, 伏神系統)
    val prevResult: ICombinedWithMeta = prevCtx.getResult()

    val srcPlate =
      Divines.getSinglePlateWithName(prevResult.srcPlate, nameShortImpl, nameFullImpl, locale) as SinglePlateWithName
    val dstPlate =
      Divines.getSinglePlateWithName(prevResult.dstPlate, nameShortImpl, nameFullImpl, locale) as SinglePlateWithName
    val 變卦對於本卦的六親 = prevResult.變卦對於本卦的六親

    return CombinedWithMetaName(srcPlate, dstPlate, 變卦對於本卦的六親, Meta(納甲系統.getTitle(locale), 伏神系統.getTitle(locale)))
  }
}

/** 具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 , 八字一定要包含 日干支 以及 月支 */
class CombinedWithMetaNameDayMonthContext(val src: IHexagram,
                                          val dst: IHexagram,
                                          val locale: Locale = Locale.TAIWAN,
                                          val 納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                                          val 伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                                          val nameShortImpl: IHexagramNameShort,
                                          val nameFullImpl: IHexagramNameFull,

                                          val eightWordsNullable: IEightWordsNullable, // 一定要包含 日干支 以及 月支
                                          val tianyiImpl: ITianyi,
                                          val yangBladeImpl: IYangBlade) :
  IResult<ICombinedWithMetaNameDayMonth> {
  constructor(src: Hexagram,
              dst: Hexagram,
              locale: Locale,
              納甲系統: SettingsGingFang,
              伏神系統: HiddenEnergyWangImpl,
              nameShortImpl: IHexagramNameShort,
              nameFullImpl: IHexagramNameFull,
              day: StemBranch,
              monthBranch: Branch,
              tianyiImpl: ITianyi,
              yangBladeImpl: IYangBlade) : this(src, dst, locale, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl,
                                                EightWordsNullable(StemBranchOptional.empty(),
                                                                   StemBranchOptional[null, monthBranch], day,
                                                                   StemBranchOptional.empty()),
                                                tianyiImpl, yangBladeImpl)


  val day = eightWordsNullable.day.let { StemBranch[it.stem!!, it.branch!!] }
  val monthBranch = eightWordsNullable.month.branch!!

  init {
    if (eightWordsNullable.day.stem == null || eightWordsNullable.day.branch == null && eightWordsNullable.month.branch == null) {
      throw RuntimeException("八字 必須包含 日干支 以及 月支 ")
    }
  }


  override fun getResult(): ICombinedWithMetaNameDayMonth {

    val prevCtx = CombinedWithMetaNameContext(src, dst, locale, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl)
    val prevResult = prevCtx.getResult() as CombinedWithMetaName

    //val eightWordsNullable = EightWordsNullable(StemBranchOptional.empty(), StemBranchOptional[null, monthBranch], StemBranchOptional[day], StemBranchOptional.empty())

    // 神煞
    val 空亡: Set<Branch> = day.empties.toSet()
    val 驛馬: Branch = day.branch.let { Characters.getHorse(it) }
    val 桃花: Branch = day.branch.let { Characters.getPeach(it) }
    val 貴人: Set<Branch> = day.stem.let { tianyiImpl.getTianyis(it).toSet() }
    val 羊刃: Branch = day.stem.let { yangBladeImpl.getYangBlade(it) }
    val 六獸: List<SixAnimal> = day.let { SixAnimals.getSixAnimals(it.stem) }
    return CombinedWithMetaNameDayMonth(prevResult, eightWordsNullable, 空亡, 驛馬, 桃花, 貴人, 羊刃, 六獸)
  }
}

/** 完整易卦排盤 , 包含時間、地點、八字、卦辭爻辭、神煞 等資料 */
class CombinedFullContext(val src: IHexagram,
                          val dst: IHexagram,
                          val locale: Locale = Locale.TAIWAN,
                          val 納甲系統: ISettingsOfStemBranch = SettingsGingFang(),
                          val 伏神系統: IHiddenEnergy = HiddenEnergyWangImpl(),
                          val nameShortImpl: IHexagramNameShort,
                          val nameFullImpl: IHexagramNameFull,

                          val gender: Gender?,
                          val question: String?,
                          val approach: DivineApproach,
                          val time: ChronoLocalDateTime<*>?,
                          val loc: ILocation? = Location.of(Locale.TAIWAN),
                          val place: String? = null,
                          val eightWords: EightWords,
                          val tianyiImpl: ITianyi = TianyiAuthorizedImpl(),
                          val yangBladeImpl: IYangBlade = YangBladeNextBlissImpl(),

                          val expressionImpl: IExpression,
                          val imageImpl: IImage,
                          val judgementImpl: IHexagramJudgement,
                          val textLocale: Locale
                         ) : IResult<ICombinedFull> {


  override fun getResult(): ICombinedFull {
    val prevCtx = CombinedWithMetaNameDayMonthContext(src, dst, locale, 納甲系統, 伏神系統, nameShortImpl, nameFullImpl,
                                                      eightWords, tianyiImpl, yangBladeImpl)
    val prevResult = prevCtx.getResult() as CombinedWithMetaNameDayMonth

    val gmtJulDay: Double? = time?.let { TimeTools.getGmtJulDay(it, loc) }
    val decoratedTime = time?.let { TimeSecDecorator.getOutputString(it, Locale.TAIWAN) }

    val meta = Meta(prevResult.納甲系統 , prevResult.伏神系統)
    val divineMeta = DivineMeta(gender, question, approach, gmtJulDay, loc, place, decoratedTime, meta, null)

    val srcText =
      IChing.getHexagramText(src, textLocale, nameFullImpl, nameShortImpl, expressionImpl, imageImpl, judgementImpl)
    val dstText =
      IChing.getHexagramText(dst, textLocale, nameFullImpl, nameShortImpl, expressionImpl, imageImpl, judgementImpl)

    val pairTexts: Pair<HexagramText, HexagramText> = Pair(srcText, dstText)
    return CombinedFull(prevResult, eightWords, divineMeta, pairTexts)
  }
}


