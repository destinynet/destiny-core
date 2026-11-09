/**
 * Created by smallufo on 2026-11-03.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IAspectCalculator
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet

/**
 * 光線傳遞的星曆版：「下一個要成形的」由入相位引擎 [applicationOutcome] 往後追蹤判定 ——
 * 傳遞者與它的入相位結局必須是 [ApplicationOutcome.Perfected]（途中被插隊、撲空、退縮，光就沒送到），取最先成形的那顆。
 * 本命存表、prompt 用這一版（判準與入相位引擎同一份 [OutcomeOptions]）
 */
class TranslationOfLightEphemerisImpl(
  aspectCalculator: IAspectCalculator,
  private val applicationOutcome: IApplicationOutcome,
  aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
  allowBetweenApplying: Boolean = false,
) : AbstractTranslationOfLight(aspectCalculator, aspects, allowBetweenApplying) {

  override fun pickApplying(planet: Planet, h: IHoroscopeModel, applying: List<Contact>): Pair<Contact, ApplicationOutcome.Perfected?>? =
    applying.mapNotNull { c ->
      (applicationOutcome.getOutcome(h, planet, c.other) as? ApplicationOutcome.Perfected)?.let { c to it }
    }.minByOrNull { (_, perfected) -> perfected.eventDays }
}
