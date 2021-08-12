/**
 * Created by smallufo on 2021-08-13.
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.卯
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import kotlinx.serialization.Serializable

@Serializable
data class RisingSignConfig(
  val houseSystem: HouseSystem = HouseSystem.PLACIDUS,
  val coordinate: Coordinate = Coordinate.ECLIPTIC,
  val impl : Impl = Impl.HouseCusp
) {
  enum class Impl {
    HouseCusp,
    TradChinese
  }
}

@DestinyMarker
class RisingSignConfigBuilder : Builder<RisingSignConfig> {
  var houseSystem: HouseSystem = HouseSystem.PLACIDUS
  var coordinate: Coordinate = Coordinate.ECLIPTIC
  var impl : RisingSignConfig.Impl = RisingSignConfig.Impl.HouseCusp

  override fun build(): RisingSignConfig {
    return RisingSignConfig(houseSystem, coordinate, impl)
  }

  companion object {
    fun risingSign(block: RisingSignConfigBuilder.() -> Unit = {}): RisingSignConfig {
      return RisingSignConfigBuilder().apply(block).build()
    }
  }
}


class RisingSignFeature(private val houseCuspImpl : IHouseCusp ,
                        private val starPositionImpl: IStarPosition<*> ,
                        private val dayHourFeature: DayHourFeature) : Feature<RisingSignConfig , ZodiacSign> {
  override val key: String = "risingSign"

  override val defaultConfig: RisingSignConfig = RisingSignConfig()

  override val builder: Builder<RisingSignConfig> = RisingSignConfigBuilder()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, config: RisingSignConfig): ZodiacSign {
    return when (config.impl) {
      RisingSignConfig.Impl.HouseCusp -> houseCuspImpl.getRisingSign(gmtJulDay , loc , config.houseSystem , config.coordinate)
      RisingSignConfig.Impl.TradChinese -> {
        // 太陽星座
        val sunSign = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC).lngDeg.sign

        // 時支
        val hour = dayHourFeature.getModel(gmtJulDay, loc).second.branch

        val gap = (hour.index - sunSign.branch.index).let {
          if (it < 0 )
            it + 12
          else
            it
        }

        val target = (卯.index - gap)

        val resultEB = Branch[target]

        return ZodiacSign.of(resultEB)
      }
    }
  }
}
