/**
 * @author smallufo
 * Created on 2008/6/27 at 上午 4:28:26
 */
package destiny.astrology

import mu.KotlinLogging
import java.io.Serializable

/** 利用 [IHoroscopeAspectsCalculator] , 計算命盤之中，星體所呈現的交角，及其容許度  */
class HoroscopeAspectsCalculator(private val calculator: IHoroscopeAspectsCalculator) : Serializable {

  /**
   * 如果沒有形成任何交角（不太可能 , 除非 points 很少 ），則傳回 size = 0 之 Set
   *
   * @param positionMap : 計算 此 map 星體之間所形成的交角
   * @param points : 欲計算交角的 points , 因為 positionMap 可能包含許多小星體、沒必要計算的星體
   */
  fun getAspectDataSet(positionMap: Map<Point, IPos>,
                       points: Collection<Point> = positionMap.keys ,
                       aspects : Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)): Set<HoroscopeAspectData> {

    val dataSet = mutableSetOf<HoroscopeAspectData>()

    points.map { point ->
      val map: Map<Point, Pair<Aspect,Double>> = calculator.getPointAspectAndScore(point, positionMap, points , aspects)
      logger.trace("與 {} 形成所有交角的 pointAspect Map = {}", point, map)

      map.filter { (_ , aspectAndScore) -> aspects.contains(aspectAndScore.first) }
        .map { (key , aspectAndScore) ->
          HoroscopeAspectData(point, key, aspectAndScore.first,
            IHoroscopeModel.getAspectError(positionMap , point, key, aspectAndScore.first)?:0.0 , aspectAndScore.second)
        }.toSet()

    }.flatMapTo(dataSet ) { it }
      .toSet()

    return dataSet
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }

}
