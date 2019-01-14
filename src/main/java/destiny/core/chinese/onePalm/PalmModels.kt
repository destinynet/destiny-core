package destiny.core.chinese.onePalm

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDateHour
import destiny.core.chinese.Branch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

interface IPalmModel {

  val gender: Gender

  val year: Branch

  val month: Branch

  val day: Branch

  val hour: Branch

  val houseMap: Map<Branch, House>

  enum class Pillar {
    年, 月, 日, 時
  }

  enum class House {
    命, 財帛, 兄弟, 田宅, 男女, 奴僕, 配偶, 疾厄, 遷移, 官祿, 福德, 相貌
  }

  /**
   * 取得哪些宮位有「柱」坐落其中，列出來
   */
  val nonEmptyPillars: Map<Branch, Collection<Pillar>>


  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  fun getPillars(branch: Branch): Collection<Pillar>

  /**
   * 取得此柱，在哪個地支
   */
  fun getBranch(pillar: Pillar): Branch {
    return when (pillar) {
      Pillar.年 -> year
      Pillar.月 -> month
      Pillar.日 -> day
      Pillar.時 -> hour
    }
  }

  /** 取得此地支，是什麼宮  */
  fun getHouse(branch: Branch): House {
    return houseMap[branch]!!
  }

  /** 與上面相反，取得此宮位於什麼地支  */
  fun getBranch(house: House): Branch {
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

data class PalmModel(

  override val gender: Gender,

  override val year: Branch,

  override val month: Branch,

  override val day: Branch,

  override val hour: Branch,

  override val houseMap: Map<Branch, IPalmModel.House>
                    ) : IPalmModel, Serializable {

  /** 每「柱」各在哪個地支宮位 */
  private val pillarMap: Map<IPalmModel.Pillar, Branch> = mapOf(
    IPalmModel.Pillar.年 to year,
    IPalmModel.Pillar.月 to month,
    IPalmModel.Pillar.日 to day,
    IPalmModel.Pillar.時 to hour)

  /**
   * @return 取得哪些宮位有「柱」坐落其中，列出來
   */
  override val nonEmptyPillars: Map<Branch, Collection<IPalmModel.Pillar>>
    get() =
      Branch.values()
        .filter { pillarMap.values.contains(it) }
        .map { branch -> branch to pillarMap.filter { (_, v) -> v === branch }.keys }
        .toMap()

  /**
   * 取得在某一地支宮位，包含了哪些「柱」 (年/月/日/時)
   */
  override fun getPillars(branch: Branch): Collection<IPalmModel.Pillar> {
    return pillarMap.filter { (_, v) -> v === branch }.keys
  }
}

interface IPalmMetaModel : IPalmModel {
  val lmt: ChronoLocalDateTime<*>
  val loc: ILocation
  val place: String?
  val name: String?
  val chineseDateHour: ChineseDateHour
}

data class HouseDescription(
  val branch: Branch,
  val dao: String,
  val star: String,
  val intro: String,
  val map: Map<IPalmModel.Pillar, String>
                           ) : Serializable

/** 一則 [IPalmModel] 的文字解釋 */
interface IPalmModelDesc {

  val houseDescriptions: List<HouseDescription>

  /** 時柱 對應的七言絕句 */
  val hourPoem: String

  /* 「時柱」的解釋 */
  val hourContent: String

}

/**
 * 2019 Jan 新增 , 提供網頁輸出使用
 */
interface IPalmMetaModelDesc : IPalmMetaModel, IPalmModelDesc

data class PalmModelDesc(
  override val houseDescriptions: List<HouseDescription>,
  override val hourPoem: String,
  override val hourContent: String
                        ) : IPalmModelDesc, Serializable


data class PalmMetaModel(
  private val palmModel: IPalmModel,
  override val lmt: ChronoLocalDateTime<*>,
  override val loc: ILocation,
  override val place: String?,
  override val name: String?,
  override val chineseDateHour: ChineseDateHour) : IPalmMetaModel, IPalmModel by palmModel, Serializable

data class PalmMetaModelDesc(
  val palmMetaModel: IPalmMetaModel,
  val palmModelDesc: IPalmModelDesc
                            ) : IPalmMetaModelDesc, IPalmMetaModel by palmMetaModel, IPalmModelDesc by palmModelDesc,
  Serializable

