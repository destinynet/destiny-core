/**
 * Created by smallufo on 2021-03-03.
 */
package destiny.core.astrology

import destiny.core.astrology.LunarStation.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDay
import destiny.core.chinese.BranchTools
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate


/** 二十八星宿值日 */
interface ILunarDailyStation {

  fun getDailyStar(date: LocalDate, loc: ILocation): LunarStation
}

/**
 * 查表法，按照「星期幾」實作28星宿值日
 */
class LunarDailyStationByWeek(private val dayImpl: IDay) : ILunarDailyStation, Serializable {

  override fun getDailyStar(date: LocalDate, loc: ILocation): LunarStation {
    val noon = date.atTime(12, 0)
    val day: StemBranch = dayImpl.getDay(noon, loc)
    val week: DayOfWeek = date.dayOfWeek

    val fiveElement = BranchTools.trilogy(day.branch)
    return map[fiveElement]!![week.value - 1]
  }

  companion object {
    private val map = mapOf(
      // 星期1 ~ 星期日
      水 to listOf(畢, 翼, 箕, 奎, 鬼, 氐, 虛),
      木 to listOf(張, 尾, 壁, 井, 亢, 女, 昴),
      火 to listOf(心, 室, 参, 角, 牛, 胃, 星),
      金 to listOf(危, 觜, 軫, 斗, 婁, 柳, 房),
    )
  }
}
