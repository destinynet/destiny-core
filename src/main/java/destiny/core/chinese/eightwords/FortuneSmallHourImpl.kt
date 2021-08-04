/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 以時柱推算小運 , 由 醉醒子 提出
 * 陽男陰女順推 , 反之逆推
 *
 * 如：一九九八（戊寅）年戊午月戊寅日壬子時生男，
 * 陽年男命，八字小運的推排以時辰干支 壬子為起點 順行推排，
 * 一歲小運：癸丑；
 * 二歲小運：甲寅；
 * 三歲小運：乙卯；
 * 四歲小運：丙辰...
 */
class FortuneSmallHourImpl(private val eightWordsImpl: IEightWordsFactory,
                           private val fortuneDirectionImpl: IFortuneDirection,
                           /** 歲數實作  */
                           private val intAgeImpl: IIntAge,
                           override val ageNoteImpls: List<IntAgeNote>
                          ) : IPersonFortuneSmall, Serializable {

  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  count: Int): List<FortuneData> {

    val forward = fortuneDirectionImpl.isForward(lmt, location, gender)
    val eightWords = eightWordsImpl.getEightWords(lmt, location)
    val gmtJulDay = TimeTools.getGmtJulDay2(lmt, location)

    return implByRangesMap(gmtJulDay, eightWords, gender, location, count, forward)
  }

  /** 內定實作法 : 透過 [IIntAge.getRangesMap] 取得歲數 map , 套上干支 */
  private fun implByRangesMap(gmtJulDay: GmtJulDay,
                              eightWords: IEightWords,
                              gender: Gender,
                              location: ILocation,
                              count: Int,
                              forward: Boolean): List<FortuneData> {
    var sb = eightWords.hour
    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, count).map { (age, pair) ->
      sb = if (forward) sb.next as StemBranch else sb.prev as StemBranch
      val (from, to) = pair
      val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
      val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
      FortuneData(sb, from, to, age, age + 1, startFortuneAgeNotes, endFortuneAgeNotes)
    }.toList()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FortuneSmallHourImpl) return false

    if (eightWordsImpl != other.eightWordsImpl) return false
    if (fortuneDirectionImpl != other.fortuneDirectionImpl) return false
    if (intAgeImpl != other.intAgeImpl) return false
    if (ageNoteImpls != other.ageNoteImpls) return false

    return true
  }

  override fun hashCode(): Int {
    var result = eightWordsImpl.hashCode()
    result = 31 * result + fortuneDirectionImpl.hashCode()
    result = 31 * result + intAgeImpl.hashCode()
    result = 31 * result + ageNoteImpls.hashCode()
    return result
  }


}
