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
sealed class StarMain(nameKey: String) : ZStar(nameKey, ZStar::class.java.name, nameKey + "_ABBR", Type.主星) {

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

  override fun toString(): String {
    return nameKey
  }

  companion object {

    val values by lazy { arrayOf(紫微, 天機, 太陽, 武曲, 天同, 廉貞, 天府, 太陰, 貪狼, 巨門, 天相, 天梁, 七殺, 破軍) }

    // （局數 , 日數 , 是否閏月 , 上個月的天數 , 紫微星實作) -> 地支
    private val fun紫微 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> iPurpleBranch.getBranchOfPurpleStar(state, days, leap, prevMonthDays) }
    private val fun天機 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(state, days, leap, prevMonthDays, iPurpleBranch).prev(1) }
    private val fun太陽 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(state, days, leap, prevMonthDays, iPurpleBranch).prev(3) }
    private val fun武曲 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(state, days, leap, prevMonthDays, iPurpleBranch).prev(4) }
    private val fun天同 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(state, days, leap, prevMonthDays, iPurpleBranch).prev(5) }
    private val fun廉貞 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun紫微.invoke(state, days, leap, prevMonthDays, iPurpleBranch).prev(8) }
    private val fun天府 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> getMirrored(fun紫微.invoke(state, days, leap, prevMonthDays, iPurpleBranch)) }
    private val fun太陰 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(1) }
    private val fun貪狼 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(2) }
    private val fun巨門 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(3) }
    private val fun天相 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(4) }
    private val fun天梁 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(5) }
    private val fun七殺 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(6) }
    private val fun破軍 = { state: Int, days: Int, leap: Boolean, prevMonthDays: Int, iPurpleBranch: IPurpleStarBranch -> fun天府.invoke(state, days, leap, prevMonthDays, iPurpleBranch).next(10) }

    val starFuncMap: Map<StarMain, Function5<Int, Int, Boolean, Int, IPurpleStarBranch, Branch>> by lazy {
      mapOf(
        紫微 to fun紫微,
        天機 to fun天機,
        太陽 to fun太陽,
        武曲 to fun武曲,
        天同 to fun天同,
        廉貞 to fun廉貞,
        天府 to fun天府,
        太陰 to fun太陰,
        貪狼 to fun貪狼,
        巨門 to fun巨門,
        天相 to fun天相,
        天梁 to fun天梁,
        七殺 to fun七殺,
        破軍 to fun破軍
           )
    }

    /**
     * 以「寅申」為軸，取得對宮的地支
     * @param branch
     * @return
     */
    private fun getMirrored(branch: Branch): Branch {
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
