package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import java.io.Serializable

/** 二十八星宿 值月 */
interface ILunarStationMonthly {

  fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation

}


/**
 * 月禽 , 《鰲頭通書》、《參籌秘書》、《八門禽遁》
 *
 * A : 會得年禽月易求，太陽需用角為頭，太陰室宿火尋馬(火星值?)，金心土胃水騎牛，木星直年參星是，次第推求順數週。
 * B : 會得年星月易求，日角月宿火星遊，水牛木參正月起，金心土胃順行周。
 *
 * A 與 B 兩歌訣其實是同一套算法
 */
@Impl([Domain(Domains.LunarStation.KEY_MONTH, LunarStationMonthlyAoHead.VALUE, true)])
class LunarStationMonthlyAoHead : ILunarStationMonthly, Serializable {

  override fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  companion object {
    private fun getFirstMonth(year: Planet): LunarStation {
      return when (year) {
        SUN -> 角
        MOON -> 室
        MARS -> 星
        VENUS -> 心
        SATURN -> 胃
        MERCURY -> 牛
        JUPITER -> 參
        else -> throw IllegalArgumentException("No such pair")
      }
    }

    const val VALUE = "AO_HEAD"
  }
}

/**
 * 月禽 , 《禽星易見》、《剋擇講義》
 *
 * 「日室月星火年牛，水參木心正月求，金胃土角建寅位，年起月宿例訣頭」
 *
 * 太陽值年，正月是室。
 * 太陰值年，正月起星。
 * 火星值年，正月起牛。
 * 水星值年，正月起參。
 * 木星值年，正月起心。
 * 金星值年，正月起胃。
 * 土星值年，正月起角。
 */
@Impl([Domain(Domains.LunarStation.KEY_MONTH, LunarStationMonthlyAnimalExplained.VALUE, false)])
class LunarStationMonthlyAnimalExplained : ILunarStationMonthly, Serializable {

  override fun getMonthlyStation(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  companion object {
    private fun getFirstMonth(year: Planet): LunarStation {
      return when (year) {
        SUN -> 室
        MOON -> 星
        MARS -> 牛
        MERCURY -> 參
        JUPITER -> 心
        VENUS -> 胃
        SATURN -> 角
        else -> throw IllegalArgumentException("No such pair")
      }
    }

    const val VALUE = "ANIMAL_EXPLAINED"
  }
}
