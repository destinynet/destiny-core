package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.IBirthDataNamePlace
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * 出生資料的時刻精度。總序：[DAY] < [DayNightOnly] < [HOUR2] < [MINUTE]。
 *
 * ## 儲存規則：一律存「已知區間的中點」
 *
 * | grain | 已知區間 | 儲存的時刻 |
 * |---|---|---|
 * | [DAY] | 一整日 | 12:00 |
 * | [DayNightOnly] `(DAY)` | 日出～日落 | 該區間中點 ≈ 12:00（太陽上中天） |
 * | [DayNightOnly] `(NIGHT)` | 日落～次日日出 | 該區間中點 ≈ 00:00（太陽下中天） |
 * | [HOUR2] | 該時辰 | 該時辰中點 |
 * | [MINUTE] | 退化為一點 | 該點 |
 *
 * 這條規則換來一條**可檢查的不變式**：儲存的時刻在儲存的地點下解讀，必須與 grain 相容 ——
 * [MINUTE] 恆真；[HOUR2] 須 `IHour.getHour(time, loc) == 使用者輸入的時辰`；
 * [DayNightOnly] 須 `IDayNight.getDayNight(time, loc) == value`；[DAY] 無約束。
 * 有了它，「grain 說夜生但時刻是正午」這種自相矛盾的資料進不來。
 *
 * ## 為什麼是 sealed class 而非 enum
 *
 * [DayNightOnly] 需要攜帶 payload。舊版以兩個 enum entry 展開（`DAY_NIGHT_DIURNAL` /
 * `DAY_NIGHT_NOCTURNAL`）—— 在只有兩個值時可行，但一旦要表達「精度 × 已解析狀態」
 * 的乘積就會爆炸。舊名保留在 companion 上，既有呼叫點不必改。
 *
 * 序列化與持久化的字串形式與 enum 時代**完全一致**（見 [BirthDataGrainSerializer]），
 * 故 DB 舊資料零 migration。
 */
@Serializable(with = BirthDataGrainSerializer::class)
sealed class BirthDataGrain {

  /**
   * 序列化／持久化用的識別字串。
   *
   * sealed class 沒有 enum 的 `name`，明確補上 —— 既有呼叫點（如 converter 的
   * `context.grain.name`）與 [BirthDataGrainSerializer] 都依賴它。
   */
  abstract val name: String

  /** 只知日期，連晝夜都不知道。時刻為捏造的正午。 */
  data object DAY : BirthDataGrain() {
    override val name: String = "DAY"
  }

  /**
   * 不知時刻，但知晝生／夜生 —— ADB 的 Rodden 註記、家屬口述常有此等級。
   *
   * 足以解鎖只依晝夜生的技法（如 Firdaria；序列以年計，±12h 誤差可忽略），
   * 但 ASC／宮位仍不可得。
   */
  data class DayNightOnly(val value: DayNight) : BirthDataGrain() {
    override val name: String = when (value) {
      DayNight.DAY   -> "DAY_NIGHT_DIURNAL"
      DayNight.NIGHT -> "DAY_NIGHT_NOCTURNAL"
    }
  }

  /**
   * 知時辰（子～亥），儲存的時刻為**該時辰的中點**。
   *
   * 刻意**不攜帶 [destiny.core.chinese.Branch]**：時辰可由已儲存的時刻 + 地點以
   * `IHour.getHour(time, loc)` 反查，存進 grain 只是會 stale 的重複資料。
   *
   * 存中點而非起點是關鍵決定：中點距任一邊界約一小時，遠大於「真太陽時 vs 平太陽時」
   * 的分歧（均時差 ≤16 分）與經度時差（台灣 121°E vs 120°E ＝ 4 分），
   * 因此 round-trip（存進去 → 讀回來反查時辰）對這些 config 差異完全免疫。
   * 存起點則貼著邊界，任何定義微調都可能讓它掉到前一個時辰。
   *
   * **晝夜不可由此 grain 單獨決定** —— 見 [DayNightSource]。
   */
  data object HOUR2 : BirthDataGrain() {
    override val name: String = "HOUR2"
  }

  /** 精確到分。 */
  data object MINUTE : BirthDataGrain() {
    override val name: String = "MINUTE"
  }

  override fun toString(): String = name

  companion object {
    /** 舊 enum entry 名，保留以免既有呼叫點全面改寫。 */
    val DAY_NIGHT_DIURNAL: DayNightOnly = DayNightOnly(DayNight.DAY)

    /** 舊 enum entry 名，保留以免既有呼叫點全面改寫。 */
    val DAY_NIGHT_NOCTURNAL: DayNightOnly = DayNightOnly(DayNight.NIGHT)

    /**
     * 依總序排列的全部五個值。取代 enum 的 `entries`。
     *
     * **必須 lazy**：`data object DAY` 的建構會觸發外層 [BirthDataGrain] 的 class init，
     * 進而觸發 companion init；若此處是 eager 的 `listOf(DAY, ...)`，
     * 就會在 `DAY` 的 INSTANCE 尚未指派時讀到 null，整份清單全是 null 且不拋例外。
     */
    val entries: List<BirthDataGrain> by lazy {
      listOf(DAY, DAY_NIGHT_DIURNAL, DAY_NIGHT_NOCTURNAL, HOUR2, MINUTE)
    }

    /** 取代 enum 的 `valueOf`。無法辨識時拋 [IllegalArgumentException]，與 `valueOf` 行為一致。 */
    fun of(id: String): BirthDataGrain = entries.firstOrNull { it.name == id }
      ?: throw IllegalArgumentException("Unknown BirthDataGrain: $id")

    /** 無法辨識時回傳 null 的寬容版本。 */
    fun ofOrNull(id: String?): BirthDataGrain? = id?.let { key -> entries.firstOrNull { it.name == key } }
  }
}

/**
 * 維持與 enum 時代 byte-identical 的字串形式。
 *
 * sealed class 的預設多型序列化會產出 `{"type": "..."}` 物件，而 `grain` 欄位散布在
 * `TimeLineEventsModel` / `MergedUserEventsModel` 等餵給 LLM 的素材裡 ——
 * 換掉 JSON 形狀等於同時動到素材與所有既存快取。
 */
object BirthDataGrainSerializer : KSerializer<BirthDataGrain> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BirthDataGrain", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: BirthDataGrain) {
    encoder.encodeString(value.name)
  }

  override fun deserialize(decoder: Decoder): BirthDataGrain {
    return BirthDataGrain.of(decoder.decodeString())
  }
}

/**
 * 是否包含 Axis 點 (ASC/MC) 的計算。
 *
 * 只有 [BirthDataGrain.MINUTE] 才有意義：[BirthDataGrain.HOUR2] 的時刻不確定達 ±1 小時，
 * ASC 誤差 ±15°，超過半個星座。
 */
val BirthDataGrain.includeAxis: Boolean
  get() = this == BirthDataGrain.MINUTE

/**
 * 是否計算 Profection (小限)
 * Profection 需要宮位資訊，因此需要精確時間
 */
val BirthDataGrain.includeProfection: Boolean
  get() = this == BirthDataGrain.MINUTE

/**
 * 是否計算 Lunar Returns (月返照)
 * 月返照盤需要精確的宮位，因此需要精確時間
 */
val BirthDataGrain.includeLunarReturns: Boolean
  get() = this == BirthDataGrain.MINUTE

/**
 * 是否算得出**時柱**（八字）與命宮／身宮（紫微）—— 這兩套只需要**時辰**，不需要分鐘。
 *
 * 這是唯一一個 [BirthDataGrain.HOUR2] 與 [BirthDataGrain.MINUTE] **等價**的閘門：
 * 時柱由時辰決定，而 HOUR2 儲存的正是該時辰的中點 —— 依定義必落在同一個時辰內，
 * 反查必得原時辰（見 [BirthDataGrain.HOUR2] 的 KDoc 與 round-trip 測試）。
 *
 * 對照 [includeAxis]：ASC 需要分鐘（±1 小時 → ±15°），所以那個閘門對 HOUR2 是 false。
 * 兩者判準不同，不可共用 —— 舊實作以 `grain == MINUTE` 一併把八字紫微擋掉，
 * 是把「西洋盤需要分鐘」的要求誤加到中式命理上。
 */
val BirthDataGrain.includeChineseHour: Boolean
  get() = this == BirthDataGrain.MINUTE || this == BirthDataGrain.HOUR2

/**
 * 本命月亮的位置是否可信 —— 亦即可否作為相位標的。
 *
 * 月亮日行約 13.18°（＝ **0.55°/小時**）：
 * - 無精確時刻時以正午錨定，誤差達 **±6.5°**，**超過任何 orb**；
 * - [BirthDataGrain.HOUR2] 的時刻不確定僅 ±1 小時，誤差 **±0.55°**，落在任何 orb 之內
 *   （換星座的風險亦由 22% 降至 1.8%）。故時辰級解鎖本命月亮 ——
 *   這是時辰相對日級真正值錢的地方，比 Firdaria 值錢。
 *
 * 無精確時刻時本命月亮不該進入任何相位判讀：它產出的是「看起來很有說服力的噪音」，
 * 對 LLM 而言比缺漏更糟 —— 分不出真假，就會拿去論斷。
 *
 * 注意這與「排除**行運**月亮」是兩件事：行運月亮被排除的理由是效應僅數小時，
 * 與出生時刻精度無關。兩者都要做，但條件不同 —— 舊實作只做了行運側，
 * 註解寫著 `exclude MOON`，本命側卻照收。
 */
val BirthDataGrain.includeLunarPosition: Boolean
  get() = this == BirthDataGrain.MINUTE || this == BirthDataGrain.HOUR2

/**
 * 能自報時刻精度的出生資料。
 *
 * [IBirthDataNamePlace] 本身只是「時刻 + 地點 + 姓名」，**不帶精度** —— 它拿到的時刻，
 * 可能是使用者填的分鐘，也可能是 [BirthDataGrain.HOUR2] 的時辰中點或 [BirthDataGrain.DAY]
 * 的捏造正午。消費端若無從分辨，就只能假設精確，然後排出捏造的 ASC 與宮位。
 *
 * 由持有 grain 的實體（如 `destiny.service.base.BirthData`）實作，讓
 * 只吃 [IBirthDataNamePlace] 的 digester 問得出來。
 */
interface IGrainedBirthData : IBirthDataNamePlace {
  val birthDataGrain: BirthDataGrain
}

/**
 * 取得出生資料的時刻精度；來源不表態時退回 [BirthDataGrain.MINUTE]。
 *
 * 退回 MINUTE 而非 DAY，是為了與既有語意一致（`BirthData.grain` 為 null 代表
 * 未表態的歷史資料，一律視同精確），也才不會讓沒實作 [IGrainedBirthData] 的
 * 既有呼叫端（如事件盤、賽事盤）突然失去軸點。
 *
 * **新的消費端請優先讓自己的模型攜帶 grain**，這個函式是給「型別上拿不到 grain、
 * 但實際物件多半有」的既有介面用的補救。
 */
fun IBirthDataNamePlace.resolveGrain(): BirthDataGrain =
  (this as? IGrainedBirthData)?.birthDataGrain ?: BirthDataGrain.MINUTE

