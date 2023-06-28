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
data class PersonHoroscopeConfig(val horoscopeConfig: HoroscopeConfig = HoroscopeConfig(),
                                 val gender:Gender = Gender.男,
                                 val name : String? = null): java.io.Serializable

@DestinyMarker
class PersonHoroscopeConfigBuilder : Builder<PersonHoroscopeConfig> {

  var horoscopeConfig: HoroscopeConfig = HoroscopeConfig()
  fun horoscope(block : HoroscopeConfigBuilder.() -> Unit = {}) {
    this.horoscopeConfig = HoroscopeConfigBuilder.horoscope(block)
  }

  var gender: Gender = Gender.男
  var name: String? = null

  override fun build(): PersonHoroscopeConfig {
    return PersonHoroscopeConfig(horoscopeConfig, gender, name)
  }

  companion object {
    fun personHoroscope(block: PersonHoroscopeConfigBuilder.() -> Unit = {}): PersonHoroscopeConfig {
      return PersonHoroscopeConfigBuilder().apply(block).build()
    }
  }
}

@Named
class PersonHoroscopeFeature(private val horoscopeFeature: Feature<HoroscopeConfig, IHoroscopeModel>) : AbstractCachedPersonFeature<PersonHoroscopeConfig, IPersonHoroscopeModel>() {

  override val key: String = "personHoroscope"

  override val defaultConfig: PersonHoroscopeConfig = PersonHoroscopeConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: PersonHoroscopeConfig): IPersonHoroscopeModel {
    val horoscopeModel = horoscopeFeature.getModel(gmtJulDay, loc, config.horoscopeConfig)
    return PersonHoroscopeModel(horoscopeModel, gender, name)
  }
}
