/**
 * @author smallufo
 * Created on 2007/2/10 at 上午 8:42:33
 */
package destiny.iching.mume

import destiny.iching.Hexagram
import destiny.iching.IHexagram

import java.io.Serializable

/**
 * 梅花易 , 梅花的學名為 Prunus(李屬) mume(梅種)
 *
 * @param hexagram 本卦
 * @param motivate 動爻
 */
class MumeContext(
  val hexagram: IHexagram,
  /** 動爻 , 1~6  */
  val motivate: Int) : Serializable {

  /**
   * @return 變卦
   */
  val targetHexagram: IHexagram
    get() {
      val yinyangs = BooleanArray(6)
      for (i in 1..6)
        yinyangs[i - 1] = hexagram.getLine(i)

      yinyangs[motivate - 1] = !hexagram.getLine(motivate)
      return Hexagram.getHexagram(yinyangs)
    }


}
