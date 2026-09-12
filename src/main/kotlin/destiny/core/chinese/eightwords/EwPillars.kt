/**
 * Created by smallufo on 2026-09-12.
 */
package destiny.core.chinese.eightwords

import destiny.core.Gender
import destiny.core.Scale
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranch
import destiny.tools.serializers.GenderSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * **只有四柱**的八字 —— 古書命例（`ew-eightwords-v1`）的儲存格式。
 *
 * ## 🔴 它與 [EwBdnp] 的差別只有一件事：**沒有出生時刻與地點**
 *
 * 古書（三命通會、滴天髓闡微、人鑑·命理存驗）給的是四柱本身，不是出生時刻 ——
 * 原文就只寫「壬子壬子乙酉丁丑」。body 的 `_note` 自己註明了：
 *
 * > 古書命例 (純八字): time/location/age/上升星座/命宮干支/節氣資訊 已省略;
 * > 大運/流年起運歲數無法精確計算; 日主分數用簡化版 (土月假設非土令)。
 *
 * ⇒ 它**反序列化不成 [EwBdnp]**（`time` / `location` / `risingStemBranch` /
 * `risingSign` / `solarTermsPos` 都是非空欄位），而那正是 2026-09-11 之前
 * 741 筆古書命例一筆都比不到的原因。
 *
 * ## 為什麼不是「把 EwBdnp 的那幾個欄位放寬成可空」
 *
 * `time` / `location` 是 `IBirthDataNamePlace` 要求的非空成員 —— 放寬等於弱化
 * 那 69,581 筆**真的有出生時刻**的語料的型別保證，為了 1% 的例外犧牲 99% 的常態。
 *
 * ## ✅ 但兩者共用同一支簽章函式
 *
 * 兩者都實作 [IEightWords]，而 `Signatures.of` 收的正是 [IEightWords]
 * （2026-09-11 由 `EwBdnp` 放寬）。簽章的四階只用到日干、月支、日支、時支、年干支，
 * 沒有一項需要出生時刻 —— 所以**不必為古書再寫一份簽章規則**，
 * 而那正是 `Signatures` KDoc 紅字警告的那件事（兩份分岔的症狀是
 * 「查不到 → 自動退一階 → 照樣端得出人」，沒有人會發現退錯階）。
 *
 * ## ⚠️ 欄位取捨
 *
 * 依 dev 實測（741 筆）：`八字特徵` 722 筆非空、`日主分數` 741 筆都有、
 * `八分法解釋` 298 筆、`recentYears` **0 筆**、`大運` 全空（起運歲數算不出來）。
 * ⇒ 後兩者不收；前三者收但給預設值，因為古書語料的完整度本來就參差。
 */
@Serializable
data class EwPillars(
  @Serializable(with = GenderSerializer::class)
  val gender: Gender,
  val name: String? = null,

  /** 四柱。與 [EwBdnp.ew] 同一個形狀，所以兩者的 [IEightWords] 實作也一致 */
  @SerialName("八字")
  val ew: Map<Scale, StemBranch>,

  /** 十神與藏干。型別直接借 [EwBdnp.StemAndBranch] —— 儲存格式本來就是同一份 */
  @SerialName("八字干支")
  val ewDetails: Map<Scale, EwBdnp.StemAndBranch> = emptyMap(),

  @SerialName("納音")
  val nayin: Map<Scale, String> = emptyMap(),
  @SerialName("空亡")
  val voids: Map<Scale, String> = emptyMap(),
  @SerialName("生肖")
  val animal: String? = null,

  @SerialName("八字特徵")
  val notes: Set<EwEvent.EwIdentity> = emptySet(),

  /** ⚠️ 古書版是**簡化算法**（土月假設非土令），與 [EwBdnp.score] 不完全可比 */
  @SerialName("日主分數（八分法，滿分八分）")
  val score: Double? = null,
  @SerialName("八分法解釋")
  val scoreDescription: String? = null,

  /** body 裡那句自述。留著是為了讓「這份資料為什麼比較少」在 debug 時一眼看得到 */
  @SerialName("_note")
  val note: String? = null,
) : IEightWords {

  /**
   * ⚠️ 這四個是**衍生**的，讀 [ew]，所以不進 JSON、也不可能與四柱本身分岔。
   * 與 [EwBdnp] 的實作逐字相同 —— 兩者對簽章而言必須是同一種東西。
   */
  override val year: StemBranch get() = ew.getValue(Scale.YEAR)
  override val month: IStemBranch get() = ew.getValue(Scale.MONTH)
  override val day: StemBranch get() = ew.getValue(Scale.DAY)
  override val hour: IStemBranch get() = ew.getValue(Scale.HOUR)
}
