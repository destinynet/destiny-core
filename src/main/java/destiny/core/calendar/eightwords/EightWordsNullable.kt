/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.*
import destiny.tools.ChineseStringTools
import java.io.Serializable

/**
 * 八字資料結構，可以包含 null 值
 */
interface IEightWordsNullable {
  val year: IStemBranchOptional
  val month: IStemBranchOptional
  val day: IStemBranchOptional
  val hour: IStemBranchOptional

  /** 取得四柱  */
  val stemBranches: List<IStemBranchOptional>
    get() = listOf(year, month, day, hour)
}

interface IEightWords : IEightWordsNullable , IEightWordsNullableFactory {
  override val year : StemBranch
  override val month : StemBranch
  override val day: StemBranch
  override val hour: StemBranch

  override val eightWordsNullable: IEightWordsNullable
    get() = EightWordsNullable(year, month, day, hour)
}

data class EightWordsNullable(override val year: IStemBranchOptional,
                              override val month: IStemBranchOptional,
                              override val day: IStemBranchOptional,
                              override val hour: IStemBranchOptional) : IEightWordsNullable , IEightWordsNullableFactory , Serializable {

  override val eightWordsNullable: EightWordsNullable
    get() = this

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


  companion object {

    fun empty() : EightWordsNullable {
      return EightWordsNullable(StemBranchOptional.empty() ,
                                StemBranchOptional.empty() ,
                                StemBranchOptional.empty() ,
                                StemBranchOptional.empty())
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
