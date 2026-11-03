/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.MaltreatmentOptions.ConditionType
import kotlin.math.abs

/**
 * Antiochus／Porphyry 的受剋（與對應的受扶）條件：被居上、對沖、包夾、貼合、放射光線，加上 Abu Ma'shar 的「離開一顆、迎上另一顆」。只看盤面時刻的位置與速度，不需要星曆。
 *
 * 居上的基準（星座或度數）由注入的 [overcomingImpl] 決定，必須與 [MaltreatmentOptions.overcomingBasis] 相同；包夾由 [besiegementImpl]；其餘細節見 [MaltreatmentOptions]。
 * 施予者本身不會被自己處置；包夾要兩顆施予者都不是這顆星
 */
class MaltreatmentAntiochusImpl(
  private val besiegementImpl: IBesiegement,
  private val overcomingImpl: IOvercoming = OvercomingSignImpl(),
  val options: MaltreatmentOptions = MaltreatmentOptions.BY_SIGN,
) : IMaltreatment {

  init {
    require(overcomingImpl.basis == options.overcomingBasis) {
      "居上的基準不一致：options.overcomingBasis=${options.overcomingBasis}，注入的 IOvercoming 是 ${overcomingImpl.basis}"
    }
  }

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
      if (ConditionType.SEQUENCE in on && sources.size == 2) {
        sequence(h, planet, deg, sources)?.let { add(it) }
      }
    }
    return conditions.takeIf { it.isNotEmpty() }?.let { Maltreatment(planet, polarity, it) }
  }

  /** 黃經速度；速度不明時用平均日速 */
  private fun speed(h: IHoroscopeModel, p: Planet): Double? = h.getStarPosition(p)?.speedLng ?: AverageDailyMotionMap.getAvgDailySpeed(p)

  /**
   * 離開一顆、迎上另一顆：在其餘古典行星裡，找 [planet] 容許度內（[MaltreatmentOptions.aspectEffective]）最近一次離開與下一個迎上的相位，
   * 兩者分別是 [sources] 裡不同的兩顆才成立。離合以兩星的黃經速度線性外推（只看盤面附近幾天，不需要星曆）；
   * [MaltreatmentOptions.sequenceSameSign]：兩次準確時這顆星都還在目前的星座
   */
  private fun sequence(h: IHoroscopeModel, planet: Planet, deg: ZodiacDegree, sources: Set<Planet>): Maltreatment.Condition.Sequence? {
    val v = speed(h, planet) ?: return null
    data class Hit(val other: Planet, val aspect: Aspect, val orb: Double, val days: Double)
    val hits = (Planet.classicalList - planet).flatMap { o ->
      val oDeg = h.positionMap[o]?.lngDeg ?: return@flatMap emptyList()
      val rel = v - (speed(h, o) ?: return@flatMap emptyList())
      if (rel == 0.0) return@flatMap emptyList()
      val r = (deg.value - oDeg.value).mod(360.0)
      options.sequenceAspects.filter { options.aspectEffective.isEffective(planet, deg, o, oDeg, it) }.map { a ->
        val targets = if (a.degree == 0.0 || a.degree == 180.0) listOf(a.degree) else listOf(a.degree, 360 - a.degree)
        val signed = targets.map { t -> (r - t + 540).mod(360.0) - 180 }.minBy { abs(it) }
        // signed 以 rel 的速度變化：準確的時刻是 -signed / rel（負數 ＝ 已經過去 ＝ 離開）
        Hit(o, a, abs(signed), -signed / rel)
      }
    }
    val sep = hits.filter { it.days < 0 }.maxByOrNull { it.days } ?: return null
    val app = hits.filter { it.days > 0 }.minByOrNull { it.days } ?: return null
    if (sep.other == app.other || sep.other !in sources || app.other !in sources) return null
    if (options.sequenceSameSign) {
      val signAt = { days: Double -> (deg.value + v * days).toZodiacDegree().sign }
      if (signAt(sep.days) != deg.sign || signAt(app.days) != deg.sign) return null
    }
    return Maltreatment.Condition.Sequence(sep.other, sep.aspect, sep.orb, app.other, app.aspect, app.orb)
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
