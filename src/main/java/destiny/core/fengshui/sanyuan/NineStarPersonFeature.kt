/**
 * Created by smallufo on 2023-03-21.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.BirthDataNamePlace
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.Scale
import destiny.core.Scale.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.AbstractCachedPersonFeature
import jakarta.inject.Inject
import jakarta.inject.Named

data class NineStarPersonModel(val bdnp: IBirthDataNamePlace, val models: List<NineStarModel>) {

  fun getModel(scale: Scale) : NineStarModel {
    return models.first { it.scale == scale }
  }
}

@Named
class NineStarPersonFeature(private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<NineStarConfig, NineStarPersonModel>() {

  override val defaultConfig: NineStarConfig = NineStarConfig(scales = listOf(YEAR, MONTH, DAY, HOUR))

  @Inject
  private lateinit var nineStarFeature : NineStarFeature // Feature<NineStarConfig, List<NineStarModel>>

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: NineStarConfig): NineStarPersonModel {

    return nineStarFeature.getModel(gmtJulDay, loc, config).let { list ->

      val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
      val bdnp = BirthDataNamePlace(gender, lmt , loc, name, place)
      NineStarPersonModel(bdnp, list)
    }
  }


}
