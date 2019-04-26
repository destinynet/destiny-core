/**
 * Created by smallufo on 2019-04-04.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import destiny.core.chinese.Branch.*
import destiny.iching.Hexagram
import destiny.iching.Hexagram.*
import destiny.iching.IHexagram


/**
 * 《易緯》 孟喜易
 * 六日七分圖
 * https://i.imgur.com/LQndQq9.png
 * 總共 60卦， 坎離震兌 並未值日
 */
val branchHexagramsMap: Map<Branch, List<Hexagram>> = mapOf(
  子 to listOf(未濟, 蹇, 頤, 中孚, 復),
  丑 to listOf(屯, 謙, 睽, 升, 臨),
  寅 to listOf(小過, 蒙, 益, 漸, 泰),
  卯 to listOf(需, 隨, 晉, 解, 大壯),
  辰 to listOf(豫, 訟, 蠱, 革, 夬),
  巳 to listOf(旅, 師, 比, 小畜, 乾),
  午 to listOf(大有, 家人, 井, 咸, 姤),
  未 to listOf(鼎, 豐, 渙, 履, 遯),
  申 to listOf(恆, 節, 同人, 損, 否),
  酉 to listOf(巽, 萃, 大畜, 賁, 觀),
  戌 to listOf(歸妹, 無妄, 明夷, 困, 剝),
  亥 to listOf(艮, 既濟, 噬嗑, 大過, 坤)
)


/**
 * 值日卦
 * 可能有 六日七分 實作 (坎離震兌 並未值日)
 * 也會有 焦氏易林 實作 (坎離震兌 各值一日)
 * 也會有 先天64卦 實作
 *  */
interface IDailyHexagram : Descriptive {

  /**
   * 取得當下的值日卦，起迄時刻的 gmt julDay 為何
   */
  fun getHexagram(gmtJulDay: Double): Pair<Hexagram, Pair<Double, Double>>

  /**
   * 取得某時刻之後，出現此卦的 時間點範圍
   * @param forward true : 順查 , false : 逆查
   */
  fun getDutyDay(hexagram: IHexagram, gmtJulDay: Double, forward : Boolean = true): Pair<Double, Double>?
}


