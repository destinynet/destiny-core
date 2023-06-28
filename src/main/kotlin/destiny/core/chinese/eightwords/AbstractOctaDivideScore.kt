/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.Scale
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.SolarTermsTimePos
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.Stem
import mu.KotlinLogging


abstract class AbstractOctaDivideScore : IEwContextScore {

  protected val hiddenStems: IHiddenStems = HiddenStemsStandardImpl()

  /**
   * 取得此柱的干支分別貢獻幾分
   */
  override fun getPillarScore(scale: Scale, eightWords: IEightWords, gmtJulDay: GmtJulDay, solarTermsTimePos: SolarTermsTimePos): Pair<Double, Double> {
    // 年干、月干、時干
    val dayStem = eightWords.day.stem
    val sb = eightWords.getScale(scale)

    return when (scale) {
      Scale.YEAR, Scale.HOUR -> {
        getScoreOfStem(dayStem, sb.stem) to getScoreOfYDH(dayStem, sb.branch)
      }
      Scale.MONTH            -> {
        getScoreOfStem(dayStem, sb.stem) to getScoreOfMonth(dayStem, sb.branch, gmtJulDay, solarTermsTimePos)
      }
      Scale.DAY              -> {
        0.0 to getScoreOfYDH(dayStem, sb.branch)
      }
    }
  }

  /**
   * 月支貢獻分數
   */
  abstract fun getScoreOfMonth(dayStem: Stem, monthBranch: Branch, gmtJulDay: GmtJulDay, solarTermsTimePos: SolarTermsTimePos): Double

  /**
   * 只針對 年、日、時支 對照日干 取得分數
   */
  abstract fun getScoreOfYDH(dayStem: Stem, branch: Branch): Double

  /**
   * 天干八分法：以元神(日主)對照年、月、時干
   * 元神逢年、月、時干為正印、偏印、劫財、比肩，得1分。
   */
  private fun getScoreOfStem(dayStem: Stem, otherStem: Stem): Double {
    var score = 0.0

    if (otherStem.helps(dayStem)) {
      score += 1.0
    }

    return score
  }


  private fun Stem.helps(target: Stem): Boolean {
    return this.fiveElement.helps(target.fiveElement)
  }

  protected fun FiveElement.helps(target: FiveElement): Boolean {
    return this == target || this.isProducingTo(target)
  }


  companion object {
    val logger = KotlinLogging.logger { }
  }
}
