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
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.DayHourConfigBuilder
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

@Serializable
data class HourlyConfig(val impl: Impl = Impl.Yuan,
                        val dayHourConfig: DayHourConfig = DayHourConfig()): java.io.Serializable {

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
                                private val dayHourFeature: Feature<DayHourConfig, Pair<StemBranch, StemBranch>>,
                                private val julDayResolver: JulDayResolver,
                                private val implMap: Map<HourlyConfig.Impl, ILunarStationHourly>) : Feature<HourlyConfig, LunarStation> {
  override val key: String = "lsHourly"

  override val defaultConfig: HourlyConfig = HourlyConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: HourlyConfig): LunarStation {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: HourlyConfig): LunarStation {
    //return implMap[config.impl]!!.getHourly(lmt, loc)

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
        val (dayStation, dayYuan) = dailyFeature.getModel(lmt, loc, config.dayHourConfig).let { it.station() to it.yuan() }

        val dayPlanet = dayStation.planet

        val hourBranch: Branch = dayHourFeature.getModel(lmt, loc, config.dayHourConfig).second.branch

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
        val dayStation = dailyFeature.getModel(lmt, loc, config.dayHourConfig).station()

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

        val hourSteps = dayHourFeature.getModel(lmt, loc, config.dayHourConfig).second.branch.getAheadOf(Branch.子)
        start.next(hourSteps)
      }
    }
  }

  companion object {
    /**
     * 翻禽 ( 他將 )
     * 週時支上起將星，順行逐位向時禽。尋得時禽權且逆，回卓時上覓他人。
     * 順數一週逆一轉，週數兩轉兩番禽。時師若不加進將，枉死千年亦不靈。
     */
    fun getOpponent(dayIndex: DayIndex, hourStation: LunarStation): LunarStation {
      val leader = dayIndex.leader()

      val steps = hourStation.getAheadOf(leader)
      return leader.next(steps).next(steps)
    }

    /**
     * 倒將 (我正將)
     * @param opponent 翻禽
     */
    fun getReversed(hourStation: LunarStation, opponent: LunarStation): LunarStation {
      val steps = opponent.getAheadOf(hourStation)
      return opponent.next(steps)
    }

    /**
     * 倒將 (我正將)
     */
    fun getReversed(dayIndex: DayIndex, hourStation: LunarStation): LunarStation {
      // 先求出 翻禽
      val opponent = getOpponent(dayIndex, hourStation)
      return getReversed(hourStation, opponent)
    }

    /**
     * 活曜 (我本身) , method 1
     * A : 陽畢陰尾金牛頭，木虛水氐火奎流，土翼常將寅上轉，此是翻禽活曜頭。
     * B : 日畢月尾火奎牛，水氐木虛金尋牛，土曜還從翼上起，活曜加寅逆推求。
     * A 與 B 講的是同一套算法。
     */
    fun getSelf1(hourStation: LunarStation, hourBranch : Branch) : LunarStation {
      val start = when(hourStation.planet) {
        SUN -> 畢
        MOON -> 尾
        VENUS -> 牛
        JUPITER -> 虛
        MERCURY -> 氐
        MARS -> 奎
        SATURN -> 翼
        else -> throw IllegalArgumentException("impossible")
      }
      val step1 = hourStation.getAheadOf(start)
      val step2 = hourBranch.getAheadOf(Branch.寅.prev(step1))
      return start.next(step1).next(step2)
    }

    /**
     * 活曜 (我本身) , method 2
     * 陽畢陰尾金牛頭，木虛水氐火奎流，土箕常將寅上轉，此是翻禽活曜頭。
     * 此詩訣土宿時從箕水豹起
     */
    fun getSelf2(hourStation: LunarStation, hourBranch : Branch) : LunarStation {
      val start = when(hourStation.planet) {
        SUN -> 畢
        MOON -> 尾
        VENUS -> 牛
        JUPITER -> 虛
        MERCURY -> 氐
        MARS -> 奎
        SATURN -> 箕
        else -> throw IllegalArgumentException("impossible")
      }
      val step1 = hourStation.getAheadOf(start)
      val step2 = hourBranch.getAheadOf(Branch.寅.prev(step1))
      return start.next(step1).next(step2)
    }
  }
}
