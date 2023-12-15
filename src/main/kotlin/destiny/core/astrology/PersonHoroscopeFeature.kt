/**
 * Created by smallufo on 2021-11-01.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import jakarta.inject.Named
import kotlinx.serialization.Serializable



@Serializable
data class PersonHoroscopeConfig(
  override val horoscopeConfig: HoroscopeConfig = HoroscopeConfig(),
  override var gender: Gender = Gender.男,
  override var name: String? = null) : IPersonHoroscopeConfig, IHoroscopeConfig by horoscopeConfig

context(IHoroscopeConfig)
@DestinyMarker
class PersonHoroscopeConfigBuilder : Builder<PersonHoroscopeConfig> {
  var gender: Gender = Gender.男
  var name: String? = null

  override fun build(): PersonHoroscopeConfig {
    return PersonHoroscopeConfig(horoscopeConfig, gender, name)
  }

  companion object {
    context(IHoroscopeConfig)
    fun personHoroscope(block: PersonHoroscopeConfigBuilder.() -> Unit = {}): PersonHoroscopeConfig {
      return PersonHoroscopeConfigBuilder().apply(block).build()
    }
  }
}

@Named
class PersonHoroscopeFeature(private val horoscopeFeature: Feature<IHoroscopeConfig, IHoroscopeModel>) : AbstractCachedPersonFeature<IPersonHoroscopeConfig, IPersonHoroscopeModel>() {

  override val key: String = "personHoroscope"

  override val defaultConfig: IPersonHoroscopeConfig = PersonHoroscopeConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: IPersonHoroscopeConfig): IPersonHoroscopeModel {
    val horoscopeModel = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
    return PersonHoroscopeModel(horoscopeModel, gender, name)
  }
}
