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

interface ICombined {
  val src: IHexagram
  val dst: IHexagram
}

interface ICombinedNames {
  val srcName: IHexagramName
  val dstName: IHexagramName
}

interface ICombinedTexts : ICombinedNames {
  val srcText: HexagramText
  val dstText: HexagramText
}

open class Meta(val 納甲系統: String? = null,
                val 伏神系統: String? = null) : Serializable


/**
 * 卜一個卦的最基本資料結構：本卦、變卦
 */
data class Combined(override val src: Hexagram,
                    override val dst: Hexagram) : ICombined, Serializable {
  constructor(src: IHexagram , dst: IHexagram) : this(Hexagram.getHexagram(src) , Hexagram.getHexagram(dst))
}

open class CombinedNames(override val srcName: HexagramName,
                         override val dstName: HexagramName) : ICombinedNames


open class CombinedWithNames(val combined: Combined,
                             val names: CombinedNames) : ICombined by combined, ICombinedNames by names


data class CombinedTexts(private val srcText: HexagramText,
                         private val dstText: HexagramText) : ICombinedNames {
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


interface ICombinedWithMeta : ICombined {
  val srcPlate: ISinglePlate
  val dstPlate: ISinglePlate
  val 變卦對於本卦的六親: List<Relative>
  val meta: Meta
}

open class CombinedWithMeta(override val srcPlate: ISinglePlate,
                            override val dstPlate: ISinglePlate,
                            override val 變卦對於本卦的六親: List<Relative>,
                            override val meta: Meta) : ICombinedWithMeta , ICombined by Combined(srcPlate.hexagram, dstPlate.hexagram)


interface ISinglePlateWithNames : ISinglePlate, IHexagramName




class SinglePlateWithName(private val singlePlate: SinglePlate,
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
                      ) : ICombined by Combined(src, dst),
  ISinglePlateWithNames by SinglePlateWithName(SinglePlate(src, 本宮, 本卦宮序, 本卦世爻, 本卦應爻, 本卦納甲, 本卦六親, 伏神納甲, 伏神六親),
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
  DivinePlate(Hexagram.getHexagram(plate.src),
              Hexagram.getHexagram(plate.dst),
              meta,
              plate.srcNameFull, plate.dstNameFull,
              plate.srcNameShort, plate.dstNameShort,
              plate.本宮, plate.變宮, plate.本卦宮序, plate.變卦宮序, plate.本卦世爻,
              plate.本卦應爻, plate.變卦世爻, plate.變卦應爻, plate.本卦納甲, plate.變卦納甲,
              plate.本卦六親, plate.變卦六親, plate.變卦對於本卦的六親, plate.伏神納甲,
              plate.伏神六親, plate.pairTexts)