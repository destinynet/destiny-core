/**
 * Created by smallufo on 2021-09-26.
 */
package destiny.core.iching.divine

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.chinese.*
import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.converters.IHexagramSerializer
import destiny.tools.converters.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.*
import javax.inject.Named

@Serializable
data class DivineTraditionalConfig(@Serializable(with = IHexagramSerializer::class)
                                   val src: IHexagram = Hexagram.乾,
                                   @Serializable(with = IHexagramSerializer::class)
                                   val dst: IHexagram = Hexagram.乾,
                                   val settingsOfStemBranch: SettingsOfStemBranch = SettingsOfStemBranch.GingFang,
                                   val hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang,
                                   val tianyi: Tianyi = Tianyi.Authorized,
                                   val yangBlade: YangBlade = YangBlade.NextBliss,
                                   @Serializable(with = LocaleSerializer::class)
                                   val locale: Locale = Locale.TAIWAN,
                                   val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable
@DestinyMarker
class DivineTraditionalConfigBuilder : Builder<DivineTraditionalConfig> {

  var src: IHexagram = Hexagram.乾

  var dst: IHexagram = Hexagram.乾

  var settingsOfStemBranch: SettingsOfStemBranch = SettingsOfStemBranch.GingFang

  var hiddenEnergy: HiddenEnergy = HiddenEnergy.Wang

  var tianyi: Tianyi = Tianyi.Authorized

  var yangBlade: YangBlade = YangBlade.NextBliss

  var locale: Locale = Locale.TAIWAN

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}) {
    this.eightWordsConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  override fun build(): DivineTraditionalConfig {
    return DivineTraditionalConfig(src, dst, settingsOfStemBranch, hiddenEnergy, tianyi, yangBlade, locale, eightWordsConfig)
  }

  companion object {
    fun divineTraditionalConfig(block: DivineTraditionalConfigBuilder.() -> Unit = {}) : DivineTraditionalConfig {
      return DivineTraditionalConfigBuilder().apply(block).build()
    }
  }
}


/**
 * similar with [DivineTraditionalContext]
 */
@Named
class DivineTraditionalFeature(private val divineTraditionalContext : ICombinedWithMetaNameDayMonthContext,
                               private val eightWordsFeature: EightWordsFeature,
                               private val divineSettingsMap: Map<SettingsOfStemBranch, ISettingsOfStemBranch>,
                               private val hiddenEnergyMap: Map<HiddenEnergy, IHiddenEnergy>,
                               private val tianyiMap: Map<Tianyi, ITianyi>,
                               private val yangBladeMap: Map<YangBlade, IYangBlade>) : AbstractCachedFeature<DivineTraditionalConfig , ICombinedWithMetaNameDayMonth>() {
  override val key: String = "divineTraditionalFeature"

  override val defaultConfig: DivineTraditionalConfig = DivineTraditionalConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: DivineTraditionalConfig): ICombinedWithMetaNameDayMonth {
    val combinedWithMetaName = divineTraditionalContext.getCombinedWithMetaName(config.src, config.dst, config.locale)


    val ewNullable = eightWordsFeature.getModel(gmtJulDay, loc, config.eightWordsConfig)
    // 空亡
    val voids: Set<Branch> = ewNullable.day.empties.toSet()
    // 驛馬
    val horse: Branch = ewNullable.day.branch.let { Characters.getHorse(it) }
    // 桃花
    val flower: Branch = ewNullable.day.branch.let { Characters.getPeach(it) }
    // 貴人
    val tianyis: Set<Branch> = ewNullable.day.stem.let { tianyiMap[config.tianyi]!!.getTianyis(it).toSet() }
    // 羊刃
    val yangBlade: Branch = ewNullable.day.stem.let { yangBladeMap[config.yangBlade]!!.getYangBlade(it) }
    // 六獸
    val sixAnimals: List<SixAnimal> = ewNullable.day.let { SixAnimals.getSixAnimals(it.stem) }

    return CombinedWithMetaNameDayMonth(combinedWithMetaName, ewNullable, voids, horse, flower, tianyis, yangBlade, sixAnimals)
  }

}
