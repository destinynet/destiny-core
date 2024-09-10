/**
 * Created by smallufo on 2015-05-23.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.IBirthData
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUtils
import destiny.tools.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IPithyModel {

  /** 八字  */
  val eightWords: IEightWords

  /** 地分  */
  val direction: Branch

  /** 月將（太陽星座）*/
  val monthSign: Branch

  /** 取得「晝夜」 */
  val dayNight: DayNight

  /** 貴神  */
  val benefactor: StemBranch

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
      logger.trace {
        "地分 " + direction + " 領先時辰 " + eightWords.hour.branch + "  " + steps + " 步"
      }
      val branch = monthSign.next(steps)

      val stem = StemBranchUtils.getHourStem(eightWords.day.stem, monthSign.next(steps))
      logger.trace {
        "月將 = $monthSign , 加上 $steps 步 , 將神地支 = $branch , 天干為 $stem"
      }
      return StemBranch[stem, branch]
    }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}


/**
 * 六壬金口訣，核心資料結構
 * 口訣 : Pithy
 */
data class Pithy(

  /** 八字  */
  override val eightWords: IEightWords,

  /** 地分  */
  override val direction: Branch,

  /** 月將（太陽星座）*/
  override val monthSign: Branch,

  /** 取得「晝夜」 */
  override val dayNight: DayNight,

  /** 貴神  */
  override val benefactor: StemBranch
) : IPithyModel, Serializable


/**
 * 添加了性別、詳細時間
 */
interface IPithyModernModel : IPithyModel, IBirthData

data class PithyModernModel(
  private val pithy: IPithyModel,
  override val gender: Gender,
  override val time: ChronoLocalDateTime<*>,
  override val location: ILocation
) : IPithyModernModel, IPithyModel by pithy, Serializable


/**
 * 給 web 端使用，多了 地名、question 等比較無關的欄位
 */
interface IPithyDetailModel : IPithyModernModel {

  val place: String?
  val question: String?
  val method: Method

  /** 起課方式  */
  enum class Method {
    RANDOM, MANUAL
  }
}


data class PithyDetailModel(
  private val pithyModernModel: IPithyModernModel,
  override val place: String?,
  override val question: String?,
  override val method: IPithyDetailModel.Method
) : IPithyDetailModel, IPithyModernModel by pithyModernModel, Serializable
