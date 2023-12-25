/**
 * Created by smallufo on 2023-12-25.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.tools.AbstractCachedPersonFeature
import jakarta.inject.Named


@Named
class ZiweiPresentFeature(private val ziweiFeature: IZiweiFeature) : AbstractCachedPersonFeature<IZiweiPresentConfig, Map<FlowType, IPlate>>() {

  override val defaultConfig: IZiweiPresentConfig
    get() = ZiweiPresentConfig(ziweiFeature.defaultConfig as ZiweiConfig, GmtJulDay.nowCeiling())

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: IZiweiPresentConfig): Map<FlowType, IPlate> {
    val plate: IPlate = ziweiFeature.getPersonModel(gmtJulDay, loc, gender, name, place, config)
    val viewGmt = config.viewGmt

    return buildMap {
      put(FlowType.MAIN, plate)
      ziweiFeature.reverseFlows(plate, viewGmt, config)?.let { sectionAndYear ->
        sectionAndYear.section?.let { section ->
          val flowYear = sectionAndYear.year
          val plateSection = ziweiFeature.getFlowSection(plate, section, config)
          val plateYear = ziweiFeature.getFlowYear(plate, section, flowYear, config)

          put(FlowType.SECTION, plateSection)
          put(FlowType.YEAR, plateYear)
        }
      }
    }
  }
}
