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
import destiny.iching.Hexagram
import destiny.iching.HexagramText
import destiny.iching.Symbol
import java.io.Serializable
import java.util.*

/**
 * 卜卦結果的輸出盤，作為 DTO
 */
open class Meta(val 納甲系統: String? = null,
                val 伏神系統: String? = null) : Serializable

open class DivinePlate(
  val src: Hexagram,
  val dst: Hexagram,
  open val meta: Meta,
  /** 本卦全名（三或四個中文字） */
  val srcNameFull: String,
  /** 變卦全名（三或四個中文字） */
  val dstNameFull: String,
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
  val 伏神納甲: List<StemBranch?>,

  val 本卦六親: List<Relative>,
  val 變卦六親: List<Relative>,
  val 變卦對於本卦的六親: List<Relative>,
  val 伏神六親: List<Relative?>,
  val pairTexts: Pair<HexagramText, HexagramText>?
                      ) : Serializable


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
  DivinePlate(plate.src, plate.dst, meta , plate.srcNameFull, plate.dstNameFull,
              plate.本宮, plate.變宮, plate.本卦宮序, plate.變卦宮序, plate.本卦世爻,
              plate.本卦應爻, plate.變卦世爻, plate.變卦應爻, plate.本卦納甲, plate.變卦納甲,
              plate.伏神納甲, plate.本卦六親, plate.變卦六親, plate.變卦對於本卦的六親,
              plate.伏神六親 , plate.pairTexts)