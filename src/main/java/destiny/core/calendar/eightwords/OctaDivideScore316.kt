/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.SimpleBranch
import destiny.core.chinese.Stem
import javax.inject.Named


@Named
class OctaDivideScore316 : AbstractOctaDivideScore() {

  /**
   * 只針對 年、日、時支 對照日干 取得分數
   */
  override fun getScoreOfYDH(dayStem: Stem, branch: Branch): Double {
    var score = 0.0

    SimpleBranch.getFiveElement(branch).also { fiveElement ->

      if (fiveElement == dayStem.fiveElement) {
        score += 1.0
      } else if (fiveElement.isProducingTo(dayStem.fiveElement)) {
        score += 1.0
      } else if (fiveElement == 土) {
        when (dayStem.fiveElement) {
          水, 木, 火 -> {
            hiddenStems.getHiddenStems(branch).map { it.fiveElement }.any { hiddenStemFiveElement ->
              hiddenStemFiveElement == dayStem.fiveElement
            }.also {
              if (it) {
                score += 0.5
              }
            }
          }
          土       -> {
            score += 1.0
          }
          金       -> {
            score += 1.0
          }
        }
      }
    }

    return score
  }

  override fun getScoreOfMonth(dayStem: Stem, monthBranch: Branch, ewContext: IEightWordsContextModel): Double {
    var score = 0.0
    if (!setOf(辰, 戌, 丑, 未).contains(monthBranch)) {

      SimpleBranch.getFiveElement(monthBranch).also { fiveElement ->
        if (fiveElement == dayStem.fiveElement) {
          score += 2.0
        } else if (fiveElement.isProducingTo(dayStem.fiveElement)) {
          score += 2.0
        }
      }
    } else {

      val proportion = (ewContext.gmtJulDay - ewContext.solarTermsTimePos.prevMajor.second) /
        (ewContext.solarTermsTimePos.nextMajor.second - ewContext.solarTermsTimePos.prevMajor.second)

      // 司令天干
      val masterStem: Stem = hiddenStems.getHiddenStems(monthBranch).let { stems ->
        if (proportion <= 0.3) {
          stems[1]
        } else if (proportion <= 0.4) {
          stems[2]
        } else {
          stems[0]
        }
      }

      if (masterStem.fiveElement == 土 && dayStem.fiveElement == 土) {
        // 最後18天 , 土 司令
        score += 2.0
      } else {
        // 前9天 && 中間3天 , 若日主為土 , 都 +1
        if (dayStem.fiveElement == 土) {
          score += 1.0
        } else {
          if (proportion <= 0.3 && dayStem.fiveElement == masterStem.fiveElement) {
            score += 1.0
          } else if (proportion <= 0.4 && dayStem.fiveElement == masterStem.fiveElement) {
            score += 1.0
          }
        }
      }
    }

    return score
  }


}