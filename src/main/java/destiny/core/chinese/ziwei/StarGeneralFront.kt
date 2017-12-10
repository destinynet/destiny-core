/**
 * Created by smallufo on 2017-06-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*

/**
 * 將前12星
 *
 *
 * 排列規則：
 *
 *
 * 按流年（生年）地支起將星，順行：
 *
 *
 * 流年將前諸星：
 * 寅午戍年將星午, 申子辰年子將星, 巳酉丑將酉上駐, 亥卯未將卯上停
 * 攀鞍歲驛並息神, 華蓋劫煞災煞輕, 天煞指背咸池續, 月煞亡神次第行
 */
sealed class StarGeneralFront(nameKey: String) : ZStar(nameKey, StarGeneralFront::class.java.name, ZStar.Type.將前) {

  object 將星 : StarGeneralFront("將星")
  object 攀鞍 : StarGeneralFront("攀鞍")
  object 歲馹 : StarGeneralFront("歲馹")
  object 息神 : StarGeneralFront("息神")
  object 華蓋 : StarGeneralFront("華蓋")
  object 劫煞 : StarGeneralFront("劫煞")
  object 災煞 : StarGeneralFront("災煞")
  object 天煞 : StarGeneralFront("天煞")
  object 指背 : StarGeneralFront("指背")
  object 咸池 : StarGeneralFront("咸池")
  object 月煞 : StarGeneralFront("月煞")
  object 亡神 : StarGeneralFront("亡神")

  companion object {

    val values = arrayOf(將星, 攀鞍, 歲馹, 息神, 華蓋, 劫煞, 災煞, 天煞, 指背, 咸池, 月煞, 亡神)

    private val list = arrayOf(*values)

    private val funFiveElement = { 五行: FiveElement ->
      when (五行) {
        火 -> 午
        木 -> 卯
        水 -> 子
        金 -> 酉
        else -> throw AssertionError("Error : " + 五行)
      }
    }

    private val funYearBranch = { 年支: Branch, 星: StarGeneralFront ->
      val 五行 = BranchTools.trilogy(年支)
      // 第一個 (將星)
      val head = funFiveElement.invoke(五行)
      val steps = list.indexOf(星)
      head.next(steps)
    }

    val fun將星 = { 年支: Branch -> funYearBranch.invoke(年支, 將星) }
    val fun攀鞍 = { 年支: Branch -> funYearBranch.invoke(年支, 攀鞍) }
    val fun歲馹 = { 年支: Branch -> funYearBranch.invoke(年支, 歲馹) }
    val fun息神 = { 年支: Branch -> funYearBranch.invoke(年支, 息神) }
    val fun華蓋 = { 年支: Branch -> funYearBranch.invoke(年支, 華蓋) }
    val fun劫煞 = { 年支: Branch -> funYearBranch.invoke(年支, 劫煞) }
    val fun災煞 = { 年支: Branch -> funYearBranch.invoke(年支, 災煞) }
    val fun天煞 = { 年支: Branch -> funYearBranch.invoke(年支, 天煞) }
    val fun指背 = { 年支: Branch -> funYearBranch.invoke(年支, 指背) }
    val fun咸池 = { 年支: Branch -> funYearBranch.invoke(年支, 咸池) }
    val fun月煞 = { 年支: Branch -> funYearBranch.invoke(年支, 月煞) }
    val fun亡神 = { 年支: Branch -> funYearBranch.invoke(年支, 亡神) }

    val starFuncMap: Map<StarGeneralFront, Function1<Branch, Branch>> = mapOf(
      將星 to fun將星,
      攀鞍 to fun攀鞍,
      歲馹 to fun歲馹,
      息神 to fun息神,
      華蓋 to fun華蓋,
      劫煞 to fun劫煞,
      災煞 to fun災煞,
      天煞 to fun天煞,
      指背 to fun指背,
      咸池 to fun咸池,
      月煞 to fun月煞,
      亡神 to fun亡神
    )
  }
}
