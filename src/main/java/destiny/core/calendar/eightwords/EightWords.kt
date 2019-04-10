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

interface IEightWords : IEightWordsNullable, IEightWordsNullableFactory {
  override val year: IStemBranch
  override val month: IStemBranch
  override val day: StemBranch
  override val hour: IStemBranch

  override val stemBranches: List<IStemBranch>
    get() = listOf(year, month, day, hour)

  override val eightWordsNullable: IEightWordsNullable
    get() = EightWordsNullable(year, month, day, hour)
}

data class EightWordsNullable(override val year: IStemBranchOptional,
                              override val month: IStemBranchOptional,
                              override val day: IStemBranchOptional,
                              override val hour: IStemBranchOptional) : IEightWordsNullable, IEightWordsNullableFactory,
  Serializable {

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

    fun empty(): EightWordsNullable {
      return EightWordsNullable(StemBranchOptional.empty(),
                                StemBranchOptional.empty(),
                                StemBranchOptional.empty(),
                                StemBranchOptional.empty())
    }
  }
}

/**
 * 八字資料結構 , 四柱任何一柱都不可以為 null
 * 2006-06-12 將此 class 繼承 [EightWordsNullable]
 * 2018-04-01 起， 將此 class 與 [EightWordsNullable] 拆離繼承關係
 */
data class EightWords(override val year: StemBranch,
                      override val month: IStemBranch,
                      override val day: StemBranch,
                      override val hour: StemBranch) : IEightWords,
  Serializable {

  /** 以字串 "甲子","甲子","甲子","甲子" 方式 construct 此物件 , 任何一柱都不可以為 null */
  constructor(year: String, month: String, day: String, hour: String)
    : this(StemBranch[year.toCharArray()[0], year.toCharArray()[1]],
           StemBranch[month.toCharArray()[0], month.toCharArray()[1]],
           StemBranch[day.toCharArray()[0], day.toCharArray()[1]],
           StemBranch[hour.toCharArray()[0], hour.toCharArray()[1]])

  /** 直接用八個干支建立八字  */
  constructor(yearStem: Stem, yearBranch: Branch,
              monthStem: Stem, monthBranch: Branch,
              dayStem: Stem, dayBranch: Branch,
              hourStem: Stem, hourBranch: Branch)
    : this(StemBranch[yearStem, yearBranch],
           StemBranch[monthStem, monthBranch],
           StemBranch[dayStem, dayBranch],
           StemBranch[hourStem, hourBranch])

  /** 強制將 [EightWordsNullable] downcast 成 [EightWords] , 可能會拋出錯誤 , 注意 */
  constructor(ewn: IEightWordsNullable) : this(
    StemBranch[ewn.year], StemBranch[ewn.month], StemBranch[ewn.day],
    StemBranch[ewn.hour])

  override fun toString(): String {
    return ("\n" +
      hour.stem + day.stem + month.stem + year.stem
      + "\n" +
      hour.branch + day.branch + month.branch + year.branch)
  }
}