/**
 * Created by smallufo on 2026-10-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.IPointAspectPattern.AspectType.APPLYING
import destiny.core.astrology.IPointAspectPattern.AspectType.SEPARATING
import kotlin.math.abs

/**
 * Translation of Light。定義（William Lilly, Christian Astrology）：
 * 1. 傳遞者比兩個徵象星都快
 * 2. 它在兩者的容許度內（古典：兩星光芒半徑相加），與一個是出相位、與另一個是入相位
 * 3. 出相位那顆是它「最近剛離開」的，入相位那顆是它「下一個要成形」的 —— 中間若還有別的星先成形，光就被那顆截走（prohibition）
 * 4. 兩個徵象星彼此若已經是入相位，自己就會成形，不需要中間人，不算傳遞（[allowBetweenApplying] 可放寬）
 *
 * 2026-10-31 取代 2014 年的舊版（依 IBesieged（現 [destiny.core.astrology.IAspectNeighbors]） 找前後兩顆星），舊版的問題：
 * - 前後兩顆星由 IBesieged 依「時間上前一次、後一次成形的交角」找，不看容許度；找的時候不含合相，
 *   之後判斷出／入相位時卻含合相 —— 傳遞者剛離開合相、正要去三分的情形會被漏掉
 * - 只看時間上最近的那兩顆；其中一顆若比傳遞者快（例：太陽前後最近的是金星），就直接判定不成立，
 *   不會再往後找比它慢的那顆。七顆古典行星的本命盤裡，這是兩版差異的大宗
 * - 不排除「兩個徵象星彼此本來就入相位」（例：火星六合土星 0.6° 入相位，太陽夾在中間也被算成傳遞）
 *
 * 本版只在當下的容許度內找，前後順序依「離成形還要／已經多久」（容許度 ÷ 兩星的相對速度）排；
 * 結果直接帶出兩邊的 [IPointAspectPattern]（相位、容許度、出入相位）
 *
 * 「下一個要成形的」是哪一顆，由子類別決定（[pickApplying]）：
 * - [TranslationOfLightStaticImpl]：估算（容許度 ÷ 相對速度），只要盤面時刻，不需要星曆；看不到傳遞者途中被別的星截走的情形
 * - [TranslationOfLightEphemerisImpl]：由入相位引擎往後追蹤，傳遞者的入相位結局必須是 [ApplicationOutcome.Perfected]
 */
abstract class AbstractTranslationOfLight(
  protected val aspectCalculator: IAspectCalculator,
  protected val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
  private val allowBetweenApplying: Boolean = false,
  /** 相位要依星座也成立才算（[fitsSigns]），見 [OutcomeOptions.signBoundAspects] */
  private val signBound: Boolean = false,
) : ITranslationOfLight {

  private fun pattern(h: IHoroscopeModel, a: Planet, b: Planet): IPointAspectPattern? =
    with(aspectCalculator) { h.getAspectPattern(setOf(a, b), aspects) }?.takeIf { !signBound || it.fitsSigns(h) }

  /** 在容許度內、比傳遞者慢的一顆古典行星；[days] ＝ 離成形（入相位）或離開成形（出相位）的估算日數 ＝ 容許度 ÷ 相對速度 */
  protected data class Contact(val other: Planet, val pattern: IPointAspectPattern, val days: Double)

  /** 從入相位的 [applying] 中挑「下一個要成形的」；連帶回傳引擎追蹤到的成形（估算版為 null）。都不成立則 null */
  protected abstract fun pickApplying(planet: Planet, h: IHoroscopeModel, applying: List<Contact>): Pair<Contact, ApplicationOutcome.Perfected?>?

  override fun getResult(planet: Planet, h: IHoroscopeModel): TranslationOfLight? {
    if (planet !in Planet.classicalSet) return null
    val speedOf = { p: Planet -> h.getStarPosition(p)?.speedLng }
    val speed = speedOf(planet) ?: return null

    val contacts = Planet.classicalList.filter { it !== planet }.mapNotNull { other ->
      val otherSpeed = speedOf(other) ?: return@mapNotNull null
      if (abs(otherSpeed) >= abs(speed)) return@mapNotNull null
      pattern(h, planet, other)
        ?.takeIf { it.aspectType != null }
        ?.let { Contact(other, it, it.orb / abs(speed - otherSpeed)) }
    }

    val from = contacts.filter { it.pattern.aspectType == SEPARATING }.minByOrNull { it.days } ?: return null
    val (to, arrival) = pickApplying(planet, h, contacts.filter { it.pattern.aspectType == APPLYING }) ?: return null

    val between = pattern(h, from.other, to.other)
    if (between?.aspectType == APPLYING && !allowBetweenApplying) return null

    return TranslationOfLight(planet, separating = from.pattern, applying = to.pattern, between = between, arrival = arrival)
  }
}
