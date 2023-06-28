/**
 * @author smallufo
 * Created on 2007/2/10 at 上午 8:42:33
 */
package destiny.core.iching.mume

import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram

import java.io.Serializable

/**
 * 梅花易 , 梅花的學名為 Prunus(李屬) mume(梅種)
 *
 * @param hexagram 本卦
 * @param motivate 動爻
 */
data class MumeContext(
  val hexagram: IHexagram,

  /** 動爻 , 1~6  */
  val motivate: Int) : Serializable {

  /**
   * @return 變卦
   */
  val targetHexagram: IHexagram by lazy {
    Hexagram.of(hexagram.getTargetYinYangs(motivate))
  }

}
