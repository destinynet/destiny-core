/**
 * Created by smallufo on 2023-08-06.
 */
package destiny.core.calendar

import destiny.tools.getTitle
import destiny.tools.Lang


data class SolarTermsEvent(override val begin: GmtJulDay,
                           /** 節氣  */
                           val solarTerms: SolarTerms) : IEvent {

  override fun getTitle(lang: Lang): String {
    return solarTerms.getTitle(lang)
  }
}

interface ISolarTermsSpan : IEventSpan {
  val solarTerms: SolarTerms
}

data class SolarTermsSpan(
  override val solarTerms: SolarTerms,
  override val begin: GmtJulDay,
  override val end : GmtJulDay) : ISolarTermsSpan {
  override fun getTitle(lang: Lang): String {
    return solarTerms.getTitle(lang)
  }
}
