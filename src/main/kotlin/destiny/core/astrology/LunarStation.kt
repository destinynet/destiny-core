/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology

import destiny.core.*
import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import destiny.core.astrology.Planet.*
import destiny.core.chinese.Animal
import destiny.tools.ArrayTools
import destiny.tools.getTitle
import destiny.tools.serializers.astrology.LunarStationSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

/** 二十八宿 */
@Serializable(with = LunarStationSerializer::class)
sealed class LunarStation(
  val news: News,
  nameKey: String,
  val animal: Animal,
  val planet: Planet
) : Star(nameKey, LunarStation::class.java.name), ILoop<LunarStation>, Comparable<LunarStation> {

  object 角 : LunarStation(EAST, "LunarStation.角", Animal.蛟, JUPITER)
  object 亢 : LunarStation(EAST, "LunarStation.亢", Animal.龍, VENUS)
  object 氐 : LunarStation(EAST, "LunarStation.氐", Animal.貉, SATURN)
  object 房 : LunarStation(EAST, "LunarStation.房", Animal.兔, SUN)
  object 心 : LunarStation(EAST, "LunarStation.心", Animal.狐, MOON)
  object 尾 : LunarStation(EAST, "LunarStation.尾", Animal.虎, MARS)
  object 箕 : LunarStation(EAST, "LunarStation.箕", Animal.豹, MERCURY)

  object 斗 : LunarStation(NORTH, "LunarStation.斗", Animal.獬, JUPITER)
  object 牛 : LunarStation(NORTH, "LunarStation.牛", Animal.牛, VENUS)
  object 女 : LunarStation(NORTH, "LunarStation.女", Animal.蝠, SATURN)
  object 虛 : LunarStation(NORTH, "LunarStation.虛", Animal.鼠, SUN)
  object 危 : LunarStation(NORTH, "LunarStation.危", Animal.燕, MOON)
  object 室 : LunarStation(NORTH, "LunarStation.室", Animal.豬, MARS)
  object 壁 : LunarStation(NORTH, "LunarStation.壁", Animal.貐, MERCURY)

  object 奎 : LunarStation(WEST, "LunarStation.奎", Animal.狼, JUPITER)
  object 婁 : LunarStation(WEST, "LunarStation.婁", Animal.狗, VENUS)
  object 胃 : LunarStation(WEST, "LunarStation.胃", Animal.雉, SATURN)
  object 昴 : LunarStation(WEST, "LunarStation.昴", Animal.雞, SUN)
  object 畢 : LunarStation(WEST, "LunarStation.畢", Animal.烏, MOON)
  object 觜 : LunarStation(WEST, "LunarStation.觜", Animal.猴, MARS)
  object 參 : LunarStation(WEST, "LunarStation.參", Animal.猿, MERCURY)

  object 井 : LunarStation(SOUTH, "LunarStation.井", Animal.犴, JUPITER)
  object 鬼 : LunarStation(SOUTH, "LunarStation.鬼", Animal.羊, VENUS)
  object 柳 : LunarStation(SOUTH, "LunarStation.柳", Animal.獐, SATURN)
  object 星 : LunarStation(SOUTH, "LunarStation.星", Animal.馬, SUN)
  object 張 : LunarStation(SOUTH, "LunarStation.張", Animal.鹿, MOON)
  object 翼 : LunarStation(SOUTH, "LunarStation.翼", Animal.蛇, MARS)
  object 軫 : LunarStation(SOUTH, "LunarStation.軫", Animal.蚓, MERCURY)

  override fun compareTo(other: LunarStation): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }

  /** 角木蛟 , 亢金龍 ... 這樣的完整名稱 :  星 + 行星星曜 + 動物 , 共三字元 */
  fun getFullName(locale: Locale): String {
    return "${this.toString(locale)}${this.planet.getAbbreviation(locale)}${this.animal.getTitle(locale)}"
  }

  override fun next(n: Int): LunarStation {
    val thisIndex = list.indexOf(this)
    return get(thisIndex + n)
  }

  companion object : IPoints<LunarStation> {

    override val type: KClass<out Point> = LunarStation::class

    operator fun get(index: Int): LunarStation {
      return ArrayTools[values, index]
    }

    override val values by lazy {
      arrayOf(
        角, 亢, 氐, 房, 心, 尾, 箕,
        斗, 牛, 女, 虛, 危, 室, 壁,
        奎, 婁, 胃, 昴, 畢, 觜, 參,
        井, 鬼, 柳, 星, 張, 翼, 軫
      )
    }

    val list: List<LunarStation> by lazy {
      values.asList()
    }

    override fun fromString(value: String, locale: Locale): LunarStation? {
      return values.firstOrNull {
        // 不能用 英文比 , 因為 壁,畢 兩者相同
        it.toString(Locale.TRADITIONAL_CHINESE).equals(value, ignoreCase = true)
      }
    }
  }
}

