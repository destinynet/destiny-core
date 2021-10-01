/**
 * Created by smallufo on 2021-08-18.
 */
package destiny.core.chinese

import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.MonthConfig
import destiny.core.calendar.eightwords.MonthConfigBuilder
import destiny.core.calendar.eightwords.YearMonthConfig
import destiny.core.calendar.eightwords.YearMonthFeature
import destiny.core.chinese.MonthMasterConfig.Impl
import destiny.tools.AbstractCachedFeature
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import kotlinx.serialization.Serializable
import javax.inject.Named

@Serializable
data class MonthMasterConfig(val impl: Impl = Impl.SunSign ,
                             /** 只有在 impl = [Impl.Combined] 時，才有必要參考此設定 */
                             val monthConfig: MonthConfig = MonthConfig()): java.io.Serializable {
  enum class Impl {
    SunSign,  // 星體觀測（過中氣）
    Combined  // 月支六合（過節）
  }
}

@DestinyMarker
class MonthMasterConfigBuilder : Builder<MonthMasterConfig> {
  var impl: Impl = Impl.SunSign

  var monthConfig: MonthConfig = MonthConfig()
  fun month(block: MonthConfigBuilder.() -> Unit = {}) {
    this.monthConfig = MonthConfigBuilder.monthConfig(block)
  }

  override fun build(): MonthMasterConfig {
    return MonthMasterConfig(impl, monthConfig)
  }

  companion object {
    fun monthMaster(block: MonthMasterConfigBuilder.() -> Unit = {}): MonthMasterConfig {
      return MonthMasterConfigBuilder().apply(block).build()
    }
  }
}

@Named
class MonthMasterFeature(private val starPositionImpl: IStarPosition<*>,
                         private val yearMonthFeature: YearMonthFeature) : AbstractCachedFeature<MonthMasterConfig, Branch>() {
  override val key: String = "monthMaster"

  override val defaultConfig: MonthMasterConfig = MonthMasterConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: MonthMasterConfig): Branch {
    return when (config.impl) {
      Impl.SunSign  -> starPositionImpl.getPosition(Planet.SUN, gmtJulDay, loc, Centric.GEO, Coordinate.ECLIPTIC).sign.branch
      Impl.Combined -> yearMonthFeature.getModel(gmtJulDay, loc, YearMonthConfig(monthConfig = config.monthConfig)).branch.combined
    }
  }
}
