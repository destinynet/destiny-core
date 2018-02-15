/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranchOptional
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranchOptional
import destiny.tools.ChineseStringTools
import java.io.Serializable

/**
 * 八字資料結構，可以包含 null 值
 */
open class EightWordsNullable(open val year: IStemBranchOptional,
                              open val month: IStemBranchOptional,
                              open val day: IStemBranchOptional,
                              open val hour: IStemBranchOptional) : Serializable {

  /** 取得四柱  */
  open val stemBranches: List<IStemBranchOptional> = listOf(year, month, day, hour)


  constructor() : this(StemBranchOptional.empty(),
                       StemBranchOptional.empty(),
                       StemBranchOptional.empty(),
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

    fun empty() : EightWordsNullable {
      return EightWordsNullable(StemBranchOptional.empty() , StemBranchOptional.empty() , StemBranchOptional.empty() , StemBranchOptional.empty())
    }

    /**
     * 從 list of integer (1-based) 轉換成 EightWordsNullable
     * TODO : 這要如何能夠自動 downcast 成 EightWords 呢？
     * <T extends EightWordsNullable> T 好像不行 , return new 不知要寫什麼？
    </T> */
    fun getFromIntList(list: List<Int>): EightWordsNullable {
      assert(list.size == 8)
      val yearStem = if (list[0] == 0) null else Stem[list[0] - 1]
      val yearBranch = if (list[1] == 0) null else Branch[list[1] - 1]
      val monthStem = if (list[2] == 0) null else Stem[list[2] - 1]
      val monthBranch = if (list[3] == 0) null else Branch[list[3] - 1]
      val dayStem = if (list[4] == 0) null else Stem[list[4] - 1]
      val dayBranch = if (list[5] == 0) null else Branch[list[5] - 1]
      val hourStem = if (list[6] == 0) null else Stem[list[6] - 1]
      val hourBranch = if (list[7] == 0) null else Branch[list[7] - 1]
      return EightWordsNullable(
        StemBranchOptional[yearStem , yearBranch],
        StemBranchOptional[monthStem , monthBranch],
        StemBranchOptional[dayStem , dayBranch],
        StemBranchOptional[hourStem , hourBranch])
    }
  }
}
