/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.chinese.IStemBranch
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

@Serializable
data class FortuneLargeConfig(val impl: Impl = Impl.DefaultSpan,
                              val span : Double = 120.0): java.io.Serializable {
  enum class Impl {
    DefaultSpan,    // 傳統、標準大運 (每柱十年)
    SolarTermsSpan  // 節氣星座過運法 (每柱五年)
  }
}

@DestinyMarker
class FortuneLargeConfigBuilder : Builder<FortuneLargeConfig> {

  var impl: FortuneLargeConfig.Impl = FortuneLargeConfig.Impl.DefaultSpan
  var span : Double = 120.0

  override fun build(): FortuneLargeConfig {
    return FortuneLargeConfig(impl, span)
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
   * @param lmt 出生時刻 LMT
   * @param fromGmtJulDay 計算此時刻是屬於哪條月大運當中
   * 實際會與 [IPersonContextModel.getStemBranchOfFortuneMonth] 結果相同
   * */

  fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch

  fun getStemBranch(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getStemBranch(gmtJulDay, loc, gender, fromGmtJulDay, config)
  }
}

class FortuneLargeFeature(private val implMap : Map<FortuneLargeConfig.Impl, IPersonFortuneLarge>,
                          private val julDayResolver: JulDayResolver) : IFortuneLargeFeature {
  override val key: String = "fortuneLargeFeature"

  override val defaultConfig: FortuneLargeConfig = FortuneLargeConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneLargeConfig): List<FortuneData> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val count = when (config.impl) {
      FortuneLargeConfig.Impl.DefaultSpan    -> 9
      FortuneLargeConfig.Impl.SolarTermsSpan -> 18
    }

    return implMap[config.impl]!!.getFortuneDataList(lmt, loc, gender, count)
  }

  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, fromGmtJulDay: GmtJulDay, config: FortuneLargeConfig): IStemBranch {
    return implMap[config.impl]!!.getStemBranch(gmtJulDay, loc, gender, julDayResolver.getLocalDateTime(fromGmtJulDay))
  }
}
