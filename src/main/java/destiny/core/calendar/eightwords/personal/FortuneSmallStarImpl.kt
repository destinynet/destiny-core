/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 《星學大成》記載的推排方法：
 * 八字小運推算不分陰命陽命，一律以男子一歲時從丙寅起順數，女子一歲時從壬申起逆數。
 */
class FortuneSmallStarImpl(private val intAgeImpl: IIntAge ,
                           private val ageNoteImpls: List<IntAgeNote>) : IPersonFortuneSmall , Serializable {
  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  count: Int): List<FortuneData> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt , location)
    var sb = if (gender == Gender.男) StemBranch.丙寅.previous else StemBranch.壬申.next

    return intAgeImpl.getRangesMap(gender , gmtJulDay , location , 1 , count).map { (age , pair) ->
      val (from , to) = pair
      val startFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> impl.getAgeNote(from) }.filter { it != null }.map { it!! }.toList()
      val endFortuneAgeNotes: List<String> =
        ageNoteImpls.map { impl -> impl.getAgeNote(to) }.filter { it != null }.map { it!! }.toList()
      sb = if (gender == Gender.男) sb.next else sb.previous
      FortuneData(sb , from , to , age , age+1 , startFortuneAgeNotes , endFortuneAgeNotes)
    }.toList()
  }
}