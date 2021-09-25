/**
 * Created by smallufo on 2021-09-26.
 */
package destiny.core.iching.divine

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YangBlade
import destiny.tools.Builder
import destiny.tools.Feature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

@Serializable
data class DivineConfig(val settingsOfStemBranch: SettingsOfStemBranch = SettingsOfStemBranch.GingFang,
                        val hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang,
                        val tianyi: Tianyi = Tianyi.Authorized,
                        val yangBlade: YangBlade = YangBlade.NextBliss,
                        val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable

class DivineConfigBuilder : Builder<DivineConfig> {

  var settingsOfStemBranch: SettingsOfStemBranch = SettingsOfStemBranch.GingFang

  var hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang

  var tianyi: Tianyi = Tianyi.Authorized

  var yangBlade: YangBlade = YangBlade.NextBliss

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}) {
    this.eightWordsConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  override fun build(): DivineConfig {
    return DivineConfig(settingsOfStemBranch, hiddenEnergy, tianyi, yangBlade, eightWordsConfig)
  }

  companion object {
    fun divineConfig(block: DivineConfigBuilder.() -> Unit = {}) : DivineConfig {
      return DivineConfigBuilder().apply(block).build()
    }
  }
}


class DivineCoreFeature : Feature<DivineConfig , ICombinedWithMetaNameDayMonth>{
  override val key: String = "divineCoreFeature"

  override val defaultConfig: DivineConfig = DivineConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: DivineConfig): ICombinedWithMetaNameDayMonth {
    TODO("Not yet implemented")
  }

  override fun getModel(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: DivineConfig): ICombinedWithMetaNameDayMonth {
    TODO("Not yet implemented")
  }
}
