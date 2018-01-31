/**
 * @author smallufo
 * 2002/8/25 下午 11:22:48
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch


/**
 * 八字資料結構 , 四柱任何一柱都不可以為 null
 * 2006/06/12 將此 class 繼承 EightWordsNullable
 */
data class EightWords(val year: StemBranch, val month: StemBranch, val day: StemBranch, val hour: StemBranch) {

  /** 以字串 "甲子","甲子","甲子","甲子" 方式 construct 此物件 , 任何一柱都不可以為 null */
  constructor(year: String, month: String, day: String, hour: String)
    : this(StemBranch[year.toCharArray()[0], year.toCharArray()[1]],
            StemBranch[month.toCharArray()[0], month.toCharArray()[1]],
            StemBranch[day.toCharArray()[0], day.toCharArray()[1]],
            StemBranch[hour.toCharArray()[0], hour.toCharArray()[1]])

  /** 直接用八個干支建立八字  */
  constructor(yearStem: Stem,
              yearBranch: Branch,
              monthStem: Stem,
              monthBranch: Branch,
              dayStem: Stem,
              dayBranch: Branch,
              hourStem: Stem,
              hourBranch: Branch)
    : this(StemBranch[yearStem, yearBranch],
            StemBranch[monthStem, monthBranch],
            StemBranch[dayStem, dayBranch],
            StemBranch[hourStem, hourBranch])

  val yearStem = year.stem

  val monthStem = month.stem

  val dayStem = day.stem

  val hourStem = hour.stem

  val yearBranch = year.branch

  val monthBranch = month.branch

  val dayBranch = day.branch

  val hourBranch = hour.branch

  val stemBranches = listOf(year, month, day, hour)

  fun getIntList() : List<Int> {
    return listOf(
      year.stem.indexFromOne ,
      year.branch.indexFromOne,
      month.stem.indexFromOne,
      month.branch.indexFromOne,
      day.stem.indexFromOne,
      day.branch.indexFromOne,
      hour.stem.indexFromOne,
      hour.branch.indexFromOne
          )
  }

  val nullable : EightWordsNullable = EightWordsNullable(year , month , day , hour)


  override fun toString(): String {
    return ("\n" +
      hour.stem + day.stem + month.stem + year.stem
      + "\n" +
      hour.branch + day.branch + month.branch + year.branch)
  }



}
