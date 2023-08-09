package destiny.core.astrology

import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.calendar.GmtJulDay
import destiny.tools.getTitle
import java.util.*
import kotlin.time.Duration

data class RetrogradeEvent(val span: RetrogradeSpan) : IStarEventSpan by span {

  override fun getTitle(locale: Locale): String {
    return buildString {
      append(span.star)
      append(" ")
      val name = when (span.phase) {
        RetrogradePhase.PREPARING    -> "準備逆行"
        RetrogradePhase.RETROGRADING -> "逆行"
        RetrogradePhase.LEAVING      -> "結束逆行"
      }
      append(name)
    }
  }

  override fun getDescription(locale: Locale): String {
    return " 為期 ${span.duration.inWholeDays}天"
  }
}


data class VocEvent(val voc: Misc.VoidCourse) : IStarEventSpan by voc {

  override fun getTitle(locale: Locale): String {
    return "${voc.star} 空亡"
  }

  override fun getDescription(locale: Locale): String {
    return buildString {
      append("為期 ")
      if (voc.duration.inWholeHours > 0) {
        append(voc.duration.inWholeHours).append("小時")
      } else {
        append(voc.duration.inWholeMinutes).append("分鐘")
      }
    }
  }
}

data class SolarEclipseEvent(val eclipse: AbstractSolarEclipse,
                             override val fromPos: IZodiacDegree,
                             override val toPos: IZodiacDegree) : IStarEventSpan {
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



data class LunarEclipseEvent(val eclipse : AbstractLunarEclipse,
                             override val fromPos: IZodiacDegree,
                             override val toPos: IZodiacDegree) : IStarEventSpan {
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
