/**
 * Created by smallufo on 2017-02-16.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay

/**
 * 此顆星時間上「前一次」與「後一次」和其他星精確成形的交角 —— 給空亡（void of course）判斷用。
 *
 * 任何星在任何時候都有前後兩個成形的交角，所以這是中性的時間搜尋，不看容許度。
 * 2026-10-31 之前名為 IBesieged，也被拿來判斷包夾；包夾改由
 * [destiny.core.astrology.classical.IBesiegement]（只看盤面時刻、兩側 7° 內）判斷。
 */
interface IAspectNeighbors {

  /**
   * @param planet 計算此顆行星，前後與哪兩顆行星成形
   * @param gmtJulDay GMT 時間
   * @param otherPlanets 其他行星
   * @param angles 欲計算的交角角度
   * @return 前者為「之前」形成交角者。後者為「之後」形成交角者
   */
  fun getNeighborsByDegrees(planet: Planet,
                            gmtJulDay: GmtJulDay,
                            otherPlanets: Set<Planet>,
                            angles: Set<Double>): Pair<IAngleData?, IAngleData?>

  fun getNeighborsByAspects(planet: Planet,
                            gmtJulDay: GmtJulDay,
                            otherPlanets: Set<Planet>,
                            aspects: Set<Aspect>): Pair<IAspectData?, IAspectData?> {
    return getNeighborsByDegrees(planet, gmtJulDay, otherPlanets, aspects.map { it.degree }.toSet()).let { (prior, after) ->
      prior?.toAspectData() to after?.toAspectData()
    }
  }
}
