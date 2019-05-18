/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:48:49
 */
package destiny.astrology

import destiny.core.Descriptive

/**
 * 計算一張命盤 ( Horoscope ) 中，的交角列表
 * 分為古典 [HoroscopeAspectsCalculatorClassical]
 * 以及西洋 [HoroscopeAspectsCalculatorModern] 實作
 *
 *  */
interface IHoroscopeAspectsCalculator : Descriptive {

  /**
   * 取得與 [Point] 形成交角的星體，以及其交角是哪種，以及交角緊密度 (0~1)
   * 如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspectAndScore(point: Point,
                             positionMap: Map<Point, IPos>,
                             points: Collection<Point> = positionMap.keys,
                             aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)
  ): Map<Point, Pair<Aspect, Double>>

  /**
   * 取得與 [Point] 形成交角的星體，以及其交角是哪種 ，如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspect(point: Point,
                     positionMap: Map<Point, IPos>,
                     points: Collection<Point> = positionMap.keys,
                     aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)
  ): Map<Point, Aspect> {
    return getPointAspectAndScore(point, positionMap, points, aspects)
      .map { (point , pair) ->
        point to pair.first
      }.toMap()
  }


  fun getPointAspect(point: Point, horoscope: IHoroscopeModel,
                     points: Collection<Point>,
                     aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)): Map<Point, Aspect> {
    return getPointAspect(point, horoscope.positionMap, points, aspects)
  }


}
