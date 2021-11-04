/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.astrology.IPoint
import destiny.core.astrology.Point
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.IYinYang
import kotlin.reflect.KClass

/** 長生 12 神煞  */
sealed class StarLongevity(nameKey: String) : ZStar(nameKey, ZStar::class.java.name, nameKey + "_ABBR", Type.長生) {

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

  companion object : IPoint<StarLongevity> {

    override val type: KClass<out Point> = StarLongevity::class

    override val values by lazy { arrayOf(長生, 沐浴, 冠帶, 臨官, 帝旺, 衰, 病, 死, 墓, 絕, 胎, 養) }

    override fun fromString(value: String): StarLongevity? {
      return values.firstOrNull {
        it.nameKey == value
      }
    }

    private val list by lazy { arrayOf(*values) }

    private val func = { fiveElement: FiveElement ->
      when (fiveElement) {
        水 -> 申
        木 -> 亥
        金 -> 巳
        火 -> 寅
        土 -> 申 // 土水共長生
      }
    }

    private val mainFunc = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang, star: StarLongevity ->
      // 第一個（長生）
      val head = func.invoke(fiveElement)

      val steps = list.indexOf(star)
      if (gender === Gender.男 && yinYang.booleanValue || gender === Gender.女 && !yinYang.booleanValue) {
        // 陽男 陰女 順行
        head.next(steps)
      } else {
        // 陰男 陽女 逆行
        head.prev(steps)
      }
    }

    val fun長生 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 長生) }
    val fun沐浴 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 沐浴) }
    val fun冠帶 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 冠帶) }
    val fun臨官 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 臨官) }
    val fun帝旺 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 帝旺) }

    val fun衰 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 衰) }
    val fun病 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 病) }
    val fun死 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 死) }
    val fun墓 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 墓) }
    val fun絕 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 絕) }
    val fun胎 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 胎) }
    val fun養 = { fiveElement: FiveElement, gender: Gender, yinYang: IYinYang -> mainFunc.invoke(fiveElement, gender, yinYang, 養) }

    val starFuncMap: Map<StarLongevity, Function3<FiveElement, Gender, IYinYang, Branch>> by lazy {
      mapOf(
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

}
