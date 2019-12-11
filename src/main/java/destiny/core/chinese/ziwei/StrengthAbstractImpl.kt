/**
 * Created by smallufo on 2017-04-20.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMinor.*
import destiny.core.chinese.ziwei.StarUnlucky.*
import java.io.Serializable
import java.util.*


/**
 * 此顆星，於此地支中，強度為何
 * 1 最強 , 7 最弱
 *
 * 參照表格
 * https://goo.gl/ZHYgh9
 * http://destiny66.com/xuetang/tzyy_wei/ji_been/02.htm
 * http://only-cafe.myweb.hinet.net/destiny/index_des.htm
 * http://imgur.com/a/iBwP0 令东来修正版
 *
 * 1  2       3    4    5      6          7
 * 南派依序分成 →廟、旺、    得地     、平和、   閒地       、陷    ，等六級。
 * 北派依序分成 →廟、旺、    得地、利益、平和、   不得地(失地)、陷    ，等七級。
 */

private val commonTable: Set<Triple<ZStar, Branch, Int>> = setOf(
  Triple(天魁, 子, 2), Triple(天魁, 丑, 2), Triple(天魁, 卯, 1), Triple(天魁, 午, 1), Triple(天魁, 亥, 2)
  , Triple(天鉞, 寅, 2), Triple(天鉞, 巳, 2), Triple(天鉞, 未, 2), Triple(天鉞, 申, 1), Triple(天鉞, 酉, 1)
  , Triple(天官, 寅, 5), Triple(天官, 卯, 2), Triple(天官, 辰, 2), Triple(天官, 巳, 2), Triple(天官, 午, 1), Triple(天官, 未, 1), Triple(天官, 酉, 5), Triple(天官, 戌, 5), Triple(天官, 亥, 2)
  , Triple(天福, 子, 5), Triple(天福, 寅, 2), Triple(天福, 卯, 5), Triple(天福, 巳, 2), Triple(天福, 午, 5), Triple(天福, 申, 1), Triple(天福, 酉, 1), Triple(天福, 亥, 1)
  , Triple(陀羅, 丑, 1), Triple(陀羅, 寅, 7), Triple(陀羅, 辰, 1), Triple(陀羅, 巳, 7), Triple(陀羅, 未, 1), Triple(陀羅, 申, 7), Triple(陀羅, 戌, 1), Triple(陀羅, 亥, 7)
  , Triple(擎羊, 子, 7), Triple(擎羊, 丑, 1), Triple(擎羊, 卯, 7), Triple(擎羊, 辰, 1), Triple(擎羊, 午, 5), Triple(擎羊, 未, 1), Triple(擎羊, 酉, 7), Triple(擎羊, 戌, 1)
  /** 年支星 . [StarUnlucky.火星] 以及 [StarUnlucky.鈴星] 有其他版本爭議 . 無 [StarMinor.月德] , 無 [StarMinor.蜚廉]  */
  , Triple(孤辰, 寅, 5), Triple(孤辰, 巳, 7), Triple(孤辰, 申, 5), Triple(孤辰, 亥, 7)
  , Triple(華蓋, 丑, 7), Triple(華蓋, 辰, 1), Triple(華蓋, 未, 7), Triple(華蓋, 戌, 5)
  , Triple(咸池, 子, 7), Triple(咸池, 卯, 5), Triple(咸池, 午, 7), Triple(咸池, 酉, 5)
  , Triple(寡宿, 丑, 5), Triple(寡宿, 辰, 7), Triple(寡宿, 未, 6), Triple(寡宿, 戌, 7)
  , Triple(破碎, 丑, 7), Triple(破碎, 巳, 7), Triple(破碎, 酉, 5)
  , Triple(年馬, 寅, 2), Triple(年馬, 巳, 5), Triple(年馬, 申, 2), Triple(年馬, 亥, 5)
  , Triple(紅鸞, 子, 1), Triple(紅鸞, 丑, 7), Triple(紅鸞, 寅, 2), Triple(紅鸞, 卯, 1), Triple(紅鸞, 辰, 7), Triple(紅鸞, 巳, 2), Triple(紅鸞, 午, 2), Triple(紅鸞, 未, 7), Triple(紅鸞, 申, 1), Triple(紅鸞, 酉, 2), Triple(紅鸞, 戌, 7), Triple(紅鸞, 亥, 1)
  , Triple(天喜, 子, 2), Triple(天喜, 丑, 7), Triple(天喜, 寅, 1), Triple(天喜, 卯, 2), Triple(天喜, 辰, 7), Triple(天喜, 巳, 1), Triple(天喜, 午, 1), Triple(天喜, 未, 7), Triple(天喜, 申, 2), Triple(天喜, 酉, 1), Triple(天喜, 戌, 7), Triple(天喜, 亥, 2)
  , Triple(龍池, 子, 2), Triple(龍池, 丑, 5), Triple(龍池, 寅, 5), Triple(龍池, 卯, 1), Triple(龍池, 辰, 1), Triple(龍池, 巳, 7), Triple(龍池, 午, 6), Triple(龍池, 未, 1), Triple(龍池, 申, 5), Triple(龍池, 酉, 1), Triple(龍池, 戌, 7), Triple(龍池, 亥, 2)
  , Triple(鳳閣, 子, 1), Triple(鳳閣, 丑, 4), Triple(鳳閣, 寅, 1), Triple(鳳閣, 卯, 2), Triple(鳳閣, 辰, 7), Triple(鳳閣, 巳, 1), Triple(鳳閣, 午, 5), Triple(鳳閣, 未, 7), Triple(鳳閣, 申, 6), Triple(鳳閣, 酉, 1), Triple(鳳閣, 戌, 1), Triple(鳳閣, 亥, 2)
  , Triple(天德, 子, 1), Triple(天德, 丑, 1), Triple(天德, 寅, 5), Triple(天德, 卯, 5), Triple(天德, 辰, 1), Triple(天德, 巳, 2), Triple(天德, 午, 2), Triple(天德, 未, 1), Triple(天德, 申, 5), Triple(天德, 酉, 6), Triple(天德, 戌, 1), Triple(天德, 亥, 5)
  , Triple(天哭, 子, 5), Triple(天哭, 丑, 1), Triple(天哭, 寅, 5), Triple(天哭, 卯, 1), Triple(天哭, 辰, 5), Triple(天哭, 巳, 3), Triple(天哭, 午, 7), Triple(天哭, 未, 5), Triple(天哭, 申, 1), Triple(天哭, 酉, 6), Triple(天哭, 戌, 5), Triple(天哭, 亥, 5)
  , Triple(天虛, 子, 7), Triple(天虛, 丑, 1), Triple(天虛, 寅, 2), Triple(天虛, 卯, 1), Triple(天虛, 辰, 7), Triple(天虛, 巳, 2), Triple(天虛, 午, 5), Triple(天虛, 未, 7), Triple(天虛, 申, 1), Triple(天虛, 酉, 2), Triple(天虛, 戌, 7), Triple(天虛, 亥, 5)
  , Triple(天空, 子, 7), Triple(天空, 丑, 5), Triple(天空, 寅, 7), Triple(天空, 卯, 5), Triple(天空, 辰, 1), Triple(天空, 巳, 1), Triple(天空, 午, 1), Triple(天空, 未, 7), Triple(天空, 申, 2), Triple(天空, 酉, 2), Triple(天空, 戌, 7), Triple(天空, 亥, 5)

  /** 月系星 . [StarLucky.左輔] , [StarLucky.右弼] 有其他版本  */
  , Triple(天巫, 寅, 5), Triple(天巫, 巳, 7), Triple(天巫, 申, 7), Triple(天巫, 亥, 5)
  , Triple(月馬, 寅, 2), Triple(月馬, 巳, 5), Triple(月馬, 申, 2), Triple(月馬, 亥, 5)
  , Triple(陰煞, 寅, 5), Triple(陰煞, 卯, 7), Triple(陰煞, 辰, 6), Triple(陰煞, 巳, 7), Triple(陰煞, 午, 7), Triple(陰煞, 未, 5), Triple(陰煞, 戌, 7), Triple(陰煞, 亥, 1)
  , Triple(天月, 寅, 5), Triple(天月, 卯, 7), Triple(天月, 辰, 6), Triple(天月, 巳, 7), Triple(天月, 午, 7), Triple(天月, 未, 5), Triple(天月, 戌, 7), Triple(天月, 亥, 1)
  , Triple(解神, 子, 1), Triple(解神, 丑, 5), Triple(解神, 寅, 1), Triple(解神, 卯, 1), Triple(解神, 辰, 1), Triple(解神, 巳, 2), Triple(解神, 午, 1), Triple(解神, 未, 5), Triple(解神, 申, 6), Triple(解神, 酉, 2), Triple(解神, 戌, 1), Triple(解神, 亥, 6)
  , Triple(天姚, 子, 7), Triple(天姚, 丑, 5), Triple(天姚, 寅, 2), Triple(天姚, 卯, 1), Triple(天姚, 辰, 7), Triple(天姚, 巳, 5), Triple(天姚, 午, 5), Triple(天姚, 未, 2), Triple(天姚, 申, 6), Triple(天姚, 酉, 1), Triple(天姚, 戌, 1), Triple(天姚, 亥, 7)
  , Triple(天刑, 子, 5), Triple(天刑, 丑, 7), Triple(天刑, 寅, 1), Triple(天刑, 卯, 1), Triple(天刑, 辰, 5), Triple(天刑, 巳, 7), Triple(天刑, 午, 5), Triple(天刑, 未, 7), Triple(天刑, 申, 7), Triple(天刑, 酉, 1), Triple(天刑, 戌, 1), Triple(天刑, 亥, 7)

  /** 時系星 . [StarLucky.文昌] , [StarLucky.文曲] 有其他版本  */
  , Triple(封誥, 子, 1), Triple(封誥, 丑, 2), Triple(封誥, 寅, 1), Triple(封誥, 卯, 1), Triple(封誥, 辰, 5), Triple(封誥, 巳, 7), Triple(封誥, 午, 1), Triple(封誥, 未, 6), Triple(封誥, 申, 7), Triple(封誥, 酉, 5), Triple(封誥, 戌, 2), Triple(封誥, 亥, 7)
  , Triple(台輔, 子, 1), Triple(台輔, 丑, 1), Triple(台輔, 寅, 1), Triple(台輔, 卯, 5), Triple(台輔, 辰, 2), Triple(台輔, 巳, 5), Triple(台輔, 午, 2), Triple(台輔, 未, 5), Triple(台輔, 申, 5), Triple(台輔, 酉, 7), Triple(台輔, 戌, 1), Triple(台輔, 亥, 7)
  , Triple(地劫, 子, 7), Triple(地劫, 丑, 7), Triple(地劫, 寅, 5), Triple(地劫, 卯, 5), Triple(地劫, 辰, 7), Triple(地劫, 巳, 6), Triple(地劫, 午, 1), Triple(地劫, 未, 5), Triple(地劫, 申, 1), Triple(地劫, 酉, 5), Triple(地劫, 戌, 5), Triple(地劫, 亥, 7)
  , Triple(地空, 子, 5), Triple(地空, 丑, 7), Triple(地空, 寅, 7), Triple(地空, 卯, 5), Triple(地空, 辰, 7), Triple(地空, 巳, 1), Triple(地空, 午, 1), Triple(地空, 未, 5), Triple(地空, 申, 1), Triple(地空, 酉, 1), Triple(地空, 戌, 7), Triple(地空, 亥, 7)

  /** 日月  */
  , Triple(三台, 子, 5), Triple(三台, 丑, 1), Triple(三台, 寅, 5), Triple(三台, 卯, 7), Triple(三台, 辰, 1), Triple(三台, 巳, 5), Triple(三台, 午, 2), Triple(三台, 未, 1), Triple(三台, 申, 5), Triple(三台, 酉, 1), Triple(三台, 戌, 2), Triple(三台, 亥, 5)
  , Triple(八座, 子, 7), Triple(八座, 丑, 1), Triple(八座, 寅, 1), Triple(八座, 卯, 5), Triple(八座, 辰, 2), Triple(八座, 巳, 1), Triple(八座, 午, 2), Triple(八座, 未, 5), Triple(八座, 申, 1), Triple(八座, 酉, 1), Triple(八座, 戌, 5), Triple(八座, 亥, 1)

  /** 日時  */
  , Triple(天貴, 子, 1), Triple(天貴, 丑, 2), Triple(天貴, 寅, 5), Triple(天貴, 卯, 2), Triple(天貴, 辰, 2), Triple(天貴, 巳, 5), Triple(天貴, 午, 1), Triple(天貴, 未, 2), Triple(天貴, 申, 7), Triple(天貴, 酉, 1), Triple(天貴, 戌, 2), Triple(天貴, 亥, 5)
  , Triple(恩光, 子, 5), Triple(恩光, 丑, 1), Triple(恩光, 寅, 5), Triple(恩光, 卯, 1), Triple(恩光, 辰, 1), Triple(恩光, 巳, 5), Triple(恩光, 午, 1), Triple(恩光, 未, 2), Triple(恩光, 申, 5), Triple(恩光, 酉, 7), Triple(恩光, 戌, 1), Triple(恩光, 亥, 6)

  /** 年月時 (天壽 未，有爭議 這裡用廟,有版本用旺)  */
  , Triple(天才, 子, 2), Triple(天才, 丑, 5), Triple(天才, 寅, 1), Triple(天才, 卯, 2), Triple(天才, 辰, 7), Triple(天才, 巳, 1), Triple(天才, 午, 2), Triple(天才, 未, 5), Triple(天才, 申, 1), Triple(天才, 酉, 2), Triple(天才, 戌, 7), Triple(天才, 亥, 1)
  , Triple(天壽, 子, 5), Triple(天壽, 丑, 1), Triple(天壽, 寅, 2), Triple(天壽, 卯, 7), Triple(天壽, 辰, 1), Triple(天壽, 巳, 5), Triple(天壽, 午, 5), Triple(天壽, 未, 1), Triple(天壽, 申, 2), Triple(天壽, 酉, 5), Triple(天壽, 戌, 1), Triple(天壽, 亥, 2)

  /** 宮位  */
  , Triple(天傷, 子, 7), Triple(天傷, 丑, 5), Triple(天傷, 寅, 5), Triple(天傷, 卯, 7), Triple(天傷, 辰, 5), Triple(天傷, 巳, 5), Triple(天傷, 午, 7), Triple(天傷, 未, 7), Triple(天傷, 申, 5), Triple(天傷, 酉, 5), Triple(天傷, 戌, 5), Triple(天傷, 亥, 2)
  , Triple(天使, 子, 7), Triple(天使, 丑, 7), Triple(天使, 寅, 5), Triple(天使, 卯, 5), Triple(天使, 辰, 7), Triple(天使, 巳, 5), Triple(天使, 午, 5), Triple(天使, 未, 5), Triple(天使, 申, 5), Triple(天使, 酉, 7), Triple(天使, 戌, 7), Triple(天使, 亥, 2)
) // commonTable


private val commonStarMap: Map<ZStar, List<Pair<Branch, Int>>> = commonTable
  .groupBy { it.first }
  .mapValues { it.component2().map { t -> Pair(t.second, t.third) } }

private val commonPairMap: Map<Pair<ZStar, Branch>, Int> = commonTable
  .groupBy { Pair(it.first, it.second) }
  .mapValues { it -> it.component2().map { it.third }.first() }


abstract class StrengthAbstractImpl : IStrength, Serializable {

  /** 取得一個星體，在 12 個宮位的廟旺表  */
  override fun getMapOf(star: ZStar): Map<Branch, Int> {
    // 先從特殊實作層找起
    return (getImplMapOf(star) ?: emptyMap()).toMutableMap().apply {
      // 再與 commonTable 整合
      commonStarMap[star]?.forEach { (branch, intValue) ->
        this.putIfAbsent(branch, intValue)
      }
    }
  }

  override fun getStrengthOf(star: ZStar, branch: Branch): Int? {
    return getImplStrengthOf(star, branch) // 先從特殊實作表格找起
      ?: commonPairMap[Pair(star, branch)]
  }

  /** 取得「某顆星，在某地支」的該派實作  */
  internal abstract fun getImplStrengthOf(star: ZStar, branch: Branch): Int?

  /** 取得「某顆星」於 12地支的廟旺表  */
  internal abstract fun getImplMapOf(star: ZStar): Map<Branch, Int>?

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(StrengthAbstractImpl::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }
  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }
}
