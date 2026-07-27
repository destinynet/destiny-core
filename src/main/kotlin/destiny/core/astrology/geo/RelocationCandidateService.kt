/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.HoroscopeFeature
import destiny.core.astrology.IHoroscopeConfig
import destiny.core.astrology.IObliquityCalculator
import destiny.core.astrology.IStarPositionWithAzimuthCalculator
import destiny.core.astrology.Planet
import destiny.core.astrology.Star
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ILocationPlace
import jakarta.inject.Named

/**
 * 產品層共用的 candidate 建構（設計 doc 5.1）：對每個候選城市算出
 * angularity / nearestLine / houseShifts（相對 [getCandidates] 的 baseLoc）/ 換置盤。
 *
 * 純演算法組合，不含 swisseph，故置於 destiny-core（比照 [destiny.core.fengshui.StarMountainService]）。
 * 候選很多時，呼叫端可先用 [AstroGeoContext] 的純幾何量粗排，再對 top-N 呼叫本服務。
 */
@Named
class RelocationCandidateService(
  private val horoscopeFeature: HoroscopeFeature,
  private val starPosition: IStarPositionWithAzimuthCalculator,
  private val obliquityCalculator: IObliquityCalculator,
) {

  /**
   * @param baseLoc houseShifts 的「基準地」：產品 A 用現居地，未提供時呼叫端退化為出生地
   * @param jd 一律當參數（設計 doc 拍板）：本命 ACG 傳 natal gmt、CycloCartoGraphy 傳當下 gmt、Aimed SR 傳 SR 時刻
   */
  fun getCandidates(
    jd: GmtJulDay,
    baseLoc: ILocation,
    places: List<ILocationPlace>,
    basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
    stars: Collection<Star> = Planet.list,
    horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
  ): List<RelocationCandidate> {
    val ctx = AstroGeoContext.of(jd, stars, starPosition, obliquityCalculator.getObliquity(jd))
    val baseChart = horoscopeFeature.calculate(jd, baseLoc, horoscopeConfig)
    return places.map { place ->
      val relocatedChart = horoscopeFeature.calculate(jd, place, horoscopeConfig)
      val houseShifts = stars.mapNotNull { star ->
        val from = baseChart.getHouse(star) ?: return@mapNotNull null
        val to = relocatedChart.getHouse(star) ?: return@mapNotNull null
        if (from != to) HouseShift(star, from, to) else null
      }
      RelocationCandidate(
        place = place,
        angularity = ctx.angularity(place, basis),
        nearestLine = ctx.nearestLines(place, basis),
        houseShifts = houseShifts,
        relocatedChart = relocatedChart,
      )
    }
  }
}
