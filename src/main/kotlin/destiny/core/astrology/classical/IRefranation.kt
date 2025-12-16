/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.tools.KotlinLogging
import java.io.Serializable

/**
 * Refranation 定義：返回、臨陣脫逃
 * 這是六種 Denials of Perfection 之一 ，定義於此：
 * http://www.skyscript.co.uk/tobyn2.html#ref
 * 當兩顆星正在 apply 某交角，在形成 Perfect 之前，其中一顆轉為逆行
 *
 * 此程式必須能正確判斷以下兩種情形
 * 1. 本星即將 apply 他星，而在 perfect 前，本星逆行，代表自我退縮。
 * 2. 本星即將 apply 他星，而在 perfect 前，他星逆行，代表對方退縮。
 *
 * TODO : 應該加上演算法：如果星體順轉逆（或逆轉順），並且逃離了 aspect 的有效範圍，才是真的「臨陣脫逃」
 *
 * TODO : antiscia / antiscion (映點) 臨陣退縮
 */
interface IRefranation {

  /**
   * return Pair<Point, Aspect>
   *   「先」臨陣脫逃者，是哪顆星
   *   以及，他們原本要形成什麼交角
   */
  fun getResult(horoscope: IHoroscopeModel, planet: Planet, otherPoint: AstroPoint, aspects: Set<Aspect>): Pair<AstroPoint, Aspect>?

  /** 取得重要交角 [Aspect.Importance.HIGH] 的結果 */
  fun getImportantResult(horoscope: IHoroscopeModel, planet: Planet, otherPoint: AstroPoint): Pair<AstroPoint, Aspect>? {
    return getResult(horoscope, planet, otherPoint, Aspect.getAspects(Aspect.Importance.HIGH).toSet())
  }

}



class RefranationImpl(private val aspectCalculator: IAspectCalculator,
                      private val relativeTransitImpl: IRelativeTransit,
                      private val retrogradeImpl: IRetrograde) : IRefranation, Serializable {


  override fun getResult(horoscope: IHoroscopeModel,
                         planet: Planet,
                         otherPoint: AstroPoint,
                         aspects: Set<Aspect>): Pair<AstroPoint, Aspect>? {

    return aspects.asSequence().map { aspect ->
      /** 此兩星正在 apply 哪個交角  */
      aspect to with(aspectCalculator) { horoscope.getAspectType(planet, otherPoint, aspect) }
    }.firstOrNull { (_, type) -> type === IPointAspectPattern.AspectType.APPLYING }
      ?.let { (applyingAspect, _) -> applyingAspect }
      ?.let { applyingAspect ->
        logger.debug("兩星 : {} {} 正在接近此角度 {}", planet, otherPoint, applyingAspect)

        /** 「先」臨陣脫逃者，是誰，這裡強調「先」，因為有可能在 Perfect 交角之前，雙方都臨陣脫逃。 */
        val refranator: AstroPoint? = relativeTransitImpl.getRelativeTransit(
          planet,
          otherPoint as Star,
          applyingAspect.degree,
          horoscope.gmtJulDay,
          true,
          StarTypeOptions.MEAN
        )?.let { perfectAspectGmt1 ->
          if (applyingAspect.degree == 0.0 || applyingAspect.degree == 180.0) {
            // 衝/合 只會有一個時刻，不用算「補角」
            perfectAspectGmt1
          } else {
            // 額外計算 「補角」（360-degree）的時刻
            relativeTransitImpl.getRelativeTransit(
              planet, otherPoint, 360 - applyingAspect.degree,
              horoscope.gmtJulDay, true, StarTypeOptions.MEAN
            )!!.let { perfectAspectGmt2 ->
              if (perfectAspectGmt1 > perfectAspectGmt2)
                perfectAspectGmt2
              else
                perfectAspectGmt1
            }
          }
        }?.let { perfectAspectGmt ->
          perfectAspectGmt.let {
            logger.debug("準確形成交角 ({}) 度 於 {}", applyingAspect.degree, perfectAspectGmt)

            val nextStationaryGmtJulDay1 = retrogradeImpl.getNextStationary(planet, horoscope.gmtJulDay, true)
            val nextStationaryGmtJulDay2 = retrogradeImpl.getNextStationary(otherPoint, horoscope.gmtJulDay, true)


            // 如果這兩個 GMT 時間，有任何一個早於 (prior to) perfectAspectGmt，則代表有一顆星臨陣脫逃
            when {
              nextStationaryGmtJulDay1 < perfectAspectGmt -> planet
              nextStationaryGmtJulDay2 < perfectAspectGmt -> otherPoint
              else -> null // 沒有臨陣脫逃者
            }
          }
        }

        refranator?.let {
          it to applyingAspect
        }
      }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }

}
