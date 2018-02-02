/**
 * Created by smallufo on 2018-02-02.
 */
package destiny.iching.divine

import destiny.core.chinese.StemBranch
import destiny.iching.Hexagram
import destiny.iching.Symbol

/**
 * 卜卦結果的輸出盤，作為 DTO
 */
data class DivinePlate(
  val src: Hexagram,
  val dst: Hexagram,
  val 本宮: Symbol,
  val 變宮: Symbol,
  val 本卦宮序:Int,
  val 變卦宮序:Int,
  val 本卦世爻:Int,
  val 本卦應爻:Int,
  val 變卦世爻:Int,
  val 變卦應爻:Int,

  val 本卦納甲:List<StemBranch>,
  val 變卦納甲:List<StemBranch>,
  val 伏神納甲:List<StemBranch?>,

  val 本卦六親:List<Relative>,
  val 變卦六親:List<Relative>,
  val 變卦對於本卦的六親:List<Relative>,
  val 伏神六親:List<Relative?>
                      )