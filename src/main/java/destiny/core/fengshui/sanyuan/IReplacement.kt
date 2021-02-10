/**
 * Created by smallufo on 2018-03-01.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.fengshui.Mountain
import java.io.Serializable

data class ReplacementStar(val period: Int, val enabled: Boolean) : Serializable

/** 替卦 */
interface IReplacement {
  fun getReplacementStar(mnt: Mountain): ReplacementStar
}

/**
 * 13個 true , 可用替星
 * 11個 false , 不可用替
 *
 * 子癸甲申貪狼尋 (1,坎)
 * 坤壬乙卯未巨門 (2,坤)
 * 乾巽六位皆武曲 (6,乾)
 * 艮丙辛酉丑破軍 (7,兌)
 * 若問寅午庚丁上
 * 一律挨來是弼星 (9,離)
 */
class ReplacementDefaultImpl : IReplacement, Serializable {

  val map: Map<Mountain, ReplacementStar> = mapOf(
    Mountain.壬 to ReplacementStar(2, true),   // 坤、壬、乙，巨門(2,坤)從頭出
    Mountain.子 to ReplacementStar(1, false),
    Mountain.癸 to ReplacementStar(1, false),  // 甲、癸、申，貪狼(1,坎)一路行

    Mountain.丑 to ReplacementStar(7, true),
    Mountain.艮 to ReplacementStar(7, true),   // 艮、丙、辛，位位是破軍(7,兌)
    Mountain.寅 to ReplacementStar(9, true),

    Mountain.甲 to ReplacementStar(1, true),   // 甲、癸、申，貪狼(1,坎)一路行
    Mountain.卯 to ReplacementStar(2, true),
    Mountain.乙 to ReplacementStar(2, true),   // 坤、壬、乙，巨門(2,坤)從頭出

    Mountain.辰 to ReplacementStar(6, true),   // 巽、辰、亥，盡是武曲(6,乾)位
    Mountain.巽 to ReplacementStar(6, true),   // 巽、辰、亥，盡是武曲(6,乾)位
    Mountain.巳 to ReplacementStar(6, true),

    Mountain.丙 to ReplacementStar(7, true),   // 艮、丙、辛，位位是破軍(7,兌)
    Mountain.午 to ReplacementStar(9, false),
    Mountain.丁 to ReplacementStar(9, false),

    Mountain.未 to ReplacementStar(2, false),
    Mountain.坤 to ReplacementStar(2, false),  // 坤、壬、乙，巨門(2,坤)從頭出
    Mountain.申 to ReplacementStar(1, true),   // 甲、癸、申，貪狼(1,坎)一路行

    Mountain.庚 to ReplacementStar(9, true),
    Mountain.酉 to ReplacementStar(7, false),
    Mountain.辛 to ReplacementStar(7, false),  // 艮、丙、辛，位位是破軍(7,兌)

    Mountain.戌 to ReplacementStar(6, false),
    Mountain.乾 to ReplacementStar(6, false),
    Mountain.亥 to ReplacementStar(6, false)   // 巽、辰、亥，盡是武曲(6,乾)位
                                                 )

  override fun getReplacementStar(mnt: Mountain): ReplacementStar {
    return map.getValue(mnt)
  }


}

