/**
 * Created by smallufo on 2015-05-23.
 */
package destiny.core.chinese.liuren.golden

import destiny.astrology.DayNight
import destiny.core.calendar.eightwords.EightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils

import java.io.Serializable

/**
 * 六壬金口訣，核心資料結構
 * 口訣 : Pithy
 */
class Pithy(

  /** 八字  */
  val eightWords: EightWords,

  /** 地分  */
  val direction: Branch,

  /** 月將（太陽星座）*/
  val monthSign: Branch,

  /** 取得「晝夜」 */
  val dayNight: DayNight,

  /** 貴神  */
  val benefactor: StemBranch) : Serializable {

  /**
   * 取得「人元」 : 演算法如同「五鼠遁時」法
   * 甲己還是甲 乙庚丙作初
   * 丙辛起戊子 丁壬庚子辰
   * 戊癸壬子頭 時元從子推
   */
  val human: Stem
    get() = StemBranchUtils.getHourStem(eightWords.day.stem, direction)

  /**
   * 取得「將神」 : 從時辰開始，順數至「地分」
   *
   * 從「地分」領先「時辰」多少
   * 接下來，將月將 加上此 step
   */
  val johnson: StemBranch
    get() {
      val steps = direction.getAheadOf(eightWords.hour.branch)
      //println("地分 " + direction + " 領先時辰 " + eightWords.hourBranch + "  " + steps + " 步")
      val branch = monthSign.next(steps)

      val stem = StemBranchUtils.getHourStem(eightWords.day.stem, monthSign.next(steps))
      //println("月將 = $monthSign , 加上 $steps 步 , 將神地支 = $branch , 天干為 $stem")
      return StemBranch.get(stem, branch)
    }

  /**
   * 取得「日」的空亡
   */
  val dayEmpties: Collection<Branch>
    get() = eightWords.day.empties


  override fun toString(): String {
    return "[Pithy " +
      "八字=" + eightWords +
      ", 地分=" + direction +
      ", 人元=" + human +
      ", 月將=" + monthSign +
      ", 貴神=" + benefactor +
      ']'.toString()
  }
}
