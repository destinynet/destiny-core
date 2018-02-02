/**
 * @author smallufo
 * @date 2002/8/19
 * @time 下午 10:25:17
 */
package destiny.iching.divine

import destiny.core.Descriptive
import destiny.core.chinese.StemBranch
import destiny.iching.IHexagram

/** 納甲法的設定介面  */
interface ISettingsOfStemBranch : Descriptive {

  /**
   * 取得某個卦的某個爻的干支納甲
   * 1 <= lineIndex <=6
   */
  fun getStemBranch(hexagram: IHexagram, lineIndex: Int): StemBranch
}
