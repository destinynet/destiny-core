package destiny.core.astrology

import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.calendar.GmtJulDay
import destiny.tools.getTitle
import java.util.*

data class RetrogradeEvent(val span: RetrogradeSpan) : IStarEventSpan by span {
  override val title: String
    get() {
      return buildString {
        append(span.star)
        append(" ")
        val name = when(span.phase) {
          RetrogradePhase.PREPARING    -> "準備逆行"
          RetrogradePhase.RETROGRADING -> "逆行"
          RetrogradePhase.LEAVING      -> "結束逆行"
        }
        append(name)
      }
    }
  override val description: String = " 為期 ${span.duration.inWholeDays}天"
}


data class VocEvent(val voc: Misc.VoidCourse) : IStarEventSpan by voc {
  override val title: String = "${voc.star} 空亡"
  override val description: String = " 為期 ${voc.duration.inWholeHours}小時"
}

data class SolarEclipseEvent(val eclipse: AbstractSolarEclipse,
                             override val fromPos: IZodiacDegree,
                             override val toPos: IZodiacDegree) : IStarEventSpan {
  override val star: Star = Planet.SUN
  override val begin: GmtJulDay = eclipse.begin
  override val end: GmtJulDay = eclipse.end

  override val title: String
    get() = eclipse.solarType.getTitle(Locale.TAIWAN)

  override val description: String = " 為期 ${eclipse.duration.inWholeMinutes} 分鐘"
}

data class LunarEclipseEvent(val eclipse : AbstractLunarEclipse,
                             override val fromPos: IZodiacDegree,
                             override val toPos: IZodiacDegree) : IStarEventSpan {
  override val star: Star = Planet.MOON
  override val begin: GmtJulDay = eclipse.begin
  override val end: GmtJulDay = eclipse.end

  override val title: String
    get() = eclipse.lunarType.getTitle(Locale.TAIWAN)

  override val description: String = " 為期 ${eclipse.duration.inWholeMinutes} 分鐘"
}
