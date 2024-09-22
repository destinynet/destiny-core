/**
 * Created by smallufo on 2021-03-17.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.chinese.lunarStation.Habitat.*
import java.io.Serializable

enum class Habitat {
  天, 地, 山, 水, 家
}

interface IHabitat {
  fun getHabitats(lunarStation: LunarStation): Set<Habitat>
}


/** 《禽星易見》 */
class HabitatAnimalStar : IHabitat, Serializable {
  override fun getHabitats(lunarStation: LunarStation): Set<Habitat> {
    return map.keys.filter { h -> map[h]!!.contains(lunarStation) }
      .toSet()
  }

  companion object {
    private val map = mapOf(
      天 to setOf(女, 危, 畢, 井),
      地 to setOf(氐, 房, 心, 尾, 箕, 牛, 虛, 室, 婁, 昴, 鬼, 星, 翼, 軫),
      山 to setOf(奎, 胃, 觜, 參, 柳, 張),
      水 to setOf(角, 亢, 斗, 壁)
    )
  }
}

/** 《鰲頭通書》 */
class HabitatAoHeadImpl : IHabitat, Serializable {
  override fun getHabitats(lunarStation: LunarStation): Set<Habitat> {
    return map.keys.filter { h -> map[h]!!.contains(lunarStation) }
      .toSet()
  }

  companion object {
    private val map by lazy {
      mapOf(
        天 to setOf(女, 危, 昴, 畢, 井),
        // 原文似乎少了後方兩個 : 虛, 翼
        地 to setOf(氐, 房, 心, 尾, 箕, 斗, 牛, 室, 奎, 婁, 胃, 觜, 參, 鬼, 柳, 星, 張, 虛, 翼),
        山 to setOf(氐, 房, 心, 尾, 箕, 牛, 奎, 婁, 胃, 觜, 參, 柳, 星, 張),
        水 to setOf(角, 亢, 斗, 壁, 軫),
        家 to setOf(婁, 昴, 鬼, 室, 星),
      )
    }
  }
}
