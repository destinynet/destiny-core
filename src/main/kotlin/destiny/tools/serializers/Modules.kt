/**
 * Created by smallufo on 2025-06-23.
 */
package destiny.tools.serializers

import destiny.core.astrology.*
import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.astrology.prediction.IReturnDto
import destiny.core.astrology.prediction.ISolarArcModel
import destiny.core.astrology.prediction.ReturnDto
import destiny.core.astrology.prediction.SolarArcModel
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.time.ZoneId

fun SerializersModuleBuilder.eclipseModule() {
  polymorphic(IEclipse::class) {
    subclass(AbstractSolarEclipse.SolarEclipsePartial::class)
    subclass(AbstractSolarEclipse.SolarEclipseTotal::class)
    subclass(AbstractSolarEclipse.SolarEclipseTotalCentered::class)
    subclass(AbstractSolarEclipse.SolarEclipseAnnular::class)
    subclass(AbstractSolarEclipse.SolarEclipseAnnularCentered::class)
    subclass(AbstractSolarEclipse.SolarEclipseHybrid::class)
    subclass(AbstractLunarEclipse.LunarEclipsePenumbra::class)
    subclass(AbstractLunarEclipse.LunarEclipsePartial::class)
    subclass(AbstractLunarEclipse.LunarEclipseTotal::class)
  }
}

fun SerializersModuleBuilder.astrologyModule() {
  polymorphic(IHoroscopeDto::class) {
    subclass(HoroscopeDto::class)
  }
  polymorphic(IPersonHoroscopeDto::class) {
    subclass(Natal::class)
  }
  polymorphic(IReturnDto::class) {
    subclass(ReturnDto::class)
  }
  polymorphic(ISynastryAspect::class) {
    subclass(SynastryAspect::class)
  }
  polymorphic(ISolarArcModel::class) {
    subclass(SolarArcModel::class)
  }
  polymorphic(IZodiacDegree::class) {
    subclass(ZodiacDegree::class)
  }
  polymorphic(IPos::class) {
    subclass(StarPosition::class)
  }
  polymorphic(IPointAspectPattern::class) {
    subclass(PointAspectPattern::class)
  }
  eclipseModule()
}

fun SerializersModuleBuilder.astroTimeLine() {
  polymorphic(IReturnDto::class) {
    subclass(ReturnDto::class)
  }
  polymorphic(ITimeLineEventsModel::class) {
    subclass(TimeLineEventsModel::class)
  }
  polymorphic(ITimeLineWithUserEventsModel::class) {
    subclass(TimeLineWithUserEventsModel::class)
  }

}


object JsonProvider {
  fun getJson(zoneId: ZoneId): Json {
    val julDayResolver = JulDayResolver1582CutoverImpl()
    val gmtJulDayLmtSerializer = GmtJulDayLocalDateTimeSerializer(zoneId, julDayResolver)
    val gmtJulDayDateSerializer = GmtJulDayLocalDateSerializer(zoneId, julDayResolver)

    return Json {
      prettyPrint = false
      serializersModule = SerializersModule {
        contextual(GmtJulDay::class, gmtJulDayLmtSerializer)
        contextual(ITimeLineEvent::class, ITimeLineEventSerializer(gmtJulDayLmtSerializer, gmtJulDayDateSerializer))
        astrologyModule()
        astroTimeLine()
      }
    }
  }
}
