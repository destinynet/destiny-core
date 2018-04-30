package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.chinese.Branch
import java.io.Serializable

interface IPalmModel {
  val gender: Gender

  val year: Branch

  val month: Branch

  val day: Branch

  val hour: Branch

  val houseMap: Map<Branch, Palm.House>

  enum class Pillar {
    年, 月, 日, 時
  }

  enum class House {
    命, 財帛, 兄弟, 田宅, 男女, 奴僕, 配偶, 疾厄, 遷移, 官祿, 福德, 相貌
  }

  /**
   * 取得哪些宮位有「柱」坐落其中，列出來
   */
  val nonEmptyPillars: Map<Branch, Collection<Palm.Pillar>>


  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  fun getPillars(branch: Branch): Collection<Palm.Pillar>

  /**
   * 取得此柱，在哪個地支
   */
  fun getBranch(pillar: Palm.Pillar): Branch {
    return when (pillar) {
      Palm.Pillar.年 -> year
      Palm.Pillar.月 -> month
      Palm.Pillar.日 -> day
      Palm.Pillar.時 -> hour
    }
  }

  /** 取得此地支，是什麼宮  */
  fun getHouse(branch: Branch): Palm.House {
    return houseMap[branch]!!
  }

  /** 與上面相反，取得此宮位於什麼地支  */
  fun getBranch(house: Palm.House): Branch {
    return houseMap.map { (k, v) -> v to k }.toMap()[house]!!
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
      .map { i -> (i - 1) * 10 + 1 to Branch[month.index + (i - 1) * positive] }
      .sortedBy { pair -> pair.first }.toMap()
  }

  /**
   * 小運從掌中年上起月，月上起日，男順、女逆，輪數至本生日起運。本生日所在宮為一歲運，下一宮為二歲運。
   * @param age 虛歲 , 從 1 開始
   */
  fun getMinorFortunes(age: Int): Branch {
    val positive = if (gender === Gender.男) 1 else -1
    return Branch[day.index + (age - 1) * positive]
  }
}

data class PalmModel(

  override val gender: Gender,

  override val year: Branch,

  override val month: Branch,

  override val day: Branch,

  override val hour: Branch,

  override val houseMap: Map<Branch, Palm.House>
                    ) : IPalmModel, Serializable {


  /** 每「柱」各在哪個地支宮位 */
  private val pillarMap: Map<Palm.Pillar, Branch> = mapOf(
    Palm.Pillar.年 to year,
    Palm.Pillar.月 to month,
    Palm.Pillar.日 to day,
    Palm.Pillar.時 to hour)

  /**
   * @return 取得哪些宮位有「柱」坐落其中，列出來
   */
  override val nonEmptyPillars: Map<Branch, Collection<Palm.Pillar>>
    get() =
      Branch.values()
        .filter { pillarMap.values.contains(it) }
        .map { branch -> branch to pillarMap.filter { (_, v) -> v === branch }.keys }
        .toMap()

  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  override fun getPillars(branch: Branch): Collection<Palm.Pillar> {
    return pillarMap.filter { (_, v) -> v === branch }.keys
  }
}