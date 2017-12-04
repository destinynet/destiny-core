/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*

/**
 * 14 顆主星
 * resource key 存放於 [ZStar] 的 ZStar.properties 當中
 */
sealed class StarMain(nameKey: String) : ZStar(nameKey, ZStar::class.java.name, nameKey + "_ABBR", ZStar.Type.主星) {
  object 紫微 : StarMain("紫微")
  object 天機 : StarMain("天機")
  object 太陽 : StarMain("太陽")
  object 武曲 : StarMain("武曲")
  object 天同 : StarMain("天同")
  object 廉貞 : StarMain("廉貞")
  object 天府 : StarMain("天府")
  object 太陰 : StarMain("太陰")
  object 貪狼 : StarMain("貪狼")
  object 巨門 : StarMain("巨門")
  object 天相 : StarMain("天相")
  object 天梁 : StarMain("天梁")
  object 七殺 : StarMain("七殺")
  object 破軍 : StarMain("破軍")

  companion object {

    val values = arrayOf(紫微, 天機, 太陽, 武曲, 天同, 廉貞, 天府, 太陰, 貪狼, 巨門, 天相, 天梁, 七殺, 破軍)
    val list = values.map { it -> it }.toList()

    // （局數 , 日數 , 是否閏月 , 上個月的天數 , 紫微星實作) -> 地支
    val fun紫微 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> iPurpleBranch.getBranchOfPurpleStar(set, days, leap, prevMonthDays) }
    val fun天機 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(set, days, leap, prevMonthDays, iPurpleBranch).prev(1) }
    val fun太陽 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(set, days, leap, prevMonthDays, iPurpleBranch).prev(3) }
    val fun武曲 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(set, days, leap, prevMonthDays, iPurpleBranch).prev(4) }
    val fun天同 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(set, days, leap, prevMonthDays, iPurpleBranch).prev(5) }
    val fun廉貞 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(set, days, leap, prevMonthDays, iPurpleBranch).prev(8) }
    val fun天府 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> getOpposite(fun紫微.invoke(set, days, leap, prevMonthDays, iPurpleBranch)) }
    val fun太陰 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(1) }
    val fun貪狼 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(2) }
    val fun巨門 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(3) }
    val fun天相 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(4) }
    val fun天梁 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(5) }
    val fun七殺 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(6) }
    val fun破軍 = { set: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(set, days, leap, prevMonthDays, iPurpleBranch).next(10) }

    /**
     * 以「寅申」為軸，取得對宮的地支
     * @param branch
     * @return
     */
    private fun getOpposite(branch: Branch): Branch {
      return when (branch) {
        寅 -> 寅
        申 -> 申
        卯 -> 丑
        丑 -> 卯
        辰 -> 子
        子 -> 辰
        巳 -> 亥
        亥 -> 巳
        午 -> 戌
        戌 -> 午
        未 -> 酉
        酉 -> 未
      }
    }
  }


}
