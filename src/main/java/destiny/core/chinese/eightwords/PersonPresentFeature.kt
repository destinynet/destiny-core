/**
 * Created by smallufo on 2021-08-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable

@Serializable
data class PersonPresentConfig(val personContextConfig: EightWordsPersonConfig = EightWordsPersonConfig(),
                               val viewGmt: GmtJulDay = GmtJulDay.now()) : java.io.Serializable

@DestinyMarker
class PersonPresentConfigBuilder : Builder<PersonPresentConfig> {

  var personContextConfig: EightWordsPersonConfig = EightWordsPersonConfig()

  fun personContextConfig(block: PersonConfigBuilder.() -> Unit = {}) {
    personContextConfig = PersonConfigBuilder.ewPersonConfig(block)
  }

  var viewGmt: GmtJulDay = GmtJulDay.now()

  override fun build(): PersonPresentConfig {
    return PersonPresentConfig(personContextConfig, viewGmt)
  }

  companion object {
    fun personPresent(block: PersonPresentConfigBuilder.() -> Unit = {}) : PersonPresentConfig {
      return PersonPresentConfigBuilder().apply(block).build()
    }
  }
}


class PersonPresentFeature : PersonFeature<PersonPresentConfig , IPersonPresentModel> {
  override val key: String = "personPresent"

  override val defaultConfig: PersonPresentConfig = PersonPresentConfig()

  override fun getModel(
    gmtJulDay: GmtJulDay,
    loc: ILocation,
    gender: Gender,
    name: String?,
    place: String?,
    config: PersonPresentConfig
  ): IPersonPresentModel {
    TODO("Not yet implemented")
  }
}
