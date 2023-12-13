/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import mu.KotlinLogging
import java.io.Serializable
import kotlin.math.abs

interface ITranslationOfLight {

  /**
   * 此星盤中 , planet 是否有傳遞光線 , 若有的話 , 從哪顆星 (triple.first) 傳遞到 哪顆星 (triple.second) , 以及 , 這兩顆星是否有形成什麼交角 (可能為null)
   */
  fun getResult(planet: Planet, h: IHoroscopeModel): Triple<Planet, Planet, IPointAspectPattern.Type?>?
}


/**
 * Translation of Light 的定義以及演算法 :
 * 根據此文的定義
 * https://destiny.to/ubbthreads/ubbthreads.php/posts/6999
 * 「(1)某行星運行速度快於兩個徵象星,
 * 且(2)該行星與一個徵象星有出相位, 與另一個有入相位,
 *   (3)而這兩個徵象星彼此是出相位」
 *
 * 比對各家說法
 * https://www.astrologyweekly.com/special-horary/translation-of-light.php
 *
 *
 * 目前演算法，先求出「夾」此星體的其他兩顆星體
 * 再比較三顆星的速度 , 如果中間的 planet 比較快 (至此已經符合第一個條件)
 *
 * 再比較 planet 與 前後兩顆星是否分別形成入相位以及出相位
 *
 * 最後，包夾此行星的兩顆星，是否要形成「出相位」，我認為「不一定」，因為意義不同，應該可以設為選項
 */
class TranslationOfLightImpl(private val aspectsCalculator: IAspectsCalculator,
                             private val besiegedImpl: IBesieged) : ITranslationOfLight, Serializable {

  override fun getResult(planet: Planet, h: IHoroscopeModel): Triple<Planet, Planet, IPointAspectPattern.Type?>? {
    // 不考慮合相的交角
    val aspects = setOf(Aspect.SEXTILE, Aspect.SQUARE, Aspect.TRINE, Aspect.OPPOSITION)

    val (p1, p2) = besiegedImpl.getBesiegingPlanets(planet, h.gmtJulDay, true, aspects).iterator().let {
      it.next() to it.next()
    }

    return h.getStarPosition(planet)?.speedLng?.let { planetSpeed ->
      h.getStarPosition(p1)?.speedLng?.let { p1Speed ->
        h.getStarPosition(p2)?.speedLng?.takeIf { p2Speed ->
          // 中間被夾的星，速度比較快
          abs(planetSpeed) > abs(p1Speed) && abs(planetSpeed) > abs(p2Speed)
        }?.let { _ ->
          //2008-11-05 新增規則： 第三顆星需與這兩顆星分別形成出相位以及入相位 , 從 出相位 傳遞光線 到 入相位

          with(aspectsCalculator) {
            mapOf(
              p1 to h.getAspectType(planet, p1, Aspect.getAspects(Aspect.Importance.HIGH)),
              p2 to h.getAspectType(planet, p2, Aspect.getAspects(Aspect.Importance.HIGH))
            ).takeIf { map -> map.values.all { it != null } }?.mapValues { (_, maybeAspectType) -> maybeAspectType!! }?.takeIf { map ->
                // 分別形成 出相位 以及 入相位
                map.values.toSet() == setOf(IPointAspectPattern.Type.APPLYING, IPointAspectPattern.Type.SEPARATING)
              }?.let { map: Map<Planet, IPointAspectPattern.Type> ->

                // p1 , p2 不一定有交角
                val typeOptional: IPointAspectPattern.Type? = h.getAspectType(p1, p2, Aspect.getAspects(Aspect.Importance.HIGH))

                // 出相位
                val separatingPoint = map.keys.first { p -> map.getValue(p) === IPointAspectPattern.Type.SEPARATING }
                // 入相位
                val applyingPoint = map.keys.minus(separatingPoint).first()

                Triple(separatingPoint, applyingPoint, typeOptional)
              }
          }

        }
      }
    }
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
