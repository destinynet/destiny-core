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

interface IMeta {
  val 納甲系統: String
  val 伏神系統: String
}

data class Meta(override val 納甲系統: String,
                override val 伏神系統: String) : IMeta, Serializable


/**
 * 卜一個卦的最基本資料結構：本卦、變卦
 */
data class Combined(override val src: Hexagram,
                    override val dst: Hexagram) : ICombined, Serializable {
  constructor(src: IHexagram, dst: IHexagram) : this(Hexagram.getHexagram(src), Hexagram.getHexagram(dst))
}


/** 單一卦象，卦名、世爻應爻、六親等資訊 */
interface ISinglePlate : IHexagram {
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
                       override val 伏神六親: List<Relative?>) : ISinglePlate, IHexagram by hexagram


interface ICombinedWithMeta : ICombined {
  val srcPlate: ISinglePlate
  val dstPlate: ISinglePlate
  val 變卦對於本卦的六親: List<Relative>
  val meta: Meta
}

data class CombinedWithMeta(override val srcPlate: ISinglePlate,
                            override val dstPlate: ISinglePlate,
                            override val 變卦對於本卦的六親: List<Relative>,
                            override val meta: Meta) : ICombinedWithMeta,
  ICombined by Combined(srcPlate.hexagram, dstPlate.hexagram)


interface ISinglePlateWithName : ISinglePlate, IHexagramName

data class SinglePlateWithName(private val singlePlate: SinglePlate,
                               private val hexagramName: HexagramName) :
  ISinglePlateWithName,
  ISinglePlate by singlePlate,
  IHexagramName by hexagramName


/** 給 [PairChartWithTitle] 使用 , 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照) */
interface ICombinedWithMetaName : ICombinedWithMeta {
  override val srcPlate: ISinglePlateWithName
  override val dstPlate: ISinglePlateWithName
}

/** [Combined] + [Meta] + [HexagramName] */
data class CombinedWithMetaName(override val srcPlate: SinglePlateWithName,
                                override val dstPlate: SinglePlateWithName,
                                override val 變卦對於本卦的六親: List<Relative>,
                                override val meta: Meta) : ICombinedWithMetaName,
  ICombinedWithMeta by CombinedWithMeta(srcPlate, dstPlate, 變卦對於本卦的六親, meta)


/** TODO : Replace with [ICombinedWithMetaName] 以及 [CombinedWithMetaName] */
open class DivinePlate(
  src: Hexagram,
  dst: Hexagram,
  val meta: Meta,
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
                      ) :
  ICombined by Combined(src, dst),
  ISinglePlateWithName by SinglePlateWithName(SinglePlate(src, 本宮, 本卦宮序, 本卦世爻, 本卦應爻, 本卦納甲, 本卦六親, 伏神納甲, 伏神六親),
                                              HexagramName(srcNameShort, srcNameFull)),
  IMeta by meta


/**
 * 額外的卦象資訊，對整體卦象輸出沒有影響
 */
interface IDivineMeta : IMeta {
  val gender: Gender?
  val question: String?
  val approach: DivineApproach?
  val gmtJulDay: Double?
  val loc: Location?
  val place: String?
  /** 已經 format 的時間 */
  val decoratedTime: String?
}

class DivineMeta(override val gender: Gender?,
                 override val question: String?,
                 override val approach: DivineApproach?,
                 override val gmtJulDay: Double? = null,
                 override val loc: Location? = Location.of(Locale.TAIWAN),
                 override val place: String?,
                 /** 已經 format 的時間 */
                 override val decoratedTime: String?,
                 val meta: Meta ,
                 val link: String?) : IMeta by meta , IDivineMeta

class DivinePlateFull(
  plate: DivinePlate,
  val divineMeta: DivineMeta,
  override val eightWordsNullable: EightWordsNullable,
  val 空亡: Set<Branch>?,
  val 驛馬: Branch?,
  val 桃花: Branch?,
  val 貴人: Set<Branch>?,
  val 羊刃: Branch?,
  val 六獸: List<SixAnimal>?) : IEightWordsNullable,
  DivinePlate(Hexagram.getHexagram(plate.src),
              Hexagram.getHexagram(plate.dst),
              Meta(divineMeta.納甲系統 , divineMeta.伏神系統),
              plate.srcNameFull, plate.dstNameFull,
              plate.srcNameShort, plate.dstNameShort,
              plate.本宮, plate.變宮, plate.本卦宮序, plate.變卦宮序, plate.本卦世爻,
              plate.本卦應爻, plate.變卦世爻, plate.變卦應爻, plate.本卦納甲, plate.變卦納甲,
              plate.本卦六親, plate.變卦六親, plate.變卦對於本卦的六親, plate.伏神納甲,
              plate.伏神六親, plate.pairTexts)