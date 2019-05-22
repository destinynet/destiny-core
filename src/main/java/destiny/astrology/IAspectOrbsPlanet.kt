/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 12:44:34
 */
package destiny.astrology

/**
 * 考量行星交角的容許度介面。
 * 在「現代占星術」中，一般而言，交角不看星體
 * 例如 , [AspectOrbsDefaultImpl] 中 , 合相的交角都算 11 度 ,
 * 而此實作會考慮星體是哪一顆，例如可以設定日月合相的交角放大到 12度
 * 內定實作為 [AspectOrbsPlanetDefaultImpl]
 * 未來可以用資料庫實作
 *
 */
interface IAspectOrbsPlanet {


  /**
   * @param aspect  欲取得容許度之交角
   * @return 交角容許度 , 以及其 threshold
   */
  fun getPlanetAspectOrbAndThreshold(p1: Point, p2: Point, aspect: Aspect): Pair<Double,Double>?

  /**
   * @param aspect 欲取得容許度之交角
   * @return 交角容許度
   */
  fun getPlanetAspectOrb(p1: Point, p2: Point, aspect: Aspect): Double? {
    return getPlanetAspectOrbAndThreshold(p1, p2, aspect)?.first
  }
}
