/**
 * Created by smallufo on 2021-08-22.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IntAgeImpl
import destiny.core.IntAgeNote
import destiny.core.IntAgeNoteImpl
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable


@Serializable
data class FortuneSmallConfig(val impl: Impl = Impl.Hour,
                              /** 取得幾條小運 */
                              val count: Int = 120,
                              val intAgeImpl : IntAgeImpl = IntAgeImpl.EightWords,
                              val intAgeNotes: List<IntAgeNoteImpl> = listOf(IntAgeNoteImpl.WestYear, IntAgeNoteImpl.Minguo),
                              val eightWordsConfig: EightWordsConfig = EightWordsConfig()) {
  enum class Impl {
    Hour,   // 以時柱推算小運
    Star,   // 《星學大成》
  }
}

@DestinyMarker
class FortuneSmallConfigBuilder : Builder<FortuneSmallConfig> {
  var impl: FortuneSmallConfig.Impl = FortuneSmallConfig.Impl.Hour
  var count: Int = 120
  var intAgeImpl: IntAgeImpl = IntAgeImpl.EightWords

  var intAgeNotes: List<IntAgeNoteImpl> = listOf(IntAgeNoteImpl.WestYear, IntAgeNoteImpl.Minguo)
  fun intAgeNotes(impls: List<IntAgeNoteImpl>) {
    intAgeNotes = impls
  }

  override fun build(): FortuneSmallConfig {
    return FortuneSmallConfig(impl, count, intAgeImpl, intAgeNotes)
  }

  companion object {
    fun fortuneSmall(block: FortuneSmallConfigBuilder.() -> Unit = {}) : FortuneSmallConfig {
      return FortuneSmallConfigBuilder().apply(block).build()
    }
  }
}

class FortuneSmallFeature(private val julDayResolver: JulDayResolver,
                          private val ageNoteImplMap: Map<IntAgeNoteImpl , IntAgeNote>,
                          private val fortuneSmallImplMap: Map<FortuneSmallConfig.Impl, IPersonFortuneSmall>) : PersonFeature<FortuneSmallConfig, List<FortuneData>> {
  override val key: String = "fortuneSmall"

  override val defaultConfig: FortuneSmallConfig = FortuneSmallConfig()

  override fun getModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: FortuneSmallConfig): List<FortuneData> {

    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)


    val ageNoteImpls: List<IntAgeNote> = config.intAgeNotes.map { impl: IntAgeNoteImpl ->
      ageNoteImplMap[impl]!!
    }.toList()

    return fortuneSmallImplMap[config.impl]!!.getFortuneDataList(lmt, loc, gender, config.count, ageNoteImpls)
  }

}
