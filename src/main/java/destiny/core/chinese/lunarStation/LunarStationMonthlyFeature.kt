/**
 * Created by smallufo on 2021-08-19.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.YearMonthConfig
import destiny.core.calendar.eightwords.YearMonthFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class MonthlyConfig(val impl: Impl = Impl.AoHead,
                         val monthAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS,
                         val yearlyConfig: YearlyConfig = YearlyConfig(),
                         val yearMonthConfig: YearMonthConfig = YearMonthConfig(),
                         val dayHourConfig: DayHourConfig = DayHourConfig()) {
  enum class Impl {
    AoHead,           // 《鰲頭通書》
    AnimalExplained   // 《剋擇講義》
  }
}

@DestinyMarker
class MonthlyConfigBuilder : Builder<MonthlyConfig> {
  var impl: MonthlyConfig.Impl = MonthlyConfig.Impl.AoHead

  override fun build(): MonthlyConfig {
    return MonthlyConfig(impl)
  }

  companion object {
    fun monthly(block: MonthlyConfigBuilder.() -> Unit = {}): MonthlyConfig {
      return MonthlyConfigBuilder().apply(block).build()
    }
  }
}

interface ILunarStationMonthlyFeature : Feature<MonthlyConfig, LunarStation> {

  fun getMonthly(yearStation: LunarStation, monthNumber: Int, impl: MonthlyConfig.Impl): LunarStation
}

class LunarStationMonthlyFeature(private val yearlyFeature: LunarStationYearlyFeature,
                                 private val monthFeature: YearMonthFeature,
                                 private val chineseDateFeature : ChineseDateFeature) : ILunarStationMonthlyFeature {
  override val key: String = "lsMonthly"

  override val defaultConfig: MonthlyConfig = MonthlyConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: MonthlyConfig): LunarStation {
    val yearStation = yearlyFeature.getModel(gmtJulDay, loc, config.yearlyConfig).station

    val chineseDate = chineseDateFeature.getModel(gmtJulDay, loc, config.dayHourConfig)
    val monthBranch = monthFeature.getModel(gmtJulDay, loc, config.yearMonthConfig).branch
    val monthNumber = IFinalMonthNumber.getFinalMonthNumber(
      chineseDate.month, chineseDate.leapMonth, monthBranch, chineseDate.day, config.monthAlgo
    )

    return getFirthMonth(yearStation, config.impl).next(monthNumber - 1)
  }

  override fun getMonthly(yearStation: LunarStation, monthNumber: Int, impl: MonthlyConfig.Impl): LunarStation {
    return getFirthMonth(yearStation, impl).next(monthNumber - 1)
  }

  private fun getFirthMonth(yearStation: LunarStation, impl: MonthlyConfig.Impl): LunarStation {

    return when (impl) {
      MonthlyConfig.Impl.AoHead          -> FirstMonthAoHead.getFirthMonth(yearStation)
      MonthlyConfig.Impl.AnimalExplained -> FirstMonthAnimalExplained.getFirthMonth(yearStation)
    }
  }

  private sealed interface IFirstMonth {
    val impl : MonthlyConfig.Impl
    fun getFirthMonth(yearStation: LunarStation) : LunarStation
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
  private object FirstMonthAoHead : IFirstMonth {
    override val impl: MonthlyConfig.Impl = MonthlyConfig.Impl.AoHead

    override fun getFirthMonth(yearStation: LunarStation) : LunarStation {
      return when (yearStation.planet) {
        SUN     -> 角
        MOON    -> 室
        MARS    -> 星
        VENUS   -> 心
        SATURN  -> 胃
        MERCURY -> 牛
        JUPITER -> 參
        else    -> throw IllegalArgumentException("No such pair")
      }
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
  private object FirstMonthAnimalExplained : IFirstMonth {
    override val impl: MonthlyConfig.Impl = MonthlyConfig.Impl.AnimalExplained

    override fun getFirthMonth(yearStation: LunarStation): LunarStation {
      return when (yearStation.planet) {
        SUN     -> 室
        MOON    -> 星
        MARS    -> 牛
        MERCURY -> 參
        JUPITER -> 心
        VENUS   -> 胃
        SATURN  -> 角
        else    -> throw IllegalArgumentException("No such pair")
      }
    }

  }
}
