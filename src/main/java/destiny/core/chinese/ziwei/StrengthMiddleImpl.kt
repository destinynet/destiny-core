/**
 * Created by smallufo on 2017-04-20.
 */
package destiny.core.chinese.ziwei

import com.google.common.collect.ImmutableTable
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ziwei.StarUnlucky.*
import java.util.*

/**
 * 中州派
 * 參考表格 http://www.dlzww.com/news/html/?517.html
 *
 * https://www.ptt.cc/man/Life/D3AF/M.1294834996.A.E89.html
 *
 * 亦即是此表格 https://goo.gl/ZHYgh9 中的綠色(南派)
 */
class StrengthMiddleImpl : StrengthAbstractImpl() {


  internal override fun getImplStrengthOf(star: ZStar, branch: Branch): Optional<Int> {
    return Optional.ofNullable(table.get(star, branch))
  }

  internal override fun getImplMapOf(star: ZStar): Map<Branch, Int> {
    return (table.rowMap() as java.util.Map<ZStar, Map<Branch, Int>>).getOrDefault(star, HashMap())
  }

  companion object {

    /**
     * 此顆星，於此地支中，強度為何
     * 1 最強 , 7 最弱
     *
     * 1  2       3    4    5      6          7
     * 南派依序分成 →廟、旺、    得地     、平和、   閒地       、陷    ，等六級。
     * 北派依序分成 →廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
     */
    private val table = ImmutableTable.Builder<ZStar, Branch, Int>()
      .put(StarMain.紫微, 子, 5).put(StarMain.紫微, 丑, 1).put(StarMain.紫微, 寅, 1).put(StarMain.紫微, 卯, 2).put(StarMain.紫微, 辰, 7).put(StarMain.紫微, 巳, 2).put(StarMain.紫微, 午, 1).put(StarMain.紫微, 未, 1).put(StarMain.紫微, 申, 2).put(StarMain.紫微, 酉, 5).put(StarMain.紫微, 戌, 6).put(StarMain.紫微, 亥, 2)
      .put(StarMain.天機, 子, 1).put(StarMain.天機, 丑, 7).put(StarMain.天機, 寅, 2).put(StarMain.天機, 卯, 2).put(StarMain.天機, 辰, 1).put(StarMain.天機, 巳, 5).put(StarMain.天機, 午, 1).put(StarMain.天機, 未, 7).put(StarMain.天機, 申, 5).put(StarMain.天機, 酉, 2).put(StarMain.天機, 戌, 1).put(StarMain.天機, 亥, 5)
      .put(StarMain.太陽, 子, 7).put(StarMain.太陽, 丑, 7).put(StarMain.太陽, 寅, 2).put(StarMain.太陽, 卯, 1).put(StarMain.太陽, 辰, 2).put(StarMain.太陽, 巳, 2).put(StarMain.太陽, 午, 1).put(StarMain.太陽, 未, 5).put(StarMain.太陽, 申, 6).put(StarMain.太陽, 酉, 6).put(StarMain.太陽, 戌, 7).put(StarMain.太陽, 亥, 7)
      .put(StarMain.武曲, 子, 2).put(StarMain.武曲, 丑, 1).put(StarMain.武曲, 寅, 6).put(StarMain.武曲, 卯, 7).put(StarMain.武曲, 辰, 1).put(StarMain.武曲, 巳, 5).put(StarMain.武曲, 午, 2).put(StarMain.武曲, 未, 1).put(StarMain.武曲, 申, 5).put(StarMain.武曲, 酉, 2).put(StarMain.武曲, 戌, 1).put(StarMain.武曲, 亥, 5)
      .put(StarMain.天同, 子, 2).put(StarMain.天同, 丑, 7).put(StarMain.天同, 寅, 6).put(StarMain.天同, 卯, 1).put(StarMain.天同, 辰, 5).put(StarMain.天同, 巳, 1).put(StarMain.天同, 午, 7).put(StarMain.天同, 未, 7).put(StarMain.天同, 申, 2).put(StarMain.天同, 酉, 5).put(StarMain.天同, 戌, 5).put(StarMain.天同, 亥, 1)
      .put(StarMain.廉貞, 子, 5).put(StarMain.廉貞, 丑, 2).put(StarMain.廉貞, 寅, 1).put(StarMain.廉貞, 卯, 6).put(StarMain.廉貞, 辰, 2).put(StarMain.廉貞, 巳, 7).put(StarMain.廉貞, 午, 5).put(StarMain.廉貞, 未, 1).put(StarMain.廉貞, 申, 1).put(StarMain.廉貞, 酉, 5).put(StarMain.廉貞, 戌, 2).put(StarMain.廉貞, 亥, 7)
      .put(StarMain.天府, 子, 1).put(StarMain.天府, 丑, 1).put(StarMain.天府, 寅, 1).put(StarMain.天府, 卯, 5).put(StarMain.天府, 辰, 1).put(StarMain.天府, 巳, 5).put(StarMain.天府, 午, 2).put(StarMain.天府, 未, 1).put(StarMain.天府, 申, 5).put(StarMain.天府, 酉, 7).put(StarMain.天府, 戌, 1).put(StarMain.天府, 亥, 2)
      .put(StarMain.太陰, 子, 1).put(StarMain.太陰, 丑, 1).put(StarMain.太陰, 寅, 6).put(StarMain.太陰, 卯, 7).put(StarMain.太陰, 辰, 6).put(StarMain.太陰, 巳, 7).put(StarMain.太陰, 午, 7).put(StarMain.太陰, 未, 5).put(StarMain.太陰, 申, 5).put(StarMain.太陰, 酉, 2).put(StarMain.太陰, 戌, 2).put(StarMain.太陰, 亥, 1)
      .put(StarMain.貪狼, 子, 2).put(StarMain.貪狼, 丑, 1).put(StarMain.貪狼, 寅, 5).put(StarMain.貪狼, 卯, 3).put(StarMain.貪狼, 辰, 1).put(StarMain.貪狼, 巳, 7).put(StarMain.貪狼, 午, 2).put(StarMain.貪狼, 未, 1).put(StarMain.貪狼, 申, 5).put(StarMain.貪狼, 酉, 5).put(StarMain.貪狼, 戌, 1).put(StarMain.貪狼, 亥, 7)
      .put(StarMain.巨門, 子, 2).put(StarMain.巨門, 丑, 2).put(StarMain.巨門, 寅, 1).put(StarMain.巨門, 卯, 1).put(StarMain.巨門, 辰, 5).put(StarMain.巨門, 巳, 5).put(StarMain.巨門, 午, 2).put(StarMain.巨門, 未, 7).put(StarMain.巨門, 申, 1).put(StarMain.巨門, 酉, 1).put(StarMain.巨門, 戌, 2).put(StarMain.巨門, 亥, 2)
      .put(StarMain.天相, 子, 1).put(StarMain.天相, 丑, 1).put(StarMain.天相, 寅, 1).put(StarMain.天相, 卯, 7).put(StarMain.天相, 辰, 2).put(StarMain.天相, 巳, 5).put(StarMain.天相, 午, 2).put(StarMain.天相, 未, 6).put(StarMain.天相, 申, 1).put(StarMain.天相, 酉, 7).put(StarMain.天相, 戌, 6).put(StarMain.天相, 亥, 5)
      .put(StarMain.天梁, 子, 1).put(StarMain.天梁, 丑, 2).put(StarMain.天梁, 寅, 1).put(StarMain.天梁, 卯, 1).put(StarMain.天梁, 辰, 2).put(StarMain.天梁, 巳, 7).put(StarMain.天梁, 午, 1).put(StarMain.天梁, 未, 2).put(StarMain.天梁, 申, 7).put(StarMain.天梁, 酉, 3).put(StarMain.天梁, 戌, 2).put(StarMain.天梁, 亥, 7)
      .put(StarMain.七殺, 子, 2).put(StarMain.七殺, 丑, 1).put(StarMain.七殺, 寅, 1).put(StarMain.七殺, 卯, 7).put(StarMain.七殺, 辰, 2).put(StarMain.七殺, 巳, 5).put(StarMain.七殺, 午, 2).put(StarMain.七殺, 未, 2).put(StarMain.七殺, 申, 1).put(StarMain.七殺, 酉, 6).put(StarMain.七殺, 戌, 1).put(StarMain.七殺, 亥, 5)
      .put(StarMain.破軍, 子, 1).put(StarMain.破軍, 丑, 2).put(StarMain.破軍, 寅, 7).put(StarMain.破軍, 卯, 2).put(StarMain.破軍, 辰, 2).put(StarMain.破軍, 巳, 6).put(StarMain.破軍, 午, 1).put(StarMain.破軍, 未, 1).put(StarMain.破軍, 申, 7).put(StarMain.破軍, 酉, 7).put(StarMain.破軍, 戌, 2).put(StarMain.破軍, 亥, 5)

      .put(地劫, 亥, 2) // 與 commonTable 不同之處

      .put(StarLucky.文曲, 子, 1).put(StarLucky.文曲, 丑, 1).put(StarLucky.文曲, 寅, 5).put(StarLucky.文曲, 卯, 2).put(StarLucky.文曲, 辰, 1).put(StarLucky.文曲, 巳, 1).put(StarLucky.文曲, 午, 7).put(StarLucky.文曲, 未, 2).put(StarLucky.文曲, 申, 5).put(StarLucky.文曲, 酉, 1).put(StarLucky.文曲, 戌, 7).put(StarLucky.文曲, 亥, 2)
      .put(StarLucky.文昌, 子, 2).put(StarLucky.文昌, 丑, 1).put(StarLucky.文昌, 寅, 7).put(StarLucky.文昌, 卯, 5).put(StarLucky.文昌, 辰, 2).put(StarLucky.文昌, 巳, 1).put(StarLucky.文昌, 午, 7).put(StarLucky.文昌, 未, 5).put(StarLucky.文昌, 申, 2).put(StarLucky.文昌, 酉, 1).put(StarLucky.文昌, 戌, 7).put(StarLucky.文昌, 亥, 2)

      // 火星、鈴星、左輔、右弼 https://www.ptt.cc/man/Life/D3AF/M.1294834996.A.E89.html
      .put(火星, 子, 5).put(火星, 丑, 2).put(火星, 寅, 1).put(火星, 卯, 5).put(火星, 辰, 6).put(火星, 巳, 2).put(火星, 午, 1).put(火星, 未, 6).put(火星, 申, 7).put(火星, 酉, 7).put(火星, 戌, 1).put(火星, 亥, 5)
      .put(鈴星, 子, 7).put(鈴星, 丑, 7).put(鈴星, 寅, 1).put(鈴星, 卯, 1).put(鈴星, 辰, 2).put(鈴星, 巳, 2).put(鈴星, 午, 1).put(鈴星, 未, 2).put(鈴星, 申, 2).put(鈴星, 酉, 7).put(鈴星, 戌, 1).put(鈴星, 亥, 1)
      .put(StarLucky.左輔, 子, 2).put(StarLucky.左輔, 丑, 1).put(StarLucky.左輔, 寅, 1).put(StarLucky.左輔, 卯, 7).put(StarLucky.左輔, 辰, 1).put(StarLucky.左輔, 巳, 5).put(StarLucky.左輔, 午, 2).put(StarLucky.左輔, 未, 1).put(StarLucky.左輔, 申, 5).put(StarLucky.左輔, 酉, 7).put(StarLucky.左輔, 戌, 1).put(StarLucky.左輔, 亥, 6)
      .put(StarLucky.右弼, 子, 1).put(StarLucky.右弼, 丑, 1).put(StarLucky.右弼, 寅, 2).put(StarLucky.右弼, 卯, 7).put(StarLucky.右弼, 辰, 1).put(StarLucky.右弼, 巳, 5).put(StarLucky.右弼, 午, 2).put(StarLucky.右弼, 未, 1).put(StarLucky.右弼, 申, 6).put(StarLucky.右弼, 酉, 7).put(StarLucky.右弼, 戌, 1).put(StarLucky.右弼, 亥, 5)

      .build()
  }
}
