package destiny.core.calendar.eightwords

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.tools.AbstractCachedFeature
import kotlinx.serialization.Serializable
import javax.inject.Named

@Serializable
data class TradChineseRisingSignConfig(
  val hourImpl: HourImpl = HourImpl.TST
): java.io.Serializable

@Named
class TradChineseRisingSignFeature(private val starPositionImpl: IStarPosition<*>,
                                   private val dayHourFeature: IDayHourFeature) : AbstractCachedFeature<TradChineseRisingSignConfig, ZodiacSign>() {

  override val key: String = "tradChinese"

  override val defaultConfig: TradChineseRisingSignConfig = TradChineseRisingSignConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, config: TradChineseRisingSignConfig): ZodiacSign {
    // 太陽星座
    val sunSign = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC).lngDeg.sign

    // 時支

    val hour = dayHourFeature.getModel(gmtJulDay, loc, DayHourConfig(hourBranchConfig = HourBranchConfig(hourImpl = config.hourImpl))).second.branch

    val gap = (hour.index - sunSign.branch.index).let {
      if (it < 0 )
        it + 12
      else
        it
    }

    val target = (Branch.卯.index - gap)

    val resultEB = Branch[target]

    return ZodiacSign.of(resultEB)
  }
}
