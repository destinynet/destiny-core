/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.IntAgeNoteImpl
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.chinese.IStemBranch
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import javax.inject.Named

@Serializable
data class FortuneLargeConfig(val impl: Impl = Impl.DefaultSpan,
                              val span : Double = 120.0,
                              val intAgeNotes: List<IntAgeNoteImpl> = listOf(IntAgeNoteImpl.WestYear, IntAgeNoteImpl.Minguo),
                              val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable {
  enum class Impl {
    DefaultSpan,    // 傳統、標準大運 (每柱十年)
    SolarTermsSpan  // 節氣星座過運法 (每柱五年)
  }
}

fun FortuneLargeConfig.Impl.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when(this@asDescriptive) {
      FortuneLargeConfig.Impl.DefaultSpan -> "傳統「節」過運"
      FortuneLargeConfig.Impl.SolarTermsSpan -> "「節」＋「氣（星座）」過運"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when(this@asDescriptive) {
      FortuneLargeConfig.Impl.DefaultSpan -> "太陽過黃道節氣的「節」來劃分大運，傳統此法一柱約十年。"
      FortuneLargeConfig.Impl.SolarTermsSpan -> "除了傳統法，額外考量「星座」（意即：中氣）過運。通常一柱大運為五年。"
    }
  }
}




@DestinyMarker
class FortuneLargeConfigBuilder : Builder<FortuneLargeConfig> {

  var impl: FortuneLargeConfig.Impl = FortuneLargeConfig.Impl.DefaultSpan

  var span : Double = 120.0

  var intAgeNotes: List<IntAgeNoteImpl> = listOf(IntAgeNoteImpl.WestYear, IntAgeNoteImpl.Minguo)
  fun intAgeNotes(impls: List<IntAgeNoteImpl>) {
    intAgeNotes = impls
  }

  var eightWordsConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block : EightWordsConfigBuilder.() -> Unit = {}) {
    this.eightWordsConfig = EightWordsConfigBuilder.ewConfig(block)
  }


  override fun build(): FortuneLargeConfig {
    return FortuneLargeConfig(impl, span, intAgeNotes, eightWordsConfig)
  }

  companion object {
    fun fortuneLarge(block: FortuneLargeConfigBuilder.() -> Unit = {}) : FortuneLargeConfig {
      return FortuneLargeConfigBuilder().apply(block).build()
    }
  }
}

interface IFortuneLargeFeature : PersonFeature<FortuneLargeConfig, List<FortuneData>> {
  /**
   * 逆推大運
   * 由 GMT 反推月大運
   * @param fromGmtJulDay 計算此時刻是屬於哪條月大運當中
   * 實際會與 [IPersonContextModel.getStemBranchOfFortuneMonth] 結果相同
   * */
  fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch

  fun getStemBranch(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getStemBranch(gmtJulDay, loc, gender, fromGmtJulDay, config)
  }
}

@Named
class FortuneLargeFeature(private val eightWordsFeature: EightWordsFeature,
                          private val implMap : Map<FortuneLargeConfig.Impl, IPersonFortuneLarge>,
                          private val ageNoteImplMap: Map<IntAgeNoteImpl , IntAgeNote>,
                          private val julDayResolver: JulDayResolver) : IFortuneLargeFeature, AbstractCachedPersonFeature<FortuneLargeConfig, List<FortuneData>>() {
  override val key: String = "fortuneLargeFeature"

  override val defaultConfig: FortuneLargeConfig = FortuneLargeConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneLargeConfig): List<FortuneData> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val count = when (config.impl) {
      FortuneLargeConfig.Impl.DefaultSpan    -> 9
      FortuneLargeConfig.Impl.SolarTermsSpan -> 18
    }

    val ageNoteImpls: List<IntAgeNote> = config.intAgeNotes.map { impl: IntAgeNoteImpl ->
      ageNoteImplMap[impl]!!
    }.toList()

    return implMap[config.impl]!!.getFortuneDataList(lmt, loc, gender, count, ageNoteImpls, eightWordsFeature, config)
  }

  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch {

    return implMap[config.impl]!!.getStemBranch(gmtJulDay, loc, gender, julDayResolver.getLocalDateTime(maxOf(gmtJulDay , fromGmtJulDay)), eightWordsFeature, config)
  }
}
