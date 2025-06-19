/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUtils.toAnimal
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField

enum class FortuneLargeImpl {
  DefaultSpan,    // 傳統、標準大運 (每柱十年)
  SolarTermsSpan  // 節氣星座過運法 (每柱五年)
}

@Serializable
data class FortuneLargeConfig(val impl: FortuneLargeImpl = FortuneLargeImpl.DefaultSpan,
                              val span : Double = 120.0,
                              val intAgeNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo),
                              val eightWordsConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable

context(IEightWordsConfig)
@DestinyMarker
class FortuneLargeConfigBuilder : Builder<FortuneLargeConfig> {

  var impl: FortuneLargeImpl = FortuneLargeImpl.DefaultSpan

  var span : Double = 120.0

  var intAgeNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo)
  fun intAgeNotes(impls: List<IntAgeNote>) {
    intAgeNotes = impls
  }

  override fun build(): FortuneLargeConfig {
    return FortuneLargeConfig(impl, span, intAgeNotes, ewConfig)
  }

  companion object {
    context(IEightWordsConfig)
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
  fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch?

  fun getStemBranch(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch? {
    return getStemBranch(lmt.toGmtJulDay(loc), loc, gender, fromGmtJulDay, config)
  }

  fun IPersonPresentModel.toEwBdnp(): EwBdnp {
    val birthYear = this.time.get(ChronoField.YEAR_OF_ERA)
    val age = (LocalDateTime.now().year - birthYear).takeIf { it > 0 }

    val thisYear = LocalDateTime.now().year

    val recentYears = listOf(thisYear - 1, thisYear, thisYear + 1).mapNotNull { y ->
      // 每年七月一日，計算大運
      val yearMiddle = LocalDateTime.of(y, 7, 1, 0, 0).toGmtJulDay(location)
      getStemBranch(time, location, gender, yearMiddle, FortuneLargeConfig())?.let { fortuneLarge ->
        yearData.invoke(this, fortuneLarge, y)
      }
    }


    return EwBdnp(
      this.gender, this.time as LocalDateTime, this.location, age, name, place,
      this.eightWords.year.branch.toAnimal(),
      this.risingStemBranch,
      ZodiacSign.of(this.risingStemBranch.branch),
      this.eightWords.let { ew.invoke(it) },
      this.eightWords.let { ew -> nayin.invoke(ew) },
      this.eightWords.let { ew -> empties.invoke(ew) },
      this.eightWords.let { ew -> ewDetails.invoke(ew) },
      this.solarTermsTimePos.let { solarTermsPos.invoke(it) },
      this.eightWords.let { ew -> patterns.invoke(ew) },
      fortuneLarges.invoke(this),
      this.score,
      (if (this.score <= 2) "日主稍弱" else if (this.score >= 6) "日主強旺" else null),
      recentYears
    )
  }
}

@Named
class FortuneLargeFeature(private val implMap : Map<FortuneLargeImpl, IPersonFortuneLarge>,
                          private val julDayResolver: JulDayResolver) : IFortuneLargeFeature, AbstractCachedPersonFeature<FortuneLargeConfig, List<FortuneData>>() {

  override val defaultConfig: FortuneLargeConfig = FortuneLargeConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneLargeConfig): List<FortuneData> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val count = when (config.impl) {
      FortuneLargeImpl.DefaultSpan    -> 9
      FortuneLargeImpl.SolarTermsSpan -> 18
    }

    return implMap[config.impl]!!.getFortuneDataList(lmt, loc, gender, count, config)
  }

  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch? {

    return implMap[config.impl]!!.getStemBranch(gmtJulDay, loc, gender, julDayResolver.getLocalDateTime(maxOf(gmtJulDay , fromGmtJulDay)), config)
  }
}
