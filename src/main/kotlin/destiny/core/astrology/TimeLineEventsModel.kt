package destiny.core.astrology

import destiny.core.DayNight
import destiny.core.EventRole
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.Situation
import destiny.core.astrology.prediction.*
import destiny.core.calendar.*
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng.Companion.toLng
import destiny.tools.serializers.*
import destiny.tools.serializers.astrology.AstroPointSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.chrono.ChronoLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException


sealed interface ITimeLineEvent {
  val astro: AstroEvent
  val description: String
  val convergentTime: GmtJulDay
  val divergentTime: GmtJulDay
  val source: EventSource
}

class ITimeLineEventSerializer(private val gmtJulDayTimeSerializer: KSerializer<GmtJulDay>,
                               private val gmtJulDayDateSerializer : KSerializer<GmtJulDay>) : KSerializer<ITimeLineEvent> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ITimeLineEvent") {
    element<String>("source")
    element<String>("description")
    element("convergentTime", gmtJulDayTimeSerializer.descriptor)
    element("divergentDate", gmtJulDayDateSerializer.descriptor)
  }

  override fun serialize(encoder: Encoder, value: ITimeLineEvent) {
    encoder.encodeStructure(descriptor) {
      encodeStringElement(descriptor, 0, value.source.name)
      encodeStringElement(descriptor, 1, value.description)
      encodeSerializableElement(descriptor, 2, gmtJulDayTimeSerializer, value.convergentTime)
      encodeSerializableElement(descriptor, 3, gmtJulDayDateSerializer, value.divergentTime)
    }
  }

  override fun deserialize(decoder: Decoder): ITimeLineEvent {
    throw UnsupportedOperationException("Deserialization not supported")
  }
}

data class TimeLineEvent(
  override val source: EventSource,
  val astroEvent: AstroEventDto,
  override val divergentTime: GmtJulDay
) : ITimeLineEvent {
  override val astro: AstroEvent
    get() = astroEvent.event

  override val description: String
    get() = astroEvent.event.description

  override val convergentTime: GmtJulDay
    get() = astroEvent.begin
}


interface ITimeLineEventsModel {
  val natal: IPersonHoroscopeDto
  val grain: BirthDataGrain
  val fromTime: GmtJulDay
  val toTime: GmtJulDay
  val events: List<ITimeLineEvent>
  val lunarReturns : List<ReturnCoverageDto>
  val annualProfections : List<Profection>
  val monthProfections : List<Profection>
  val firdarias: List<Firdaria>
  val solarReturns: List<ReturnCoverageDto>

  /**
   * 區間內的黃道釋放，每個 Lot 一筆（Fortune 與 Spirit）。
   *
   * 按 Lot 分組而非攤平：兩個 Lot 的期間混在同一串 `List<ZodiacalReleasing>` 裡就分不出誰是誰，
   * 而 angularity（PEAK 等）只在知道是從哪個 Lot 釋放時才可解讀。
   */
  val zodiacalReleasings: List<ZrByLot>
}

@Serializable
data class TimeLineEventsModel(
  override val natal: IPersonHoroscopeDto,
  override val grain: BirthDataGrain,
  @Contextual
  override val fromTime: GmtJulDay,
  @Contextual
  override val toTime: GmtJulDay,
  override val events: List<@Contextual ITimeLineEvent>,
  override val lunarReturns: List<ReturnCoverageDto> = emptyList(),
  override val annualProfections: List<Profection> = emptyList(),
  override val monthProfections: List<Profection> = emptyList(),
  override val firdarias: List<Firdaria> = emptyList(),
  override val solarReturns: List<ReturnCoverageDto> = emptyList(),
  override val zodiacalReleasings: List<ZrByLot> = emptyList(),
) : ITimeLineEventsModel

@Serializable
enum class EventSentiment {
  POSITIVE, // 明確是好的
  NEGATIVE, // 明確是壞的
  NEUTRAL,  // 中性，沒有強烈好壞
}

@Serializable(with = AbstractEventSerializer::class)
sealed class AbstractEvent {
  abstract val situation: Situation
  abstract val details: String
  abstract val sentiment: EventSentiment?

  /**
   * 這一筆的主詞。**`null` ＝ 未答**（唯一語意）。
   *
   * ⚠️ 與前身的 `agency` 欄位不同：舊欄位的 `null` 是「照型別預設」，
   * 而新設計裡型別**不再提供預設** —— [Situation.roles] 只列舉可能，不猜最常見。
   *
   * 單值 situation 不必填（[effectiveRole] 會從 `roles` 補），
   * 兩值 situation 這一欄才帶資訊（主動追求 vs 被追、成人自願受洗 vs 嬰兒受洗）。
   *
   * ⚠️ 值域外的值（例如替只可能承受的 situation 申報發起端）會在建構時炸掉（見 `init`）——
   * 沉默地留著一筆，比炸掉糟得多：它會一路流進統計而沒人看得出來。
   */
  abstract val role: EventRole?

  /**
   * 合併後的主詞。單值 situation 由型別補；兩值 situation 未答時為 null。
   *
   * **讀取端一律問這裡**，不要自己 `role ?: situation.roles.singleOrNull()`
   * —— 同一段邏輯多處推導是本專案的頭號缺陷族。
   *
   * 寫成函式而非屬性 —— 見 [grain] 的 KDoc：衍生屬性會被 `FormatSpec.of` 反射成
   * LLM 該產出的必填欄位。
   */
  fun effectiveRole(): EventRole? = role ?: situation.roles.singleOrNull()

  /**
   * 主詞是否落在 [Situation.roles] 之外。回傳錯誤訊息，合法則 null。
   *
   * ⚠️ **這個守衛沒有消失，只是退化。** 前身做的是**跨層一致性檢查**
   * （型別層先驗 vs 逐筆申報可能互相矛盾）；本函式只做**成員檢查**（值在不在值域內）。
   * 真正被治好的是「同一根軸被編碼兩次」，不是「守衛歸零」——
   * 兩個獨立 enum 的 pair 仍寫得出非法組合。
   *
   * ⚠️ 由**四個子型別各自的 `init`** 呼叫，不放在本基底 —— Kotlin 的初始化順序讓
   * 基底 `init` 早於子型別建構子參數的賦值，在那裡讀 [role] 只會拿到 null。
   * 判斷邏輯只有這一份，子型別各自只是呼叫它。
   */
  fun roleConflict(): String? {
    val declared = role ?: return null
    return if (declared in situation.roles) null
    else "$situation 的可能主詞是 ${situation.roles}，本筆卻申報 $declared —— " +
      "若真有這種個案，該修的是 situation 的 roles，不是在這裡塞一個值域外的值。"
  }

  /** 代表月份 —— 排序與顯示用。有延時者取其**起始**月，要涵蓋範圍請改用 [yearMonthRange]。 */
  abstract fun yearMonth() : YearMonth

  /**
   * 事件涵蓋的月份區間 —— 分群與月級掃描的依據。
   *
   * 點事件退化為 `start == endInclusive`（即 [yearMonth]），故此方法是 [yearMonth] 的超集；
   * 判讀「這事件跨到哪」一律問這裡。這是繼 [grain]（精度）之後的**第三軸：延時** ——
   * 「六月下旬起連環爆、燒到七月底未止」這種事，在只有單一時間點的模型裡無法表達。
   */
  open fun yearMonthRange(): YearMonthRange = yearMonth().let { YearMonthRange(it, it) }

  /**
   * 本事件的時刻精度。由子型別身分決定，不進序列化，也不可外部指定。
   *
   * 判讀端請問這個（或 [canDayLevelTransit] / [chartGrain]），不要對子型別寫窮舉 `when`。
   *
   * **衍生成員一律寫成函式**（同 [yearMonth] / [yearMonthRange] / [chartTime] / [dateLabel]），
   * 只有真正序列化的四個欄位才是屬性。這不是風格偏好：`FormatSpec.of` 的 JSON schema 是用
   * `KClass.memberProperties` 反射產生的，衍生屬性會被當成 LLM 該產出的必填欄位，
   * 而反序列化用的 `Json` 未開 `ignoreUnknownKeys`，LLM 照做就會炸。
   */
  abstract fun grain(): EventGrain

  /**
   * 可用來排事件盤的當地時刻；[EventGrain.MONTH] 無日期可錨定，回傳 null。
   *
   * [EventGrain.DAY] 取正午 —— 這是**刻意的捏造**，僅為了讓日行度較慢的行星（太陽、水金火）
   * 有個代表位置；其 ASC/MC 完全無效，故 [chartGrain] 會把它標成 [BirthDataGrain.DAY]，
   * 由下游的 grain 閘門把軸點與宮位擋掉。
   */
  abstract fun chartTime(): LocalDateTime?

  /** 原始日期字面，精度隨 [grain]（`2020-03` / `2020-03-03` / `2020-03-03T14:30`）。稽核比對用，不參與計算。 */
  abstract fun dateLabel(): String
}

@Serializable
data class MonthEvent(
  @Serializable(with = YearMonthSerializer::class)
  val date: YearMonth,
  override val situation: Situation,
  override val details: String,
  override val sentiment: EventSentiment? = null,
  override val role: EventRole? = null,
) : AbstractEvent() {
  init { roleConflict()?.let { throw IllegalArgumentException(it) } }

  override fun yearMonth(): YearMonth {
    return date
  }

  override fun grain(): EventGrain = EventGrain.MONTH
  override fun chartTime(): LocalDateTime? = null
  override fun dateLabel(): String = date.toString()
}

@Serializable
data class DayEvent(
  @Serializable(with = LocalDateSerializer::class)
  val date: LocalDate,
  override val situation: Situation,
  override val details: String,
  override val sentiment: EventSentiment? = null,
  override val role: EventRole? = null,
) : AbstractEvent() {
  init { roleConflict()?.let { throw IllegalArgumentException(it) } }

  override fun yearMonth(): YearMonth {
    return YearMonth.from(date)
  }

  override fun grain(): EventGrain = EventGrain.DAY
  override fun chartTime(): LocalDateTime = date.atTime(12, 0)
  override fun dateLabel(): String = date.toString()
}

@Serializable
data class MinuteEvent(
  @Serializable(with = LocalDateTimeSerializer::class)
  val date: LocalDateTime,
  override val situation: Situation,
  override val details: String,
  override val sentiment: EventSentiment? = null,
  override val role: EventRole? = null,
) : AbstractEvent() {
  init { roleConflict()?.let { throw IllegalArgumentException(it) } }

  override fun yearMonth(): YearMonth {
    return YearMonth.from(date)
  }

  override fun grain(): EventGrain = EventGrain.MINUTE
  override fun chartTime(): LocalDateTime = date
  override fun dateLabel(): String = date.toString()
}

/**
 * 有延時的事件 —— 名聲崩跌、纏訟、久病，這類「一路燒下去」的事，本質上不是某一天。
 *
 * 之所以不能用 [DayEvent] 硬湊：把 2023 那次爭議記成「道歉日」，錨點就落在整串事件的
 * **最後一步**而非起點。以該錨點回推的星象規則會整體晚一截，而這種偏移在單點模型裡看不出來。
 *
 * @param from     期間起點。若只知道月份，取該月 1 日，並讓 [ignition] 留 null。
 * @param to       期間終點；**null 代表進行中／未止**（見 [ongoing]），而不是「不知道」。
 * @param ignition 起爆日 —— 值得為它排一張日盤的那一天（影片上傳、宣判、確診）。
 *                 通常等於 [from]，但當 [from] 只是「六月下旬」這種粗略起點時必須留 null，
 *                 否則就是拿一個捏造的日期去排盤，正是 [EventGrain] 要防的事。
 *                 有值 → [grain] 為 [EventGrain.DAY]；null → [EventGrain.MONTH]，只做月級掃描。
 */
@Serializable
data class PeriodEvent(
  @Serializable(with = LocalDateSerializer::class)
  val from: LocalDate,
  @Serializable(with = LocalDateSerializer::class)
  val to: LocalDate? = null,
  @Serializable(with = LocalDateSerializer::class)
  val ignition: LocalDate? = null,
  override val situation: Situation,
  override val details: String,
  override val sentiment: EventSentiment? = null,
  override val role: EventRole? = null,
) : AbstractEvent() {
  init { roleConflict()?.let { throw IllegalArgumentException(it) } }


  init {
    require(to == null || !to.isBefore(from)) { "PeriodEvent: to ($to) 早於 from ($from)" }
    require(ignition == null || (!ignition.isBefore(from) && (to == null || !ignition.isAfter(to)))) {
      "PeriodEvent: ignition ($ignition) 落在 $from .. $to 之外"
    }
  }

  /**
   * 是否仍在進行中（[to] 為 null）。[AbstractEventSerializer] 的出口會據此附加
   * `"ongoing": true` 標記，否則 LLM 會把「沒有終點」讀成「已結束」。
   *
   * 寫成函式而非屬性 —— 見 [AbstractEvent.grain] 的 KDoc：衍生成員一律寫成函式，
   * 否則 `FormatSpec.of` 反射出的 JSON schema 會把它當成 LLM 該產出的必填欄位。
   */
  fun ongoing(): Boolean = to == null

  override fun yearMonth(): YearMonth = YearMonth.from(from)

  /**
   * 進行中（[ongoing]）者只由起點決定區間，**刻意不外推到「現在」** ——
   * 資料模型不該知道 today 是哪天。呼叫端請改用 [effectiveYearMonthRange] 以 viewDay 提供上界。
   */
  override fun yearMonthRange(): YearMonthRange =
    YearMonthRange(YearMonth.from(from), YearMonth.from(to ?: from))

  override fun grain(): EventGrain = if (ignition != null) EventGrain.DAY else EventGrain.MONTH
  override fun chartTime(): LocalDateTime? = ignition?.atTime(12, 0)
  override fun dateLabel(): String = "$from..${to ?: ""}${ignition?.let { "@$it" } ?: ""}"
}

object AbstractEventSerializer : KSerializer<AbstractEvent> {

  // 定義三種日期格式，從最詳細到最不詳細
  private val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral('T')
    .appendValue(java.time.temporal.ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(java.time.temporal.ChronoField.MINUTE_OF_HOUR, 2)
    .optionalStart()
    .appendLiteral(':')
    .appendValue(java.time.temporal.ChronoField.SECOND_OF_MINUTE, 2)
    .optionalStart()
    .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 0, 9, true)
    .optionalEnd()
    .optionalEnd()
    .toFormatter()
  private val LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD
  private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

  /**
   * ⚠️ **這份 descriptor 是手寫的，而且 roundtrip 測試抓不到它的錯。**
   *
   * 2026-09-02 以變異測試證實：把 `element("situation", …)` 改回 `"eventType"` 之後，
   * [serialize] / [deserialize] 的 roundtrip 測試**仍然全綠** —— 因為實際的編解碼
   * 走的是四個子型別**編譯器產生的** serializer，本 descriptor 從頭到尾沒被碰到。
   *
   * ⚠️ **更正（2026-09-02 複核）**：本段初稿寫「唯一的消費者是 `FormatSpec.of`」，**那是錯的**，
   * 而且與本檔 [AbstractEvent.grain] 的 KDoc 自相矛盾 —— 那一段寫的才對：
   * `FormatSpec.of` 的 JSON schema 是用 `KClass.memberProperties` 反射產生的，
   * **不經過 `KSerializer.descriptor`**（追蹤鏈：`FormatSpec.of` → `KClass.toJsonSchema`
   * → `orderedProperties()` → `memberProperties`）。
   *
   * ⇒ 這份 descriptor **目前沒有 runtime 消費者**：編解碼走子型別的 serializer、
   * schema 走 Kotlin 反射，兩條路都不經過這裡。它是 `KSerializer` 介面要求存在的
   * **對外宣告的契約**。
   *
   * ⇒ 欄位名寫錯的懲罰因此不是例外、不是紅燈、**今天也不是錯的 schema** ——
   * 是它會**靜靜地與真實欄位漂開**，直到哪天出現一個真的 descriptor-driven 的消費者
   * 才一次爆出來。而那時沒有人會記得這份 descriptor 從未被驗證過。
   *
   * ⛔ **`AbstractEventTest` 的 `descriptor 的 element 名稱` 那條是本 descriptor 的
   * 唯一守門員。** 不要刪它，也不要以為 roundtrip 測試涵蓋了這裡。
   */
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AbstractEvent") {
    // 點事件（MinuteEvent / DayEvent / MonthEvent）：由 date 的字面格式決定是哪一種
    element<String>("date", isOptional = true)
    // 有延時的事件（PeriodEvent）：以 from 的存在作為判別式
    element<String>("from", isOptional = true)
    element<String>("to", isOptional = true)
    element<String>("ignition", isOptional = true)
    // 出口附加的唯讀標記（見 serialize），輸入端寬容剝除 —— 不是資料欄位
    element<Boolean>("ongoing", isOptional = true)
    element("situation", Situation.serializer().descriptor)
    element<String>("details")
    element("sentiment", EventSentiment.serializer().descriptor, isOptional = true)
    element("role", EventRole.serializer().descriptor, isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: AbstractEvent) {
    when (value) {
      is MonthEvent  -> encoder.encodeSerializableValue(MonthEvent.serializer(), value)
      is DayEvent    -> encoder.encodeSerializableValue(DayEvent.serializer(), value)
      is MinuteEvent -> encoder.encodeSerializableValue(MinuteEvent.serializer(), value)
      is PeriodEvent -> {
        if (value.ongoing()) {
          // 進行中的事件在出口附加顯式標記 —— 「to 欄位缺席」對 LLM 而言與「已結束」無法區分。
          // deserialize 端會把此標記剝除，維持 roundtrip 對稱。
          val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This serializer can only be used with JSON format.")
          val obj = jsonEncoder.json.encodeToJsonElement(PeriodEvent.serializer(), value).jsonObject
          jsonEncoder.encodeJsonElement(JsonObject(obj + ("ongoing" to JsonPrimitive(true))))
        } else {
          encoder.encodeSerializableValue(PeriodEvent.serializer(), value)
        }
      }
    }
  }

  override fun deserialize(decoder: Decoder): AbstractEvent {
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw IllegalStateException("This serializer can only be used with JSON format.")

    // 將整個 JSON 物件讀取為 JsonElement
    val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

    // 判別式：有 "from" 就是有延時的事件，其餘一律靠 "date" 的字面格式分派。
    // 兩者互斥 —— 同時出現代表上游搞混了，寧可炸掉也不要沉默地丟掉一半資訊。
    if (jsonObject.containsKey("from")) {
      require(!jsonObject.containsKey("date")) {
        "AbstractEvent : 'from' 與 'date' 不可並存 —— 有延時的事件用 from/to，點事件用 date。"
      }
      // "ongoing" 是出口附加的唯讀標記（見 serialize），輸入端寬容剝除 ——
      // 曾被序列化的素材（或照抄範例的 LLM）餵回來時才不會炸
      return Json.decodeFromJsonElement(PeriodEvent.serializer(), JsonObject(jsonObject.filterKeys { it != "ongoing" }))
    }

    // 提取 "date" 欄位的字串值
    val dateString = jsonObject["date"]?.jsonPrimitive?.content
      ?: throw IllegalArgumentException("JSON object must contain a 'date' field.")

    // 核心邏輯：根據字串格式決定如何解析。從最詳細的格式開始嘗試。
    return try {
      // 1. 嘗試解析為 LocalDateTime (e.g., YYYY-MM-DDTHH:MM:SS or YYYY-MM-DDTHH:MM)
      LocalDateTime.parse(dateString, LOCAL_DATE_TIME_FORMATTER)
      // 如果成功，則將整個物件作為 MinuteEvent 進行反序列化
      Json.decodeFromJsonElement(MinuteEvent.serializer(), jsonObject)
    } catch (e: DateTimeParseException) {
      try {
        // 2. 嘗試解析為 LocalDate (e.g., YYYY-MM-DD)
        LocalDate.parse(dateString, LOCAL_DATE_FORMATTER)
        // 如果成功，則將整個物件作為 DayEvent 進行反序列化
        Json.decodeFromJsonElement(DayEvent.serializer(), jsonObject)
      } catch (e2: DateTimeParseException) {
        try {
          // 3. 嘗試解析為 YearMonth (e.g., YYYY-MM)
          YearMonth.parse(dateString, YEAR_MONTH_FORMATTER)
          // 如果成功，則將整個物件作為 MonthEvent 進行反序列化
          Json.decodeFromJsonElement(MonthEvent.serializer(), jsonObject)
        } catch (e3: DateTimeParseException) {
          throw IllegalArgumentException("Date string '$dateString' is not in a valid format (YYYY-MM-DD'T'HH:mm[:ss], YYYY-MM-DD, or YYYY-MM).", e3)
        }
      }
    }
  }
}

/**
 * 事件的**有效**月份區間：進行中（[PeriodEvent.ongoing]）者由呼叫端以 [ongoingUpperBound]
 * 提供上界（通常是 viewDay 所在月）—— 這是 [PeriodEvent.yearMonthRange] KDoc 承諾的落地點。
 *
 * 資料模型不知道 today，所以「延燒中的事件燒到哪」只能在讀取時計算：
 * 不外推的話，六月起延燒未止的事件在七月底看盤，七月的行運根本不會被掃到。
 * 非進行中的事件（含所有點事件）原樣返回，[ongoingUpperBound] 為 null 亦原樣返回。
 */
fun AbstractEvent.effectiveYearMonthRange(ongoingUpperBound: YearMonth?): YearMonthRange {
  val range = yearMonthRange()
  return if (ongoingUpperBound != null && this is PeriodEvent && ongoing() && ongoingUpperBound > range.endInclusive) {
    YearMonthRange(range.start, ongoingUpperBound)
  } else {
    range
  }
}

/**
 * 依 [effectiveYearMonthRange] 分群 —— 各自向外擴張 [extMonth] 個月後合併相鄰／重疊者。
 *
 * 以區間（而非單一 [AbstractEvent.yearMonth]）為依據，[PeriodEvent] 才能把自己跨到的月份
 * 一併拉進同一群。點事件退化成 `start == endInclusive`，行為與改動前完全相同。
 *
 * @param ongoingUpperBound 進行中事件的區間上界（通常是 viewDay 所在月）。
 *   提供時，延燒中的事件會把它燒到的月份（乃至期間內發生的其他事件）拉進同一群。
 */
fun List<AbstractEvent>.groupAdjacentEvents(extMonth: Int = 1, ongoingUpperBound: YearMonth? = null): List<List<AbstractEvent>> {
  if (this.size < 2) {
    return listOf(this)
  }

  val mergedRanges: List<YearMonthRange> = this.map { it.effectiveYearMonthRange(ongoingUpperBound) }.groupMergedRanges(extMonth)

  return mergedRanges.map { range: YearMonthRange ->
    this.filter { event -> event.effectiveYearMonthRange(ongoingUpperBound).overlaps(range) }
  }
}

interface ITimeLineWithUserEventsModel : ITimeLineEventsModel {
  val today: LocalDate
  val summary: String
  val userEvents : List<AbstractEvent>
}

@Serializable
data class TimeLineWithUserEventsModel(
  private val timeLineEventsModel : ITimeLineEventsModel,
  @Serializable(with = LocalDateSerializer::class)
  override val today: LocalDate,
  override val summary: String,
  override val userEvents: List<AbstractEvent>
) : ITimeLineWithUserEventsModel , ITimeLineEventsModel by timeLineEventsModel

@Serializable
data class EventGroup(
  @Contextual
  val fromTime : GmtJulDay,
  @Contextual
  val toTime : GmtJulDay,
  val userEvents : List<AbstractEvent>,
  val astroEvents : List<@Contextual ITimeLineEvent>,
  val lunarReturns : List<ReturnCoverageDto>,
  val firdarias: List<Firdaria>,
  val profections: List<Profection>,
  /** 事件窗內的黃道釋放（L1/L2，兩個 Lot）—— 以事件為條件的樣本，答「出事時走到哪」；基準率屬 [Past.zodiacalReleasings] 的全段序列，L3 統計走工具 */
  val zodiacalReleasings: List<ZrByLot> = emptyList(),
  @SerialName("transit_synastry_map")
  val transits : Map<@Contextual GmtJulDay, Synastry>
)

/**
 * 掃描層的身分 —— past 與 future 共用同一組定義，這正是重點：
 * LLM 要「從 past 已驗證的 pattern 對照 future」，前提是兩邊用**相同的幾何詞彙**掃描。
 * 若某類訊號（如月相、行運星互相位）只存在於一側，該類規則就永遠無法校準。
 */
@Serializable
enum class ScanLayer {
  /** 推運層：太陽弧 + 次限（past 的日級錨點另含三推／小限）。找「年份級」的背景。 */
  PROGRESSION,

  /** 背景層：火星 + 外行星，含行運星互相位、滯留、食、慢星換座。找「窗口」。 */
  BACKGROUND,

  /** 快層：日水金對本命。職能是把背景層標出的窗口定位到 ±2 天，不是自己找窗口。 */
  FAST,

  /** 月相層：只吐月相（日月角度），不吐月亮對本命的相位（月速 13°/日，效應僅數小時）。 */
  LUNAR_PHASE
}

/**
 * 素材的取樣說明書 —— 把「掃了什麼詞彙、掃到哪裡」寫進資料本身。
 *
 * 動機：取樣邊界若只存在於程式碼（如快層只覆蓋前 90 天、past 的月相只掃事件群窗口），
 * 讀素材的 LLM 分不出「沒事發生」與「我們沒掃」—— 這正是本專案自己批評過的**沉默截斷**。
 * 附帶效益：盲測各輪的素材版本可以直接 diff coverage，不必再靠人工在 docs 裡記錄。
 *
 * 欄位刻意用人讀的字串（而非結構化型別）：消費者是 LLM 與盲測稽核者，不是程式。
 *
 * @param transiting   此層行運端（外圈）的星體
 * @param natalTargets 本命側標的的文字描述；月相層無本命側，留 null
 * @param features     此層產出的事件種類（transit-to-natal aspects, stations, eclipses…）
 * @param span         覆蓋範圍。全段者寫實際日期區間；窗口式者寫規則（如 "each dated event −5 ~ +1 days"）
 * @param note         取樣邊界的補充說明 —— 讀者「必須知道否則會誤讀」的那句話寫在這
 */
@Serializable
data class ScanCoverage(
  val layer: ScanLayer,
  val transiting: List<String> = emptyList(),
  val natalTargets: String? = null,
  val features: List<String> = emptyList(),
  val span: String,
  val note: String? = null
)

/**
 * 一段星體順逆三相之一（入影／逆行／出影）。
 *
 * ⚠️ **不是逐日資料。** `IRetrograde.getDailyRetrogrades()` 是給日曆用的逐日序列，
 * 長跨度會爆量；這裡存的是 `getPeriodCycles()` 的區間形狀。
 * 與 [StationaryMoment] **同源於一次掃描** —— 兩者分成兩種查詢語意，底下不得掃兩次。
 */
@Serializable
data class RetrogradePhaseSpan(
  @Serializable(with = AstroPointSerializer::class)
  val star: AstroPoint,
  val phase: RetrogradePhase,
  @Contextual val from: GmtJulDay,
  @Contextual val to: GmtJulDay,
)

/**
 * 一次留（轉向），以及該時刻對本命各點的相位。
 *
 * ⚠️ [contacts] 在**產生時**就過了 [allowsNatalTarget] 閘門 —— 與 `ReturnSpan` 同款。
 * 下游（corpus、funCall）拿不到 grain，這裡不濾就沒有第二次機會。
 */
@Serializable
data class StationaryMoment(
  @Serializable(with = AstroPointSerializer::class)
  val star: AstroPoint,
  val type: StationaryType,
  @Contextual val gmt: GmtJulDay,
  val contacts: List<SynastryAspect> = emptyList(),
)

/**
 * past 與 future 兩側投影到計數／取用工具母體（corpus）時共有的「與事件無關」欄位。
 *
 * ## 為什麼要有這個介面
 *
 * 兩側各自新增一種天象時，要記得改的地方有五處（[Past] 欄位、[Future] 欄位、
 * 兩側掃描、兩條投影路徑）—— 全靠人記。實際踩過的形態：取用工具掛在 FORECAST 側，
 * 而 future 的投影漏了對應欄位，工具於是永遠回「不適用」——
 * 「工具說有、素材說沒有」是本專案付過多次代價的缺陷族。
 * 投影端只准從這個介面取這些欄位；漏掉哪一側，編譯器先亮。
 *
 * ## ⚠️ [corpusReturns] 的對應是不對稱的，而那正是要釘住的語意
 *
 * past 這一側**不是** [Past.solarReturns]（那是按事件群取的**事件條件樣本**，
 * 拿去當分母就是倖存者偏誤），而是 [Past.fullSpanReturns]（全段、與事件無關）；
 * future 側的 [Future.solarReturns] 本來就與事件無關（還沒有事件），可直接投影。
 * 這個對應先前只存在於兩條投影路徑的程式碼裡 —— 拿錯欄位不會有任何錯誤訊息。
 *
 * ## ⛔ 期間類（firdaria／profection／ZR）**刻意不在**此介面
 *
 * 兩側雖都有 [Past.firdariaPeriods]／[Future.firdariaPeriods] 等欄位，但投影端只有
 * past 側取用 —— future 的 corpus 是 returns-only（只掛取用類工具），期間類唯一的
 * 工具是計數類，在沒有校準事件窗的一側沒有分子可數。把期間欄位放進本介面，
 * 等於邀請 future 側也投影它、繞過「預測側與過去之間唯一通道是觸發表」的設計。
 * 哪天真有期間類的**取用**工具掛上預測側，再擴充本介面，讓編譯器強迫兩側同補。
 */
interface ICorpusProjectable {
  val fromTime: GmtJulDay
  val toTime: GmtJulDay
  /** 該側時段內的逆行三相區間 */
  val retrogradeSpans: List<RetrogradePhaseSpan>
  /** 該側時段內的留（轉向點），含該時刻對本命的相位（已過 grain 閘門） */
  val stationaries: List<StationaryMoment>
  /** 與事件無關的返照序列 —— past 是全段母體，future 是預測窗序列。見類別 KDoc 的不對稱說明 */
  val corpusReturns: List<IReturnDto>
}

@Serializable
data class Past(
  val eventGroups: List<EventGroup>,
  val solarReturns: List<@Contextual IReturnDto>,
  val longTermTriggers: List<@Contextual ITimeLineEvent>,
  @Contextual
  override val fromTime: GmtJulDay,
  @Contextual
  override val toTime: GmtJulDay,
  val coverage: List<ScanCoverage> = emptyList(),
  /**
   * 負對照窗 —— 與 [eventGroups] **同一組掃描層、同樣一個日曆月**，但該月不屬於任何已記錄事件。
   *
   * 動機：讀素材的 LLM 只看得到「有事的月份長什麼樣」，於是任何在事件月出現的組態都顯得特殊。
   * 沒有平凡月份可比，精確率就無從估計 —— 這正是 R9 自評的第一威脅（疊層法則召回 8/8、
   * 精確率因窗口取樣不可估）。給幾個對照範例，比給一串統計數字更貼 LLM 的推理形態。
   *
   * **語意務必精確**：`userEvents` 為空代表「該月不落在任何**已記錄**事件的窗口內」，
   * 不代表「該月什麼都沒發生」—— 校準集是手挑的重大事件，不是完整生活史。
   * 這層限制寫在對應的 [ScanCoverage.note] 裡，不可省略，否則就是本專案自己批評過的沉默截斷。
   *
   * 與 [eventGroups] 分開存放而非混入其中：混入的話，「空的 userEvents」會與
   * 「有事但事件沒被記錄到的群」在結構上無法區分。
   */
  val negativeControlGroups: List<EventGroup> = emptyList(),
  /**
   * 已記錄事件集本身的說明 —— 幾筆、跨多久、平均密度，以及那個密度是**下界**。
   *
   * 動機：讀素材的 LLM 會拿事件筆數除以跨度來當基準率，據以估計「未來 N 個月會不會有事」。
   * 但校準集是手挑的重大事件，不是完整生活史，這樣算出來的密度系統性偏低。
   * 密度是從資料本身就能算出來的東西，缺的是「這個分母代表什麼」——補上這句，
   * 推論由讀者自己做。
   *
   * **只陳述事實，不下指示**：寫「此為下界」是描述取樣性質，寫「不要低估」則是在教答案；
   * 盲測素材只能是前者。
   */
  val eventSetNote: String? = null,
  /**
   * 全段的逆行三相區間。**素材不印**（它是背景日曆，不是對本命的事件），只走 funCall 這條路。
   * 與 [stationaries] 同源於一次 `IRetrograde.getPeriodCycles()`。
   */
  override val retrogradeSpans: List<RetrogradePhaseSpan> = emptyList(),
  /** 全段的留（轉向點），含該時刻對本命的相位（已過 grain 閘門）。 */
  override val stationaries: List<StationaryMoment> = emptyList(),
  /**
   * ⭐ 全段掃描（[longTermTriggers]，＝計數母體）**每個 source 實際移動了哪些點**。
   *
   * ## 為什麼母體要自陳這件事
   *
   * 讀者手上只有結果：一個「沒被掃到的行運星」與一個「掃了但沒成相的行運星」
   * 在資料上長得一模一樣，而前者的計數是 0 卻附著收據 ——
   * 與 [FullSpanCorpus.gatedNatalTargets] 要解的是同一個病，只是換成移動端。
   *
   * ⚠️ **必須由掃描設定導出，不得從資料反推**（`occasions` 裡出現過哪些行運星）。
   * 反推會把「掃了但這段期間沒成相」誤報成「不在母體裡」—— 那正是本欄要防的那種假陳述。
   * 推導的唯一正典是 [movingPointsOf]。
   */
  val longTermMovingPoints: Map<EventSource, Set<@Serializable(with = AstroPointSerializer::class) AstroPoint>> = emptyMap(),
  /**
   * 全段的法達期間。
   *
   * ⚠️ 與 [EventGroup.firdarias] **語意不同，兩者都需要**：
   * 群裡那份只涵蓋有事件的月份，是**以事件為條件**取出來的樣本 ——
   * 讀者由它看得出「出事時法達走到哪」，卻算不出「其餘時間走到哪」，
   * 於是任何以它為分母的比率都帶倖存者偏誤。本欄提供的是全段母體。
   *
   * 法達與小限是出生資料與年齡的確定性函數，不需星曆掃描，代價與 [longTermTriggers] 不同量級。
   */
  val firdariaPeriods: List<Firdaria> = emptyList(),
  /**
   * ⚠️ **法達是因為精度而算不出來，還是這段期間剛好沒有？** —— 兩者都讓 [firdariaPeriods] 為空，
   * 但對讀者是相反的指示，所以必須由**設定**記下來，不可從空集合反推。
   *
   * 法達只需晝夜，不需宮位；但「晝夜」本身在 [BirthDataGrain.DAY] 下就是不知道
   * （見 [DayNightSource.Unavailable]），[BirthDataGrain.HOUR2] 橫跨日出／日落時亦然
   * （[DayNightSource.Indeterminate]）。兩種情況下法達整組算不出來，
   * 而**手推同樣無效** —— 缺的是判斷序列從太陽起還是從月亮起的那個資訊。
   *
   * 沒有這一欄時，下游的計數工具會把它歸進「取樣邊界，你自己推導吧」那一類，
   * 那句話會把讀者送去手推一個手推同樣無效的東西。
   */
  val firdariaGated: Boolean = false,
  /** 全段的小限。理由同 [firdariaPeriods]；[EventGroup.profections] 是事件條件下的樣本，非母體。 */
  val profections: List<Profection> = emptyList(),
  /**
   * 全段的黃道釋放，每個 Lot 一筆（Fortune 與 Spirit），含 L1~L3。
   *
   * ⚠️ 與 [firdariaPeriods]／[profections] 有一個關鍵差異：那兩者在 [EventGroup] 裡本來就有
   * （事件條件下的樣本，可當假說產生器），**黃道釋放連那個都沒有**。
   * 因此它若只存在於此而不呈現給讀者，等於這個技法不存在 —— 呈現層須至少給出 L1 與 L2。
   *
   * 兩個 Lot 都由 ASC 起算，故僅在 `grain.includeAxis` 成立時才有值。
   */
  val zodiacalReleasings: List<ZrByLot> = emptyList(),
  /**
   * 全段的返照序列（目前只算太陽返照）。
   *
   * ⚠️ 與 [solarReturns] **是兩件事，不可互相取代**：那一份按事件群取
   * （10 張對 9 個事件群），是**事件條件樣本** —— 讀者由它看得出「出事那年返照長什麼樣」，
   * 卻算不出「其餘年份長什麼樣」，任何以它為分母的比率都帶倖存者偏誤。
   * 本欄是全段、與事件無關的母體。這與 [firdariaPeriods]／[profections]／[zodiacalReleasings]
   * 對上各自的事件窗版本是同一個形狀。
   *
   * ⚠️ **呈現層刻意不渲染它。** 與 [profections] 同款：它的角色是基準率的分母，
   * 而分母該由確定性的計數承接，不該逐張印給讀者 ——
   * 四十餘年的返照盤全部展開，體積會與其餘各段總和相當。
   */
  val fullSpanReturns: List<@Contextual IReturnDto> = emptyList(),
) : ICorpusProjectable {
  /** ⚠️ 是 [fullSpanReturns]、不是 [solarReturns] —— 理由見 [ICorpusProjectable] 的不對稱說明 */
  override val corpusReturns: List<IReturnDto> get() = fullSpanReturns
}

@Serializable
data class Future(
  @Contextual
  override val fromTime: GmtJulDay,
  @Contextual
  override val toTime: GmtJulDay,
  val astroEvents: List<@Contextual ITimeLineEvent>,
  val lunarReturns: List<ReturnCoverageDto>,
  val solarReturns: List<@Contextual IReturnDto>,
  val firdariaPeriods: List<Firdaria>,
  val profections: List<Profection>,
  val coverage: List<ScanCoverage> = emptyList(),
  /**
   * **預測窗內**的逆行三相區間。與 [Past.retrogradeSpans] 同一個形狀、同一段程式碼掃出來，
   * 差別只在掃描區間：那邊是全段（母體），這邊是 [fromTime] ~ [toTime]（本窗的日曆）。
   *
   * ⚠️ **掃描範圍必須是預測窗，不是全段** —— 與返照（[solarReturns]）的處置同構。
   * 把過去那份掛進 FORECAST 會繞過「觸發表是 past 與 future 之間唯一通道」的設計。
   *
   * ⚠️ 為什麼要有這一份：`get_retrograde_stationary` 依裁定掛進 FORECAST 的取用類工具，
   * 而 corpus-backed 的工具**只看得到 corpus**。此欄若不填，工具在 FORECAST 一律回
   * NOT_APPLICABLE —— 「工具說有、素材說沒有」是本專案記過七次的缺陷族。
   *
   * 素材同樣不印（背景日曆，不是對本命的事件），只走 corpus → funCall。
   */
  override val retrogradeSpans: List<RetrogradePhaseSpan> = emptyList(),
  /** 預測窗內的留（轉向點），含該時刻對本命的相位（已過 grain 閘門）。理由同 [retrogradeSpans]。 */
  override val stationaries: List<StationaryMoment> = emptyList(),
) : ICorpusProjectable {
  /** 預測窗的返照本來就與事件無關（還沒有事件），可直接投影 —— 見 [ICorpusProjectable] */
  override val corpusReturns: List<IReturnDto> get() = solarReturns
}

@Serializable
data class MergedUserEventsModel(
  val natal: IPersonHoroscopeDto,
  val grain: BirthDataGrain,
  val summary: String,
  val past: Past,
  @Serializable(with = LocalDateSerializer::class)
  val viewDay : LocalDate,
  val future : Future? = null,
)

@Serializable
data class ExtractedEvents(
  override val name: String,
  @Serializable(with = GenderSerializer::class)
  override val gender: Gender,
  @Serializable(with = LocalDateSerializer::class) // ISO_DATE
  val birthDay: LocalDate,
  @Serializable(with = LocalTimeSerializer::class) // ISO_LOCAL_TIME
  val hourMinute: LocalTime? = null,
  val lat: Double, val lng: Double, val tzid: String,
  override val place: String,
  val intro: String,
  val events: List<AbstractEvent>,
  /**
   * 晝夜生 —— [hourMinute] 為 null 時的次佳精度：不知幾點幾分、但知晝生或夜生
   * （ADB 的 Rodden 註記、家屬口述常有此等級），足以解鎖 Firdaria
   * （見 [BirthDataGrain.DAY_NIGHT_DIURNAL] / [BirthDataGrain.DAY_NIGHT_NOCTURNAL]）。
   * [hourMinute] 非 null 時此欄位被忽略（晝夜由盤面太陽位置推導）。
   * 置於參數尾端而非緊鄰 [hourMinute]：既有 positional 建構呼叫點不必動。
   */
  val dayNight: DayNight? = null
) : IBirthDataNamePlace {

  /**
   * 出生資料的時刻精度 —— grain 推導的**唯一定義點**。
   * 呼叫端（如 `ReportFactory.getMergedUserEventsModel`）一律問這裡，不得自行以
   * `hourMinute != null` 二分 —— 那正是 DAY_NIGHT 兩級曾經不可達的原因。
   * 寫成函式而非屬性：見 [AbstractEvent.grain] 的 FormatSpec 反射理由。
   */
  fun birthGrain(): BirthDataGrain = when {
    hourMinute != null -> BirthDataGrain.MINUTE
    dayNight != null   -> BirthDataGrain.DayNightOnly(dayNight)
    else               -> BirthDataGrain.DAY
  }

  /**
   * 依 [BirthDataGrain] 的儲存規則「存已知區間的中點」錨定：
   * 知時刻取該時刻；夜生取夜晚中點（≈ 當地午夜，即太陽下中天）；其餘取正午。
   *
   * 夜生取 00:00 而非正午，是為了守住 [BirthDataGrain] 的不變式 ——
   * 儲存的時刻在儲存的地點下解讀必須與 grain 相容。舊實作不分晝夜一律給正午，
   * 於是每一筆 `DAY_NIGHT_NOCTURNAL` 都自相矛盾（拿 `IDayNight` 去問會回答 DAY）。
   * 之所以一直沒出事，是因為晝夜被當成 payload 直接注入 Firdaria，沒人回頭讀那個時刻。
   *
   * 極區（|lat| > 66.5°）另當別論：當地午夜可能是白天。此處是純資料類別、
   * 拿不到 `IDayNight`，故維持 00:00 的近似；需要精確中點的寫入路徑請見
   * `BirthDataService.resolveDateTime`。
   */
  override val time: ChronoLocalDateTime<*>
    get() = when {
      hourMinute != null         -> birthDay.atTime(hourMinute)
      dayNight == DayNight.NIGHT -> birthDay.atTime(0, 0)
      else                       -> birthDay.atTime(12, 0)
    }

  override val location: ILocation
    get() = Location(lat.toLat(), lng.toLng(), tzid)
}

