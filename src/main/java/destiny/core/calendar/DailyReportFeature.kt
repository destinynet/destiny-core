/**
 * Created by smallufo on 2022-02-12.
 */
package destiny.core.calendar

import destiny.core.astrology.*
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseConfigBuilder
import destiny.core.astrology.classical.VoidCourseFeature
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.calendar.eightwords.DayHourConfig
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.calendar.eightwords.HourBranchConfigBuilder
import destiny.core.calendar.eightwords.IHourBranchFeature
import destiny.core.chinese.Branch
import destiny.core.chinese.lunarStation.*
import destiny.core.toString
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.CacheGrain
import destiny.tools.DestinyMarker
import destiny.tools.location.ReverseGeocodingService
import destiny.tools.serializers.LocaleSerializer
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*
import javax.cache.Cache
import javax.inject.Named


@Serializable
data class DailyReportConfig(val hourBranchConfig: HourBranchConfig = HourBranchConfig(),
                             val lunarStationConfig : LunarStationConfig = LunarStationConfig(
                               hourlyConfig = HourlyConfig(impl = HourlyImpl.Fixed, dayHourConfig = DayHourConfig(hourBranchConfig = hourBranchConfig))
                             ),
                             val vocConfig: VoidCourseConfig = VoidCourseConfig(),
                             @Serializable(with = LocaleSerializer::class)
                             val locale: Locale = Locale.TAIWAN) : java.io.Serializable
@DestinyMarker
class DailyReportConfigBuilder : Builder<DailyReportConfig> {

  var hourBranchConfig: HourBranchConfig = HourBranchConfig()
  fun hourBranch(block : HourBranchConfigBuilder.() -> Unit = {}) {
    this.hourBranchConfig = HourBranchConfigBuilder.hourBranchConfig(block)
  }

  var lunarStationConfig : LunarStationConfig = LunarStationConfig(
    hourlyConfig = HourlyConfig(impl = HourlyImpl.Fixed)
  )
  fun lunarStation(block : LunarStationConfigBuilder.() -> Unit = {}) {
    this.lunarStationConfig = LunarStationConfigBuilder.lunarStation(block)
  }

  var vocConfig : VoidCourseConfig = VoidCourseConfig()
  fun vocConfig(block: VoidCourseConfigBuilder.() -> Unit = {}) {
    this.vocConfig = VoidCourseConfigBuilder.voidCourse(block)
  }

  var locale : Locale = Locale.TAIWAN

  override fun build(): DailyReportConfig {
    return DailyReportConfig(hourBranchConfig, lunarStationConfig, vocConfig, locale)
  }

  companion object {
    fun dailyReport(block : DailyReportConfigBuilder.() -> Unit = {}) : DailyReportConfig {
      return DailyReportConfigBuilder().apply(block).build()
    }
  }
}


@Named
class DailyReportFeature(private val hourBranchFeature: IHourBranchFeature,
                         val lunarStationFeature: LunarStationFeature,
                         private val riseTransFeature: IRiseTransFeature,
                         private val solarTermsImpl: ISolarTerms,
                         private val relativeTransitImpl: IRelativeTransit,
                         private val eclipseImpl: IEclipseFactory,
                         private val voidCourseFeature: VoidCourseFeature,
                         private val reverseGeocodingService: ReverseGeocodingService,
                         private val julDayResolver: JulDayResolver,
                         @Transient
                         private val dailyReportFeatureCache: Cache<LmtCacheKey<*>, List<TimeDesc>>) : AbstractCachedFeature<DailyReportConfig, List<TimeDesc>>() {

  override val key: String = "dailyReport"

  override val defaultConfig: DailyReportConfig = DailyReportConfig()

  @Suppress("UNCHECKED_CAST")
  override val lmtCache: Cache<LmtCacheKey<DailyReportConfig>, List<TimeDesc>>
    get() = dailyReportFeatureCache as Cache<LmtCacheKey<DailyReportConfig>, List<TimeDesc>>

  override var lmtCacheGrain: CacheGrain? = CacheGrain.HOUR

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: DailyReportConfig): List<TimeDesc> {

    val lmt = TimeTools.getLmtFromGmt(julDayResolver.getLocalDateTime(gmtJulDay),  loc)

    val lmtStart = lmt.with(ChronoField.HOUR_OF_DAY, 0).with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0)
      .with(ChronoField.NANO_OF_SECOND, 0)

    val lmtEnd = lmtStart.plus(1, ChronoUnit.DAYS)

    return calculate(lmtStart, lmtEnd, loc, config)
  }


  private fun calculate(lmtStart: ChronoLocalDateTime<*>, lmtEnd: ChronoLocalDateTime<*>, loc: ILocation, config: DailyReportConfig): List<TimeDesc> {

    logger.info { "from $lmtStart to $lmtEnd" }

    val set = sortedSetOf<TimeDesc>()

    // 12地支 + 隔天的子初
    val listBranches: List<TimeDesc.TypeHour> = hourBranchFeature.getDailyBranchStartListWithNextDayZi(lmtStart.toLocalDate(), loc, config.hourBranchConfig).let { list ->
      val branchMiddleMap: Map<Branch, ChronoLocalDateTime<*>> = hourBranchFeature.getDailyBranchMiddleMap(lmtStart.toLocalDate(), loc, config.hourBranchConfig)
      val list12 = list.take(12).map { (branch , branchStart) ->
        val middleLmt = branchMiddleMap[branch]!!
        val hourlyLunarStation = lunarStationFeature.hourlyFeature.getModel(middleLmt, loc, config.lunarStationConfig.hourlyConfig)
        val descs = buildList {
          add("$branch 初")
          add(hourlyLunarStation.getFullName(config.locale))
        }
        TimeDesc.TypeHour(branchStart as LocalDateTime, branch, hourlyLunarStation, descs)
      }

      val tomorrowLunarStationMap = hourBranchFeature.getDailyBranchMiddleMap(lmtStart.toLocalDate().plus(1, ChronoUnit.DAYS), loc, config.hourBranchConfig)
      val nextDayZi = list.last().let { (branch , branchStart) ->
        val middleLmt = tomorrowLunarStationMap[Branch.子]!!
        val hourlyLunarStation = lunarStationFeature.hourlyFeature.getModel(middleLmt, loc, config.lunarStationConfig.hourlyConfig)
        val descs = buildList {
          add("$branch 初")
          add(hourlyLunarStation.getFullName(config.locale))
        }
        TimeDesc.TypeHour(branchStart as LocalDateTime, branch, hourlyLunarStation, descs)
      }
      list12.plus(nextDayZi)
    }

    // 日月 四個至點
    val listTransPoints: List<TimeDesc> = TransPoint.values().flatMap { tp ->
      listOf(Planet.SUN, Planet.MOON).map { planet ->
        TimeDesc.TypeTransPoint(
          riseTransFeature.getLmtTrans(lmtStart, planet, tp, loc, julDayResolver, config.hourBranchConfig.transConfig) as LocalDateTime,
          planet.toString(Locale.TAIWAN) + tp.getTitle(Locale.TAIWAN),
          planet,
          tp
        )
      }
    }

    // 節氣
    val listSolarTerms: List<TimeDesc> =
      solarTermsImpl.getPeriodSolarTermsLMTs(lmtStart, lmtEnd, loc).map { solarTermsTime ->
        TimeDesc.TypeSolarTerms(
          solarTermsTime.time as LocalDateTime, solarTermsTime.solarTerms.toString(), solarTermsTime.solarTerms
        )
      }

    // 日月交角
    val listSunMoonAngle: List<TimeDesc> = listOf(
      0.0 to "新月",
      90.0 to "上弦月",
      180.0 to "滿月",
      270.0 to "下弦月"
    ).flatMap { (deg, desc) ->
      relativeTransitImpl.getPeriodRelativeTransitLMTs(Planet.MOON, Planet.SUN, lmtStart, lmtEnd, loc, deg, julDayResolver)
        .filter { t -> TimeTools.isBetween(t, lmtStart, lmtEnd) }
        .map { t -> TimeDesc.TypeSunMoon(t as LocalDateTime, desc, deg.toInt()) }
    }

    set.addAll(listBranches)
    set.addAll(listTransPoints)
    set.addAll(listSolarTerms)
    set.addAll(listSunMoonAngle)


    val fromGmtJulDay = TimeTools.getGmtJulDay(lmtStart, loc)
    val toGmtJulDay = TimeTools.getGmtJulDay(lmtEnd, loc)

    // 日食
    eclipseImpl.getNextSolarEclipse(fromGmtJulDay, true, ISolarEclipse.SolarType.values().toList()).also { eclipse ->
      val begin = TimeTools.getLmtFromGmt(eclipse.begin, loc, julDayResolver) as LocalDateTime
      val max = TimeTools.getLmtFromGmt(eclipse.max, loc, julDayResolver) as LocalDateTime
      val end = TimeTools.getLmtFromGmt(eclipse.end, loc, julDayResolver) as LocalDateTime

      if (TimeTools.isBetween(begin, lmtStart, lmtEnd)) {
        set.add(TimeDesc.TypeSolarEclipse(begin, eclipse.solarType, EclipseTime.BEGIN))
      }

      if (TimeTools.isBetween(max, lmtStart, lmtEnd)) {
        // 日食 食甚 觀測資料
        val locPlace: LocationPlace? = eclipseImpl.getEclipseCenterInfo(eclipse.max)?.let { (obs, _) ->
          val maxLoc = Location(obs.lat, obs.lng)
          reverseGeocodingService.getNearbyLocation(maxLoc, config.locale)?.let { place ->
            LocationPlace(maxLoc, place)
          }
        }
        set.add(
          TimeDesc.TypeSolarEclipse(max, eclipse.solarType, EclipseTime.MAX, locPlace)
        )
      }

      if (TimeTools.isBetween(end, lmtStart, lmtEnd)) {
        set.add(TimeDesc.TypeSolarEclipse(end, eclipse.solarType, EclipseTime.END))
      }
    }

    // 月食
    eclipseImpl.getNextLunarEclipse(fromGmtJulDay, true).also { eclipse ->
      val begin = TimeTools.getLmtFromGmt(eclipse.begin, loc, julDayResolver) as LocalDateTime
      val max = TimeTools.getLmtFromGmt(eclipse.max, loc, julDayResolver) as LocalDateTime
      val end = TimeTools.getLmtFromGmt(eclipse.end, loc, julDayResolver) as LocalDateTime

      if (TimeTools.isBetween(begin, lmtStart, lmtEnd)) {
        set.add(TimeDesc.TypeLunarEclipse(begin, eclipse.lunarType, EclipseTime.BEGIN))
      }
      if (TimeTools.isBetween(max, lmtStart, lmtEnd)) {
        set.add(TimeDesc.TypeLunarEclipse(max, eclipse.lunarType, EclipseTime.MAX))
      }
      if (TimeTools.isBetween(end, lmtStart, lmtEnd)) {
        set.add(TimeDesc.TypeLunarEclipse(end, eclipse.lunarType, EclipseTime.END))
      }
    }

    // 月亮空亡
    voidCourseFeature.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, relativeTransitImpl, config.vocConfig)
      .forEach { voc ->
        if (voc.beginGmt > fromGmtJulDay) {
          set.add(TimeDesc.VoidMoon.Begin(voc, loc))
        }
        if (voc.endGmt < toGmtJulDay) {
          set.add(TimeDesc.VoidMoon.End(voc, loc))
        }
      }

    return set.toList()
  }

  companion object {
    private val logger = KotlinLogging.logger { }
    const val CACHE_DAILY_REPORT_FEATURE = "dailyReportFeatureCache"
  }
}
