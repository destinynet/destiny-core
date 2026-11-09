/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IAspectCalculator
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 光線傳遞的估算版：「下一個要成形的」取估算日數（容許度 ÷ 相對速度）最短的那顆入相位。
 * 只看盤面時刻的位置與速度，不需要星曆；看不到傳遞者途中被別的星截走（插隊）的情形 —— 要看的話用 [TranslationOfLightEphemerisImpl]
 */
class TranslationOfLightStaticImpl(
  aspectCalculator: IAspectCalculator,
  aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
  allowBetweenApplying: Boolean = false,
) : AbstractTranslationOfLight(aspectCalculator, aspects, allowBetweenApplying) {

  override fun pickApplying(planet: Planet, h: IHoroscopeModel, applying: List<Contact>): Pair<Contact, ApplicationOutcome.Perfected?>? =
    applying.minByOrNull { it.days }?.let { it to null }
}
