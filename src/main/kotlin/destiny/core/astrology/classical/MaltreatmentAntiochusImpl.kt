/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.classical.MaltreatmentOptions.ConditionType
import kotlin.math.abs

/**
 * Antiochus／Porphyry 的受剋（與對應的受扶）條件：被居上、對沖、包夾、貼合、放射光線。只看盤面時刻的位置，不需要星曆。
 *
 * 居上的基準（星座或度數）由注入的 [overcomingImpl] 決定、包夾由 [besiegementImpl]；其餘細節見 [MaltreatmentOptions]。
 * 施予者本身不會被自己處置；包夾要兩顆施予者都不是這顆星
 */
class MaltreatmentAntiochusImpl(
  private val besiegementImpl: IBesiegement,
  private val overcomingImpl: IOvercoming = OvercomingSignImpl(),
  val options: MaltreatmentOptions = MaltreatmentOptions.BY_SIGN,
) : IMaltreatment {

  override fun getMaltreatment(h: IHoroscopeModel, planet: Planet, polarity: Maltreatment.Polarity): Maltreatment? {
    val deg = h.positionMap[planet]?.lngDeg ?: return null
    val malefic = polarity == Maltreatment.Polarity.MALTREATMENT
    val sources = (if (malefic) options.malefics else options.benefics) - planet
    val on = if (malefic) options.maleficConditions else options.beneficConditions

    val conditions = buildList<Maltreatment.Condition> {
      for (src in sources) {
        val srcDeg = h.positionMap[src]?.lngDeg ?: continue

        if (ConditionType.OVERCOME in on) {
          overcomingImpl.getOvercoming(src, srcDeg, planet, deg)
            ?.takeIf { it.superior === src && it.aspect in (if (malefic) options.maleficOvercoming else options.beneficOvercoming) }
            ?.let { add(Maltreatment.Condition.Overcome(it)) }
        }

        val angle = deg.getAngle(srcDeg)  // 0–180

        val opposed = ConditionType.OPPOSED in on && run {
          val orb = abs(180 - angle)
          (options.oppositionOrb?.let { orb <= it } ?: (srcDeg.sign.placeFrom(deg.sign) == 7))
            .also { if (it) add(Maltreatment.Condition.Opposed(src, orb)) }
        }

        if (ConditionType.ADHERENCE in on && angle <= options.adherenceOrb &&
          (!options.adherenceSameSign || srcDeg.sign == deg.sign)) {
          add(Maltreatment.Condition.Adherence(src, angle))
        }

        if (ConditionType.RAY in on) {
          // 已列為對沖的，不再以對沖的光線重複一條（對沖沒有方向之分）
          val rays = (if (malefic) options.maleficRays else options.beneficRays).let { if (opposed) it - Aspect.OPPOSITION else it }
          hurledRay(src, srcDeg, deg, rays)?.let { add(it) }
        }
      }

      if (ConditionType.BESIEGED in on && sources.size == 2) {
        besiegementImpl.getBesiegement(h, planet, sources)?.let { add(Maltreatment.Condition.Besieged(it)) }
      }
    }
    return conditions.takeIf { it.isNotEmpty() }?.let { Maltreatment(planet, polarity, it) }
  }

  /**
   * [src] 以 [aspects] 之一的光線打中 [target]（[MaltreatmentOptions.rayOrb] 以內）。
   * dexter：[src] 在黃道後方（[target] 順黃道走 aspect 度就到 [src]），光線逆黃道方向投射回 [target]
   */
  private fun hurledRay(src: Planet, srcDeg: ZodiacDegree, target: ZodiacDegree, aspects: Set<Aspect>): Maltreatment.Condition.RayHurled? {
    val ahead = srcDeg.aheadOf(target)   // target → src 順黃道的距離
    return aspects.firstNotNullOfOrNull { aspect ->
      val dexterOrb = abs(ahead - aspect.degree)
      val sinisterOrb = abs((360 - ahead) - aspect.degree)
      val dexter = dexterOrb <= options.rayOrb && options.rayDirection != RayDirection.SINISTER
      val sinister = sinisterOrb <= options.rayOrb && options.rayDirection != RayDirection.DEXTER
      when {
        dexter && (!sinister || dexterOrb <= sinisterOrb) -> Maltreatment.Condition.RayHurled(src, aspect, dexterOrb, dexter = true)
        sinister -> Maltreatment.Condition.RayHurled(src, aspect, sinisterOrb, dexter = false)
        else -> null
      }
    }
  }
}
