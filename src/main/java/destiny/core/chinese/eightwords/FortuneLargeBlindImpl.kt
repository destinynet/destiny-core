/**
 * Created by smallufo on 2018-04-28.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.EightWordsFeature
import destiny.core.calendar.eightwords.IEightWordsStandardFactory
import destiny.core.chinese.IStemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/**
 * 盲派起大運
 *
 * 交運時間盲師一派有準確的說法，依年命納音五行定。
 *　火命人交運在清明前三天的午時;
 *　土命人交運在芒種後九天辰時;
 *　金命人交運在處暑當日申時;
 *　木命人交運在大寒當日寅時;
 *　水命人交運在冬至前三天亥時。
 *
 * 交運時間歌訣
 * 火命交運清明前，之前三天午時中。
 * 土命交運芒種后，之後九天辰時定。
 * 金命交運處署日，處署當日申時整。
 * 木命交運在大寒，大寒當日寅時更。
 * 水命交運冬至前，之前三天亥時定。
 * 交運之時不外出，怕與自己屬相衝。
 * 三日不見紅白事，身旺體壯家業興。
 */
class FortuneLargeBlindImpl(
  override val eightWordsImpl: IEightWordsStandardFactory,
  override val ageNoteImpls: List<IntAgeNote>) : IPersonFortuneLarge, Serializable {

  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, count: Int): List<FortuneData> {
    TODO("not implemented")
  }

  override fun getFortuneDataList(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, count: Int, span: Double, ageNoteImpls: List<IntAgeNote>, eightWordsFeature: EightWordsFeature, config: EightWordsConfig): List<FortuneData> {
    TODO("Not yet implemented")
  }

  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, targetGmt: ChronoLocalDateTime<*>): IStemBranch {
    TODO("Not yet implemented")
  }

  override fun getStemBranch(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, targetGmt: ChronoLocalDateTime<*>, eightWordsFeature: EightWordsFeature, config: EightWordsConfig): IStemBranch {
    TODO("Not yet implemented")
  }

  override fun toString(locale: Locale): String {
    TODO("not implemented")
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FortuneLargeBlindImpl) return false

    if (eightWordsImpl != other.eightWordsImpl) return false
    if (ageNoteImpls != other.ageNoteImpls) return false

    return true
  }

  override fun hashCode(): Int {
    var result = eightWordsImpl.hashCode()
    result = 31 * result + ageNoteImpls.hashCode()
    return result
  }


}
