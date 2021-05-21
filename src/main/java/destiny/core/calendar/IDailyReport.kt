/**
 * Created by smallufo on 2018-07-18.
 */
package destiny.core.calendar

import destiny.core.astrology.*
import destiny.core.astrology.classical.IVoidCourse
import destiny.core.astrology.eclipse.IEclipseFactory
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.calendar.eightwords.IHour
import destiny.core.chinese.Branch
import destiny.core.chinese.lunarStation.ILunarStationContext
import destiny.tools.location.ReverseGeocodingService
import java.io.Serializable
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*

interface IDailyReport {

  fun getList(lmt: ChronoLocalDateTime<*>, loc: ILocation, locale: Locale): List<TimeDesc>
}

class DailyReportImpl(val hourSolarTransImpl: IHour,
                      val riseTransImpl: IRiseTrans,
                      val relativeTransitImpl: IRelativeTransit,
                      val solarTermsImpl: ISolarTerms,
                      val eclipseImpl: IEclipseFactory,
                      val reverseGeocodingService: ReverseGeocodingService,
                      val julDayResolver: JulDayResolver,
                      val lunarStationContextFolk: ILunarStationContext,
                      val voidCourseMedieval: IVoidCourse,
                      val horoContext: IHoroscopeContext) : IDailyReport, Serializable {

  private fun getList(lmtStart: ChronoLocalDateTime<*>,
                      lmtEnd: ChronoLocalDateTime<*>,
                      loc: ILocation,
                      locale: Locale): List<TimeDesc> {
    val set = TreeSet<TimeDesc>()

    // 每個時辰開始時刻 (子初可能是前一晚）
    val hourStartMap = hourSolarTransImpl.getDailyBranchStartMap(lmtStart.toLocalDate() , loc , julDayResolver)
      .mapValues { (_,v) -> v as LocalDateTime }


    // 每個時辰的 時禽
    val hourLunarStationMap = hourSolarTransImpl.getDailyBranchMiddleMap(lmtStart.toLocalDate(), loc, julDayResolver)
      .map { (b, middleLmt) ->
        b to lunarStationContextFolk.hourlyImpl.getHourly(middleLmt, loc)
      }.toMap()

    // 12地支
    val listBranches: List<TimeDesc> = Branch.values().map { eb ->

      val branchStart: LocalDateTime = hourStartMap[eb]!!
      val hourlyLunarStation: LunarStation = hourLunarStationMap[eb]!!

      val descs = listOf("$eb 初", hourlyLunarStation.getFullName(locale))
      TimeDesc.TypeHour(branchStart, eb, hourlyLunarStation, descs)
    }


    // 日月 四個至點
    val listTransPoints: List<TimeDesc> = TransPoint.values().flatMap { tp ->
      listOf(Planet.SUN, Planet.MOON).map { planet ->
        TimeDesc.TypeTransPoint(
          riseTransImpl.getLmtTrans(lmtStart, planet, tp, loc, julDayResolver) as LocalDateTime,
          planet.toString(Locale.TAIWAN) + tp.toString(Locale.TAIWAN),
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
          reverseGeocodingService.getNearbyLocation(maxLoc, locale)?.let { place ->
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
    voidCourseMedieval.getVoidCourses(fromGmtJulDay, toGmtJulDay, loc, horoContext, relativeTransitImpl, Planet.MOON).forEach { voc ->
      if (voc.beginGmt > fromGmtJulDay) {
        set.add(TimeDesc.VoidMoon.Begin(voc, loc))
      }
      if (voc.endGmt < toGmtJulDay) {
        set.add(TimeDesc.VoidMoon.End(voc, loc))
      }
    }

    return set.toList()
  }


  override fun getList(lmt: ChronoLocalDateTime<*>, loc: ILocation, locale: Locale): List<TimeDesc> {
    val lmtStart = lmt.with(ChronoField.HOUR_OF_DAY, 0).with(ChronoField.MINUTE_OF_HOUR, 0).with(ChronoField.SECOND_OF_MINUTE, 0).with(ChronoField.NANO_OF_SECOND, 0)
    val lmtEnd = lmtStart.plus(1, ChronoUnit.DAYS)
    return getList(lmtStart, lmtEnd, loc, locale)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DailyReportImpl) return false

    if (hourSolarTransImpl != other.hourSolarTransImpl) return false
    if (riseTransImpl != other.riseTransImpl) return false
    if (relativeTransitImpl != other.relativeTransitImpl) return false
    if (solarTermsImpl != other.solarTermsImpl) return false
    if (eclipseImpl != other.eclipseImpl) return false
    if (reverseGeocodingService != other.reverseGeocodingService) return false
    if (julDayResolver != other.julDayResolver) return false
    if (lunarStationContextFolk != other.lunarStationContextFolk) return false
    if (voidCourseMedieval != other.voidCourseMedieval) return false
    if (horoContext != other.horoContext) return false

    return true
  }

  override fun hashCode(): Int {
    var result = hourSolarTransImpl.hashCode()
    result = 31 * result + riseTransImpl.hashCode()
    result = 31 * result + relativeTransitImpl.hashCode()
    result = 31 * result + solarTermsImpl.hashCode()
    result = 31 * result + eclipseImpl.hashCode()
    result = 31 * result + reverseGeocodingService.hashCode()
    result = 31 * result + julDayResolver.hashCode()
    result = 31 * result + lunarStationContextFolk.hashCode()
    result = 31 * result + voidCourseMedieval.hashCode()
    result = 31 * result + horoContext.hashCode()
    return result
  }


}
