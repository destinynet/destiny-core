/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.SolarTermsTimePos
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.土
import destiny.core.chinese.SimpleBranch
import destiny.core.chinese.Stem
import jakarta.inject.Named

/**
 * 八分法
 * https://destiny.to/ubbthreads/ubbthreads.php/topics/1365115
 */
@Named
class OctaDivideScore46 : AbstractOctaDivideScore() {

  /**
   * 只針對 年、日、時支 對照日干 取得分數
   *
   * 地支八分法1：以元神(日主)對照年、日、時支
   * 元神逢年、日、時支為正印、偏印、劫財、比肩，得1分；
   * 元神逢年、日、時支為餘氣、庫，得0.5分。
   */
  override fun getScoreOfYDH(dayStem: Stem, branch: Branch): Double {
    var score = 0.0

    SimpleBranch.getFiveElement(branch).also { fiveElement ->


      if (fiveElement.helps(dayStem.fiveElement)) {
        score += 1.0
      } else if (fiveElement == 土) {
        hiddenStems.getHiddenStems(branch).map { it.fiveElement }.any { hiddenStemFiveElement ->
          hiddenStemFiveElement == dayStem.fiveElement
        }.also {
          if (it) {
            score += 0.5
          }
        }
      }
    }

    return score
  }

  /**
   * 地支八分法2：以元神(日主)對照月支
   * 公式：
   * 元神逢月支為正印、偏印、劫財、比肩，得2分；
   * 元神逢月支為辰、戌、丑、未四季月：
   * 辰月 - 木令：木1分、土1分。土令(立夏前18天)：土2分。
   * 未月 - 火令：火1分、土1分。土令(立秋前18天)：土2分。
   * 戌月 - 金令：金1分、土1分。土令(立冬前18天)：土2分。
   * 丑月 - 水令：水1分、土1分。土令(立春前18天)：土2分。
   */
  override fun getScoreOfMonth(dayStem: Stem, monthBranch: Branch, gmtJulDay: GmtJulDay, solarTermsTimePos: SolarTermsTimePos): Double {
    var score = 0.0

    if (!setOf(辰, 戌, 丑, 未).contains(monthBranch)) {

      SimpleBranch.getFiveElement(monthBranch).also { fiveElement ->

        if (fiveElement.helps(dayStem.fiveElement)) {
          score += 2.0
        }
      }

      //score += getScoreOfNonEarthMonth(dayStem, monthBranch)
    } else {
      // 土月

      val toEndDays = (solarTermsTimePos.nextMajor.second - gmtJulDay)
      logger.info { "toEndDays = $toEndDays" }


      if (toEndDays <= 18) {
        if (dayStem.fiveElement == 土) {
          // 土令 的 土， +2 分
          score += 2.0
        }
      } else {
        logger.info { "\t${monthBranch}月 ${hiddenStems.getHiddenStems(monthBranch)[1].fiveElement}令" }
        hiddenStems.getHiddenStems(monthBranch).map { it.fiveElement }.forEach { hiddenStemFiveElement ->
          if (hiddenStemFiveElement == dayStem.fiveElement) {
            score += 1.0
          } else if (dayStem.fiveElement == 土) {
            score += 1.0
          }
        }
      }
    }


    return score
  }


}
