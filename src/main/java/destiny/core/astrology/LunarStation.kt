/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology

import destiny.core.News
import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import java.util.*

/** 二十八宿 */
sealed class LunarStation(val news: News,
                          nameKey: String) : Star(nameKey, LunarStation::class.java.name) {
  object 角 : LunarStation(EAST, "角")
  object 亢 : LunarStation(EAST, "亢")
  object 氐 : LunarStation(EAST, "氐")
  object 房 : LunarStation(EAST, "房")
  object 心 : LunarStation(EAST, "心")
  object 尾 : LunarStation(EAST, "尾")
  object 箕 : LunarStation(EAST, "箕")

  object 斗 : LunarStation(NORTH, "斗")
  object 牛 : LunarStation(NORTH, "牛")
  object 女 : LunarStation(NORTH, "女")
  object 虛 : LunarStation(NORTH, "虛")
  object 危 : LunarStation(NORTH, "危")
  object 室 : LunarStation(NORTH, "室")
  object 壁 : LunarStation(NORTH, "壁")

  object 奎 : LunarStation(WEST, "奎")
  object 婁 : LunarStation(WEST, "婁")
  object 胃 : LunarStation(WEST, "胃")
  object 昴 : LunarStation(WEST, "昴")
  object 畢 : LunarStation(WEST, "畢")
  object 觜 : LunarStation(WEST, "觜")
  object 参 : LunarStation(WEST, "参")

  object 井 : LunarStation(SOUTH, "井")
  object 鬼 : LunarStation(SOUTH, "鬼")
  object 柳 : LunarStation(SOUTH, "柳")
  object 星 : LunarStation(SOUTH, "星")
  object 張 : LunarStation(SOUTH, "張")
  object 翼 : LunarStation(SOUTH, "翼")
  object 軫 : LunarStation(SOUTH, "軫")

}

fun LunarStation.animal(locale: Locale) : String {
  return ResourceBundle.getBundle(resource, locale).getString("$nameKey.animal")
}
