/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.eightwords.HiddenStemsStandardImpl
import destiny.core.chinese.eightwords.IHiddenStems
import mu.KotlinLogging


abstract class AbstractOctaDivideScore : IEwContextScore {


  protected val hiddenStems: IHiddenStems = HiddenStemsStandardImpl()


  override fun getScore(ewContext: IEightWordsContextModel): Double {

    var score = 0.0

    // 年干、月干、時干
    val dayStem = ewContext.eightWords.day.stem

    score += listOf(
      ewContext.eightWords.year.stem,
      ewContext.eightWords.month.stem,
      ewContext.eightWords.hour.stem
    ).sumOf { stem -> getScoreOfStem(dayStem, stem).also { if (it > 0) logger.info { "\t透過天干 $stem 貢獻 $it 分" } } }

    logger.info { "年、月、時干，累計得分 $score" }

    ewContext.eightWords.also { ew ->
      score += getScoreOfYDH(dayStem, ew.year.branch).also { if (it > 0) logger.info { "\t年支 貢獻 $it 分" } }
      score += getScoreOfYDH(dayStem, ew.day.branch).also { if (it > 0) logger.info { "\t日支 貢獻 $it 分" } }
      score += getScoreOfYDH(dayStem, ew.hour.branch).also { if (it > 0) logger.info { "\t時支 貢獻 $it 分" } }
    }

    logger.info { "年、日、時支，累計得分 $score" }


    val monthBranch = ewContext.eightWords.month.branch
    score += getScoreOfMonth(dayStem, monthBranch, ewContext).also { if (it > 0) logger.info { "\t月支 貢獻 $it 分" } }

    return score
  }

  /**
   * 月支貢獻分數
   */
  abstract fun getScoreOfMonth(dayStem: Stem, monthBranch: Branch, ewContext: IEightWordsContextModel): Double

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
    if (dayStem.fiveElement == otherStem.fiveElement) {
      score += 1.0
    } else if (otherStem.fiveElement.isProducingTo(dayStem.fiveElement)) {
      score += 1.0
    }
    return score
  }


  companion object {
    val logger = KotlinLogging.logger { }
  }
}
