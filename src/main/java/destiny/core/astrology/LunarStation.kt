/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology

import destiny.core.ILoop
import destiny.core.News
import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import destiny.core.astrology.Planet.*
import destiny.core.chinese.Animal
import destiny.core.chinese.toString
import destiny.tools.ArrayTools
import destiny.tools.serializers.LunarStationSerializer
import kotlinx.serialization.Serializable
import java.util.*

/** 二十八宿 */
@Serializable(with = LunarStationSerializer::class)
sealed class LunarStation(val news: News,
                          nameKey: String,
                          val animal: Animal,
                          val planet: Planet) : Star(nameKey, LunarStation::class.java.name), ILoop<LunarStation> {
  object 角 : LunarStation(EAST, "角", Animal.蛟, JUPITER)
  object 亢 : LunarStation(EAST, "亢", Animal.龍, VENUS)
  object 氐 : LunarStation(EAST, "氐", Animal.貉, SATURN)
  object 房 : LunarStation(EAST, "房", Animal.兔, SUN)
  object 心 : LunarStation(EAST, "心", Animal.狐, MOON)
  object 尾 : LunarStation(EAST, "尾", Animal.虎, MARS)
  object 箕 : LunarStation(EAST, "箕", Animal.豹, MERCURY)

  object 斗 : LunarStation(NORTH, "斗", Animal.獬, JUPITER)
  object 牛 : LunarStation(NORTH, "牛", Animal.牛, VENUS)
  object 女 : LunarStation(NORTH, "女", Animal.蝠, SATURN)
  object 虛 : LunarStation(NORTH, "虛", Animal.鼠, SUN)
  object 危 : LunarStation(NORTH, "危", Animal.燕, MOON)
  object 室 : LunarStation(NORTH, "室", Animal.豬, MARS)
  object 壁 : LunarStation(NORTH, "壁", Animal.貐, MERCURY)

  object 奎 : LunarStation(WEST, "奎", Animal.狼, JUPITER)
  object 婁 : LunarStation(WEST, "婁", Animal.狗, VENUS)
  object 胃 : LunarStation(WEST, "胃", Animal.雉, SATURN)
  object 昴 : LunarStation(WEST, "昴", Animal.雞, SUN)
  object 畢 : LunarStation(WEST, "畢", Animal.烏, MOON)
  object 觜 : LunarStation(WEST, "觜", Animal.猴, MARS)
  object 參 : LunarStation(WEST, "參", Animal.猿, MERCURY)

  object 井 : LunarStation(SOUTH, "井", Animal.犴, JUPITER)
  object 鬼 : LunarStation(SOUTH, "鬼", Animal.羊, VENUS)
  object 柳 : LunarStation(SOUTH, "柳", Animal.獐, SATURN)
  object 星 : LunarStation(SOUTH, "星", Animal.馬, SUN)
  object 張 : LunarStation(SOUTH, "張", Animal.鹿, MOON)
  object 翼 : LunarStation(SOUTH, "翼", Animal.蛇, MARS)
  object 軫 : LunarStation(SOUTH, "軫", Animal.蚓, MERCURY)

  /** 角木蛟 , 亢金龍 ... 這樣的完整名稱 :  星 + 行星星曜 + 動物 , 共三字元 */
  fun getFullName(locale: Locale) : String {
    return "${this.toString(locale)}${this.planet.getAbbreviation(locale)}${this.animal.toString(locale)}"
  }

  override fun next(n: Int): LunarStation {
    val thisIndex = values.indexOf(this)
    return get(thisIndex + n)
  }

  companion object {
    operator fun get(index: Int): LunarStation {
      return ArrayTools[values.toTypedArray(), index]
    }

    val values: List<LunarStation> by lazy {
      LunarStation::class.sealedSubclasses.map { k ->
        k.objectInstance as LunarStation
      }
    }
  }
}

