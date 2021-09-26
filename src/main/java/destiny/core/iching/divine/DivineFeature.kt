/**
 * Created by smallufo on 2021-09-27.
 */
package destiny.core.iching.divine

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable

@Serializable
data class DivineConfig(val traditionalConfig: DivineTraditionalConfig = DivineTraditionalConfig(),
                        val approach: DivineApproach? = null,
                        val place: String? = null): java.io.Serializable

@DestinyMarker
class DivineConfigBuilder : Builder<DivineConfig> {

  var traditionalConfig = DivineTraditionalConfig()
  fun tradConfig(block : DivineTraditionalConfigBuilder.() -> Unit = {}) {
    traditionalConfig = DivineTraditionalConfigBuilder.divineTraditionalConfig(block)
  }

  var approach : DivineApproach? = null

  var place:String? = null

  override fun build(): DivineConfig {
    return DivineConfig(traditionalConfig, approach, place)
  }

  companion object {
    fun divineConfig(block : DivineConfigBuilder.() -> Unit = {}) : DivineConfig {
      return DivineConfigBuilder().apply(block).build()
    }
  }
}

class DivineFeature(private val divineTraditionalFeature: DivineTraditionalFeature) : AbstractCachedPersonFeature<DivineConfig, ICombinedFull>(){
  override val key: String = "divineFeature"

  override val defaultConfig: DivineConfig = DivineConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: DivineConfig): ICombinedFull {

    val embedded: ICombinedWithMetaNameDayMonth = divineTraditionalFeature.getModel(gmtJulDay, loc, config.traditionalConfig)


    TODO("Not yet implemented")
  }

}
