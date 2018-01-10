/**
 * Created by smallufo on 2015-05-17.
 */
package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.chinese.Branch
import java.io.Serializable

/**
 * 達摩一掌金 資料結構
 */
open class Palm : Serializable {

  val gender: Gender

  val year: Branch

  val month: Branch

  val day: Branch

  val hour: Branch

  /** 每「柱」各在哪個地支宮位 */
  private val pillarMap: Map<Pillar, Branch>

  private val houseMap: Map<Branch, House>

  /**
   * @return 取得哪些宮位有「柱」坐落其中，列出來
   */
  val nonEmptyPillars: Map<Branch, Collection<Pillar>>
    get() =
      Branch.values()
        .filter { pillarMap.values.contains(it) }
        .map { branch -> branch to pillarMap.filter { (_,v) -> v === branch }.keys }
        .toMap()

  enum class Pillar {
    年, 月, 日, 時
  }

  enum class House {
    命, 財帛, 兄弟, 田宅, 男女, 奴僕, 配偶, 疾厄, 遷移, 官祿, 福德, 相貌
  }

  constructor(gender: Gender, year: Branch, month: Branch, day: Branch, hour: Branch, houseMap: Map<Branch, House>) {
    this.gender = gender
    this.year = year
    this.month = month
    this.day = day
    this.hour = hour
    this.houseMap = houseMap

    pillarMap = mapOf(
      Pillar.年 to year,
      Pillar.月 to month,
      Pillar.日 to day,
      Pillar.時 to hour)
  }

  protected constructor(other: Palm) {
    this.gender = other.gender
    this.year = other.year
    this.month = other.month
    this.day = other.day
    this.hour = other.hour
    this.houseMap = other.houseMap

    this.pillarMap = other.pillarMap.toMap()
  }

  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  fun getPillars(branch: Branch): Collection<Pillar> {
    return pillarMap.filter { (_,v) -> v === branch }.keys
  }

  /**
   * 取得此柱，在哪個地支
   */
  fun getBranch(pillar: Pillar): Branch {
    return when (pillar) {
      Palm.Pillar.年 -> year
      Palm.Pillar.月 -> month
      Palm.Pillar.日 -> day
      Palm.Pillar.時 -> hour
    }
  }

  /** 取得此地支，是什麼宮  */
  fun getHouse(branch: Branch): House {
    return houseMap[branch]!!
  }

  /** 與上面相反，取得此宮位於什麼地支  */
  fun getBranch(house: House): Branch {
    return houseMap.map{(k,v) -> v to k}.toMap()[house]!!
  }

  /**
   * 大運從掌中年上起月，男順、女逆，輪數至本生月起運。本生月所在宮為一運，下一宮為二運，而一運管10年。
   *
   * 大運盤，每運10年，從 1歲起. 1~10 , 11~20 , 21~30 ...
   * Map 的 key 為「運的開始」: 1 , 11 , 21 , 31 ...
   * @param count : 要算多少組大運
   */
  fun getMajorFortunes(count: Int): Map<Int, Branch> {
    val positive = if (gender === Gender.男) 1 else -1

    return (1..count)
      .map { i -> (i - 1) * 10 + 1 to Branch.get(month.index + (i - 1) * positive) }
      .sortedBy { pair -> pair.first }.toMap()
  }

  /**
   * 小運從掌中年上起月，月上起日，男順、女逆，輪數至本生日起運。本生日所在宮為一歲運，下一宮為二歲運。
   * @param age 虛歲 , 從 1 開始
   */
  fun getMinorFortunes(age: Int): Branch {
    val positive = if (gender === Gender.男) 1 else -1
    return Branch.get(day.index + (age - 1) * positive)
  }

  override fun toString(): String {
    return "[Palm " + "year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ']'.toString()
  }

  companion object {

    /** 取得地支對應的「星」 (子 -> 天貴星)  */
    fun getStar(branch: Branch): String {
      return when (branch) {
        Branch.子 -> "天貴"
        Branch.丑 -> "天厄"
        Branch.寅 -> "天權"
        Branch.卯 -> "天破"
        Branch.辰 -> "天奸"
        Branch.巳 -> "天文"
        Branch.午 -> "天福"
        Branch.未 -> "天驛"
        Branch.申 -> "天孤"
        Branch.酉 -> "天刃"
        Branch.戌 -> "天藝"
        Branch.亥 -> "天壽"
      }
    }

    /** 取得地支對應的「道」 (子 -> 佛道)  */
    fun getDao(branch: Branch): String {
      return when (branch) {
        Branch.子 -> "佛"
        Branch.丑 -> "鬼"
        Branch.寅 -> "人"
        Branch.卯 -> "畜生"
        Branch.辰 -> "修羅"
        Branch.巳 -> "仙"
        Branch.午 -> "佛"
        Branch.未 -> "鬼"
        Branch.申 -> "人"
        Branch.酉 -> "畜生"
        Branch.戌 -> "修羅"
        Branch.亥 -> "仙"
      }
    }
  }
}
