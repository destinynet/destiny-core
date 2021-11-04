/**
 * Created by smallufo on 2017-06-16.
 */
package destiny.core.chinese.ziwei

import destiny.core.astrology.IPoint
import destiny.core.astrology.Point
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*
import kotlin.reflect.KClass

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
sealed class StarGeneralFront(nameKey: String) : ZStar(nameKey, StarGeneralFront::class.java.name, Type.將前) {

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

  companion object : IPoint<StarGeneralFront> {

    override val type: KClass<out Point> = StarGeneralFront::class

    override val values by lazy { arrayOf(將星, 攀鞍, 歲馹, 息神, 華蓋, 劫煞, 災煞, 天煞, 指背, 咸池, 月煞, 亡神) }

    override fun fromString(value: String): StarGeneralFront? {
      return values.firstOrNull {
        it.nameKey == value
      }
    }

    private val list by lazy { arrayOf(*values) }

    private val funFiveElement = { fiveElement: FiveElement ->
      when (fiveElement) {
        火 -> 午
        木 -> 卯
        水 -> 子
        金 -> 酉
        else -> throw AssertionError("Error : $fiveElement")
      }
    }

    private val funYearBranch = { yearBranch: Branch, star: StarGeneralFront ->
      // 五行
      val fiveElement = BranchTools.trilogy(yearBranch)
      // 第一個 (將星)
      val head = funFiveElement.invoke(fiveElement)
      val steps = list.indexOf(star)
      head.next(steps)
    }

    val fun將星 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 將星) }
    val fun攀鞍 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 攀鞍) }
    private val fun歲馹 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 歲馹) }
    private val fun息神 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 息神) }
    private val fun華蓋 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 華蓋) }
    private val fun劫煞 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 劫煞) }
    private val fun災煞 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 災煞) }
    private val fun天煞 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 天煞) }
    private val fun指背 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 指背) }
    private val fun咸池 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 咸池) }
    private val fun月煞 = { yearBranch: Branch -> funYearBranch.invoke(yearBranch, 月煞) }
    val fun亡神 = { 年支: Branch -> funYearBranch.invoke(年支, 亡神) }

    val starFuncMap: Map<StarGeneralFront, Function1<Branch, Branch>> by lazy {
      mapOf(
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
}
