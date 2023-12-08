package destiny.core.astrology

import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.astrology.eclipse.ILunarEclipse
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.calendar.GmtJulDay
import destiny.tools.getTitle
import java.util.*
import kotlin.time.Duration

data class RetrogradeEvent(val span: RetrogradeSpan) : IStarEventSpan by span {

  override fun getTitle(locale: Locale): String {
    return buildString {
      append(span.star)
      append(" ")
      append(span.phase.getTitle(locale))
    }
  }

  override fun getDescription(locale: Locale): String {
    return " 為期 ${span.duration.inWholeDays}天"
  }
}

data class SolarEclipseSpan(val eclipse: AbstractSolarEclipse,
                            override val fromPos: IZodiacDegree,
                            override val toPos: IZodiacDegree) : IStarEventSpan, ISolarEclipse by eclipse {
  override val star: Star = Planet.SUN
  override val begin: GmtJulDay = eclipse.begin
  override val end: GmtJulDay = eclipse.end

  override fun getTitle(locale: Locale): String {
    return eclipse.solarType.getTitle(Locale.TAIWAN)
  }

  override fun getDescription(locale: Locale): String {
    return eclipse.duration.toHourMinute(locale)
  }
}



data class LunarEclipseSpan(val eclipse : AbstractLunarEclipse,
                            override val fromPos: IZodiacDegree,
                            override val toPos: IZodiacDegree) : IStarEventSpan, ILunarEclipse by eclipse {
  override val star: Star = Planet.MOON
  override val begin: GmtJulDay = eclipse.begin
  override val end: GmtJulDay = eclipse.end

  override fun getTitle(locale: Locale): String {
    return eclipse.lunarType.getTitle(Locale.TAIWAN)
  }


  override fun getDescription(locale: Locale): String {
    return eclipse.duration.toHourMinute(locale)
  }
}

fun Duration.toHourMinute(locale: Locale): String {
  return buildString {
    append("為期 ")
    append(toComponents { hours, minutes, _, _ ->
      "${hours}小時 ${minutes}分鐘"
    }
    )
  }
}
