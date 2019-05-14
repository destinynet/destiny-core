/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:55:27
 */
package destiny.astrology

import java.io.Serializable
import java.util.*

/**
 * 現代占星術，計算一張星盤中，星體交角列表的實作
 * 內定只計算重要性為「高」的角度 ( [Aspect.Importance.HIGH] )
 * */

class HoroscopeAspectsCalculatorModernBuilder {

  var aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)
  fun aspects(vararg aspect : Aspect) {
    aspects = aspect.toList()
  }
  fun build() : IHoroscopeAspectsCalculator {
    return HoroscopeAspectsCalculatorModern(aspects)
  }
}

fun modernCalculator(block: HoroscopeAspectsCalculatorModernBuilder.() -> Unit = {}): IHoroscopeAspectsCalculator
  = HoroscopeAspectsCalculatorModernBuilder().apply(block).build()


class HoroscopeAspectsCalculatorModern(
  override val aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)) : IHoroscopeAspectsCalculator, Serializable {

  private val modern: AspectEffectiveModern = AspectEffectiveModern()

  /**
   * point 與這些 points 形成哪些 [Aspect]
   */
  override fun getPointAspect(point: Point, positionMap: Map<Point, IPos>, points: Collection<Point>): Map<Point, Aspect> {

    return positionMap[point]?.lng?.let { starDeg ->
      points
        .filter { it !== point }
        .filter { positionMap.containsKey(it) }
        .flatMap { eachPoint ->
          val eachDeg = positionMap.getValue(eachPoint).lng
          aspects.filter { eachAspect -> modern.isEffective(starDeg, eachDeg, eachAspect) }
            .map { eachAspect -> eachPoint to eachAspect }
        }.toMap()
    }?: emptyMap()

  }

  override fun getTitle(locale: Locale): String {
    return "現代占星術"
  }

  override fun getDescription(locale: Locale): String {
    return "現代占星術實作"
  }


}
