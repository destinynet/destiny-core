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
import destiny.tools.ArrayTools
import java.util.*

/** 二十八宿 */
sealed class LunarStation(val news: News,
                          nameKey: String,
                          val planet: Planet) : Star(nameKey, LunarStation::class.java.name), ILoop<LunarStation> {
  object 角 : LunarStation(EAST, "角", JUPITER)
  object 亢 : LunarStation(EAST, "亢", VENUS)
  object 氐 : LunarStation(EAST, "氐", SATURN)
  object 房 : LunarStation(EAST, "房", SUN)
  object 心 : LunarStation(EAST, "心", MOON)
  object 尾 : LunarStation(EAST, "尾", MARS)
  object 箕 : LunarStation(EAST, "箕", MERCURY)

  object 斗 : LunarStation(NORTH, "斗", JUPITER)
  object 牛 : LunarStation(NORTH, "牛", VENUS)
  object 女 : LunarStation(NORTH, "女", SATURN)
  object 虛 : LunarStation(NORTH, "虛", SUN)
  object 危 : LunarStation(NORTH, "危", MOON)
  object 室 : LunarStation(NORTH, "室", MARS)
  object 壁 : LunarStation(NORTH, "壁", MERCURY)

  object 奎 : LunarStation(WEST, "奎", JUPITER)
  object 婁 : LunarStation(WEST, "婁", VENUS)
  object 胃 : LunarStation(WEST, "胃", SATURN)
  object 昴 : LunarStation(WEST, "昴", SUN)
  object 畢 : LunarStation(WEST, "畢", MOON)
  object 觜 : LunarStation(WEST, "觜", MARS)
  object 參 : LunarStation(WEST, "參", MERCURY)

  object 井 : LunarStation(SOUTH, "井", JUPITER)
  object 鬼 : LunarStation(SOUTH, "鬼", VENUS)
  object 柳 : LunarStation(SOUTH, "柳", SATURN)
  object 星 : LunarStation(SOUTH, "星", SUN)
  object 張 : LunarStation(SOUTH, "張", MOON)
  object 翼 : LunarStation(SOUTH, "翼", MARS)
  object 軫 : LunarStation(SOUTH, "軫", MERCURY)

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

fun LunarStation.animal(locale: Locale): String {
  return ResourceBundle.getBundle(resource, locale).getString("$nameKey.animal")
}
