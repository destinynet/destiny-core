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
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

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
