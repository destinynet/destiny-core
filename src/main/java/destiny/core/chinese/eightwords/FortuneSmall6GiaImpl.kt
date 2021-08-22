/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchCycle
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 六甲起行年法
 *
 * 甲子旬 男起丙寅 順行 女起壬申 逆行
 * 甲戌旬 男起丙子 女起壬午
 * 甲申旬 男起丙戌 女起壬辰
 * 甲午旬 男起丙申 女起壬寅
 * 甲辰旬 男起丙午 女起壬子
 * 甲寅旬 男起丙辰 女起壬戌
 */
class FortuneSmall6GiaImpl(private val eightWordsImpl: IEightWordsFactory,
                           private val intAgeImpl: IIntAge) : IPersonFortuneSmall, Serializable {
  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>,
                                  location: ILocation,
                                  gender: Gender,
                                  count: Int,
                                  ageNoteImpls: List<IntAgeNote>): List<FortuneData> {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, location)
    val eightWords = eightWordsImpl.getEightWords(lmt, location)
    var sb = when (eightWords.day.cycle) {
      StemBranchCycle.甲子 -> if (gender == Gender.男) StemBranch.丙寅.prev else StemBranch.壬申.next
      StemBranchCycle.甲戌 -> if (gender == Gender.男) StemBranch.丙子.prev else StemBranch.壬午.next
      StemBranchCycle.甲申 -> if (gender == Gender.男) StemBranch.丙戌.prev else StemBranch.壬辰.next
      StemBranchCycle.甲午 -> if (gender == Gender.男) StemBranch.丙申.prev else StemBranch.壬寅.next
      StemBranchCycle.甲辰 -> if (gender == Gender.男) StemBranch.丙午.prev else StemBranch.壬子.next
      StemBranchCycle.甲寅 -> if (gender == Gender.男) StemBranch.丙辰.prev else StemBranch.壬戌.next
    }

    return intAgeImpl.getRangesMap(gender , gmtJulDay , location , 1 , count).map { (age , pair) ->
      val (from , to) = pair
      val startFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(from) }.toList()
      val endFortuneAgeNotes: List<String> = ageNoteImpls.mapNotNull { impl -> impl.getAgeNote(to) }.toList()
      sb = if (gender == Gender.男) sb.next else sb.prev
      FortuneData(sb , from , to , age , age+1 , startFortuneAgeNotes , endFortuneAgeNotes)
    }.toList()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FortuneSmall6GiaImpl) return false

    if (eightWordsImpl != other.eightWordsImpl) return false
    if (intAgeImpl != other.intAgeImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = eightWordsImpl.hashCode()
    result = 31 * result + intAgeImpl.hashCode()
    return result
  }


}
