/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:48:49
 */
package destiny.core.astrology

import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.IPointAspectPattern.Type
import destiny.core.astrology.IPointAspectPattern.Type.APPLYING
import destiny.core.astrology.IPointAspectPattern.Type.SEPARATING

/**
 * 計算一張命盤 [IHoroscopeModel] 內部的交角列表
 * 或是兩張命盤，彼此的交角列表
 */
interface IAspectsCalculator  {

  fun getAspectPattern(p1: AstroPoint, p2: AstroPoint,
                       p1PosMap: Map<AstroPoint, IPos>, p2PosMap: Map<AstroPoint, IPos>,
                       laterForP1: () -> IPos?, laterForP2: () -> IPos?,
                       aspects: Set<Aspect>): IPointAspectPattern?

  /** 取得此星盤中，所有的交角資料 */
  fun getAspectPatterns(m : IHoroscopeModel,
                        points: Set<AstroPoint> = m.positionMap.keys,
                        aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): Set<IPointAspectPattern>

  /**
   * 取得於此 [AstroPoint] , 在此星盤 [h] 中 , 形成交角的資料
   */
  fun getAspectPatterns(point: AstroPoint,
                        h: IHoroscopeModel,
                        points : Set<AstroPoint> = h.positionMap.keys,
                        aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()) : Set<IPointAspectPattern> {
    return points
      .asSequence()
      .map { eachPoint -> setOf(point, eachPoint) }
      .mapNotNull { twoPoints -> h.getAspectPattern(twoPoints, aspects) }
      .toSet()
  }

  /**
   * 取得與 [AstroPoint] 形成交角的星體，以及其交角是哪種，以及交角緊密度 (0~1)
   * 如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspectAndScore(point: AstroPoint,
                             positionMap: Map<AstroPoint, IPos>,
                             points: Set<AstroPoint> = positionMap.keys,
                             aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): Set<Triple<AstroPoint , Aspect , Double>>

  /**
   * 取得與 [AstroPoint] 形成交角的星體，以及其交角是哪種 ，如果沒形成任何交角，傳回 empty
   * */
  fun getPointAspect(point: AstroPoint,
                     positionMap: Map<AstroPoint, IPos>,
                     points: Set<AstroPoint> = positionMap.keys,
                     aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()
  ): Map<AstroPoint, Aspect> {
    return getPointAspectAndScore(point, positionMap, points, aspects).associate { (point, aspect, _) ->
      point to aspect
    }
  }


  fun IHoroscopeModel.getPointAspect(point: AstroPoint,
                     points: Set<AstroPoint> = this.positionMap.keys,
                     aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()): Map<AstroPoint, Aspect> {
    return getPointAspect(point, this.positionMap, points, aspects)
  }


  /**
   * @param positionMap : 計算 此 map 星體之間所形成的交角
   * @param points : 欲計算交角的 points , 因為 positionMap 可能包含許多小星體、沒必要計算的星體
   *
   * 如果沒有形成任何交角（不太可能 , 除非 points 很少 ），則傳回 size = 0 之 Set
   */
  fun getAspectPatterns(positionMap: Map<AstroPoint, IPos>,
                        points: Set<AstroPoint> = positionMap.keys,
                        aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()): Set<IPointAspectPattern> {

    return points.asSequence().map { p1 ->
      getPointAspectAndScore(p1, positionMap, points, aspects)
        .map { (p2 , aspect , score) ->
          PointAspectPattern.of(p1, p2 , aspect, null, IHoroscopeModel.getAspectError(positionMap, p1, p2, aspect) ?: 0.0, score )
        }.toSet()
    }.flatten()
      .toSet()
  }

  /**
   * 一個星盤當中，兩個星體，是否形成交角。以及即將形成 ([APPLYING] , 入相位)，還是離開該交角 ([SEPARATING] , 出相位)
   * 如果不是形成 aspect 交角，會傳回 null
   * */
  fun IHoroscopeModel.getAspectType(p1: AstroPoint, p2: AstroPoint, aspect: Aspect): Type? {
    return getAspectPatterns(this, setOf(p1, p2), setOf(aspect)).firstOrNull()?.type
  }

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun IHoroscopeModel.getAspectAndType(p1: AstroPoint, p2: AstroPoint, aspects: Set<Aspect>): Pair<Aspect , Type>? {
    return aspects.asSequence().map { aspect ->
      aspect to this.getAspectType(p1, p2, aspect)
    }.filter { (_, type) ->
      type != null
    }.map { (aspect, type) ->
      aspect to type!!
    }.firstOrNull()
  }

  /** 此兩顆星是否與這些交角形成任何交角，如果有，是入相位還是出相位。如果沒有，則傳回 null  */
  fun IHoroscopeModel.getAspectType(p1: AstroPoint, p2: AstroPoint, aspects: Set<Aspect>): Type? {
    return getAspectAndType(p1, p2, aspects)?.second
  }


  fun IHoroscopeModel.getAspectPattern(twoPoints: Set<AstroPoint>, aspects: Set<Aspect>): IPointAspectPattern?
}
