/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei;


import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import destiny.core.chinese.Branch;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.ziwei.StarMain.Companion;
import static destiny.core.chinese.ziwei.StarUnlucky.火星;
import static destiny.core.chinese.ziwei.StarUnlucky.鈴星;

/**
 * 全書派
 *
 * 亦即是此表格 https://goo.gl/ZHYgh9 中的紫色(北派) ,
 * 根據太虛君拍的文檔（出自全書） http://imgur.com/Lwmf013 , 比對這是該頁面的紫色（北派）無誤
 * 但太虛君稱，「全書」為南派 , 「全集」為北派
 * 為求最保險的稱謂，稱此派為「全書派」 即可
 *
 * */
public class StrengthFullBookImpl extends StrengthAbstractImpl {

  /**
   * 此顆星，於此地支中，強度為何
   * 1 最強 , 7 最弱
   *
   *             1  2       3    4    5      6          7
   * 南派依序分成 →廟、旺、    得地     、平和、   閒地       、陷    ，等六級。
   * 北派依序分成 →廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
   * */
  private final static Table<ZStar , Branch , Integer> table = new ImmutableTable.Builder<ZStar, Branch, Integer>()
    .put(Companion.get紫微(), 子, 5).put(Companion.get紫微(), 丑, 1).put(Companion.get紫微(), 寅, 2).put(Companion.get紫微(), 卯, 2).put(Companion.get紫微(), 辰, 3).put(Companion.get紫微(), 巳, 2).put(Companion.get紫微(), 午, 1).put(Companion.get紫微(), 未, 1).put(Companion.get紫微(), 申, 2).put(Companion.get紫微(), 酉, 2).put(Companion.get紫微(), 戌, 3).put(Companion.get紫微(), 亥, 2)
    .put(Companion.get天機(), 子, 1).put(Companion.get天機(), 丑, 7).put(Companion.get天機(), 寅, 3).put(Companion.get天機(), 卯, 2).put(Companion.get天機(), 辰, 4).put(Companion.get天機(), 巳, 5).put(Companion.get天機(), 午, 1).put(Companion.get天機(), 未, 7).put(Companion.get天機(), 申, 3).put(Companion.get天機(), 酉, 2).put(Companion.get天機(), 戌, 3).put(Companion.get天機(), 亥, 5)
    .put(Companion.get太陽(), 子, 7).put(Companion.get太陽(), 丑, 6).put(Companion.get太陽(), 寅, 2).put(Companion.get太陽(), 卯, 1).put(Companion.get太陽(), 辰, 2).put(Companion.get太陽(), 巳, 2).put(Companion.get太陽(), 午, 2).put(Companion.get太陽(), 未, 3).put(Companion.get太陽(), 申, 3).put(Companion.get太陽(), 酉, 5).put(Companion.get太陽(), 戌, 6).put(Companion.get太陽(), 亥, 7)
    .put(Companion.get武曲(), 子, 2).put(Companion.get武曲(), 丑, 1).put(Companion.get武曲(), 寅, 3).put(Companion.get武曲(), 卯, 4).put(Companion.get武曲(), 辰, 1).put(Companion.get武曲(), 巳, 5).put(Companion.get武曲(), 午, 2).put(Companion.get武曲(), 未, 1).put(Companion.get武曲(), 申, 3).put(Companion.get武曲(), 酉, 4).put(Companion.get武曲(), 戌, 1).put(Companion.get武曲(), 亥, 5)
    .put(Companion.get天同(), 子, 2).put(Companion.get天同(), 丑, 6).put(Companion.get天同(), 寅, 4).put(Companion.get天同(), 卯, 5).put(Companion.get天同(), 辰, 5).put(Companion.get天同(), 巳, 1).put(Companion.get天同(), 午, 7).put(Companion.get天同(), 未, 6).put(Companion.get天同(), 申, 2).put(Companion.get天同(), 酉, 5).put(Companion.get天同(), 戌, 5).put(Companion.get天同(), 亥, 1)
    .put(Companion.get廉貞(), 子, 5).put(Companion.get廉貞(), 丑, 4).put(Companion.get廉貞(), 寅, 1).put(Companion.get廉貞(), 卯, 5).put(Companion.get廉貞(), 辰, 4).put(Companion.get廉貞(), 巳, 7).put(Companion.get廉貞(), 午, 5).put(Companion.get廉貞(), 未, 4).put(Companion.get廉貞(), 申, 1).put(Companion.get廉貞(), 酉, 5).put(Companion.get廉貞(), 戌, 4).put(Companion.get廉貞(), 亥, 7)
    .put(Companion.get天府(), 子, 1).put(Companion.get天府(), 丑, 1).put(Companion.get天府(), 寅, 1).put(Companion.get天府(), 卯, 3).put(Companion.get天府(), 辰, 1).put(Companion.get天府(), 巳, 3).put(Companion.get天府(), 午, 2).put(Companion.get天府(), 未, 1).put(Companion.get天府(), 申, 3).put(Companion.get天府(), 酉, 2).put(Companion.get天府(), 戌, 1).put(Companion.get天府(), 亥, 3)
    .put(Companion.get太陰(), 子, 1).put(Companion.get太陰(), 丑, 1).put(Companion.get太陰(), 寅, 2).put(Companion.get太陰(), 卯, 7).put(Companion.get太陰(), 辰, 7).put(Companion.get太陰(), 巳, 7).put(Companion.get太陰(), 午, 6).put(Companion.get太陰(), 未, 6).put(Companion.get太陰(), 申, 4).put(Companion.get太陰(), 酉, 2).put(Companion.get太陰(), 戌, 2).put(Companion.get太陰(), 亥, 1)
    .put(Companion.get貪狼(), 子, 2).put(Companion.get貪狼(), 丑, 1).put(Companion.get貪狼(), 寅, 5).put(Companion.get貪狼(), 卯, 4).put(Companion.get貪狼(), 辰, 1).put(Companion.get貪狼(), 巳, 7).put(Companion.get貪狼(), 午, 2).put(Companion.get貪狼(), 未, 1).put(Companion.get貪狼(), 申, 5).put(Companion.get貪狼(), 酉, 4).put(Companion.get貪狼(), 戌, 1).put(Companion.get貪狼(), 亥, 7)
    .put(Companion.get巨門(), 子, 2).put(Companion.get巨門(), 丑, 6).put(Companion.get巨門(), 寅, 1).put(Companion.get巨門(), 卯, 1).put(Companion.get巨門(), 辰, 2).put(Companion.get巨門(), 巳, 2).put(Companion.get巨門(), 午, 2).put(Companion.get巨門(), 未, 6).put(Companion.get巨門(), 申, 1).put(Companion.get巨門(), 酉, 1).put(Companion.get巨門(), 戌, 7).put(Companion.get巨門(), 亥, 2)
    .put(Companion.get天相(), 子, 1).put(Companion.get天相(), 丑, 1).put(Companion.get天相(), 寅, 1).put(Companion.get天相(), 卯, 7).put(Companion.get天相(), 辰, 3).put(Companion.get天相(), 巳, 3).put(Companion.get天相(), 午, 1).put(Companion.get天相(), 未, 3).put(Companion.get天相(), 申, 1).put(Companion.get天相(), 酉, 7).put(Companion.get天相(), 戌, 3).put(Companion.get天相(), 亥, 3)
    .put(Companion.get天梁(), 子, 1).put(Companion.get天梁(), 丑, 2).put(Companion.get天梁(), 寅, 1).put(Companion.get天梁(), 卯, 1).put(Companion.get天梁(), 辰, 1).put(Companion.get天梁(), 巳, 3).put(Companion.get天梁(), 午, 1).put(Companion.get天梁(), 未, 2).put(Companion.get天梁(), 申, 7).put(Companion.get天梁(), 酉, 3).put(Companion.get天梁(), 戌, 1).put(Companion.get天梁(), 亥, 7)
    .put(Companion.get七殺(), 子, 2).put(Companion.get七殺(), 丑, 1).put(Companion.get七殺(), 寅, 1).put(Companion.get七殺(), 卯, 2).put(Companion.get七殺(), 辰, 1).put(Companion.get七殺(), 巳, 5).put(Companion.get七殺(), 午, 2).put(Companion.get七殺(), 未, 1).put(Companion.get七殺(), 申, 1).put(Companion.get七殺(), 酉, 2).put(Companion.get七殺(), 戌, 1).put(Companion.get七殺(), 亥, 5)
    .put(Companion.get破軍(), 子, 1).put(Companion.get破軍(), 丑, 2).put(Companion.get破軍(), 寅, 3).put(Companion.get破軍(), 卯, 7).put(Companion.get破軍(), 辰, 2).put(Companion.get破軍(), 巳, 5).put(Companion.get破軍(), 午, 1).put(Companion.get破軍(), 未, 2).put(Companion.get破軍(), 申, 3).put(Companion.get破軍(), 酉, 7).put(Companion.get破軍(), 戌, 2).put(Companion.get破軍(), 亥, 5)

    .put(StarLucky.Companion.get文昌(), 子, 3).put(StarLucky.Companion.get文昌(), 丑, 1).put(StarLucky.Companion.get文昌(), 寅, 7).put(StarLucky.Companion.get文昌(), 卯, 4).put(StarLucky.Companion.get文昌(), 辰, 3).put(StarLucky.Companion.get文昌(), 巳, 1).put(StarLucky.Companion.get文昌(), 午, 7).put(StarLucky.Companion.get文昌(), 未, 4).put(StarLucky.Companion.get文昌(), 申, 3).put(StarLucky.Companion.get文昌(), 酉, 1).put(StarLucky.Companion.get文昌(), 戌, 7).put(StarLucky.Companion.get文昌(), 亥, 4)
    .put(StarLucky.Companion.get文曲(), 子, 3).put(StarLucky.Companion.get文曲(), 丑, 1).put(StarLucky.Companion.get文曲(), 寅, 5).put(StarLucky.Companion.get文曲(), 卯, 2).put(StarLucky.Companion.get文曲(), 辰, 3).put(StarLucky.Companion.get文曲(), 巳, 1).put(StarLucky.Companion.get文曲(), 午, 7).put(StarLucky.Companion.get文曲(), 未, 2).put(StarLucky.Companion.get文曲(), 申, 3).put(StarLucky.Companion.get文曲(), 酉, 1).put(StarLucky.Companion.get文曲(), 戌, 7).put(StarLucky.Companion.get文曲(), 亥, 2)

    // 祿存只有在 辰戌丑未 沒能量，其他都是廟
    .put(StarLucky.Companion.get祿存(), 子, 1).put(StarLucky.Companion.get祿存(), 寅, 1).put(StarLucky.Companion.get祿存(), 卯, 1).put(StarLucky.Companion.get祿存(), 巳, 1).put(StarLucky.Companion.get祿存(), 午, 1).put(StarLucky.Companion.get祿存(), 申, 1).put(StarLucky.Companion.get祿存(), 酉, 1).put(StarLucky.Companion.get祿存(), 亥, 1)

    // 北派 火鈴 強弱 是一樣的
    .put(火星, 子, 7).put(火星, 丑, 3).put(火星, 寅, 1).put(火星, 卯, 4).put(火星, 辰, 7).put(火星, 巳, 3).put(火星, 午, 1).put(火星, 未, 4).put(火星, 申, 7).put(火星, 酉, 3).put(火星, 戌, 1).put(火星, 亥, 4)
    .put(鈴星, 子, 7).put(鈴星, 丑, 3).put(鈴星, 寅, 1).put(鈴星, 卯, 4).put(鈴星, 辰, 7).put(鈴星, 巳, 3).put(鈴星, 午, 1).put(鈴星, 未, 4).put(鈴星, 申, 7).put(鈴星, 酉, 3).put(鈴星, 戌, 1).put(鈴星, 亥, 4)

    .build();
  /**
   *             1  2       3    4    5      6          7
   * 南派依序分成 →廟、旺、    得地     、平和、   閒地       、陷    ，等六級。
   * 北派依序分成 →廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
   * */

  @Override
  Optional<Integer> getImplStrengthOf(ZStar star, Branch branch) {
    return Optional.ofNullable(table.get(star , branch));
  }

  @NotNull
  @Override
  Map<Branch, Integer> getImplMapOf(ZStar star) {
    return table.rowMap().getOrDefault(star , new HashMap<>());
  }
}
