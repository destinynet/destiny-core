/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.calendar.eightwords.EightWordsNullable
import destiny.core.calendar.eightwords.IEightWordsNullable
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchOptional
import destiny.iching.Hexagram
import destiny.iching.Symbol
import java.time.chrono.ChronoLocalDateTime

/**
 * 卜卦結果的輸出盤，作為 DTO
 */
open class DivinePlate(
  val src: Hexagram,
  val dst: Hexagram,
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
  val 伏神六親: List<Relative?>
                      )

class DivinePlateWithDay(
  plate: DivinePlate,
  val day: StemBranch,
  val month: StemBranchOptional,
  val 空亡: Set<Branch>,
  val 驛馬: Branch,
  val 桃花: Branch,
  val 貴人: Set<Branch>,
  val 羊刃: Branch) : IEightWordsNullable,
  DivinePlate(plate.src, plate.dst, plate.本宮, plate.變宮, plate.本卦宮序, plate.變卦宮序, plate.本卦世爻, plate.本卦應爻, plate.變卦世爻,
              plate.變卦應爻, plate.本卦納甲, plate.變卦納甲, plate.伏神納甲, plate.本卦六親, plate.變卦六親, plate.變卦對於本卦的六親 , plate.伏神六親) {


  override val eightWordsNullable: EightWordsNullable
    get() = EightWordsNullable(StemBranchOptional.empty(), month, day, StemBranchOptional.empty())
}

class DivinePlateFull(
  plate: DivinePlate,
  val approach: DivineApproach,
  val localDateTime: ChronoLocalDateTime<*>?,
  override val eightWordsNullable: EightWordsNullable,
  val 空亡: Set<Branch>?,
  val 驛馬: Branch?,
  val 桃花: Branch?,
  val 貴人: Set<Branch>?,
  val 羊刃: Branch?) : IEightWordsNullable , DivinePlate(plate.src, plate.dst, plate.本宮, plate.變宮, plate.本卦宮序, plate.變卦宮序, plate.本卦世爻, plate.本卦應爻, plate.變卦世爻,
              plate.變卦應爻, plate.本卦納甲, plate.變卦納甲, plate.伏神納甲, plate.本卦六親, plate.變卦六親, plate.變卦對於本卦的六親 , plate.伏神六親)