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

interface IPlate {
  val src: Hexagram
  val dst: Hexagram
}

/**
 * 卜卦結果的輸出盤，作為 DTO
 */
data class DivinePlate(
  override val src: Hexagram,
  override val dst: Hexagram,
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
                      ) : IPlate

data class DivinePlateWithDay(
  val plate: DivinePlate,

  val day: StemBranch,
  val month: StemBranchOptional,
  val 空亡: Set<Branch>,
  val 驛馬: Branch,
  val 桃花: Branch,
  val 貴人: Set<Branch>,
  val 羊刃: Branch) : IEightWordsNullable, IPlate by plate {

  override val eightWordsNullable: EightWordsNullable
    get() = EightWordsNullable(StemBranchOptional.empty(), month, day, StemBranchOptional.empty())
}