/**
 * Created by smallufo on 2021-08-20.
 */
package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.Planet.Companion.aheadOf
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.DayHourConfigBuilder
import destiny.core.calendar.eightwords.DayHourFeature
import destiny.core.chinese.Branch
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class HourlyConfig(val impl: Impl = Impl.Yuan,
                        val dayHourConfig: DayHourConfig = DayHourConfig()) {

  enum class Impl {
    Yuan,   // 《禽星易見》
    Fixed   // 《剋擇講義》
  }
}

@DestinyMarker
class HourlyConfigBuilder : Builder<HourlyConfig> {
  var impl: HourlyConfig.Impl = HourlyConfig.Impl.Yuan

  var dayHourConfig: DayHourConfig = DayHourConfig()
  fun dayHour(block: DayHourConfigBuilder.() -> Unit = {}) {
    this.dayHourConfig = DayHourConfigBuilder.dayHour(block)
  }

  override fun build(): HourlyConfig {
    return HourlyConfig(impl, dayHourConfig)
  }

  companion object {
    fun hourly(block: HourlyConfigBuilder.() -> Unit = {}) : HourlyConfig {
      return HourlyConfigBuilder().apply(block).build()
    }
  }
}

class LunarStationHourlyFeature(private val dailyFeature: LunarStationDailyFeature,
                                private val dayHourFeature : DayHourFeature) : Feature<HourlyConfig, LunarStation> {
  override val key: String = "lsHourly"

  override val defaultConfig: HourlyConfig = HourlyConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HourlyConfig): LunarStation {
    return when (config.impl) {
      HourlyConfig.Impl.Yuan  -> {
        /**
         * 以「元」之首為定義
         *
         * 每「元」的最後一個時辰，與次元第一個時辰 是不連續的！
         *
         * 《禽星易見》：
         * 七曜禽星會者稀，
         * 日虛月鬼火從箕，
         * 水畢木氐金奎位，
         * 土宿還從翼上推，
         * 常將日禽尋時禽，
         * 但向禽中索取時。
         * 會者一元倒一指，
         * 不會七元七首詩。
         *
         * 一元甲子的星期天的子時是 [虛] 日鼠，
         * 二元甲子的星期天的子時是 [鬼] 金羊，
         * 三元甲子的星期天的子時是 [箕] 水豹，
         * 四元甲子的星期天的子時是 [畢] 月烏，
         * 五元甲子的星期天的子時是 [氐] 土貉，
         * 六元甲子的星期天的子時是 [奎] 木狼，
         * 七元甲子的星期天的子時是 [翼] 火蛇。
         */
        val (dayStation, dayYuan) = dailyFeature.getModel(gmtJulDay, loc, config.dayHourConfig).let { it.station() to it.yuan() }

        val dayPlanet = dayStation.planet

        val hourBranch: Branch = dayHourFeature.getModel(gmtJulDay, loc, config.dayHourConfig).second.branch

        // 該元 週日子時
        val sundayHourStart = LunarStationHourlyYuanImpl.yuanSundayHourStartMap[dayYuan]!!
        val hourSteps = dayPlanet.aheadOf(SUN) * 12 + hourBranch.getAheadOf(Branch.子)

        sundayHourStart.next(hourSteps)
      }

      HourlyConfig.Impl.Fixed -> {
        /**
         * 時禽 ， 固定排列 , 不分七元。 此法簡便易排，民間禽書常用此法。
         *
         * (日) 日宿子時起 [虛] 日鼠，
         * (一) 月宿子時起 [鬼] 金羊，
         * (二) 火宿子時起 [箕] 水豹，
         * (三) 水宿子時起 [畢] 月烏，
         * (四) 木宿子時起 [氐] 土貉，
         * (五) 金宿子時起 [奎] 木狼，
         * (六) 土宿子時起 [翼] 火蛇。
         */
        val dayStation = dailyFeature.getModel(gmtJulDay, loc, config.dayHourConfig).station()

        val start = when (dayStation.planet) {
          SUN     -> 虛
          MOON    -> 鬼
          MARS    -> 箕
          MERCURY -> 畢
          JUPITER -> 氐
          VENUS   -> 奎
          SATURN  -> 翼
          else    -> throw IllegalArgumentException("Impossible")
        }

        val hourSteps = dayHourFeature.getModel(gmtJulDay, loc, config.dayHourConfig).second.branch.getAheadOf(Branch.子)
        start.next(hourSteps)
      }
    }
  }
}
