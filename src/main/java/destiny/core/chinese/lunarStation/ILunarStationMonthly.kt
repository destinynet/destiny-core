package destiny.core.chinese.lunarStation

import destiny.core.Descriptive
import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import java.io.Serializable
import java.util.*

/** 二十八星宿 值月 */
interface ILunarStationMonthly : Descriptive {

  fun getMonthly(yearStation: LunarStation, monthNumber: Int): LunarStation

}


/**
 *
 * 月禽 , 《鰲頭通書》、《參籌秘書》、《八門禽遁》
 *
 * 《鰲頭通書》 : 明 熊宗立 (1409-1482) :
 *    《鰲頭通書》的完成，溯自明朝成化、弘治間的日師熊宗立(福建建陽人，號道軒，居鰲峰)所著的《通書大全》。
 *    曾孫秉懋(月濤)增補，命名《鰲頭通書》，梓行於世，五十餘年後原版燬於兵火。
 *    一七八六年(清乾隆五十一年)秋，其後裔啟燦(叔明)予以重梓發行。
 *
 * 《參籌秘書》 : 明 汪三益
 *
 * 《八門禽遁》 : 明 劉基(?)
 *
 * A : 會得年禽月易求，太陽需用角為頭，太陰室宿火尋馬(火星值?)，金心土胃水騎牛，木星直年參星是，次第推求順數週。
 * B : 會得年星月易求，日角月宿火星遊，水牛木參正月起，金心土胃順行周。
 *
 * A(參籌秘書) 與 B 兩歌訣其實是同一套算法
 */
@Impl(
  [
    Domain(Domains.LunarStation.MonthImpl.KEY_GENERAL, LunarStationMonthlyAoHead.VALUE, true),
    Domain(Domains.LunarStation.MonthImpl.KEY_SELECT, LunarStationMonthlyAoHead.VALUE)
  ]
)
class LunarStationMonthlyAoHead : ILunarStationMonthly, Serializable {

  override fun getMonthly(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }

  override fun toString(locale: Locale): String {
    return "《鰲頭通書》"
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

    const val VALUE =  "AoHead"
  }
}

/**
 * 月禽 , 《禽星易見》、《剋擇講義》
 *
 * 《禽星易見》 : 明 池本理
 * 《剋擇講義》 : 清 洪潮和
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
@Impl(
  [
    Domain(Domains.LunarStation.MonthImpl.KEY_GENERAL, LunarStationMonthlyAnimalExplained.VALUE),
    Domain(Domains.LunarStation.MonthImpl.KEY_SELECT, LunarStationMonthlyAnimalExplained.VALUE, true)
  ]
)
class LunarStationMonthlyAnimalExplained : ILunarStationMonthly, Serializable {

  override fun getMonthly(yearStation: LunarStation, monthNumber: Int): LunarStation {
    return getFirstMonth(yearStation.planet).next(monthNumber - 1)
  }


  override fun toString(locale: Locale): String {
    return "《剋擇講義》"
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

    const val VALUE = "AnimalExplained"
  }
}
