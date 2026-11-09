/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IAspectCalculator
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.IPos
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.tools.Score

/**
 * 以星座為單位的相位：在 [delegate] 判出的相位裡，只留下**依星座也成立**的（[fitsSigns]）——
 * 希臘化的相位以星座計，跨星座、只靠容許度成立的不算。例：摩羯 26° 與牡羊 4° 相距 68°，[delegate] 判六合，兩個星座卻是四分，這裡不留。
 *
 * 容許度、出入相位、分數全部照 [delegate]；這裡只做篩選，不會把 [delegate] 沒判出的相位補回來
 * （[delegate] 對一對星只回最先成立的那個相位，那個相位依星座不成立時，這一對就是沒有相位）。
 * 不是 30° 倍數的交角沒有對應的星座距離，一律留下。
 *
 * 要「依星座也成立」的技法，注入這個實作即可，不必各自帶開關
 */
class AspectCalculatorSignBoundImpl(private val delegate: IAspectCalculator) : IAspectCalculator {

  override fun getAspectPattern(
    p1: AstroPoint, p2: AstroPoint,
    p1PosMap: Map<AstroPoint, IZodiacDegree>, p2PosMap: Map<AstroPoint, IZodiacDegree>,
    laterForP1: ((AstroPoint) -> IZodiacDegree?)?, laterForP2: ((AstroPoint) -> IZodiacDegree?)?,
    aspects: Set<Aspect>,
  ): IPointAspectPattern? =
    delegate.getAspectPattern(p1, p2, p1PosMap, p2PosMap, laterForP1, laterForP2, aspects)?.takeIf {
      it.aspect.fitsSigns(ZodiacSign.of(p1PosMap.getValue(p1).zDeg), ZodiacSign.of(p2PosMap.getValue(p2).zDeg))
    }

  override fun IHoroscopeModel.getAspectPatterns(points: Set<AstroPoint>, aspects: Set<Aspect>): Set<IPointAspectPattern> {
    val h = this
    return with(delegate) { h.getAspectPatterns(points, aspects) }.filterTo(LinkedHashSet()) { it.fitsSigns(h) }
  }

  override fun getPointAspectAndScore(
    point: AstroPoint,
    positionMap: Map<AstroPoint, IPos>,
    points: Set<AstroPoint>,
    aspects: Set<Aspect>,
  ): Set<Triple<AstroPoint, Aspect, Score>> {
    val sign = positionMap[point]?.lngDeg?.sign ?: return emptySet()
    return delegate.getPointAspectAndScore(point, positionMap, points, aspects).filterTo(LinkedHashSet()) { (other, aspect, _) ->
      positionMap[other]?.lngDeg?.sign?.let { aspect.fitsSigns(sign, it) } ?: false
    }
  }

  override fun IHoroscopeModel.getAspectPattern(twoPoints: Set<AstroPoint>, aspects: Set<Aspect>): IPointAspectPattern? {
    val h = this
    return with(delegate) { h.getAspectPattern(twoPoints, aspects) }?.takeIf { it.fitsSigns(h) }
  }
}
