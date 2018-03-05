/**
 * Created by smallufo on 2018-03-05.
 */
package destiny.fengshui

import destiny.core.chinese.Branch
import destiny.iching.Symbol
import destiny.iching.divine.SettingsGingFang
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue


class EightGhostTest {

  /** 坎龍坤兔震山猴，巽雞乾馬兌蛇頭，艮虎離豬為曜煞，墓宅逢之立便休 */
  @Test
  fun getGhost() {
    val settings = SettingsGingFang()

    // 坎龍坤兔震山猴
    assertSame(Branch.辰, EightGhost.getGhost(Symbol.坎))
    assertTrue(EightGhost.getGhosts(Symbol.坎 , GhostType.正 , settings).containsAll(listOf(Branch.辰 , Branch.戌))) // 坎卦 兩官鬼 : 辰戌

    assertSame(Branch.卯, EightGhost.getGhost(Symbol.坤))
    assertSame(Branch.申, EightGhost.getGhost(Symbol.震))

    // 巽雞乾馬兌蛇頭
    assertSame(Branch.酉, EightGhost.getGhost(Symbol.巽))
    assertSame(Branch.午, EightGhost.getGhost(Symbol.乾))
    assertSame(Branch.巳, EightGhost.getGhost(Symbol.兌))

    // 艮虎離豬為曜煞
    assertSame(Branch.寅, EightGhost.getGhost(Symbol.艮))
    assertSame(Branch.亥, EightGhost.getGhost(Symbol.離))

    // 墓宅逢之立便休
  }

  /**
   * 乾卦包括戌乾亥三山，曜煞在午，午为乾卦的正曜煞。
   * 乾卦的后天在艮，艮的曜煞在寅，寅为乾卦的地曜煞。
   * 乾卦的先天在离，离的曜煞在亥，亥为乾卦的天曜煞。
   */
  @Test
  fun 乾三曜煞() {
    assertSame(Branch.午, EightGhost.getGhost(Symbol.乾, GhostType.正))
    assertSame(Branch.寅, EightGhost.getGhost(Symbol.乾, GhostType.地))
    assertSame(Branch.亥, EightGhost.getGhost(Symbol.乾, GhostType.天))
  }

  /**
   * 坎卦包括壬癸子三山，曜煞在辰，辰为坎卦的正曜煞。
   * 坎卦的后天在坤，坤的曜煞在卯，卯为坎卦的地曜煞。
   * 坎卦的先天在兑，兑的曜煞在巳，巳为坎卦的天曜煞。
   */
  @Test
  fun 坎三曜煞() {
    assertSame(Branch.辰, EightGhost.getGhost(Symbol.坎, GhostType.正))
    assertSame(Branch.卯, EightGhost.getGhost(Symbol.坎, GhostType.地))
    assertSame(Branch.巳, EightGhost.getGhost(Symbol.坎, GhostType.天))
  }

  /**
   * 艮卦包括丑艮寅三山，曜煞在寅，寅为艮卦的正曜煞。
   * 艮卦的后天在震，震的曜煞在申，申为艮卦的地曜煞。
   * 艮卦的先天在乾，乾的曜煞在午，午为乾卦的天曜煞。
   */
  @Test
  fun 艮三曜煞() {
    assertSame(Branch.寅, EightGhost.getGhost(Symbol.艮, GhostType.正))
    assertSame(Branch.申, EightGhost.getGhost(Symbol.艮, GhostType.地))
    assertSame(Branch.午, EightGhost.getGhost(Symbol.艮, GhostType.天))
  }

  /**
   * 震卦包括甲卯乙三山，曜煞在申，申为震卦的正曜煞。
   * 震卦的后天在离，离的曜煞在亥，亥为震卦的地曜煞。
   * 震卦的先天在艮，艮的曜煞在寅，寅为震卦的天曜煞。
   */
  @Test
  fun 震三曜煞() {
    assertSame(Branch.申, EightGhost.getGhost(Symbol.震, GhostType.正))
    assertSame(Branch.亥, EightGhost.getGhost(Symbol.震, GhostType.地))
    assertSame(Branch.寅, EightGhost.getGhost(Symbol.震, GhostType.天))
  }

  /**
   * 巽卦包括辰巽巳三山，曜煞在酉，酉为巽卦的正曜煞。
   * 巽卦的后天在兑，兑的曜煞在巳，巳为巽卦的地曜煞。
   * 巽卦的先天在坤，坤的曜煞在卯，卯为巽卦的天曜煞。
   */
  @Test
  fun 巽三曜煞() {
    assertSame(Branch.酉, EightGhost.getGhost(Symbol.巽, GhostType.正))
    assertSame(Branch.巳, EightGhost.getGhost(Symbol.巽, GhostType.地))
    assertSame(Branch.卯, EightGhost.getGhost(Symbol.巽, GhostType.天))
  }

  /**
   * 离卦包括丙午丁三山，曜煞在亥，亥为离卦的正曜煞。
   * 离卦的后天在乾，乾的曜煞在午，午为离卦的地曜煞。
   * 离卦的先天在震，震的曜煞在申，申为离卦的天曜煞。
   */
  @Test
  fun 離三曜煞() {
    assertSame(Branch.亥, EightGhost.getGhost(Symbol.離, GhostType.正))
    assertSame(Branch.午, EightGhost.getGhost(Symbol.離, GhostType.地))
    assertSame(Branch.申, EightGhost.getGhost(Symbol.離, GhostType.天))
  }

  /**
   * 坤卦包括未坤申三山，曜煞在卯，卯为坤卦的正曜煞。
   * 坤卦的后天在巽，巽的曜煞在酉，酉为坤卦的地曜煞。
   * 坤卦的先天在坎，坎的曜煞在辰，辰为坤卦的天曜煞。
   */
  @Test
  fun 坤三曜煞() {
    assertSame(Branch.卯, EightGhost.getGhost(Symbol.坤, GhostType.正))
    assertSame(Branch.酉, EightGhost.getGhost(Symbol.坤, GhostType.地))
    assertSame(Branch.辰, EightGhost.getGhost(Symbol.坤, GhostType.天))
  }

  /**
   * 兑卦包括庚酉辛三山，曜煞在巳，巳为兑卦的正曜煞。
   * 兑卦的后天在坎，坎的曜煞辰戌，辰戌为兑卦地曜煞。
   * 兑卦的先天在巽，巽的曜煞在酉，酉为兑卦的天曜煞。
   */
  @Test
  fun 兌三曜煞() {
    assertSame(Branch.巳, EightGhost.getGhost(Symbol.兌, GhostType.正))
    assertSame(Branch.辰, EightGhost.getGhost(Symbol.兌, GhostType.地))
    assertSame(Branch.酉, EightGhost.getGhost(Symbol.兌, GhostType.天))
  }
}