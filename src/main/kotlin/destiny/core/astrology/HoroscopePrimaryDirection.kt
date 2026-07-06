/**
 * Extension function on [IHoroscopeFeature] producing a Primary Direction chart model
 * ([IPrimaryDirectionModel]), analogous to [IHoroscopeFeature.getSolarArc].
 *
 * Kept in its own file (rather than inside [HoroscopeFeature]) to keep that class lean,
 * mirroring [getZodiacalReleasing] / [getFirdariaPeriods] / [getProfection].
 *
 * Created by smallufo on 2026-07-06.
 */
package destiny.core.astrology

import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.Constants.TROPICAL_YEAR_DAYS
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.prediction.ITimeKey
import destiny.core.astrology.prediction.IPrimaryDirectionModel
import destiny.core.astrology.prediction.PrimaryDirectionModel
import destiny.core.astrology.prediction.PtolemyKey
import destiny.core.calendar.GmtJulDay

/**
 * 產生「主限盤」(Primary Direction chart) 於 [viewTime] 的模型。
 *
 * 採用**黃道-赤經法 (zodiacal, no-latitude)**，與 [swissephImpl 的 PrimaryDirection 事件計算] 同源：
 * 每個本命點以其黃道經度對應的赤經 (RA) 為基準，被周日運動推進 [directionArc] 度，再投影回黃道經度。
 * 因黃道點的 RA 與經度一一對應，故推進後的 [IPrimaryDirectionModel.positionMap] 與本命盤所形成的
 * (黃道) 合盤相位，恰能重現赤經法的主限事件。
 *
 * @param viewTime  欲查閱的時刻 (必須晚於出生時間)
 * @param timeKey   時間鑰匙 (arc↔年)，預設 [PtolemyKey]
 * @param forward   順推 (true) 或逆推 (false)
 * @param obliquityCalculator 黃赤交角來源，預設純數學平均值 [Astronomical]
 */
fun IHoroscopeFeature.getPrimaryDirection(
  model: IHoroscopeModel,
  viewTime: GmtJulDay,
  aspectCalculator: IAspectCalculator,
  timeKey: ITimeKey = PtolemyKey,
  forward: Boolean = true,
  obliquityCalculator: IObliquityCalculator = Astronomical,
  threshold: Double? = null,
  aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet(),
): IPrimaryDirectionModel {

  require(viewTime >= model.gmtJulDay) { "viewTime ($viewTime) should be after model.gmtJulDay (${model.gmtJulDay})" }

  val eps = obliquityCalculator.getObliquity(model.gmtJulDay)
  val years = (viewTime - model.gmtJulDay) / TROPICAL_YEAR_DAYS
  val directionArc = timeKey.getArc(years)

  /** 把某黃道經度的 RA 推進 [arcDeg] 度，投影回黃道經度 */
  fun directedDegree(lng: Double, arcDeg: Double): IZodiacDegree {
    val ra0 = Astronomical.eclipticLongitudeToRA(lng, eps)
    val directedRA = if (forward) ra0 - arcDeg else ra0 + arcDeg
    return Astronomical.raToEclipticLongitude(directedRA, eps).toZodiacDegree()
  }

  val positionMap: Map<AstroPoint, IZodiacDegree> = model.positionMap.mapValues { (_, pos) ->
    directedDegree(pos.lng, directionArc)
  }

  // 供合盤 orb 方向判斷 (applying/separating) 使用的「稍後」位置
  val arcLater = timeKey.getArc(years + 0.01)
  val laterPosMap: Map<AstroPoint, IZodiacDegree> = model.positionMap.mapValues { (_, pos) ->
    directedDegree(pos.lng, arcLater)
  }
  val laterForOuter: (AstroPoint) -> IZodiacDegree? = { p -> laterPosMap[p] }
  val laterForInner: (AstroPoint) -> IZodiacDegree? = { p -> model.getZodiacDegree(p) }

  val synastryAspects: List<SynastryAspect> =
    synastryAspectsFine(positionMap, model, laterForOuter, laterForInner, aspectCalculator, threshold, aspects)

  return PrimaryDirectionModel(
    natalGmtJulDay = model.gmtJulDay,
    viewGmtJulDay = viewTime,
    timeKey = timeKey,
    forward = forward,
    directionArc = directionArc,
    positionMap = positionMap,
    synastryAspects = synastryAspects,
  )
}
