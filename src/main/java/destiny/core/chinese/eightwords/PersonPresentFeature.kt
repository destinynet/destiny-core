/**
 * Created by smallufo on 2021-08-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.eightwords.YearFeature
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.Feature
import destiny.tools.PersonFeature
import kotlinx.serialization.Serializable
import javax.cache.Cache

@Serializable
data class PersonPresentConfig(val personContextConfig: EightWordsPersonConfig = EightWordsPersonConfig(),
                               val viewGmt: GmtJulDay = GmtJulDay.now()) : java.io.Serializable

@DestinyMarker
class PersonPresentConfigBuilder : Builder<PersonPresentConfig> {

  var personContextConfig: EightWordsPersonConfig = EightWordsPersonConfig()

  fun ewPersonContext(block: PersonConfigBuilder.() -> Unit = {}) {
    personContextConfig = PersonConfigBuilder.ewPersonConfig(block)
  }

  var viewGmt: GmtJulDay = GmtJulDay.now()

  override fun build(): PersonPresentConfig {
    return PersonPresentConfig(personContextConfig, viewGmt)
  }

  companion object {
    fun ewPersonPresent(block: PersonPresentConfigBuilder.() -> Unit = {}) : PersonPresentConfig {
      return PersonPresentConfigBuilder().apply(block).build()
    }
  }
}


class PersonPresentFeature(private val personContextFeature: PersonContextFeature,
                           private val personLargeFeature: IFortuneLargeFeature,
                           private val yearFeature: YearFeature,
                           private val chineseDateFeature: ChineseDateFeature,
                           private val julDayResolver: JulDayResolver,
                           @Transient
                           private val ewPersonPresentFeatureCache: Cache<Feature.IGmtCacheKey<*>, IPersonPresentModel>) : PersonFeature<PersonPresentConfig , IPersonPresentModel> {

  override val key: String = "personPresent"

  override val defaultConfig: PersonPresentConfig = PersonPresentConfig()

  override val gmtCache: Cache<Feature.IGmtCacheKey<PersonPresentConfig>, IPersonPresentModel>
    get() = ewPersonPresentFeatureCache as Cache<Feature.IGmtCacheKey<PersonPresentConfig>, IPersonPresentModel>

  override fun getPersonModel(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: PersonPresentConfig): IPersonPresentModel {

    val viewGmtTime = julDayResolver.getLocalDateTime(config.viewGmt)

    val viewChineseDate = chineseDateFeature.getModel(config.viewGmt, loc)

    val pcm: IPersonContextModel = personContextFeature.getPersonCacheModel(gmtJulDay, loc, gender, name, place, config.personContextConfig)
    // 目前所處的大運
    val selectedFortuneLarge: IStemBranch = personLargeFeature.getStemBranch(gmtJulDay, loc, gender, config.viewGmt, config.personContextConfig.fortuneLargeConfig)

    // 選定的十年流年
    val selectedFortuneLargeYears: List<StemBranch> = pcm.fortuneDataLarges.firstOrNull { it.stemBranch == selectedFortuneLarge }?.let { fortuneData ->
      generateSequence(yearFeature.getModel(fortuneData.startFortuneGmtJulDay, loc)) {
        it.next(1)
      }.take(10).toList()
    }?: emptyList()

    // 當年流年
    val presentYear: StemBranch = yearFeature.getModel(viewGmtTime, loc)

    return PersonPresentModel(pcm, viewGmtTime, viewChineseDate, selectedFortuneLarge, selectedFortuneLargeYears, presentYear)
  }

  companion object {
    const val CACHE_EIGHTWORDS_PERSON_PRESENT_FEATURE = "ewPersonPresentFeatureCache"
  }
}
