/**
 * Created by smallufo on 2021-09-22.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.AbstractCachedPersonFeature
import kotlinx.serialization.Serializable

@Serializable
data class PithyModernDetailConfig(val pithyConfig: PithyConfig = PithyConfig(),
                                   val question: String? = null,
                                   val method: IPithyDetailModel.Method = IPithyDetailModel.Method.MANUAL): java.io.Serializable


class PithyModernDetailFeature(private val pithyCoreFeature: PithyCoreFeature ,
                               private val julDayResolver: JulDayResolver) : AbstractCachedPersonFeature<PithyModernDetailConfig, IPithyDetailModel>(){

  override val key: String = "pithyModernDetailFeature"

  override val defaultConfig: PithyModernDetailConfig = PithyModernDetailConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: PithyModernDetailConfig): IPithyDetailModel {

    val coreModel = pithyCoreFeature.getModel(gmtJulDay, loc, config.pithyConfig)

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)

    val modernModel = PithyModernModel(coreModel, gender, lmt, loc)

    return PithyDetailModel(modernModel, place, config.question, config.method)
  }
}
