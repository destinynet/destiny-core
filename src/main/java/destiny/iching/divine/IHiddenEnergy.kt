/**
 * @author smallufo
 * @date 2002/8/20
 * @time 下午 04:04:28
 */
package destiny.iching.divine

import destiny.core.Descriptive
import destiny.core.chinese.StemBranch
import destiny.iching.IHexagram

/** 伏神介面  */
interface IHiddenEnergy : Descriptive {
  /**
   * 取得第幾爻的伏神
   * 1 <= LineIndex <=6
   */
  fun getStemBranch(hexagram: IHexagram, settings: ISettingsOfStemBranch, lineIndex: Int): StemBranch?
}
