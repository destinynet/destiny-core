/**
 * @author smallufo
 * Created on 2008/6/19 at 上午 1:55:27
 */
package destiny.astrology

import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

/** 現代占星術，計算一張星盤中，星體交角列表的實作  */
class HoroscopeAspectsCalculatorModern : IHoroscopeAspectsCalculator, Serializable {

  private val modern: AspectEffectiveModern = AspectEffectiveModern()

  private val logger = LoggerFactory.getLogger(javaClass)

  /** 現代占星術，內定只計算重要性為「高」的角度  */
  private var aspects: Collection<Aspect> = Aspect.getAngles(Aspect.Importance.HIGH)

  /** 設定要計算哪些角度  */
  fun setAspects(aspects: Collection<Aspect>) {
    this.aspects = aspects
  }

  override fun getPointAspect(point: Point, horoscope: Horoscope, points: Collection<Point>): Map<Point, Aspect> {

    val starDeg = horoscope.getPositionWithAzimuth(point).lng

    return points
      .filter { it !== point }
      .flatMap { eachPoint ->
        val eachDeg = horoscope.getPositionWithAzimuth(eachPoint).lng
        aspects.filter { eachAspect -> modern.isEffective(starDeg, eachDeg, eachAspect) }
          .map { eachAspect -> eachPoint to eachAspect }
      }.toMap()

  }

  override fun getTitle(locale: Locale): String {
    return "現代占星術"
  }

  override fun getDescription(locale: Locale): String {
    return "現代占星術實作"
  }


}
