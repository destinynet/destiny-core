/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.astrology.IPoint
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.core.chinese.ziwei.ZStar.Type.*

/**
 * 六兇星
 */
sealed class StarUnlucky(nameKey: String, type: Type) : ZStar(nameKey, ZStar::class.java.name, nameKey + "_ABBR", type) {

  object 擎羊 : StarUnlucky("擎羊", 年干) // 甲
  object 陀羅 : StarUnlucky("陀羅", 年干) // 甲
  object 火星 : StarUnlucky("火星", 年支) // 甲
  object 鈴星 : StarUnlucky("鈴星", 年支) // 甲
  object 地劫 : StarUnlucky("地劫", 時) // 乙
  object 地空 : StarUnlucky("地空", 時) // 乙 (有時又稱天空)

  companion object : IPoint<StarUnlucky> {

    override val values by lazy { arrayOf(擎羊, 陀羅, 火星, 鈴星, 地劫, 地空) }

    override fun fromString(value: String): StarUnlucky? {
      return values.firstOrNull {
        it.nameKey == value
      }
    }

    /** 擎羊 : 年干 -> 地支  */
    val fun擎羊 = { year: Stem ->
      when (year) {
        甲 -> 卯
        乙 -> 辰
        丙, 戊 -> 午
        丁, 己 -> 未
        庚 -> 酉
        辛 -> 戌
        壬 -> 子
        癸 -> 丑
      }
    }

    /** 陀羅 : 年干 -> 地支  */
    val fun陀羅 = { year: Stem ->
      when (year) {
        甲 -> 丑
        乙 -> 寅
        丙, 戊 -> 辰
        丁, 己 -> 巳
        庚 -> 未
        辛 -> 申
        壬 -> 戌
        癸 -> 亥
      }
    }

    /**
     * 參考資料
     * https://destiny.to/ubbthreads/ubbthreads.php/topics/14679
     *
     *
     * 全書和全集裡對火鈴的排法有所不同：
     * 1. 全書是根據生年年支來安火鈴：
     * 寅午戌人丑卯方  子申辰人寅戌揚
     * 巳酉丑人卯戌位  亥卯未人酉戌房
     *
     */
    /** 火星 (全書): 年支 -> 地支  */
    val fun火星_全書 = { year: Branch ->
      when (BranchTools.trilogy(year)) {
        火 -> 丑 // 寅午戌人[丑]卯方
        水 -> 寅 // 子申辰人[寅]戌揚
        金 -> 卯 // 巳酉丑人[卯]戌位
        木 -> 酉 // 亥卯未人[酉]戌房
        else -> throw AssertionError(year)
      }
    }

    /** 鈴星 (全書): 年支 -> 地支  */
    val fun鈴星_全書 = { year: Branch ->
      when (BranchTools.trilogy(year)) {
        火 -> 卯 // 寅午戌人丑[卯]方
        水 -> 戌 // 子申辰人寅[戌]揚
        金 -> 戌 // 巳酉丑人卯[戌]位
        木 -> 戌 // 亥卯未人酉[戌]房
        else -> throw AssertionError(year)
      }
    }

    /**
     * 2. 全集則是根據生年年支及生時來安火鈴
     * 申子辰人寅火戌鈴  寅午戌人丑火卯鈴
     * 亥卯未人酉火戌鈴  巳酉丑人戌火卯鈴
     * 接著有一段說明
     * 凡命俱以生年十二支為主假如申子辰子時生人則寅宮起子時順數至本人生時安火星戌宮起
     * 子時順數至本人生時安鈴星假如甲申年丑時生人則卯宮安火亥宮安鈴餘仿此
     */

    /** 火星 (全集): (年支、時支) -> 地支 (子由使用) */
    val fun火星_全集 = { year: Branch, hour: Branch ->
      when (BranchTools.trilogy(year)) {
        火 -> Branch[hour.index + 1]
        水 -> Branch[hour.index + 2]
        金 -> Branch[hour.index + 3]
        木 -> Branch[hour.index + 9]
        else -> throw AssertionError("年支 = $year , 時支 = $hour")
      }
    }

    /** 鈴星 (全集) : (年支、時支) -> 地支 (子由使用)  */
    val fun鈴星_全集 = { year : Branch , hour : Branch->
      when (BranchTools.trilogy(year)) {
        火 -> Branch[hour.index + 3]
        水, 金, 木 -> Branch[hour.index + 10]
        else -> throw AssertionError("年支 = $year , 時支 = $hour")
      }
    }

    /** 地劫 : 時支 -> 地支  */
    val fun地劫 = { hour : Branch -> hour.prev }

    /** 地空 : 時支 -> 地支  */
    val fun地空 = { hour : Branch -> Branch[11 - hour.index] }
  }
}
