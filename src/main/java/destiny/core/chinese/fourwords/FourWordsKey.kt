/** 2009/10/12 上午3:27:09 by smallufo  */
package destiny.core.chinese.fourwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem

import java.io.Serializable

/** 四字斷終生 的 key  */
data class FourWordsKey(
  /** 日干  */
  val dayStem: Stem,
  /** 月支  */
  val monthBranch: Branch,
  /** 時支  */
  val hourBranch: Branch) : Serializable {

  override fun toString(): String {
    return "FourWordsKey [日干=$dayStem, 月支=$monthBranch, 時支=$hourBranch]"
  }


}

