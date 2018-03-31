/**
 * @author smallufo
 * 2002/8/25 下午 11:22:48
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import java.io.Serializable


/**
 * 八字資料結構 , 四柱任何一柱都不可以為 null
 * 2006-06-12 將此 class 繼承 [EightWordsNullable]
 * 2018-04-01 起， 將此 class 與 [EightWordsNullable] 拆離繼承關係
 */
data class EightWords(override val year: StemBranch,
                      override val month: StemBranch,
                      override val day: StemBranch,
                      override val hour: StemBranch) : IEightWords , Serializable {

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
  constructor(ewn: IEightWordsNullable) : this(StemBranch[ewn.year], StemBranch[ewn.month], StemBranch[ewn.day],
                                              StemBranch[ewn.hour])

  override fun toString(): String {
    return ("\n" +
      hour.stem + day.stem + month.stem + year.stem
      + "\n" +
      hour.branch + day.branch + month.branch + year.branch)
  }


}
