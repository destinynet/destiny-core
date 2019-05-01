/**
 * Created by smallufo on 2018-04-22.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.EightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContext
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.core.chinese.IStemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

class PersonContext(

  val eightWordsContext: EightWordsContext,

  /** 歲數實作  */
  val intAgeImpl: IIntAge,

  /** 大運 的實作 */
  override val fortuneLargeImpl: IPersonFortuneLarge,

  /** 小運 的實作 */
  override val fortuneSmallImpl : IPersonFortuneSmall
                   ) : IPersonContext,
  IPersonPresentContext , IEightWordsContext by eightWordsContext, Serializable {

  override fun getPersonContextModel(lmt: ChronoLocalDateTime<*>,
                                     location: ILocation,
                                     place: String?,
                                     gender: Gender,
                                     name: String?): IPersonContextModel {

    val ewModel: IEightWordsContextModel = eightWordsContext.getEightWordsContextModel(lmt, location, place)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    // 1到120歲 , 每歲的開始、以及結束
    val ageMap: Map<Int, Pair<Double, Double>> = intAgeImpl.getRangesMap(gender , gmtJulDay , location , 1 , 120)

    // 9 or 18 條大運
    val n = if (fortuneLargeImpl is FortuneLargeSolarTermsSpanImpl) 18 else 9
    val fortuneDataLarges = fortuneLargeImpl.getFortuneDataList(lmt, location, gender, n)

    // 120歲 小運 , 120柱
    val fortuneDataSmalls = fortuneSmallImpl.getFortuneDataList(lmt , location , gender , 120)

    return PersonContextModel(ewModel, gender, name , fortuneDataLarges, fortuneDataSmalls , ageMap)
  }

  override fun getPersonPresentModel(lmt: ChronoLocalDateTime<*>,
                                     location: ILocation,
                                     place: String?,
                                     gender: Gender,
                                     name: String?,
                                     viewGmt: ChronoLocalDateTime<*>): IPersonPresentModel {
    val viewChineseDate: ChineseDate = chineseDateImpl.getChineseDate(viewGmt.toLocalDate())
    val pcm = getPersonContextModel(lmt, location, place, gender, name)
    // 目前所處的大運
    val selectedFortuneLarge: IStemBranch = fortuneLargeImpl.getStemBranch(lmt, location, gender, viewGmt)
    return PersonPresentModel(pcm, viewGmt, viewChineseDate, selectedFortuneLarge)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PersonContext) return false

    if (eightWordsContext != other.eightWordsContext) return false
    if (intAgeImpl != other.intAgeImpl) return false
    if (fortuneLargeImpl != other.fortuneLargeImpl) return false
    if (fortuneSmallImpl != other.fortuneSmallImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = eightWordsContext.hashCode()
    result = 31 * result + intAgeImpl.hashCode()
    result = 31 * result + fortuneLargeImpl.hashCode()
    result = 31 * result + fortuneSmallImpl.hashCode()
    return result
  }


}

