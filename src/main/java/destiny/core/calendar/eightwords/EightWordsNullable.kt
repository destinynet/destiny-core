/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranchOptional
import destiny.tools.ChineseStringTools
import java.io.Serializable
import java.util.*

/**
 * 八字資料結構，可以包含 null 值
 */
class EightWordsNullable(val year: StemBranchOptional ,
                         val month: StemBranchOptional ,
                         val day: StemBranchOptional ,
                         val hour: StemBranchOptional) : Serializable {


  val yearStem: Stem? = year.stemOptional.orElse(null)
  val monthStem: Stem? = month.stemOptional.orElse(null)
  val dayStem: Stem? = day.stemOptional.orElse(null)
  val hourStem: Stem? = hour.stemOptional.orElse(null)

  val yearBranch: Branch? = year.branchOptional.orElse(null)
  val monthBranch: Branch? = month.branchOptional.orElse(null)
  val dayBranch: Branch? = day.branchOptional.orElse(null)
  val hourBranch: Branch? = hour.branchOptional.orElse(null)

  /** 取得四柱  */
  val stemBranches: List<StemBranchOptional> = listOf(year , month , day , hour)

  val intList: List<Int> = getIntList(this)

  constructor() : this(StemBranchOptional.empty() ,
                       StemBranchOptional.empty() ,
                       StemBranchOptional.empty() ,
                       StemBranchOptional.empty())

  override fun toString(): String {

    val h1 = hour.stem?.toString() ?: ChineseStringTools.NULL_CHAR
    val d1 = day.stem?.toString() ?: ChineseStringTools.NULL_CHAR
    val m1 = month.stem?.toString() ?: ChineseStringTools.NULL_CHAR
    val y1 = year.stem?.toString() ?: ChineseStringTools.NULL_CHAR

    val h2 = hour.branch?.toString() ?: ChineseStringTools.NULL_CHAR
    val d2 = day.branch?.toString() ?: ChineseStringTools.NULL_CHAR
    val m2 = month.branch?.toString() ?: ChineseStringTools.NULL_CHAR
    val y2 = year.branch?.toString() ?: ChineseStringTools.NULL_CHAR

    return ("\n"
      + h1 + d1 + m1 + y1
      + "\n"
      + h2 + d2 + m2 + y2
      )
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other !is EightWordsNullable) {
      return false
    }

    val that = other as EightWordsNullable?

    if (day != that!!.day) {
      return false
    }
    if (hour != that.hour) {
      return false
    }
    return if (month != that.month) {
      false
    } else year == that.year
  }

  override fun hashCode(): Int {
    var result = year.hashCode()
    result = 31 * result + month.hashCode()
    result = 31 * result + day.hashCode()
    result = 31 * result + hour.hashCode()
    return result
  }

  companion object {

    /**
     * 做法：將 年干、年支、月干、月支、日干、日支、時干、時支 分別取 index (1-based) , 若為 null 則取 0 , 再合併成一個 list
     */
    private fun getIntList(ewn: EightWordsNullable): List<Int> {

      return listOf(
        ewn.year.stem?.indexFromOne ?: 0,
        ewn.year.branch?.indexFromOne ?: 0,
        ewn.month.stem?.indexFromOne ?: 0,
        ewn.month.branch?.indexFromOne ?: 0,
        ewn.day.stem?.indexFromOne ?: 0,
        ewn.day.branch?.indexFromOne ?: 0,
        ewn.hour.stem?.indexFromOne ?: 0,
        ewn.hour.branch?.indexFromOne ?: 0)
    }

    /**
     * 從 list of integer (1-based) 轉換成 EightWordsNullable
     * TODO : 這要如何能夠自動 downcast 成 EightWords 呢？
     * <T extends EightWordsNullable> T 好像不行 , return new 不知要寫什麼？
    </T> */
    fun getFromIntList(list: List<Int>): EightWordsNullable {
      assert(list.size == 8)
      val yearStem = if (list[0] == 0) Optional.empty() else Optional.of(Stem[list[0] - 1])
      val yearBranch = if (list[1] == 0) Optional.empty() else Optional.of(Branch[list[1] - 1])
      val monthStem = if (list[2] == 0) Optional.empty() else Optional.of(Stem[list[2] - 1])
      val monthBranch = if (list[3] == 0) Optional.empty() else Optional.of(Branch[list[3] - 1])
      val dayStem = if (list[4] == 0) Optional.empty() else Optional.of(Stem[list[4] - 1])
      val dayBranch = if (list[5] == 0) Optional.empty() else Optional.of(Branch[list[5] - 1])
      val hourStem = if (list[6] == 0) Optional.empty() else Optional.of(Stem[list[6] - 1])
      val hourBranch = if (list[7] == 0) Optional.empty() else Optional.of(Branch[list[7] - 1])
      return EightWordsNullable(
        StemBranchOptional[yearStem.orElse(null), yearBranch.orElse(null)],
        StemBranchOptional[monthStem.orElse(null), monthBranch.orElse(null)],
        StemBranchOptional[dayStem.orElse(null), dayBranch.orElse(null)],
        StemBranchOptional[hourStem.orElse(null), hourBranch.orElse(null)]
                               )
    }
  }
}
