package destiny.core

import destiny.core.astrology.BirthDataGrain
import destiny.tools.ai.model.Domain
import destiny.tools.ai.model.DomainSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.Serializable as KSerializable
import destiny.tools.JSerializable

/**
 * 合盤兩造各自的時刻精度。
 *
 * ## 為什麼不是 enum
 *
 * 原本是四個 entry 的 enum（FULL / DATE 兩級 × 內外兩造）。[BirthDataGrain] 有五級之後，
 * 窮舉就會爆炸成 5×5；而且那個 2×2 是**有損的**：時辰生（[BirthDataGrain.HOUR2]）被壓成
 * DATE，於是合盤的可靠性警告會說「對方的出生時間未知、月亮不可用」——
 * 但時辰已知，月亮誤差僅 ±0.55°，完全可用。
 *
 * 改為直接攜帶兩造的 [BirthDataGrain]，判斷一律問 [BirthDataGrain.includeAxis] 等閘門，
 * 新增精度時不必再動這裡。
 *
 * ## 序列化
 *
 * 舊的四個名稱保留在 companion 上，且序列化字串**對這四種組合完全不變**
 * （見 [SynastryGrainSerializer]），故 DB 既有的 JSONB 與 chatbot 的 postback 零轉換。
 * 其餘組合寫成 `INNER|OUTER`（如 `HOUR2|MINUTE`）。
 *
 * 淘汰舊格式的分階段計畫見
 * `destiny-core-impl/docs/plans/2026-08-18-synastry-grain-legacy-removal.md`。
 * 但**讀取端的相容層不在淘汰範圍內** —— 見 [Companion.of]。
 */
@KSerializable(with = SynastryGrainSerializer::class)
data class SynastryGrain(val inner: BirthDataGrain, val outer: BirthDataGrain) {

  /**
   * 序列化／持久化用的識別字串。四種舊組合目前仍維持舊名。
   *
   * **這裡的查表是可以拿掉的**（改為永遠輸出 `INNER|OUTER`），但那是有前置條件的一步：
   * 一旦拿掉，每一張合盤圖送給 `chart.smallufo.com` 的字串都會變成新格式，
   * 那台機器落後一次就是合盤圖全掛（現在只有 HOUR2／DAY_NIGHT 的案例會受影響）。
   * 順序與前置條件見 `docs/plans/2026-08-18-synastry-grain-legacy-removal.md`。
   *
   * 注意這與 [Companion.of] 的相容層是**兩件事**：寫出端可以改，讀取端不行。
   */
  val name: String
    get() = LEGACY_NAMES[this] ?: "${inner.name}|${outer.name}"

  override fun toString(): String = name

  companion object {
    val BOTH_FULL = SynastryGrain(BirthDataGrain.MINUTE, BirthDataGrain.MINUTE)
    val INNER_FULL_OUTER_DATE = SynastryGrain(BirthDataGrain.MINUTE, BirthDataGrain.DAY)
    val INNER_DATE_OUTER_FULL = SynastryGrain(BirthDataGrain.DAY, BirthDataGrain.MINUTE)
    val BOTH_DATE = SynastryGrain(BirthDataGrain.DAY, BirthDataGrain.DAY)

    /**
     * 四種舊 enum entry 名 ↔ 組合。
     *
     * **不可移除**（理由見 [of]）。這份表同時服務讀與寫：寫出端哪天可以不用它，
     * 讀取端永遠需要。
     */
    private val LEGACY_NAMES: Map<SynastryGrain, String> by lazy {
      mapOf(
        BOTH_FULL to "BOTH_FULL",
        INNER_FULL_OUTER_DATE to "INNER_FULL_OUTER_DATE",
        INNER_DATE_OUTER_FULL to "INNER_DATE_OUTER_FULL",
        BOTH_DATE to "BOTH_DATE",
      )
    }

    /**
     * 取代 enum 的 `valueOf`。認得舊四名與 `INNER|OUTER` 新格式。
     *
     * ## 舊四名的支援「不可移除」—— 即使 DB 已經遷移乾淨
     *
     * 合盤的 session 走 `SynastryHoroscopeFactory`，其 `postbackActionDataDao` 為 null，
     * 於是 `AbstractSessionFactory.toEncryptedJson` 把整個 payload（含 grain 字串）
     * **加密進 LINE 訊息的 postback 裡**，而不是存進我們的資料庫。
     *
     * 那些字串躺在使用者的 LINE 聊天記錄中：我們查不到、改不了，而使用者隨時可能
     * 往上滑、點一則兩年前的合盤訊息。移除舊名支援 = 那一點就炸。
     *
     * 換來的只有「少四行查表」，代價卻是舊訊息報錯 —— 不划算。
     * 分階段計畫（`docs/plans/2026-08-18-synastry-grain-legacy-removal.md`）
     * 的第 4 步「移除相容層」因此**刻意標記為不執行**。
     *
     * 未來若有人來清理死碼：這不是死碼，它服務的是我們無法遷移的資料來源。
     */
    fun of(id: String): SynastryGrain {
      LEGACY_NAMES.entries.firstOrNull { it.value == id }?.let { return it.key }
      val parts = id.split("|")
      require(parts.size == 2) { "Unknown SynastryGrain: $id" }
      return SynastryGrain(BirthDataGrain.of(parts[0]), BirthDataGrain.of(parts[1]))
    }

    fun ofOrNull(id: String?): SynastryGrain? = id?.let { runCatching { of(it) }.getOrNull() }
  }
}

/** 維持四種舊組合的字串形式不變；其餘寫成 `INNER|OUTER`。 */
object SynastryGrainSerializer : KSerializer<SynastryGrain> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SynastryGrain", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: SynastryGrain) = encoder.encodeString(value.name)
  override fun deserialize(decoder: Decoder): SynastryGrain = SynastryGrain.of(decoder.decodeString())
}

/** 內盤自身的時刻精度 */
val SynastryGrain.innerGrain: BirthDataGrain
  get() = inner

/** 外盤自身的時刻精度 */
val SynastryGrain.outerGrain: BirthDataGrain
  get() = outer

enum class SynastryRelationship {
  LOVE,
  FRIENDSHIP,
  PARTNERSHIP,
  RIVAL,
  PARENT_CHILD,
}

interface ISynastryModel : JSerializable {
  val inner: IBirthDataNamePlace
  val outer: IBirthDataNamePlace
  val grain: SynastryGrain
  val domainBdnp: Domain.Bdnp
  val relationship: SynastryRelationship
}

@KSerializable
data class SynastryModel(
  @Contextual
  override val inner: IBirthDataNamePlace,
  @Contextual
  override val outer: IBirthDataNamePlace,
  override val grain: SynastryGrain,
  @KSerializable(with = DomainSerializer::class)
  override val domainBdnp: Domain.Bdnp,
  override val relationship: SynastryRelationship
) : ISynastryModel

enum class SynastryDomain {
  OVERVIEW,
  EMOTIONAL,
  COMMON,
  FINANCIAL,
  COMMUNICATION,
  INTIMACY,
  GROWTH,
  SUPPORT,
  STABILITY,
  COLLABORATION,
  INNOVATION,
  RESPECT,
  TENSIONS,
  HIERARCHY,
}


