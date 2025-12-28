/**
 * Created by smallufo on 2021-10-08.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.PersonFeature
import jakarta.inject.Named

interface IFortuneDirectionFeature : PersonFeature<EightWordsConfig, Boolean>

@Named
class FortuneDirectionFeature(private val eightWordsFeature: EightWordsFeature) : AbstractCachedPersonFeature<EightWordsConfig, Boolean>() , IFortuneDirectionFeature {

  override val defaultConfig: EightWordsConfig = EightWordsConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: EightWordsConfig): Boolean {

    val eightWords = eightWordsFeature.getModel(gmtJulDay, loc, config)

    return gender === Gender.M && eightWords.year.stem.booleanValue
      || gender === Gender.F && !eightWords.year.stem.booleanValue
  }
}
