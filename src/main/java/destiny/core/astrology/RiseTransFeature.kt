/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import destiny.tools.serializers.AstroPointSerializer
import jakarta.inject.Named
import kotlinx.serialization.Serializable
import java.time.chrono.ChronoLocalDateTime

@Serializable
data class RiseTransConfig(@Serializable(with = AstroPointSerializer::class)
                           val star: Star = Planet.SUN,
                           val transPoint: TransPoint = TransPoint.RISING,
                           val transConfig: TransConfig = TransConfig()): java.io.Serializable

@DestinyMarker
class RiseTransConfigBuilder : Builder<RiseTransConfig> {

  var star: Star = Planet.SUN
  var transPoint: TransPoint = TransPoint.RISING

  var transConfig: TransConfig = TransConfig()
  fun trans(block: TransConfigBuilder.() -> Unit) {
    transConfig = TransConfigBuilder.trans(block)
  }

  override fun build(): RiseTransConfig {
    return RiseTransConfig(star, transPoint, transConfig)
  }

  companion object {
    fun riseTrans(block : RiseTransConfigBuilder.() -> Unit ={}) : RiseTransConfig {
      return RiseTransConfigBuilder().apply(block).build()
    }
  }
}

interface IRiseTransFeature : Feature<RiseTransConfig, GmtJulDay?> {

  /**
   * 來源、目標時間都是 GMT
   */
  fun getGmtTrans(
    fromGmt: ChronoLocalDateTime<*>, star: Star, point: TransPoint, loc: ILocation,
    julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()
  ): ChronoLocalDateTime<*>? {
    val fromGmtJulDay = TimeTools.getGmtJulDay(fromGmt)

    return getModel(fromGmtJulDay, loc, RiseTransConfig(star, point, transConfig))?.let { julDayResolver.getLocalDateTime(it) }
  }

  /**
   * 來源、目標時間都是 LMT
   */
  fun getLmtTrans(
    fromLmtTime: ChronoLocalDateTime<*>, star: Star, point: TransPoint, loc: ILocation,
    julDayResolver: JulDayResolver, transConfig: TransConfig = TransConfig()
  ): ChronoLocalDateTime<*>? {
    val fromGmtJulDay = TimeTools.getGmtJulDay(fromLmtTime, loc)

    return getModel(fromGmtJulDay, loc, RiseTransConfig(star, point, transConfig))?.let { resultGmt ->
      TimeTools.getLmtFromGmt(resultGmt, loc, julDayResolver)
    }
  }
}

/**
 * 計算星體對地球表面某點的 東昇、天頂、西落、天底的時刻
 */
@Named
class RiseTransFeature(val impl : IRiseTrans) : IRiseTransFeature, AbstractCachedFeature<RiseTransConfig, GmtJulDay?>() {

  override val key: String = "riseTrans"

  override val defaultConfig: RiseTransConfig = RiseTransConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: RiseTransConfig): GmtJulDay? {
    return impl.getGmtTransJulDay(gmtJulDay , config.star , config.transPoint , loc , config.transConfig)
  }

}
