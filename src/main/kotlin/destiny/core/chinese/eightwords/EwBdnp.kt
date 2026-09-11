/**
 * Created by smallufo on 2025-03-15.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.RequestDto
import destiny.core.Scale
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.tools.serializers.GenderSerializer
import destiny.tools.serializers.ILocationSerializer
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Bdnp stands for Birth-Data-Name-Place
 */
@RequestDto
@Serializable
data class EwBdnp(
  @Serializable(with = GenderSerializer::class)
  override val gender: Gender,
  @Serializable(with = LocalDateTimeSerializer::class)
  override val time: LocalDateTime,
  @Serializable(with = ILocationSerializer::class)
  override val location: ILocation,
  val age: Int?,
  override val name: String?,
  override val place: String?,
  @SerialName("生肖")
  val animal: String,
  @SerialName("命宮干支")
  val risingStemBranch: StemBranch,
  @SerialName("上升星座")
  val risingSign: ZodiacSign,
  /**
   * 四柱。
   *
   * ## ⚠️ 型別是 [StemBranch] 而不是 `String`（2026-09-11 由 `String` 改）
   *
   * JSON **完全沒變** —— `StemBranch` 是 enum，序列化成常數名（`"己巳"`），
   * 與先前 `it.year.toString()` 產出的字串逐字相同。已存的 body 照常反序列化，
   * 不需要 migration。（同一個類別裡的 [FortuneLarge.stemBranch] 早就是
   * `IStemBranch` 且存成純字串，這條路是走過的。）
   *
   * 🔴 **收具體的 `StemBranch` 而不是 `IStemBranch`（2026-09-11 使用者決定）**：
   * 陰陽不配的組合（「甲丑」之類）雖有門派使用，但太罕見，一律不收。
   * 代價是那種資料會在反序列化時就失敗 —— 那正是我們要的：
   * 與其讓它靜靜地變成 `StemBranchUnconstrained` 混進語料，不如當場炸掉。
   *
   * ⚠️ 與 [ewDetails] 是**同一組四柱的兩種表示**（這裡是干支，那裡多帶十神與藏干）。
   * 兩份存在同一個 body 裡是**現況不是設計** —— `ew` 可由 `ewDetails` 推出來，
   * 但拿掉它會改變 JSON 形狀，那才是真的要 migration 的改動。
   */
  @SerialName("八字")
  val ew: Map<Scale, StemBranch>,
  @SerialName("八字特徵")
  val notes: Set<EwEvent.EwIdentity>,
  @SerialName("納音")
  val nayin: Map<Scale, String>,
  @SerialName("空亡")
  val voids: Map<Scale, String>,
  @SerialName("八字干支")
  val ewDetails: Map<Scale, StemAndBranch>,
  @SerialName("節氣資訊")
  val solarTermsPos: String,
  @SerialName("大運")
  val fortuneLarges: List<FortuneLarge>,
  @SerialName("日主分數（八分法，滿分八分）")
  val score: Double,
  @SerialName("八分法解釋")
  val scoreDescription: String? = null,
  val recentYears: List<YearData>
) : IBirthDataNamePlace, IEightWords {

  /*
   * ─────────────────────────────────────────── IEightWords（2026-09-11）
   *
   * ## 🔴 為什麼要實作它
   *
   * 為了讓「從四柱算簽章」這件事**只有一份實作**。
   * `Signatures.of` 原本收 `EwBdnp`，於是古書命例（`ew-eightwords-v1`，
   * 沒有 time/location，反序列化不成 EwBdnp）就進不了比對 ——
   * 而替它們另寫一份簽章規則，正是 `Signatures` KDoc 紅字警告的那件事：
   * 兩份分岔時查不到會自動退一階、照樣端得出人，沒有人會發現退錯階。
   *
   * ⇒ `Signatures.of` 改收 [IEightWords]，兩種來源共用同一支函式。
   *
   * ## ⚠️ 這四個是**衍生**的，不是新欄位
   *
   * 全部讀 [ew]，所以不進 JSON，也不可能與四柱本身分岔。
   */
  override val year: StemBranch get() = ew.getValue(Scale.YEAR)
  override val month: IStemBranch get() = ew.getValue(Scale.MONTH)
  override val day: StemBranch get() = ew.getValue(Scale.DAY)
  override val hour: IStemBranch get() = ew.getValue(Scale.HOUR)

  @Serializable
  data class StemReaction(
    val stem: Stem,
    @SerialName("十神")
    val relation: String
  )

  @Serializable
  data class StemAndBranch(
    @SerialName("天干")
    val stem: StemReaction,
    @SerialName("地支")
    val branch: Branch,
    @SerialName("地支藏干")
    val hiddenStems: List<StemReaction>
  )

  /**
   * 大運
   */
  @Serializable
  data class FortuneLarge(
    @SerialName("isCurrent")
    val current: Boolean,
    @SerialName("干支")
    val stemBranch: IStemBranch,
    @SerialName("十神藏干")
    val stemAndBranch: StemAndBranch,
    @SerialName("大運特徵")
    val notes: Set<EwEvent.EwFlow>,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    val startAge: Int,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    val endAge: Int,
  )

  /**
   * 流年
   */
  @Serializable
  data class YearData(
    val year: Int,
    @SerialName("isCurrent")
    val current: Boolean,
    @SerialName("干支")
    val stemBranch: IStemBranch,
    @SerialName("十神藏干")
    val stemAndBranch: StemAndBranch,
    @SerialName("流年特徵")
    val notes: Set<EwEvent.EwFlow>
  )
}
