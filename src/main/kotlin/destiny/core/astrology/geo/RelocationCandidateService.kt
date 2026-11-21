/**
 * @author smallufo
 * Created on 2026-07-27
 */
package destiny.core.astrology.geo

import destiny.core.astrology.HoroscopeConfig
import destiny.core.astrology.HoroscopeFeature
import destiny.core.astrology.HoroscopeModel
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IHoroscopeConfig
import destiny.core.astrology.IObliquityCalculator
import destiny.core.astrology.IStarPositionWithAzimuthCalculator
import destiny.core.astrology.Planet
import destiny.core.astrology.Star
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.ILocationPlace
import destiny.tools.KotlinLogging

/**
 * 產品層共用的 candidate 建構（設計 doc 5.1）：對每個候選城市算出
 * angularity / nearestLine / houseShifts（相對 [getCandidates] 的 baseLoc）/ 換置盤。
 *
 * 純演算法組合，不含 swisseph，故置於 destiny-core（比照 [destiny.core.fengshui.StarMountainService]）。
 * 候選很多時，呼叫端可先用 [AstroGeoContext] 的純幾何量粗排，再對 top-N 呼叫本服務。
 */
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
  ): List<RelocationCandidate> =
    getCandidates(horoscopeFeature.getModel(jd, baseLoc, horoscopeConfig), places, basis, stars)

  /**
   * 手上已經有基準盤（[baseChart]：基準地、時刻與設定都在盤上）時用這個，不必再整張起一次盤。
   * 基準盤由別的盤換過來的，用 [chartAt]
   */
  fun getCandidates(
    baseChart: IHoroscopeModel,
    places: List<ILocationPlace>,
    basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
    stars: Collection<Star> = Planet.list,
  ): List<RelocationCandidate> {
    val jd = baseChart.gmtJulDay
    val ctx = AstroGeoContext.of(jd, stars, starPosition, obliquityCalculator.getObliquity(jd))
    return places.mapNotNull { place ->
      // 同一時刻只換地點：空亡、逆行這些與地點無關的不重算（整張重算一張約 0.3 秒）。
      // 極圈附近有些時刻 Placidus 等宮制無解（宮頭算不出來）：那座城市跳過，不讓整批失敗
      val relocatedChart = runCatching { horoscopeFeature.relocate(baseChart, place) }.getOrElse {
        logger.debug { "略過 ${place.place}（${place.lat.value}, ${place.lng.value}）：${it.message}" }
        return@mapNotNull null
      }
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

  /**
   * [model] 那一刻在 [loc] 的盤，設定為 [config]：與 [model] 同一組設定就沿用它（同地點直接回傳、換地點 [HoroscopeFeature.relocate]，
   * 不重算空亡與逆行），設定不同才整張起盤。結果與整張起盤相同
   */
  fun chartAt(model: IHoroscopeModel, loc: ILocation, config: IHoroscopeConfig): IHoroscopeModel =
    if ((model as? HoroscopeModel)?.config == config) {
      if (model.location == loc) model else horoscopeFeature.relocate(model, loc)
    } else {
      horoscopeFeature.getModel(model.gmtJulDay, loc, config)
    }

  private companion object {
    private val logger = KotlinLogging.logger { }
  }
}
