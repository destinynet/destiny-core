/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 2:19:45
 */
package destiny.astrology

import destiny.astrology.classical.AspectEffectiveClassical
import destiny.astrology.classical.IPointDiameter
import destiny.astrology.classical.PointDiameterAlBiruniImpl
import java.io.Serializable
import java.util.*


class AspectEffectiveClassicalBuilder {
  var planetOrbsImpl: IPointDiameter = PointDiameterAlBiruniImpl()
  fun build(): AspectEffectiveClassical = AspectEffectiveClassical(planetOrbsImpl)
}

fun aspectEffectiveClassical(block: AspectEffectiveClassicalBuilder.() -> Unit = {}) = AspectEffectiveClassicalBuilder().apply(block).build()


class HoroscopeAspectsCalculatorClassicalBuilder {

  var classical: AspectEffectiveClassical = AspectEffectiveClassicalBuilder().build()
  var planetOrbsImpl: IPointDiameter = classical.planetOrbsImpl
    set(value) {
      field = value
      classical = aspectEffectiveClassical {
        planetOrbsImpl = value
      }
    }

  fun build() = HoroscopeAspectsCalculatorClassical(classical)
}

fun classicalCalculator(block: HoroscopeAspectsCalculatorClassicalBuilder.() -> Unit = {}) = HoroscopeAspectsCalculatorClassicalBuilder().apply(block).build()


/**
 * 古典占星術，列出一張星盤中呈現交角的星體以及角度 的實作
 *  */
class HoroscopeAspectsCalculatorClassical(
  val classical: AspectEffectiveClassical,
  val planetOrbsImpl: IPointDiameter = classical.planetOrbsImpl) : IHoroscopeAspectsCalculator, Serializable {


  override fun getPointAspectAndScore(point: Point,
                                      positionMap: Map<Point, IPos>,
                                      points: Collection<Point>,
                                      aspects: Collection<Aspect>): Set<Triple<Point, Aspect, Double>> {

    return point.takeIf { it is Planet } // 只計算行星
      ?.let {
        positionMap[point]?.lng?.let { planetDeg ->

          points.filter { it !== point }
            .filter { eachPoint -> !(point is Axis && eachPoint is Axis) }  // 過濾四角點互相形成的交角
            .filter { eachPoint -> !(point is LunarNode && eachPoint is LunarNode) } // 過濾南北交點對沖
            .flatMap { eachPoint ->
              val eachPlanetDeg = positionMap.getValue(eachPoint).lng
              aspects
                .filter { Aspect.getAngles(Aspect.Importance.HIGH).contains(it) } // 只比對 0 , 60 , 90 , 120 , 180 五個度數
                .map { aspect -> aspect to classical.isEffectiveAndScore(point, planetDeg, eachPoint, eachPlanetDeg, aspect) }
                .filter { (_, pair) -> pair.first }
                .map { (aspect, pair) ->
                  Triple(eachPoint , aspect , pair.second)
                }
            }.toSet()
        }?: emptySet()
      }?: emptySet()
  }

  override fun getTitle(locale: Locale): String {
    return "古典占星術 : " + classical.planetOrbsImpl.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return "古典占星術實作 : " + classical.planetOrbsImpl.getDescription(locale)
  }


}
