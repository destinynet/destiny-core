/**
 * Created by smallufo on 2017-06-17.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * 歲前 12 星
 */
sealed class StarYearFront(nameKey: String) : ZStar(nameKey, StarYearFront::class.java.name, Type.歲前) {

  object 歲建 : StarYearFront("歲建")
  object 晦氣 : StarYearFront("晦氣")
  object 喪門 : StarYearFront("喪門")
  object 貫索 : StarYearFront("貫索")
  object 官符 : StarYearFront("官符")
  object 小耗 : StarYearFront("小耗")
  object 歲破 : StarYearFront("歲破")
  object 龍德 : StarYearFront("龍德")
  object 白虎 : StarYearFront("白虎")
  object 天德 : StarYearFront("天德")
  object 吊客 : StarYearFront("吊客")
  object 病符 : StarYearFront("病符")

  companion object {

    val values by lazy { arrayOf(歲建, 晦氣, 喪門, 貫索, 官符, 小耗, 歲破, 龍德, 白虎, 天德, 吊客, 病符) }

    private val list by lazy { listOf(*values) }

    private val func = { 年支: Branch, 星: ZStar ->
      val steps = list.indexOf(星)
      年支.next(steps)
    }

    val fun歲建 = { 年支: Branch -> func.invoke(年支, 歲建) }
    val fun晦氣 = { 年支: Branch -> func.invoke(年支, 晦氣) }
    val fun喪門 = { 年支: Branch -> func.invoke(年支, 喪門) }
    val fun貫索 = { 年支: Branch -> func.invoke(年支, 貫索) }
    val fun官符 = { 年支: Branch -> func.invoke(年支, 官符) }
    val fun小耗 = { 年支: Branch -> func.invoke(年支, 小耗) }
    val fun歲破 = { 年支: Branch -> func.invoke(年支, 歲破) }
    val fun龍德 = { 年支: Branch -> func.invoke(年支, 龍德) }
    val fun白虎 = { 年支: Branch -> func.invoke(年支, 白虎) }
    val fun天德 = { 年支: Branch -> func.invoke(年支, 天德) }
    val fun吊客 = { 年支: Branch -> func.invoke(年支, 吊客) }
    val fun病符 = { 年支: Branch -> func.invoke(年支, 病符) }

    val starFuncMap: Map<StarYearFront, Function1<Branch, Branch>> by lazy {
      mapOf(
        歲建 to fun歲建,
        晦氣 to fun晦氣,
        喪門 to fun喪門,
        貫索 to fun貫索,
        官符 to fun官符,
        小耗 to fun小耗,
        歲破 to fun歲破,
        龍德 to fun龍德,
        白虎 to fun白虎,
        天德 to fun天德,
        吊客 to fun吊客,
        病符 to fun病符
           )
    }
  }
}
