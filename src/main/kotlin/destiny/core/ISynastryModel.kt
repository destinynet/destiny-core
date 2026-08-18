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
 * （見 [SynastryGrainSerializer]），故 DB 既有的 JSONB 與 chatbot 的 context map 零轉換。
 * 其餘組合寫成 `INNER|OUTER`（如 `HOUR2|MINUTE`）。
 */
@KSerializable(with = SynastryGrainSerializer::class)
data class SynastryGrain(val inner: BirthDataGrain, val outer: BirthDataGrain) {

  /** 序列化／持久化用的識別字串。四種舊組合維持舊名。 */
  val name: String
    get() = LEGACY_NAMES[this] ?: "${inner.name}|${outer.name}"

  override fun toString(): String = name

  companion object {
    val BOTH_FULL = SynastryGrain(BirthDataGrain.MINUTE, BirthDataGrain.MINUTE)
    val INNER_FULL_OUTER_DATE = SynastryGrain(BirthDataGrain.MINUTE, BirthDataGrain.DAY)
    val INNER_DATE_OUTER_FULL = SynastryGrain(BirthDataGrain.DAY, BirthDataGrain.MINUTE)
    val BOTH_DATE = SynastryGrain(BirthDataGrain.DAY, BirthDataGrain.DAY)

    private val LEGACY_NAMES: Map<SynastryGrain, String> by lazy {
      mapOf(
        BOTH_FULL to "BOTH_FULL",
        INNER_FULL_OUTER_DATE to "INNER_FULL_OUTER_DATE",
        INNER_DATE_OUTER_FULL to "INNER_DATE_OUTER_FULL",
        BOTH_DATE to "BOTH_DATE",
      )
    }

    /** 取代 enum 的 `valueOf`。認得舊四名與 `INNER|OUTER` 新格式。 */
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


