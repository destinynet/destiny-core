/**
 * Created by smallufo on 2025-06-23.
 */
package destiny.tools.serializers

import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.astrology.eclipse.IEclipse
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
