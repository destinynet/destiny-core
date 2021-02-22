/**
 * Created by smallufo on 2021-02-21.
 */
package destiny.core.astrology

import destiny.core.News
import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH

/** 二十八宿 */
sealed class CStar(val news: News,
                   nameKey: String) : Star(nameKey , CStar::class.java.name) {
  object 角 : CStar(EAST, "角")
  object 亢 : CStar(EAST, "亢")
  object 氐 : CStar(EAST, "氐")
  object 房 : CStar(EAST, "房")
  object 心 : CStar(EAST, "心")
  object 尾 : CStar(EAST, "尾")
  object 箕 : CStar(EAST, "箕")

  object 斗 : CStar(NORTH, "斗")
  object 牛 : CStar(NORTH, "牛")
  object 女 : CStar(NORTH, "女")
  object 虛 : CStar(NORTH, "虛")
  object 危 : CStar(NORTH, "危")
  object 室 : CStar(NORTH, "室")
  object 壁 : CStar(NORTH, "壁")

  object 奎 : CStar(WEST, "奎")
  object 婁 : CStar(WEST, "婁")
  object 胃 : CStar(WEST, "胃")
  object 昴 : CStar(WEST, "昴")
  object 畢 : CStar(WEST, "畢")
  object 觜 : CStar(WEST, "觜")
  object 参 : CStar(WEST, "参")

  object 井 : CStar(SOUTH, "井")
  object 鬼 : CStar(SOUTH, "鬼")
  object 柳 : CStar(SOUTH, "柳")
  object 星 : CStar(SOUTH, "星")
  object 張 : CStar(SOUTH, "張")
  object 翼 : CStar(SOUTH, "翼")
  object 軫 : CStar(SOUTH, "軫")

}
