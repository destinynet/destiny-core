/**
 * Created by smallufo on 2014-08-03.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale
import destiny.core.chinese.*
import destiny.tools.ChineseStringTools
import java.io.Serializable

/**
 * 八字資料結構，可以包含 null 值
 */
sealed interface IEightWordsNullable {
  val year: IStemBranchOptional
  val month: IStemBranchOptional
  val day: IStemBranchOptional
  val hour: IStemBranchOptional

  /** 取得四柱  */
  val stemBranches: List<IStemBranchOptional>
    get() = listOf(year, month, day, hour)

  fun get(scale: Scale): IStemBranchOptional {
    return when(scale) {
      Scale.YEAR -> year
      Scale.MONTH -> month
      Scale.DAY -> day
      Scale.HOUR -> hour
    }
  }

  fun getScaleMap() : Map<Scale , IStemBranchOptional>

  fun getScale(scale: Scale): IStemBranchOptional {
    return getScaleMap()[scale]!!
  }
}

fun IEightWordsNullable.getInts(): List<Int> {

  return when(this) {
    is IEightWords -> {
      listOf(
        year.stem.indexFromOne,
        year.branch.indexFromOne,
        month.stem.indexFromOne,
        month.branch.indexFromOne,
        day.stem.indexFromOne,
        day.branch.indexFromOne,
        hour.stem.indexFromOne,
        hour.branch.indexFromOne
      )
    }
    is EightWordsNullable -> {
      listOf(
        year.stem?.indexFromOne ?: 0,
        year.branch?.indexFromOne ?: 0,
        month.stem?.indexFromOne ?: 0,
        month.branch?.indexFromOne ?: 0,
        day.stem?.indexFromOne ?: 0,
        day.branch?.indexFromOne ?: 0,
        hour.stem?.indexFromOne ?: 0,
        hour.branch?.indexFromOne ?: 0
      )
    }
  }
}

interface IEightWords : IEightWordsNullable, IEightWordsNullableFactory {
  override val year: StemBranch
  override val month: IStemBranch
  override val day: StemBranch
  override val hour: IStemBranch

  override val stemBranches: List<IStemBranch>
    get() = listOf(year, month, day, hour)

  override val eightWordsNullable: IEightWordsNullable
    get() = EightWordsNullable.of(year, month, day, hour)

  override fun getScaleMap(): Map<Scale, IStemBranch> {
    return mapOf(
      Scale.YEAR to year,
      Scale.MONTH to month,
      Scale.DAY to day,
      Scale.HOUR to hour,
    )
  }

  override fun getScale(scale: Scale) : IStemBranch {
    return getScaleMap()[scale]!!
  }
}

@kotlinx.serialization.Serializable
data class EightWordsNullable private constructor(override val year: IStemBranchOptional,
                                                  override val month: IStemBranchOptional,
                                                  override val day: IStemBranchOptional,
                                                  override val hour: IStemBranchOptional) : IEightWordsNullable, IEightWordsNullableFactory, Serializable {


  override val eightWordsNullable: EightWordsNullable
    get() = this

  override fun getScaleMap(): Map<Scale, IStemBranchOptional> {
    return mapOf(
      Scale.YEAR to year,
      Scale.MONTH to month,
      Scale.DAY to day,
      Scale.HOUR to hour,
    )
  }

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

    fun of(yStem: Stem?, yBranch: Branch?, mStem: Stem?, mBranch: Branch?, dStem: Stem?, dBranch: Branch?, hStem: Stem?, hBranch: Branch?) : IEightWordsNullable {
      return StemBranch.of(yStem, yBranch)?.let { year ->
        StemBranch.of(mStem, mBranch)?.let { month ->
          StemBranch.of(dStem, dBranch)?.let { day ->
            StemBranch.of(hStem, hBranch)?.let { hour ->
              EightWords(year, month, day, hour)
            }
          }
        }
      } ?: EightWordsNullable(
        StemBranchOptional(yStem, yBranch),
        StemBranchOptional(mStem, mBranch),
        StemBranchOptional(dStem, dBranch),
        StemBranchOptional(hStem, hBranch)
      )
    }

    fun of(year: IStemBranchOptional, month: IStemBranchOptional , day: IStemBranchOptional, hour: IStemBranchOptional) : IEightWordsNullable {
      return of(year.stem , year.branch , month.stem, month.branch, day.stem, day.branch, hour.stem, hour.branch)
    }

    fun empty(): EightWordsNullable {
      return EightWordsNullable(StemBranchOptional.empty(),
                                StemBranchOptional.empty(),
                                StemBranchOptional.empty(),
                                StemBranchOptional.empty())
    }

    fun getFromIntList(list: List<Int>): IEightWordsNullable {
      require(list.size == 8)
      val yStem = if (list[0] == 0) null else Stem[list[0] - 1]
      val yBranch = if (list[1] == 0) null else Branch[list[1] - 1]
      val mStem = if (list[2] == 0) null else Stem[list[2] - 1]
      val mBranch = if (list[3] == 0) null else Branch[list[3] - 1]
      val dStem = if (list[4] == 0) null else Stem[list[4] - 1]
      val dBranch = if (list[5] == 0) null else Branch[list[5] - 1]
      val hStem = if (list[6] == 0) null else Stem[list[6] - 1]
      val hBranch = if (list[7] == 0) null else Branch[list[7] - 1]

      return of(yStem, yBranch, mStem, mBranch, dStem, dBranch, hStem, hBranch)
    }
  }
}

/**
 * 八字資料結構 , 四柱任何一柱都不可以為 null
 * 2006-06-12 將此 class 繼承 [EightWordsNullable]
 * 2018-04-01 起， 將此 class 與 [EightWordsNullable] 拆離繼承關係
 */
@kotlinx.serialization.Serializable
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
