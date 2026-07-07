/**
 * Created by smallufo on 2026-07-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IRiseTrans
import destiny.core.astrology.Planet
import destiny.core.astrology.TransPoint
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.chinese.display
import destiny.core.calendar.toLmt
import jakarta.inject.Named
import java.time.LocalDateTime

@Named
class Ew3dDtoFactory(
  private val solarTermsImpl: ISolarTerms,
  private val riseTransImpl: IRiseTrans,
  private val julDayResolver: JulDayResolver
) {

  /** 刻意固定用 [HourSolarTransImpl]（真太陽時），不注入 [IHour] 避免 bean 歧義 */
  private val hourImpl: IHour = HourSolarTransImpl(riseTransImpl)

  fun IEightWordsContextModel.toEw3dDto(): Ew3dDto {
    val lmt = time as LocalDateTime
    val gmtJulDay = lmt.toGmtJulDay(location)
    val sunLng = starPosMap[Planet.SUN]?.lngDeg?.value ?: error("SUN position absent")

    val terms = solarTermsImpl.getSolarTermsEvents(gmtJulDay - 190, gmtJulDay + 190).map { ev ->
      Ew3dDto.SolarTermEventDto(
        ev.solarTerms.name,
        ev.solarTerms.zodiacDegree,
        ev.begin.toLmt(location, julDayResolver) as LocalDateTime
      )
    }

    val branches = hourImpl.getDailyBranchStartMap(lmt.toLocalDate(), location, julDayResolver, HourBranchConfig())
      .entries.sortedBy { it.value }
      .map { (b, t) -> Ew3dDto.HourBranchDto(b.toString(), t as LocalDateTime) }

    val dayStartGmt = lmt.toLocalDate().atStartOfDay().toGmtJulDay(location)
    fun trans(p: TransPoint): LocalDateTime = riseTransImpl.getGmtTransJulDay(dayStartGmt, Planet.SUN, p, location)!!
      .toLmt(location, julDayResolver) as LocalDateTime

    return Ew3dDto(
      eightWords = Ew3dDto.Pillars(
        eightWords.year.toString(),
        eightWords.month.toString(),
        eightWords.day.toString(),
        eightWords.hour.toString()
      ),
      time = lmt,
      gmtOffsetMinutes = gmtMinuteOffset,
      location = location,
      place = place,
      chineseDate = chineseDate.display(true),
      sunLongitude = sunLng,
      solarTerms = terms,
      hourBranches = branches,
      meridianTime = trans(TransPoint.MERIDIAN),
      nadirTime = trans(TransPoint.NADIR)
    )
  }
}
