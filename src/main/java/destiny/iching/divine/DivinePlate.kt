/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.Gender
import destiny.core.calendar.Location
import destiny.core.calendar.eightwords.EightWordsNullable
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.Branch
import destiny.core.chinese.SixAnimal
import destiny.core.chinese.StemBranch
import destiny.iching.*
import java.io.Serializable
import java.util.*

interface ICore {
  val src: Hexagram
  val dst: Hexagram
}

interface ICoreNames {
  val srcName: IHexagramName
  val dstName: IHexagramName
}

interface ICoreTexts : ICoreNames {
  val srcText: HexagramText
  val dstText: HexagramText
}

open class Meta(val 納甲系統: String? = null,
                val 伏神系統: String? = null) : Serializable

interface ICoreWithMeta : ICore {
  val meta: Meta
}

/**
 * 卜一個卦的最基本資料結構：本卦、變卦
 */
open class Core(override val src: Hexagram,
                override val dst: Hexagram) : ICore, Serializable

open class CoreNames(override val srcName: HexagramName,
                     override val dstName: HexagramName) : ICoreNames


open class CoreWithNames(val core: Core,
                         val names: CoreNames) : ICore by core, ICoreNames by names

open class CoreWithMeta(val core: Core,
                        override val meta: Meta) : ICoreWithMeta, ICore by core

data class PairTexts(private val srcText: HexagramText,
                     private val dstText: HexagramText) : ICoreNames {
  override val srcName: IHexagramName
    get() = HexagramName(srcText.shortName, srcText.fullName)
  override val dstName: IHexagramName
    get() = HexagramName(dstText.shortName, dstText.fullName)
}

/** 單一卦象，卦名、世爻應爻、六親等資訊 */
interface ISinglePlate {
  val hexagram: IHexagram
  /** 本宮 , 此卦 是八卦哪一宮 */
  val symbol: Symbol
  val 宮序: Int
  /** 1~6 */
  val 世爻: Int
  /** 1~6 */
  val 應爻: Int
  val 納甲: List<StemBranch>
  val 六親: List<Relative>
  val 伏神納甲: List<StemBranch?>
  val 伏神六親: List<Relative?>
}


data class SinglePlate(override val hexagram: Hexagram,
                       /** 本宮 , 此卦 是八卦哪一宮 */
                       override val symbol: Symbol,
                       override val 宮序: Int,
                       /** 1~6 */
                       override val 世爻: Int,
                       /** 1~6 */
                       override val 應爻: Int,
                       override val 納甲: List<StemBranch>,
                       override val 六親: List<Relative>,
                       override val 伏神納甲: List<StemBranch?>,
                       override val 伏神六親: List<Relative?>) : ISinglePlate

interface ISinglePlateWithNames : ISinglePlate, IHexagramName


open class CoreWithMetaTexts(val core: Core,
                             override val meta: Meta,
                             override val srcText: HexagramText,
                             override val dstText: HexagramText) : ICoreTexts,
  ICoreNames by PairTexts(srcText, dstText),
  ICoreWithMeta by CoreWithMeta(core, meta)


class SinglePlateWithNames(private val singlePlate: SinglePlate,
                           private val hexagramName: HexagramName) :
  ISinglePlateWithNames,
  ISinglePlate by singlePlate,
  IHexagramName by hexagramName


open class DivinePlate(
  src: Hexagram,
  dst: Hexagram,
  open val meta: Meta,
  /** 本卦全名（三或四個中文字） */
  val srcNameFull: String,
  /** 變卦全名（三或四個中文字） */
  val dstNameFull: String,
  val srcNameShort: String,
  val dstNameShort: String,
  val 本宮: Symbol,
  val 變宮: Symbol,
  val 本卦宮序: Int,
  val 變卦宮序: Int,
  val 本卦世爻: Int,
  val 本卦應爻: Int,

  val 變卦世爻: Int,
  val 變卦應爻: Int,
  val 本卦納甲: List<StemBranch>,

  val 變卦納甲: List<StemBranch>,
  val 本卦六親: List<Relative>,
  val 變卦六親: List<Relative>,
  val 變卦對於本卦的六親: List<Relative>,
  override val 伏神納甲: List<StemBranch?>,
  override val 伏神六親: List<Relative?>,
  val pairTexts: Pair<HexagramText, HexagramText>?
                      ) : ICore by Core(src, dst),
  ISinglePlateWithNames by SinglePlateWithNames(SinglePlate(src, 本宮, 本卦宮序, 本卦世爻, 本卦應爻, 本卦納甲, 本卦六親, 伏神納甲, 伏神六親),
                                                HexagramName(srcNameShort, srcNameFull))


/**
 * 額外的卦象資訊，對整體卦象輸出沒有影響
 */
class DivineMeta(val gender: Gender?,
                 val question: String?,
                 val approach: DivineApproach?,
                 val gmtJulDay: Double? = null,
                 val loc: Location? = Location.of(Locale.TAIWAN),
                 val place: String?,
                 /** 已經 format 的時間 */
                 val decoratedTime: String?,
                 納甲系統: String? = null,
                 伏神系統: String? = null,
                 val link: String?) : Meta(納甲系統, 伏神系統)

class DivinePlateFull(
  plate: DivinePlate,
  override val meta: DivineMeta,
  override val eightWordsNullable: EightWordsNullable,
  val 空亡: Set<Branch>?,
  val 驛馬: Branch?,
  val 桃花: Branch?,
  val 貴人: Set<Branch>?,
  val 羊刃: Branch?,
  val 六獸: List<SixAnimal>?) : IEightWordsNullable,
  DivinePlate(plate.src, plate.dst, meta,
              plate.srcNameFull, plate.dstNameFull,
              plate.srcNameShort, plate.dstNameShort,
              plate.本宮, plate.變宮, plate.本卦宮序, plate.變卦宮序, plate.本卦世爻,
              plate.本卦應爻, plate.變卦世爻, plate.變卦應爻, plate.本卦納甲, plate.變卦納甲,
              plate.本卦六親, plate.變卦六親, plate.變卦對於本卦的六親, plate.伏神納甲,
              plate.伏神六親, plate.pairTexts)