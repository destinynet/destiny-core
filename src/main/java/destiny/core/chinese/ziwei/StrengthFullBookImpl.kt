/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei


import destiny.core.chinese.Branch

/**
 * 全書派
 *
 * 亦即是此表格 https://goo.gl/ZHYgh9 中的紫色(北派) ,
 * 根據太虛君拍的文檔（出自全書） http://imgur.com/Lwmf013 , 比對這是該頁面的紫色（北派）無誤
 * 但太虛君稱，「全書」為南派 , 「全集」為北派
 * 為求最保險的稱謂，稱此派為「全書派」 即可
 *
 */


/**
 * 此顆星，於此地支中，強度為何
 * 1 最強 , 7 最弱
 *
 * 1  2       3    4    5      6          7
 * 南派依序分成 →廟、旺、    得地     、平和、   閒地       、陷    ，等六級。
 * 北派依序分成 →廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
 */
private val table: Set<Triple<ZStar, Branch, Int>> = setOf(
  Triple(StarMain.紫微, Branch.子, 5), Triple(StarMain.紫微, Branch.丑, 1), Triple(StarMain.紫微, Branch.寅, 2), Triple(StarMain.紫微, Branch.卯, 2), Triple(StarMain.紫微, Branch.辰, 3), Triple(StarMain.紫微, Branch.巳, 2), Triple(StarMain.紫微, Branch.午, 1), Triple(StarMain.紫微, Branch.未, 1), Triple(StarMain.紫微, Branch.申, 2), Triple(StarMain.紫微, Branch.酉, 2), Triple(StarMain.紫微, Branch.戌, 3), Triple(StarMain.紫微, Branch.亥, 2)
  , Triple(StarMain.天機, Branch.子, 1), Triple(StarMain.天機, Branch.丑, 7), Triple(StarMain.天機, Branch.寅, 3), Triple(StarMain.天機, Branch.卯, 2), Triple(StarMain.天機, Branch.辰, 4), Triple(StarMain.天機, Branch.巳, 5), Triple(StarMain.天機, Branch.午, 1), Triple(StarMain.天機, Branch.未, 7), Triple(StarMain.天機, Branch.申, 3), Triple(StarMain.天機, Branch.酉, 2), Triple(StarMain.天機, Branch.戌, 3), Triple(StarMain.天機, Branch.亥, 5)
  , Triple(StarMain.太陽, Branch.子, 7), Triple(StarMain.太陽, Branch.丑, 6), Triple(StarMain.太陽, Branch.寅, 2), Triple(StarMain.太陽, Branch.卯, 1), Triple(StarMain.太陽, Branch.辰, 2), Triple(StarMain.太陽, Branch.巳, 2), Triple(StarMain.太陽, Branch.午, 2), Triple(StarMain.太陽, Branch.未, 3), Triple(StarMain.太陽, Branch.申, 3), Triple(StarMain.太陽, Branch.酉, 5), Triple(StarMain.太陽, Branch.戌, 6), Triple(StarMain.太陽, Branch.亥, 7)
  , Triple(StarMain.武曲, Branch.子, 2), Triple(StarMain.武曲, Branch.丑, 1), Triple(StarMain.武曲, Branch.寅, 3), Triple(StarMain.武曲, Branch.卯, 4), Triple(StarMain.武曲, Branch.辰, 1), Triple(StarMain.武曲, Branch.巳, 5), Triple(StarMain.武曲, Branch.午, 2), Triple(StarMain.武曲, Branch.未, 1), Triple(StarMain.武曲, Branch.申, 3), Triple(StarMain.武曲, Branch.酉, 4), Triple(StarMain.武曲, Branch.戌, 1), Triple(StarMain.武曲, Branch.亥, 5)
  , Triple(StarMain.天同, Branch.子, 2), Triple(StarMain.天同, Branch.丑, 6), Triple(StarMain.天同, Branch.寅, 4), Triple(StarMain.天同, Branch.卯, 5), Triple(StarMain.天同, Branch.辰, 5), Triple(StarMain.天同, Branch.巳, 1), Triple(StarMain.天同, Branch.午, 7), Triple(StarMain.天同, Branch.未, 6), Triple(StarMain.天同, Branch.申, 2), Triple(StarMain.天同, Branch.酉, 5), Triple(StarMain.天同, Branch.戌, 5), Triple(StarMain.天同, Branch.亥, 1)
  , Triple(StarMain.廉貞, Branch.子, 5), Triple(StarMain.廉貞, Branch.丑, 4), Triple(StarMain.廉貞, Branch.寅, 1), Triple(StarMain.廉貞, Branch.卯, 5), Triple(StarMain.廉貞, Branch.辰, 4), Triple(StarMain.廉貞, Branch.巳, 7), Triple(StarMain.廉貞, Branch.午, 5), Triple(StarMain.廉貞, Branch.未, 4), Triple(StarMain.廉貞, Branch.申, 1), Triple(StarMain.廉貞, Branch.酉, 5), Triple(StarMain.廉貞, Branch.戌, 4), Triple(StarMain.廉貞, Branch.亥, 7)
  , Triple(StarMain.天府, Branch.子, 1), Triple(StarMain.天府, Branch.丑, 1), Triple(StarMain.天府, Branch.寅, 1), Triple(StarMain.天府, Branch.卯, 3), Triple(StarMain.天府, Branch.辰, 1), Triple(StarMain.天府, Branch.巳, 3), Triple(StarMain.天府, Branch.午, 2), Triple(StarMain.天府, Branch.未, 1), Triple(StarMain.天府, Branch.申, 3), Triple(StarMain.天府, Branch.酉, 2), Triple(StarMain.天府, Branch.戌, 1), Triple(StarMain.天府, Branch.亥, 3)
  , Triple(StarMain.太陰, Branch.子, 1), Triple(StarMain.太陰, Branch.丑, 1), Triple(StarMain.太陰, Branch.寅, 2), Triple(StarMain.太陰, Branch.卯, 7), Triple(StarMain.太陰, Branch.辰, 7), Triple(StarMain.太陰, Branch.巳, 7), Triple(StarMain.太陰, Branch.午, 6), Triple(StarMain.太陰, Branch.未, 6), Triple(StarMain.太陰, Branch.申, 4), Triple(StarMain.太陰, Branch.酉, 2), Triple(StarMain.太陰, Branch.戌, 2), Triple(StarMain.太陰, Branch.亥, 1)
  , Triple(StarMain.貪狼, Branch.子, 2), Triple(StarMain.貪狼, Branch.丑, 1), Triple(StarMain.貪狼, Branch.寅, 5), Triple(StarMain.貪狼, Branch.卯, 4), Triple(StarMain.貪狼, Branch.辰, 1), Triple(StarMain.貪狼, Branch.巳, 7), Triple(StarMain.貪狼, Branch.午, 2), Triple(StarMain.貪狼, Branch.未, 1), Triple(StarMain.貪狼, Branch.申, 5), Triple(StarMain.貪狼, Branch.酉, 4), Triple(StarMain.貪狼, Branch.戌, 1), Triple(StarMain.貪狼, Branch.亥, 7)
  , Triple(StarMain.巨門, Branch.子, 2), Triple(StarMain.巨門, Branch.丑, 6), Triple(StarMain.巨門, Branch.寅, 1), Triple(StarMain.巨門, Branch.卯, 1), Triple(StarMain.巨門, Branch.辰, 2), Triple(StarMain.巨門, Branch.巳, 2), Triple(StarMain.巨門, Branch.午, 2), Triple(StarMain.巨門, Branch.未, 6), Triple(StarMain.巨門, Branch.申, 1), Triple(StarMain.巨門, Branch.酉, 1), Triple(StarMain.巨門, Branch.戌, 7), Triple(StarMain.巨門, Branch.亥, 2)
  , Triple(StarMain.天相, Branch.子, 1), Triple(StarMain.天相, Branch.丑, 1), Triple(StarMain.天相, Branch.寅, 1), Triple(StarMain.天相, Branch.卯, 7), Triple(StarMain.天相, Branch.辰, 3), Triple(StarMain.天相, Branch.巳, 3), Triple(StarMain.天相, Branch.午, 1), Triple(StarMain.天相, Branch.未, 3), Triple(StarMain.天相, Branch.申, 1), Triple(StarMain.天相, Branch.酉, 7), Triple(StarMain.天相, Branch.戌, 3), Triple(StarMain.天相, Branch.亥, 3)
  , Triple(StarMain.天梁, Branch.子, 1), Triple(StarMain.天梁, Branch.丑, 2), Triple(StarMain.天梁, Branch.寅, 1), Triple(StarMain.天梁, Branch.卯, 1), Triple(StarMain.天梁, Branch.辰, 1), Triple(StarMain.天梁, Branch.巳, 3), Triple(StarMain.天梁, Branch.午, 1), Triple(StarMain.天梁, Branch.未, 2), Triple(StarMain.天梁, Branch.申, 7), Triple(StarMain.天梁, Branch.酉, 3), Triple(StarMain.天梁, Branch.戌, 1), Triple(StarMain.天梁, Branch.亥, 7)
  , Triple(StarMain.七殺, Branch.子, 2), Triple(StarMain.七殺, Branch.丑, 1), Triple(StarMain.七殺, Branch.寅, 1), Triple(StarMain.七殺, Branch.卯, 2), Triple(StarMain.七殺, Branch.辰, 1), Triple(StarMain.七殺, Branch.巳, 5), Triple(StarMain.七殺, Branch.午, 2), Triple(StarMain.七殺, Branch.未, 1), Triple(StarMain.七殺, Branch.申, 1), Triple(StarMain.七殺, Branch.酉, 2), Triple(StarMain.七殺, Branch.戌, 1), Triple(StarMain.七殺, Branch.亥, 5)
  , Triple(StarMain.破軍, Branch.子, 1), Triple(StarMain.破軍, Branch.丑, 2), Triple(StarMain.破軍, Branch.寅, 3), Triple(StarMain.破軍, Branch.卯, 7), Triple(StarMain.破軍, Branch.辰, 2), Triple(StarMain.破軍, Branch.巳, 5), Triple(StarMain.破軍, Branch.午, 1), Triple(StarMain.破軍, Branch.未, 2), Triple(StarMain.破軍, Branch.申, 3), Triple(StarMain.破軍, Branch.酉, 7), Triple(StarMain.破軍, Branch.戌, 2), Triple(StarMain.破軍, Branch.亥, 5)

  , Triple(StarLucky.文昌, Branch.子, 3), Triple(StarLucky.文昌, Branch.丑, 1), Triple(StarLucky.文昌, Branch.寅, 7), Triple(StarLucky.文昌, Branch.卯, 4), Triple(StarLucky.文昌, Branch.辰, 3), Triple(StarLucky.文昌, Branch.巳, 1), Triple(StarLucky.文昌, Branch.午, 7), Triple(StarLucky.文昌, Branch.未, 4), Triple(StarLucky.文昌, Branch.申, 3), Triple(StarLucky.文昌, Branch.酉, 1), Triple(StarLucky.文昌, Branch.戌, 7), Triple(StarLucky.文昌, Branch.亥, 4)
  , Triple(StarLucky.文曲, Branch.子, 3), Triple(StarLucky.文曲, Branch.丑, 1), Triple(StarLucky.文曲, Branch.寅, 5), Triple(StarLucky.文曲, Branch.卯, 2), Triple(StarLucky.文曲, Branch.辰, 3), Triple(StarLucky.文曲, Branch.巳, 1), Triple(StarLucky.文曲, Branch.午, 7), Triple(StarLucky.文曲, Branch.未, 2), Triple(StarLucky.文曲, Branch.申, 3), Triple(StarLucky.文曲, Branch.酉, 1), Triple(StarLucky.文曲, Branch.戌, 7), Triple(StarLucky.文曲, Branch.亥, 2)

  // 祿存只有在 辰戌丑未 沒能量，其他都是廟
  , Triple(StarLucky.祿存, Branch.子, 1), Triple(StarLucky.祿存, Branch.寅, 1), Triple(StarLucky.祿存, Branch.卯, 1), Triple(StarLucky.祿存, Branch.巳, 1), Triple(StarLucky.祿存, Branch.午, 1), Triple(StarLucky.祿存, Branch.申, 1), Triple(StarLucky.祿存, Branch.酉, 1), Triple(StarLucky.祿存, Branch.亥, 1)

  // 北派 火鈴 強弱 是一樣的
  , Triple(StarUnlucky.火星, Branch.子, 7), Triple(StarUnlucky.火星, Branch.丑, 3), Triple(StarUnlucky.火星, Branch.寅, 1), Triple(StarUnlucky.火星, Branch.卯, 4), Triple(StarUnlucky.火星, Branch.辰, 7), Triple(StarUnlucky.火星, Branch.巳, 3), Triple(StarUnlucky.火星, Branch.午, 1), Triple(StarUnlucky.火星, Branch.未, 4), Triple(StarUnlucky.火星, Branch.申, 7), Triple(StarUnlucky.火星, Branch.酉, 3), Triple(StarUnlucky.火星, Branch.戌, 1), Triple(StarUnlucky.火星, Branch.亥, 4)
  , Triple(StarUnlucky.鈴星, Branch.子, 7), Triple(StarUnlucky.鈴星, Branch.丑, 3), Triple(StarUnlucky.鈴星, Branch.寅, 1), Triple(StarUnlucky.鈴星, Branch.卯, 4), Triple(StarUnlucky.鈴星, Branch.辰, 7), Triple(StarUnlucky.鈴星, Branch.巳, 3), Triple(StarUnlucky.鈴星, Branch.午, 1), Triple(StarUnlucky.鈴星, Branch.未, 4), Triple(StarUnlucky.鈴星, Branch.申, 7), Triple(StarUnlucky.鈴星, Branch.酉, 3), Triple(StarUnlucky.鈴星, Branch.戌, 1), Triple(StarUnlucky.鈴星, Branch.亥, 4)
)

private val commonStarMap: Map<ZStar, List<Pair<Branch, Int>>> = table
  .groupBy { it.first }
  .mapValues { it -> it.component2().map { t -> Pair(t.second, t.third) } }

private val commonPairMap: Map<Pair<ZStar, Branch>, Int> = table
  .groupBy { Pair(it.first, it.second) }
  .mapValues { it -> it.component2().map { it.third }.first() }


class StrengthFullBookImpl : StrengthAbstractImpl() {
  /**
   * 1  2       3    4    5      6          7
   * 南派依序分成 →廟、旺、    得地     、平和、   閒地       、陷    ，等六級。
   * 北派依序分成 →廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
   */

  override fun getImplStrengthOf(star: ZStar, branch: Branch): Int? {
    return commonPairMap[Pair(star, branch)]
  }

  override fun getImplMapOf(star: ZStar): Map<Branch, Int>? {
    return commonStarMap[star]?.toMap()
  }
}
