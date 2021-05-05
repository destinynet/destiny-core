/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:48:49
 */
package destiny.core.astrology

import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.AspectData.Type.APPLYING
import destiny.core.astrology.AspectData.Type.SEPARATING

/**
 * 計算一張命盤 [IHoroscopeModel] 中，的交角列表
 */
interface IAspectsCalculator  {

  /** 取得此星盤中，所有的交角資料 */
  fun IHoroscopeModel.getAspectData(
                    points : Collection<Point> = this.positionMap.keys,
                    aspects: Collection<Aspect> = Aspect.getAngles(Importance.HIGH)) : Set<AspectData>

  /**
   * 取得於此 [Point] , 在此星盤 [h] 中 , 形成交角的資料
   */
  fun getAspectData(point: Point,
                    h: IHoroscopeModel,
                    points : Collection<Point> = h.positionMap.keys,
                    aspects: Collection<Aspect> = Aspect.getAngles(Importance.HIGH)) : Set<AspectData>

  /**
   * 取得與 [Point] 形成交角的星體，以及其交角是哪種，以及交角緊密度 (0~1)
   * 如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspectAndScore(point: Point,
                             positionMap: Map<Point, IPos>,
                             points: Collection<Point> = positionMap.keys,
                             aspects: Collection<Aspect> = Aspect.getAngles(Importance.HIGH)
  ): Set<Triple<Point , Aspect , Double>>

  /**
   * 取得與 [Point] 形成交角的星體，以及其交角是哪種 ，如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspect(point: Point,
                     positionMap: Map<Point, IPos>,
                     points: Collection<Point> = positionMap.keys,
                     aspects: Collection<Aspect> = Aspect.getAngles(Importance.HIGH)
  ): Map<Point, Aspect> {
    return getPointAspectAndScore(point, positionMap, points, aspects).associate { (point, aspect, _) ->
      point to aspect
    }
  }


  fun IHoroscopeModel.getPointAspect(point: Point,
                     points: Collection<Point>,
                     aspects: Collection<Aspect> = Aspect.getAngles(Importance.HIGH)): Map<Point, Aspect> {
    return getPointAspect(point, this.positionMap, points, aspects)
  }


  /**
   * @param positionMap : 計算 此 map 星體之間所形成的交角
   * @param points : 欲計算交角的 points , 因為 positionMap 可能包含許多小星體、沒必要計算的星體
   *
   * 如果沒有形成任何交角（不太可能 , 除非 points 很少 ），則傳回 size = 0 之 Set
   */
  fun getAspectDataSet(positionMap: Map<Point, IPos>,
                       points: Collection<Point> = positionMap.keys,
                       aspects: Collection<Aspect> = Aspect.getAngles(Importance.HIGH)): Set<AspectData> {

    return points.asSequence().map { p1 ->
      getPointAspectAndScore(p1, positionMap, points, aspects)
        .map { (p2 , aspect , score) ->
          AspectData(p1 , p2 , aspect , IHoroscopeModel.getAspectError(positionMap, p1, p2, aspect) ?: 0.0, score)
        }.toSet()
    }.flatten()
      .toSet()
  }

  /**
   * 一個星盤當中，兩個星體，是否形成交角。以及即將形成 ([APPLYING] , 入相位)，還是離開該交角 ([SEPARATING] , 出相位)
   * 如果不是形成 aspect 交角，會傳回 null
   * */
  fun IHoroscopeModel.getAspectType(p1: Point, p2: Point, aspect: Aspect): AspectData.Type? {
    return this.getAspectData(setOf(p1 , p2) , setOf(aspect)).firstOrNull()?.type
  }

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun IHoroscopeModel.getAspectAndType(p1: Point, p2: Point, aspects: Collection<Aspect>): Pair<Aspect , AspectData.Type>? {
    return aspects.asSequence().map { aspect ->
      aspect to this.getAspectType(p1, p2, aspect)
    }.filter { (_, type) ->
      type != null
    }.map { (aspect, type) ->
      aspect to type!!
    }.firstOrNull()
  }

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun IHoroscopeModel.getAspectType(p1: Point, p2: Point, aspects: Collection<Aspect>): AspectData.Type? {
    return getAspectAndType(p1, p2, aspects)?.second
  }

}
