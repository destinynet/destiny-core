package destiny.core.chinese

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import java.io.Serializable
import java.util.*

/**
 * 《易緯》 孟喜易 , 六日七分
 * 以節氣的「節」分割（亦即：地支月份），平均分割五份
 * 每卦 （365又1/4）÷　60 = 6又7/80 ==> 6 又 7/80 日
 * 本實作以精確的算法，平均分割「節」至「節」為五等分。
 */
class DailyHexagramAverage5Impl(val solarTermsImpl: ISolarTerms) : IDailyHexagram, Serializable {

  override fun getHexagram(gmtJulDay: GmtJulDay): Pair<Hexagram, Pair<GmtJulDay, GmtJulDay>> {
    val majorBetween = solarTermsImpl.getMajorSolarTermsGmtBetween(gmtJulDay)

    val avgGap: Double = (majorBetween.second.second - majorBetween.first.second) / 5

    // 0~4
    val index: Int = ((gmtJulDay - majorBetween.first.second) / avgGap).toInt()

    val hex: Hexagram = branchHexagramsMap.getValue(majorBetween.first.first.branch)[index]

    return hex to majorBetween.first.second.let { init: GmtJulDay ->
      (init + avgGap * index) to (init + avgGap * (index + 1))
    }
  }

  override fun getDutyDays(hexagram: IHexagram, gmtJulDay: GmtJulDay, forward: Boolean): Pair<GmtJulDay, GmtJulDay>? {
    val hex = Hexagram.of(hexagram)
    return branchHexagramsMap.filterValues { list -> list.contains(hex) }.entries.firstOrNull()?.let { (branch, list) ->
      val majorSolarTerms = SolarTerms.of(branch).first()
      val start = solarTermsImpl.getSolarTermsTime(majorSolarTerms, gmtJulDay, forward)
      val nextMajor = majorSolarTerms.next().next()
      val end = solarTermsImpl.getSolarTermsTime(nextMajor, start, forward)
      val avgGap = (end - start) / 5
      val index = list.indexOf(hex)
      (start + avgGap * index) to (start + avgGap * (index + 1))
    }
  }

  override fun toString(locale: Locale): String {
    return "《易緯》"
  }

  override fun getDescription(locale: Locale): String {
    return "《易緯》六日七分，孟喜易。坎離震兌並未值日。"
  }
}
