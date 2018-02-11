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
data class EightWords(override val year: StemBranch,
                      override val month: StemBranch,
                      override val day: StemBranch,
                      override val hour: StemBranch
                     ) : EightWordsNullable(year, month, day, hour) {

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

  //override
  val yearStem = year.stem

  //override
  val monthStem = month.stem

  //override
  val dayStem = day.stem

  //override
  val hourStem = hour.stem

  //override
  val yearBranch = year.branch

  //override
  val monthBranch = month.branch

  //override
  val dayBranch = day.branch

  //override
  val hourBranch = hour.branch

  override val stemBranches = listOf(year, month, day, hour)

  val nullable: EightWordsNullable = EightWordsNullable(year, month, day, hour)


  override fun toString(): String {
    return ("\n" +
      hour.stem + day.stem + month.stem + year.stem
      + "\n" +
      hour.branch + day.branch + month.branch + year.branch)
  }


}
