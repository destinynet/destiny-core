/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.EightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContextModel
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

class PersonContext(

  private val eightWordsContext: EightWordsContext,

  /** 歲數實作  */
  private val intAgeImpl: IIntAge,

  /** 歲數註解實作  */
  override val ageNoteImpls: List<IntAgeNote>,

  /** 順推、逆推大運 的實作 */
  private val personFortuneLargeImpl: IPersonFortuneLarge
                   ) : IPersonContext,
  IPersonPresentContext , IEightWordsContext by eightWordsContext, Serializable {

  private var logger = LoggerFactory.getLogger(javaClass)

  override fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                                     location: ILocation,
                                     place: String?,
                                     gender: Gender): IPersonContextModel {

    val ewModel: IEightWordsContextModel = eightWordsContext.getEightWordsContextModel(lmt, location, place)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    val ageMap: Map<Int, Pair<Double, Double>> = getAgeMap(120, gmtJulDay, gender, location)

    val fortuneDataList = personFortuneLargeImpl.getFortuneDataList(lmt, location, gender, 9)

    return PersonContextModel(ewModel, gender, fortuneDataList, ageMap)
  }



  private fun getAgeMap(toAge: Int,
                        gmtJulDay: Double,
                        gender: Gender,
                        location: ILocation): Map<Int, Pair<Double, Double>> {
    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, toAge)
  }

  override fun getPersonPresentModel(lmt: ChronoLocalDateTime<*>,
                                     location: ILocation,
                                     place: String?,
                                     gender: Gender,
                                     viewGmt: ChronoLocalDateTime<*>): IPersonPresentModel {
    val viewChineseDate: ChineseDate = chineseDateImpl.getChineseDate(viewGmt.toLocalDate())
    val pcm = getPersonContextModel(lmt, location, place, gender)
    val selectedFortuneData = personFortuneLargeImpl.getStemBranch(lmt, location, gender, viewGmt)
    return PersonPresentModel(pcm, viewGmt, viewChineseDate, selectedFortuneData)
  }
}

