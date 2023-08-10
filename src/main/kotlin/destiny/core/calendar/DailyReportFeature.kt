/**
 * Created by smallufo on 2022-02-12.
 */
package destiny.core.calendar

import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.classical.IVoidCourseConfig
import destiny.core.astrology.classical.VoidCourseConfig
import destiny.core.astrology.classical.VoidCourseFeature
import destiny.core.astrology.eclipse.EclipseTime
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.astrology.eclipse.SolarType
import destiny.core.calendar.TimeTools.toGmtJulDay
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
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*
import javax.cache.Cache


interface IDailyReportConfig : ILunarStationConfig, IVoidCourseConfig {
  var locale: Locale

  val dailyReportConfig : DailyReportConfig
    get() = DailyReportConfig(lunarStationConfig, vocConfig, locale)
}

@Serializable
data class DailyReportConfig(
  override val lunarStationConfig: LunarStationConfig = LunarStationConfig(),
  override val vocConfig: VoidCourseConfig = VoidCourseConfig(),
  @Serializable(with = LocaleSerializer::class)
  override var locale: Locale = Locale.TAIWAN) : IDailyReportConfig, ILunarStationConfig by lunarStationConfig , IVoidCourseConfig by vocConfig

context(ILunarStationConfig, IVoidCourseConfig)
@DestinyMarker
class DailyReportConfigBuilder : Builder<DailyReportConfig> {

  var locale : Locale = Locale.TAIWAN

  override fun build(): DailyReportConfig {
    return DailyReportConfig(lunarStationConfig, vocConfig, locale)
  }

  companion object {
    context(ILunarStationConfig, IVoidCourseConfig)
    fun dailyReport(block: DailyReportConfigBuilder.() -> Unit = {}): DailyReportConfig {
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

  override val defaultConfig: DailyReportConfig = with(LunarStationConfig()) {
    with(VoidCourseConfig()) {
      DailyReportConfigBuilder().build()
    }
  }

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

    val fromGmt: GmtJulDay = lmtStart.toGmtJulDay(loc)
    val toGmt = lmtEnd.toGmtJulDay(loc)

    logger.info { "from $lmtStart to $lmtEnd" }

    val set = sortedSetOf<TimeDesc>()

    fun getTimeDesc(branch: Branch, branchStartGmtJulDay: GmtJulDay, middleLmt: ChronoLocalDateTime<*>, loc: ILocation): TimeDesc.TypeHour {
      val hourlyLunarStation = lunarStationFeature.hourlyFeature.getModel(middleLmt, loc, config.lunarStationConfig)
      val descs = buildList {
        add("$branch 初")
        add(hourlyLunarStation.getFullName(config.locale))
      }

      return TimeDesc.TypeHour(branchStartGmtJulDay, branch, hourlyLunarStation, descs)
    }

    val hourBranchConfig = config.lunarStationConfig.ewConfig.dayHourConfig.hourBranchConfig

    // 12地支 + 隔天的子初
    val listBranches: List<TimeDesc.TypeHour> = hourBranchFeature.getDailyBranchStartListWithNextDayZi(lmtStart.toLocalDate(), loc, hourBranchConfig).let { list ->
      val branchMiddleMap: Map<Branch, ChronoLocalDateTime<*>> = hourBranchFeature.getDailyBranchMiddleMap(lmtStart.toLocalDate(), loc, hourBranchConfig)
      val list12 = list.take(12).map { (branch , branchStart) ->
        val middleLmt = branchMiddleMap[branch]!!

        val branchStartGmtJulDay = branchStart.toGmtJulDay(loc)
        getTimeDesc(branch, branchStartGmtJulDay, middleLmt, loc)
      }

      val tomorrowLunarStationMap = hourBranchFeature.getDailyBranchMiddleMap(lmtStart.toLocalDate().plus(1, ChronoUnit.DAYS), loc, hourBranchConfig)
      val nextDayZi = list.last().let { (branch , branchStart) ->
        val middleLmt = tomorrowLunarStationMap[Branch.子]!!

        val branchStartGmtJulDay = branchStart.toGmtJulDay(loc)
        getTimeDesc(branch, branchStartGmtJulDay, middleLmt, loc)
      }
      list12.plus(nextDayZi)
    }

    // 日月 四個至點
    val listTransPoints: List<TimeDesc> = TransPoint.entries.flatMap { tp ->
      listOf(SUN, MOON).map { planet ->
        val gmt = riseTransFeature.getGmtTrans(fromGmt, planet, tp, loc, hourBranchConfig.transConfig)!!
        TimeDesc.TypeTransPoint(gmt, planet.toString(Locale.TAIWAN) + tp.getTitle(Locale.TAIWAN), planet, tp)
      }
    }

    // 節氣
    val listSolarTerms: List<TimeDesc> = solarTermsImpl.getPeriodSolarTermsEvents(fromGmt, toGmt).map { event ->
      TimeDesc.TypeSolarTerms(event.begin, event.solarTerms.toString(), event.solarTerms)
    }

    // 日月交角
    val listSunMoonAngle: List<TimeDesc> = listOf(
      0.0 to LunarPhase.NEW,
      90.0 to LunarPhase.FIRST_QUARTER,
      180.0 to LunarPhase.FULL,
      270.0 to LunarPhase.LAST_QUARTER
    ).flatMap { (deg, phase) ->
      relativeTransitImpl.getPeriodRelativeTransitGmtJulDays(MOON, SUN, fromGmt, toGmt, deg)
        .filter { day -> day in fromGmt .. toGmt }
        .map { TimeDesc.TypeSunMoon(it, phase) }
    }

    set.addAll(listBranches)
    set.addAll(listTransPoints)
    set.addAll(listSolarTerms)
    set.addAll(listSunMoonAngle)




    // 日食
    eclipseImpl.getNextSolarEclipse(fromGmt, true, SolarType.entries.toSet()).also { eclipse ->
      if (eclipse.begin in fromGmt .. toGmt) {
        set.add(TimeDesc.TypeSolarEclipse(eclipse.begin, eclipse.solarType, EclipseTime.BEGIN))
      }

      if (eclipse.max in fromGmt .. toGmt) {
        // 日食 食甚 觀測資料
        val locPlace: LocationPlace? = eclipseImpl.getEclipseCenterInfo(eclipse.max)?.let { (obs, _) ->
          val maxLoc = Location(obs.lat, obs.lng)
          reverseGeocodingService.getNearbyLocation(maxLoc, config.locale)?.let { place ->
            LocationPlace(maxLoc, place)
          }
        }
        set.add(
          TimeDesc.TypeSolarEclipse(eclipse.max, eclipse.solarType, EclipseTime.MAX, locPlace)
        )
      }

      if (eclipse.end in fromGmt .. toGmt) {
        set.add(TimeDesc.TypeSolarEclipse(eclipse.end, eclipse.solarType, EclipseTime.END))
      }
    }

    // 月食
    eclipseImpl.getNextLunarEclipse(fromGmt, true).also { eclipse ->
      if (eclipse.begin in fromGmt .. toGmt) {
        set.add(TimeDesc.TypeLunarEclipse(eclipse.begin, eclipse.lunarType, EclipseTime.BEGIN))
      }
      if (eclipse.begin in fromGmt .. toGmt) {
        set.add(TimeDesc.TypeLunarEclipse(eclipse.max, eclipse.lunarType, EclipseTime.MAX))
      }
      if (eclipse.end in fromGmt .. toGmt) {
        set.add(TimeDesc.TypeLunarEclipse(eclipse.end, eclipse.lunarType, EclipseTime.END))
      }
    }

    // 月亮空亡
    voidCourseFeature.getVoidCourses(fromGmt, toGmt, loc, relativeTransitImpl, config.vocConfig)
      .forEach { voc ->
        if (voc.begin > fromGmt) {
          set.add(TimeDesc.VoidMoon.Begin(voc, loc))
        }
        if (voc.end < toGmt) {
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
