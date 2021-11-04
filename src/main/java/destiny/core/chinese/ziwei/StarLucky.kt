/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.astrology.IPoint
import destiny.core.astrology.Point
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Characters
import destiny.core.chinese.ITianyi
import destiny.core.chinese.Stem
import destiny.core.chinese.ziwei.ZStar.Type.*
import kotlin.reflect.KClass

/**
 * 八吉星
 */
sealed class StarLucky(nameKey: String, type: Type) : ZStar(nameKey, ZStar::class.java.name, nameKey + "_ABBR", type) {

  object 文昌 : StarLucky("文昌", 月) // 甲
  object 文曲 : StarLucky("文曲", 時) // 甲
  object 左輔 : StarLucky("左輔", 月) // 甲
  object 右弼 : StarLucky("右弼", 月) // 甲
  object 天魁 : StarLucky("天魁", 年干) // 甲 , 丙火 , 天乙貴人 , 陽貴
  object 天鉞 : StarLucky("天鉞", 年干) // 甲 , 丁火 , 玉堂貴人 , 陰貴
  object 祿存 : StarLucky("祿存", 年干) // 甲
  object 天馬 : StarLucky("天馬" , 年月)  // 乙級星
  object 年馬 : StarLucky("年馬", 年支) // 乙級星 (其實就是天馬)
  object 月馬 : StarLucky("月馬", 月)   // 乙級星 (其實就是天馬)


  companion object : IPoint<StarLucky> {

    override val type: KClass<out Point> = StarLucky::class

    override val values by lazy { arrayOf(文昌, 文曲, 左輔, 右弼, 天魁, 天鉞, 祿存, 天馬) }

    override fun fromString(value: String): StarLucky? {
      return values.firstOrNull {
        it.nameKey == value
      }
    }

    /** 文昌 : 時支 -> 地支  */
    val fun文昌 = { hour: Branch -> Branch[10 - hour.index] }

    /** 文曲 : 時支 -> 地支  */
    val fun文曲 = { hour: Branch -> Branch[hour.index + 4] }

    /** 左輔 : 月數 -> 地支  */
    val fun左輔_月數 = { finalMonthNum: Int -> Branch[finalMonthNum + 3] }

    /** 左輔 : 月支 -> 地支  */
    val fun左輔_月支 = { month: Branch -> Branch[month.index + 2] }


    /** 右弼 : 月數 -> 地支  */
    val fun右弼_月數 = { finalMonthNum: Int -> Branch[11 - finalMonthNum] }

    /** 右弼 : 月支 -> 地支  */
    val fun右弼_月支 = { month: Branch -> Branch[12 - month.index] }


    /**
     * 天魁 (陽貴人) : 年干 -> 地支
     */
    val fun天魁 = { year: Stem, tianyiImpl: ITianyi -> tianyiImpl.getFirstTianyi(year, DayNight.DAY) }

    /**
     * 天鉞 (陰貴人) : 年干 -> 地支
     */
    val fun天鉞 = { year: Stem, tianyiImpl: ITianyi -> tianyiImpl.getFirstTianyi(year, DayNight.NIGHT) }

    /** 祿存 : 年干 -> 地支  */
    val fun祿存 = { year: Stem ->
      when (year) {
        Stem.甲 -> 寅
        Stem.乙 -> 卯
        Stem.丙, Stem.戊 -> 巳
        Stem.丁, Stem.己 -> 午
        Stem.庚 -> 申
        Stem.辛 -> 酉
        Stem.壬 -> 亥
        Stem.癸 -> 子
      }
    }

    /** 天馬(年的驛馬) : 年支 -> 地支  */
    val fun年馬 = { year: Branch ->
      Characters.getHorse(year)
    }

    /** 天馬(月的驛馬) : 月數 -> 地支  */
    val fun月馬_月數 = { finalMonthNum: Int ->
      when (finalMonthNum) {
        1, 5, 9 -> 申
        2, 6, 10 -> 巳
        3, 7, 11 -> 寅
        4, 8, 12 -> 亥
        else -> throw AssertionError(finalMonthNum)
      }
    }

    /** 天馬(月的驛馬) : 月支 -> 地支  */
    val fun月馬_月支 = { month: Branch ->
      Characters.getHorse(month)
    }

  }


}
