package destiny.core.astrology

import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.astrology.eclipse.ILunarEclipse
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.calendar.GmtJulDay
import destiny.tools.getTitle
import kotlin.time.Duration
import destiny.tools.Lang

data class RetrogradeEvent(val span: RetrogradeSpan) : IStarEventSpan by span {

  override fun getTitle(lang: Lang): String {
    return buildString {
      append(span.star)
      append(" ")
      append(span.phase.getTitle(lang))
    }
  }

  override fun getDescription(lang: Lang): String {
    return " 為期 ${span.duration.inWholeDays}天"
  }
}

data class SolarEclipseSpan(val eclipse: AbstractSolarEclipse,
                            override val fromPos: IZodiacDegree,
                            override val toPos: IZodiacDegree) : IStarEventSpan, ISolarEclipse by eclipse {
  override val star: Star = Planet.SUN
  override val begin: GmtJulDay = eclipse.begin
  override val end: GmtJulDay = eclipse.end

  override fun getTitle(lang: Lang): String {
    return eclipse.solarType.getTitle(lang)
  }

  override fun getDescription(lang: Lang): String {
    return eclipse.duration.toHourMinute(lang)
  }
}



data class LunarEclipseSpan(val eclipse : AbstractLunarEclipse,
                            override val fromPos: IZodiacDegree,
                            override val toPos: IZodiacDegree) : IStarEventSpan, ILunarEclipse by eclipse {
  override val star: Star = Planet.MOON
  override val begin: GmtJulDay = eclipse.begin
  override val end: GmtJulDay = eclipse.end

  override fun getTitle(lang: Lang): String {
    return eclipse.lunarType.getTitle(lang)
  }


  override fun getDescription(lang: Lang): String {
    return eclipse.duration.toHourMinute(lang)
  }
}

fun Duration.toHourMinute(lang: Lang): String {
  return buildString {
    append("為期 ")
    append(toComponents { hours, minutes, _, _ ->
      "${hours}小時 ${minutes}分鐘"
    }
    )
  }
}
