/**
 * Created by smallufo on 2021-09-27.
 */
package destiny.core.iching.divine

import destiny.core.Gender
import destiny.core.calendar.*
import destiny.core.calendar.eightwords.*
import destiny.core.iching.HexagramTextContext
import destiny.core.iching.IHexagramText
import destiny.core.iching.contentProviders.*
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import destiny.tools.serializers.IEightWordsNullableSerializer
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

@Serializable
data class DivineFullConfig(val traditionalConfig: DivineTraditionalConfig = DivineTraditionalConfig(),
                            @Serializable(with = IEightWordsNullableSerializer::class)
                            val eightWordsNullable: IEightWordsNullable = EightWordsNullable.empty(),
                            val question: String? = null,
                            val approach: DivineApproach? = null): java.io.Serializable

context(IEightWordsConfig)
@DestinyMarker
class DivineFullConfigBuilder : Builder<DivineFullConfig> {

  var traditionalConfig = DivineTraditionalConfig()
  fun tradConfig(block : DivineTraditionalConfigBuilder.() -> Unit = {}) {
    traditionalConfig = DivineTraditionalConfigBuilder.divineTraditionalConfig(block)
  }

  var eightWordsNullable: IEightWordsNullable = EightWordsNullable.empty()

  var question: String? = null

  var approach : DivineApproach? = null

  override fun build(): DivineFullConfig {
    return DivineFullConfig(traditionalConfig, eightWordsNullable, question, approach)
  }

  companion object {
    context(IEightWordsConfig)
    fun divineFullConfig(block : DivineFullConfigBuilder.() -> Unit = {}) : DivineFullConfig {
      return DivineFullConfigBuilder().apply(block).build()
    }
  }
}

interface IDivineFullFeature : PersonFeature<DivineFullConfig, ICombinedFull> {

  fun getCombinedFull(
    lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, ewn: IEightWordsNullable?,
    question: String?, approach: DivineApproach?, place: String?, config: DivineTraditionalConfig
  ): ICombinedFull
}


@Named
class DivineFullFeature(private val divineTraditionalFeature: DivineTraditionalFeature,
                        private val eightWordsFeature: EightWordsFeature,
                        private val julDayResolver: JulDayResolver,
                        private val hexagramNameFull: IHexNameFull,
                        private val hexagramNameShort: IHexNameShort,
                        private val hexExpressionImpl: IHexagramExpression,
                        @Named("hexImageFileImpl")
                        private val hexImageImpl: IHexProvider<String, String>,
                        private val hexJudgement: IHexJudgement) : IDivineFullFeature, AbstractCachedPersonFeature<DivineFullConfig, ICombinedFull>(){

  override val key: String = "divineFullFeature"

  override val defaultConfig: DivineFullConfig = DivineFullConfig()


  override fun getCombinedFull(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, ewn: IEightWordsNullable?,
                               question: String?, approach: DivineApproach?, place: String?, config: DivineTraditionalConfig
  ): ICombinedFull {

    val ewNullable = ewn ?: eightWordsFeature.getModel(lmt, loc, config.eightWordsConfig)

    val embedded = divineTraditionalFeature.getCombinedWithMetaNameDayMonth(ewNullable, config)

    val decoratedDate = DateDecorator.getOutputString(lmt.toLocalDate(), Locale.TAIWAN)
    val decoratedDateTime = DateHourMinSecDecorator.getOutputString(lmt, Locale.TAIWAN)

    val meta = Meta(config.settings, config.hiddenEnergy)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val divineMeta = DivineMeta(gender, question, approach, gmtJulDay, loc, place, decoratedDate, decoratedDateTime, meta, null)

    val textContext: IHexagramProvider<IHexagramText> =
      HexagramTextContext(hexagramNameFull, hexagramNameShort, hexExpressionImpl, hexImageImpl, hexJudgement)

    val srcText = textContext.getHexagram(config.src, config.locale)

    val dstText = textContext.getHexagram(config.dst, config.locale)

    val pairTexts = srcText to dstText

    return CombinedFull(embedded, ewNullable, divineMeta, pairTexts)
  }


  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: DivineFullConfig): ICombinedFull {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    return getCombinedFull(lmt, loc, gender, config.eightWordsNullable, config.question, config.approach, place, config.traditionalConfig)
  }

}
