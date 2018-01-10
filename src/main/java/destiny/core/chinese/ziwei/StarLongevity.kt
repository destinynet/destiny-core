/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.IYinYang
import org.slf4j.LoggerFactory

/** 長生 12 神煞  */
sealed class StarLongevity(nameKey: String) : ZStar(nameKey, ZStar::class.java.name, nameKey + "_ABBR", ZStar.Type.長生) {

  object 長生 : StarLongevity("長生")
  object 沐浴 : StarLongevity("沐浴")
  object 冠帶 : StarLongevity("冠帶")
  object 臨官 : StarLongevity("臨官")
  object 帝旺 : StarLongevity("帝旺")
  object 衰 : StarLongevity("衰")
  object 病 : StarLongevity("病")
  object 死 : StarLongevity("死")
  object 墓 : StarLongevity("墓")
  object 絕 : StarLongevity("絕")
  object 胎 : StarLongevity("胎")
  object 養 : StarLongevity("養")

  companion object {


    val values = arrayOf(長生, 沐浴, 冠帶, 臨官, 帝旺, 衰, 病, 死, 墓, 絕, 胎, 養)

    private val list = arrayOf(*values)

    private val logger = LoggerFactory.getLogger(StarLongevity::class.java)

    private val func = { 五行: FiveElement ->
      when (五行) {
        水 -> 申
        木 -> 亥
        金 -> 巳
        火 -> 寅
        土 -> 申 // 土水共長生
      }
    }

    private val mainFunc = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang, 星體: StarLongevity ->
      // 第一個（長生）
      val head = func.invoke(五行)

      val steps = list.indexOf(星體)
      if (gender === Gender.男 && 陰陽.booleanValue || gender === Gender.女 && !陰陽.booleanValue) {
        // 陽男 陰女 順行
        head.next(steps)
      } else {
        // 陰男 陽女 逆行
        head.prev(steps)
      }
    }

    val fun長生 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 長生) }
    val fun沐浴 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 沐浴) }
    val fun冠帶 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 冠帶) }
    val fun臨官 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 臨官) }
    val fun帝旺 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 帝旺) }

    val fun衰 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 衰) }
    val fun病 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 病) }
    val fun死 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 死) }
    val fun墓 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 墓) }
    val fun絕 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 絕) }
    val fun胎 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 胎) }
    val fun養 = { 五行: FiveElement, gender: Gender, 陰陽: IYinYang -> mainFunc.invoke(五行, gender, 陰陽, 養) }

    val starFuncMap: Map<StarLongevity, Function3<FiveElement, Gender, IYinYang, Branch>> = mapOf(
      長生 to fun長生,
      沐浴 to fun沐浴,
      冠帶 to fun冠帶,
      臨官 to fun臨官,
      帝旺 to fun帝旺,
      衰 to fun衰,
      病 to fun病,
      死 to fun死,
      墓 to fun墓,
      絕 to fun絕,
      胎 to fun胎,
      養 to fun養
                                                                                                                      )
  }

}
