/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsFactory
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
                           private val ageNoteImpls: List<IntAgeNote>
                          ) : IPersonFortuneSmall, Serializable {

  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  count: Int): List<FortuneData> {

    val forward = fortuneDirectionImpl.isForward(lmt, location, gender)
    val eightWords = eightWordsImpl.getEightWords(lmt, location)
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)

    return implByRangesMap(gmtJulDay, eightWords, gender, location, count, forward)
  }

  /** 內定實作法 : 透過 [IIntAge.getRangesMap] 取得歲數 map , 套上干支 */
  private fun implByRangesMap(gmtJulDay: Double,
                              eightWords: IEightWords,
                              gender: Gender,
                              location: ILocation,
                              count: Int,
                              forward: Boolean): List<FortuneData> {
    var sb = eightWords.hour
    return intAgeImpl.getRangesMap(gender, gmtJulDay, location, 1, count).map { (age, pair) ->
      sb = if (forward) sb.next else sb.previous
      val (from, to) = pair
      val startFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> impl.getAgeNote(from) }.filter { it != null }.map { it!! }.toList()
      val endFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> impl.getAgeNote(to) }.filter { it != null }.map { it!! }.toList()
      FortuneData(sb, from, to, age, age + 1, startFortuneAgeNotes, endFortuneAgeNotes)
    }.toList()
  }

  /** 另一個實作法 : 不斷呼叫 [IIntAge.getRange] , 比較沒有效率 (因底層無 cache)
   * 經測試 , 算 60柱，花費 4.2 sec
   * 而 [implByRangesMap] 只要花 0.4 sec
   *  */
  private fun implBySequence(gmtJulDay: Double,
                             eightWords: EightWords,
                             gender: Gender,
                             location: ILocation,
                             count: Int,
                             forward: Boolean): List<FortuneData> {
    var age = 0
    var sb = eightWords.hour
    return generateSequence {
      // 出生當時，就是一歲
      val (from, to) = intAgeImpl.getRange(gender, gmtJulDay, location, age + 1)
      /** 附加上 西元、民國 之類的註記 */
      val startFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> impl.getAgeNote(from) }.filter { it != null }.map { it!! }.toList()
      val endFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> impl.getAgeNote(to) }.filter { it != null }.map { it!! }.toList()

      sb = if (forward) sb.next else sb.previous
      age += 1
      FortuneData(sb, from, to, age, age + 1, startFortuneAgeNotes, endFortuneAgeNotes)
    }.takeWhile { age <= count }
      .toList()
  }
}