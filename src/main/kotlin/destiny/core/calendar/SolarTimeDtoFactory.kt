/**
 * Created by smallufo on 2026-10-23.
 */
package destiny.core.calendar

import destiny.core.astrology.Astronomical
import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IRiseTrans
import destiny.core.astrology.IStarPositionWithAzimuth
import destiny.core.astrology.IStarPositionWithAzimuthCalculator
import destiny.core.astrology.ITrueSolarTime
import destiny.core.astrology.Planet
import destiny.core.astrology.TransPoint
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.eightwords.HourBranchConfig
import destiny.core.calendar.eightwords.IHour
import destiny.core.chinese.Branch
import destiny.tools.CircleTools.normalizeSigned
import destiny.tools.SampleTools.sampleSteps
import jakarta.inject.Named
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneOffset
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.roundToInt

/**
 * 組裝 [SolarTimeDto]。窗口 = 包含 focus 的太陽日（前一個子正 → 下一個子正）；
 * 時辰起點直接問 [IHour]（HourSolarTransImpl），與時辰確認頁的表同源。
 */
@Named
class SolarTimeDtoFactory(
  private val riseTransImpl: IRiseTrans,
  private val starPosWithAzimuth: IStarPositionWithAzimuthCalculator,
  private val trueSolarTimeImpl: ITrueSolarTime,
  /** 參數名即 bean 名（CalendarConfig 另有 hourLmtImpl），同 Ew3dDtoFactory */
  private val hourSolarTransImpl: IHour,
) {

  /** 以某時刻（出生時刻）為 focus */
  fun forMoment(lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String? = null): SolarTimeDto {
    val focus = lmt.toGmtJulDay(loc)
    return build(focus, focus, loc, place)
  }

  /** 只給日期（時辰確認頁）：取包含該日民用 12:00 的太陽日，無 focus；曆法同 [forMoment]（可傳儒略曆日期） */
  fun forDate(date: ChronoLocalDate, loc: ILocation, place: String? = null): SolarTimeDto =
    build(date.atTime(LocalTime.NOON).toGmtJulDay(loc), null, loc, place)

  private fun build(anchor: GmtJulDay, focus: GmtJulDay?, loc: ILocation, place: String?): SolarTimeDto {
    val (dayFrom, dayTo) = solarDayOf(anchor, loc)
    val meridian = trans(dayFrom + SKIP, TransPoint.MERIDIAN, loc)
    val dayFromMs = dayFrom.ms()
    val dayToMs = dayTo.ms()
    val meridianMs = meridian.ms()
    val civil = civilOffsetsOf(loc, dayFromMs, dayToMs)
    val offsetAtMeridian = civil.last { it.fromMs <= meridianMs }.offsetSeconds
    val year = Instant.ofEpochMilli(meridianMs).atOffset(ZoneOffset.ofTotalSeconds(offsetAtMeridian)).year

    /** 窗口內的升／落；極夜、永晝（或落在窗口外）為 null */
    fun inWindow(point: TransPoint): Long? =
      riseTransImpl.getGmtTransJulDay(dayFrom, Planet.SUN, point, loc)?.takeIf { it < dayTo }?.ms()

    return SolarTimeDto(
      location = loc,
      place = place,
      dayFromMs = dayFromMs,
      dayToMs = dayToMs,
      meridianMs = meridianMs,
      meridianAltDeg = sunAt(meridian, loc).apparentAltitude,
      sunriseMs = inWindow(TransPoint.RISING),
      sunsetMs = inWindow(TransPoint.SETTING),
      focusMs = focus?.ms(),
      standardOffsetSeconds = standardOffsetSeconds(loc, meridianMs),
      eotSecondsAtMeridian = trueSolarTimeImpl.getEquationSecs(meridian),
      civilOffsets = civil,
      hourBranches = hourStarts(dayFrom, loc),
      samples = samples(dayFrom, (dayTo - dayFrom) * 1440.0, loc),
      eotYear = eotYear(year),
    )
  }

  /**
   * 包含 [t] 的太陽日：[前一個子正, 下一個子正)。
   * 太陽日約 1440 ± 0.5 分，從 t − 1.1 天起找到的第一個子正必 ≤ t，至多再前進兩次就跨過 t；
   * 超過 [MAX_DAY_STEPS] 次代表 [IRiseTrans] 違反「回傳 from 之後的下一次中天」的假設。
   */
  private fun solarDayOf(t: GmtJulDay, loc: ILocation): Pair<GmtJulDay, GmtJulDay> {
    var from = trans(t - 1.1, TransPoint.NADIR, loc)
    repeat(MAX_DAY_STEPS) {
      val next = trans(from + SKIP, TransPoint.NADIR, loc)
      if (next > t) return from to next
      from = next
    }
    error("no solar day containing $t at $loc after $MAX_DAY_STEPS nadir steps (last nadir = $from)")
  }

  private fun trans(from: GmtJulDay, point: TransPoint, loc: ILocation): GmtJulDay =
    riseTransImpl.getGmtTransJulDay(from, Planet.SUN, point, loc) ?: error("no $point transit for SUN at $loc")

  /** 赤道座標：lng = 赤經、lat = 赤緯；方位角／視高度同一次算出 */
  private fun sunAt(gmt: GmtJulDay, loc: ILocation): IStarPositionWithAzimuth =
    starPosWithAzimuth.getPositionWithAzimuth(Planet.SUN, gmt, loc, Centric.GEO, Coordinate.EQUATORIAL)

  private fun standardOffsetSeconds(loc: ILocation, atMs: Long): Int =
    loc.minuteOffset?.let { it * 60 } ?: loc.zoneId.rules.getStandardOffset(Instant.ofEpochMilli(atMs)).totalSeconds

  /** 13 筆：子正之前那個子時起點、丑…亥、窗口尾的子時起點 */
  private fun hourStarts(dayFrom: GmtJulDay, loc: ILocation): List<SolarTimeDto.HourStart> = buildList {
    var cursor = dayFrom - LEAD_IN // 子正前 90 分 = 亥時之中
    for (i in 0..12) {
      val branch = Branch[i % 12]
      val start = hourSolarTransImpl.getGmtNextStartOf(cursor, loc, branch, HourBranchConfig())
      add(SolarTimeDto.HourStart(branch.name, start.ms()))
      cursor = start + SKIP
    }
  }

  private fun samples(dayFrom: GmtJulDay, totalMin: Double, loc: ILocation): List<SolarTimeDto.Sample> =
    sampleSteps(totalMin, SAMPLE_STEP_MINUTES).map { tMin ->
      val gmt = dayFrom + tMin / 1440.0
      val pos = sunAt(gmt, loc)
      SolarTimeDto.Sample(tMin, pos.azimuthDeg, pos.apparentAltitude, pos.lat, subsolarLng(pos.lng, gmt))
    }

  /** 每天 12:00 UTC 的均時差（分鐘，四捨五入兩位小數） */
  private fun eotYear(year: Int): List<Double> = (1..Year.of(year).length()).map { day ->
    val noon = TimeTools.getJulDay(LocalDate.ofYearDay(year, day).atTime(12, 0).toInstant(ZoneOffset.UTC))
    (trueSolarTimeImpl.getEquationSecs(noon) / 60.0 * 100).roundToInt() / 100.0
  }

  private fun GmtJulDay.ms(): Long = toInstant().toEpochMilliseconds()

  companion object {
    /** 動畫平滑度：一日約 720 點 */
    const val SAMPLE_STEP_MINUTES = 2.0
    /** 找「下一個」同類事件時跳過剛找到那筆：1 分鐘 */
    private const val SKIP = 1.0 / 1440
    /** 起點子時的搜尋起點：子正前 90 分 */
    private const val LEAD_IN = 1.5 / 24
    /** [solarDayOf] 往後找子正的上限次數 */
    private const val MAX_DAY_STEPS = 3

    /** 直射點經度 = 赤經 − 格林威治平恆星時（東經為正，正規化到 (−180, 180]） */
    private fun subsolarLng(raDeg: Double, gmt: GmtJulDay): Double =
      (raDeg - Astronomical.gmst(gmt) * 15.0).normalizeSigned()
  }
}

/**
 * [fromMs, toMs) 之間的民用時差分段：第一段從 [fromMs] 起，之後每遇到一次時區規則的切換（夏令時）多一段。
 * 自訂時差（[ILocation.minuteOffset]）只有一段。給前端以分段換算手錶時間，不必帶時區資料庫。
 */
fun civilOffsetsOf(loc: ILocation, fromMs: Long, toMs: Long): List<SolarTimeDto.CivilOffset> {
  loc.minuteOffset?.let { return listOf(SolarTimeDto.CivilOffset(fromMs, it * 60, 0)) }
  val rules = loc.zoneId.rules
  fun seg(startMs: Long, at: Instant) =
    SolarTimeDto.CivilOffset(startMs, rules.getOffset(at).totalSeconds, rules.getDaylightSavings(at).seconds.toInt())
  return buildList {
    add(seg(fromMs, Instant.ofEpochMilli(fromMs)))
    var transition = rules.nextTransition(Instant.ofEpochMilli(fromMs))
    while (transition != null && transition.instant.toEpochMilli() < toMs) {
      add(seg(transition.instant.toEpochMilli(), transition.instant))
      transition = rules.nextTransition(transition.instant)
    }
  }
}
