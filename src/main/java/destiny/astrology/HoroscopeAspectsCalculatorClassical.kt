/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 2:19:45
 */
package destiny.astrology

import destiny.astrology.classical.PointDiameterAlBiruniImpl
import destiny.astrology.classical.PointDiameterLillyImpl

import destiny.astrology.classical.AspectEffectiveClassical
import destiny.astrology.classical.IPointDiameter

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

  /**
   * @param aspects 雖然 classical 只能計算  [Aspect.Importance.HIGH] , 但仍可透過此參數，再進一步過濾要計算的交角
   */
  override fun getPointAspect(point: Point, positionMap: Map<Point, IPos>, points: Collection<Point> , aspects: Collection<Aspect>): Map<Point, Aspect> {

    return if (point is Planet) {
      val planetDeg = positionMap.getValue(point).lng
      points
        .filter { it !== point }
        .flatMap { eachPoint ->
          val eachPlanetDeg = positionMap.getValue(eachPoint).lng
          aspects
            .filter { Aspect.getAngles(Aspect.Importance.HIGH).contains(it) } // 只比對 0 , 60 , 90 , 120 , 180 五個度數
            .filter { classical.isEffective(point, planetDeg, eachPoint, eachPlanetDeg, it) }
            .map { eachPoint to it }
        }.toMap()
    } else {
      // 非行星不計算
      emptyMap()
    }
  }

  override fun getTitle(locale: Locale): String {
    return "古典占星術 : " + classical.planetOrbsImpl.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return "古典占星術實作 : " + classical.planetOrbsImpl.getDescription(locale)
  }


}
