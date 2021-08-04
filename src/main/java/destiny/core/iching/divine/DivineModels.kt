/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.core.iching.divine

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.calendar.eightwords.IEightWordsNullableFactory
import destiny.core.calendar.locationOf
import destiny.core.chinese.Branch
import destiny.core.chinese.SixAnimal
import destiny.core.chinese.StemBranch
import destiny.core.iching.*
import java.io.Serializable
import java.util.*

interface ICombined : Serializable {
  val src: IHexagram
  val dst: IHexagram
}

interface IMeta : Serializable {
  val settings: ISettingsOfStemBranch
  val hiddenEnergy: IHiddenEnergy
}

data class Meta(override val settings: ISettingsOfStemBranch,
                override val hiddenEnergy: IHiddenEnergy) : IMeta, Serializable


/**
 * 卜一個卦的最基本資料結構：本卦、變卦
 */
data class Combined(override val src: IHexagram,
                    override val dst: IHexagram) : ICombined, Serializable


/** 單一卦象，卦名、世爻應爻、六親等資訊 */
interface ISingleHexagram : IHexagram {
  val hexagram: IHexagram

  /** 本宮 , 此卦 是八卦哪一宮 */
  val symbol: Symbol
  /** 宮序 */
  val symbolSteps: Int

  /** 世爻 , 1~6 */
  val self: Int

  /** 應爻 , 1~6 */
  val oppo: Int
  val 納甲: List<StemBranch>
  /** 六親 */
  val relatives: List<Relative>
  val 伏神納甲: List<StemBranch?>
  val 伏神六親: List<Relative?>
}


data class SingleHexagram(
  override val hexagram: IHexagram,
  /** 本宮 , 此卦 是八卦哪一宮 */
  override val symbol: Symbol,
  override val symbolSteps: Int,
  /** 世爻 , 1~6 */
  override val self: Int,
  /** 應爻 , 1~6 */
  override val oppo: Int,
  override val 納甲: List<StemBranch>,
  /** 六親 */
  override val relatives: List<Relative>,
  override val 伏神納甲: List<StemBranch?>,
  override val 伏神六親: List<Relative?>) : ISingleHexagram, IHexagram by hexagram, Serializable


/** 純粹組合兩卦 , 沒有其他 卦名、卦辭、日期 等資訊 */
interface ICombinedWithMeta : ICombined, IMeta {
  val srcModel: ISingleHexagram
  val dstModel: ISingleHexagram
  val 變卦對於本卦的六親: List<Relative>
}

data class CombinedWithMeta(
  override val srcModel: ISingleHexagram,
  override val dstModel: ISingleHexagram,
  override val 變卦對於本卦的六親: List<Relative>,
  val meta: Meta) : ICombinedWithMeta,
  ICombined by Combined(srcModel, dstModel),
  IMeta by meta,
  Serializable


interface ISingleHexagramWithName : ISingleHexagram, IHexagramName

data class SingleHexagramWithName(private val singleHexagram: ISingleHexagram,
                                  private val hexagramName: IHexagramName) :
  ISingleHexagramWithName,
  ISingleHexagram by singleHexagram,
  IHexagramName by hexagramName,
  Serializable


/** 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
interface ICombinedWithMetaName : ICombinedWithMeta {
  override val srcModel: ISingleHexagramWithName
  override val dstModel: ISingleHexagramWithName
}

/** [Combined] + [Meta] + [HexagramName] */
data class CombinedWithMetaName(
  override val srcModel: ISingleHexagramWithName,
  override val dstModel: ISingleHexagramWithName,
  override val 變卦對於本卦的六親: List<Relative>,
  val meta: Meta) : ICombinedWithMetaName,
  ICombinedWithMeta by CombinedWithMeta(srcModel, dstModel, 變卦對於本卦的六親, meta), Serializable


/**
 * 「可能」具備「日干支」「月令」 , 或許可以排出六獸 [SixAnimal] 以及神煞 ,
 * 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
 * 通常用於 書籍、古書 當中卦象對照
 * */
interface ICombinedWithMetaNameDayMonth : ICombinedWithMetaName, IEightWordsNullableFactory {
  val day: StemBranch?
  val monthBranch: Branch?

  /** 空亡 */
  val voids: Set<Branch>

  /** 驛馬 */
  val horse: Branch?

  /** 桃花 */
  val flower: Branch?

  /** 貴人 */
  val tianyis: Set<Branch>

  /** 羊刃 */
  val yangBlade: Branch?

  /** 六獸 */
  val sixAnimals: List<SixAnimal>
}

/** 「可能」具備「日干支」「月令」 , 或許可以排出六獸 [SixAnimal] 以及神煞 ,
 * 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] ) , 八字可能要包含 日干支 以及 月支  */
data class CombinedWithMetaNameDayMonth(
  private val combinedWithMetaName: ICombinedWithMetaName,
  override val eightWordsNullable: IEightWordsNullable,
  /** 空亡 */
  override val voids: Set<Branch>,

  /** 驛馬 */
  override val horse: Branch?,

  /** 桃花 */
  override val flower: Branch?,

  /** 貴人 */
  override val tianyis: Set<Branch>,

  /** 羊刃 */
  override val yangBlade: Branch?,

  /** 六獸 */
  override val sixAnimals: List<SixAnimal>) :
  ICombinedWithMetaNameDayMonth,
  ICombinedWithMetaName by combinedWithMetaName, Serializable {

  override val day = eightWordsNullable.day.let {
    if (it.stem != null && it.branch != null)
      StemBranch[it.stem!!, it.branch!!]
    else
      null
  }
  override val monthBranch = eightWordsNullable.month.branch

}

interface ICombinedWithMetaNameTexts : ICombinedWithMetaName {
  val pairTexts: Pair<IHexagramText, IHexagramText>
}


/**
 * 額外的卦象資訊，對整體卦象輸出沒有影響
 */
interface IDivineMeta : IMeta {
  val gender: Gender?
  val question: String?
  val approach: DivineApproach?
  val gmtJulDay: GmtJulDay?
  val loc: ILocation?
  val place: String?

  /** 已經 format 的時間 */
  val decoratedDate: String?
  val decoratedDateTime: String?
}


data class DivineMeta(
  override val gender: Gender?,
  override val question: String?,
  override val approach: DivineApproach?,
  override val gmtJulDay: GmtJulDay? = null,
  override val loc: ILocation? = locationOf(Locale.TAIWAN),
  override val place: String?,
  override val decoratedDate: String?,
  override val decoratedDateTime: String?,
  val meta: Meta,
  val link: String? = null) : IMeta by meta, IDivineMeta, Serializable


/** 完整卜卦盤 , 包含所有資料 */
interface ICombinedFull : ICombinedWithMetaNameDayMonth, ICombinedWithMetaNameTexts, IDivineMeta

/** 完整卜卦盤 , 具備八字(可能不完整) */
data class CombinedFull(
  private val combinedWithMetaNameDayMonth: ICombinedWithMetaNameDayMonth,
  override val eightWordsNullable: IEightWordsNullable,
  private val divineMeta: DivineMeta,
  override val pairTexts: Pair<IHexagramText, IHexagramText>) :
  ICombinedFull,
  ICombinedWithMetaNameDayMonth by combinedWithMetaNameDayMonth,
  IDivineMeta by divineMeta,
  ICombinedWithMetaNameTexts,
  Serializable {

  override val settings
    get() = divineMeta.settings
  override val hiddenEnergy
    get() = divineMeta.hiddenEnergy
}
