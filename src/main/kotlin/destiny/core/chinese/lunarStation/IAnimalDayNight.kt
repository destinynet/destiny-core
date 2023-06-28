/**
 * Created by smallufo on 2021-03-18.
 */
package destiny.core.chinese.lunarStation

import destiny.core.DayNight
import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import java.io.Serializable


interface IAnimalDayNight {
  fun getDayNight(lunarStation: LunarStation): DayNight
}

/** 《禽星易見》、《演禽通纂》  (鍾義明 採用) */
class AnimalDayNightAnimalStarImpl : IAnimalDayNight, Serializable {
  override fun getDayNight(lunarStation: LunarStation): DayNight {
    return map.filterValues { s -> s.contains(lunarStation) }
      .keys.first()
  }

  companion object {
    private val map = mapOf(
      DayNight.DAY to setOf(斗, 牛, 危, 奎, 婁, 胃, 昴, 畢, 觜, 參, 井, 鬼, 柳, 星, 張, 室),
      DayNight.NIGHT to setOf(角, 亢, 氐, 房, 心, 尾, 箕, 女, 虛, 壁, 翼, 軫)
    )
  }
}

/** 《鰲頭通書》*/
class AnimalDayNightAoHeadImpl : IAnimalDayNight , Serializable {
  override fun getDayNight(lunarStation: LunarStation): DayNight {
    TODO("待查")
  }
}
