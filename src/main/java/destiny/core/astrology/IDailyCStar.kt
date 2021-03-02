/**
 * Created by smallufo on 2021-03-03.
 */
package destiny.core.astrology

import destiny.core.astrology.CStar.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDay
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.SimpleBranch
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate


/** 二十八星宿值日 */
interface IDailyCStar {

  fun getDailyStar(date: LocalDate, loc: ILocation): CStar
}

/**
 * 查表法實作28星宿值日
 */
class DailyCStarTableImpl(private val dayImpl: IDay) : IDailyCStar, Serializable {

  override fun getDailyStar(date: LocalDate, loc: ILocation): CStar {
    val noon = date.atTime(12, 0)
    val day: StemBranch = dayImpl.getDay(noon, loc)
    val week: DayOfWeek = date.dayOfWeek
    val fiveElement = SimpleBranch.getFiveElement(day.branch)
    return map[fiveElement]!![week.value - 1]
  }

  companion object {
    val map = mapOf(
      // 星期1 ~ 星期日
      水 to listOf(畢, 翼, 箕, 奎, 鬼, 氐, 虛),
      木 to listOf(張, 尾, 壁, 井, 亢, 女, 昴),
      火 to listOf(心, 室, 参, 角, 牛, 胃, 星),
      金 to listOf(危, 觜, 軫, 斗, 婁, 柳, 房),
    )
  }
}
