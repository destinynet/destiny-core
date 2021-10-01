/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.Scale
import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsConfigBuilder
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime
import javax.inject.Named

@Serializable
data class NineStarConfig(val scales: List<Scale> = listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR),
                          val ewConfig: EightWordsConfig = EightWordsConfig()): java.io.Serializable

class NineStarConfigBuilder : Builder<NineStarConfig> {

  var scales: List<Scale> = listOf(Scale.YEAR , Scale.MONTH, Scale.DAY, Scale.HOUR)

  fun scales(scales: List<Scale>) {
    this.scales = scales
  }

  var ewConfig: EightWordsConfig = EightWordsConfig()
  fun ewConfig(block : EightWordsConfigBuilder.() -> Unit = {}) {
    ewConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  override fun build(): NineStarConfig {
    return NineStarConfig(scales, ewConfig)
  }

  companion object {
    fun nineStarConfig(block: NineStarConfigBuilder.() -> Unit = {}) : NineStarConfig {
      return NineStarConfigBuilder().apply(block).build()
    }
  }
}


@Named
class NineStarFeature(private val sanYuanImpl: ISanYuan,
                      private val ewFeature : EightWordsFeature,
                      private val starPositionImpl: IStarPosition<IStarPos>,
                      private val julDayResolver: JulDayResolver) : AbstractCachedFeature<NineStarConfig, List<NineStarModel>>() {
  override val key: String = "nineStar"

  override val defaultConfig: NineStarConfig = NineStarConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: NineStarConfig): List<NineStarModel> {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getModel(lmt, loc, config)
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: NineStarConfig): List<NineStarModel> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val yuan = sanYuanImpl.getYuan(lmt, loc)
    val eightWords: EightWords = ewFeature.getModel(lmt, loc, config.ewConfig)
    val zodiacDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC).lng

    return config.scales.map { scale ->
      when (scale) {
        Scale.YEAR -> {
          val center = INineStar.getYearCenterStar(yuan, eightWords.year)
          NineStarModel(eightWords.year, scale, center, INineStar.getYearStarMap(center))
        }
        Scale.MONTH -> {
          val center = INineStar.getMonthCenterStar(eightWords.year.branch, eightWords.month.branch)
          NineStarModel(eightWords.month, scale, center, INineStar.getMonthStarMap(eightWords.year.branch, eightWords.month.branch))
        }
        Scale.DAY -> {
          val center = INineStar.getDayCenterStar(zodiacDegree, eightWords.day)
          NineStarModel(eightWords.day, scale, center, INineStar.getDayStarMap(zodiacDegree, eightWords.day))
        }
        Scale.HOUR -> {
          val center = INineStar.getHourCenterStar(zodiacDegree, eightWords.day.branch, eightWords.hour.branch)
          NineStarModel(eightWords.hour, scale, center, INineStar.getHourStarMap(zodiacDegree, eightWords.day.branch, eightWords.hour.branch))
        }
      }
    }.toList()

  }
}
