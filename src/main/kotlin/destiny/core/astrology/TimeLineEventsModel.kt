package destiny.core.astrology

import destiny.core.EventType
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.prediction.EventSource
import destiny.core.astrology.prediction.Firdaria
import destiny.core.astrology.prediction.IReturnDto
import destiny.core.astrology.prediction.Profection
import destiny.core.astrology.prediction.ReturnCoverageDto
import destiny.core.astrology.prediction.ZodiacalReleasing
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng.Companion.toLng
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.YearMonthRange
import destiny.core.calendar.chinese.groupMergedRanges
import destiny.tools.serializers.*
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
  val zodiacalReleasings: List<ZodiacalReleasing>
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
  override val zodiacalReleasings: List<ZodiacalReleasing> = emptyList(),
) : ITimeLineEventsModel

@Serializable
enum class EventSentiment {
  POSITIVE, // 明確是好的
  NEGATIVE, // 明確是壞的
  NEUTRAL,  // 中性，沒有強烈好壞
}

@Serializable(with = AbstractEventSerializer::class)
sealed class AbstractEvent {
  abstract val eventType: EventType
  abstract val details: String
  abstract val sentiment: EventSentiment?

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
  override val eventType: EventType,
  override val details: String,
  override val sentiment: EventSentiment? = null,
) : AbstractEvent() {
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
  override val eventType: EventType,
  override val details: String,
  override val sentiment: EventSentiment? = null,
) : AbstractEvent() {
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
  override val eventType: EventType,
  override val details: String,
  override val sentiment: EventSentiment? = null,
) : AbstractEvent() {
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
  override val eventType: EventType,
  override val details: String,
  override val sentiment: EventSentiment? = null,
) : AbstractEvent() {

  init {
    require(to == null || !to.isBefore(from)) { "PeriodEvent: to ($to) 早於 from ($from)" }
    require(ignition == null || (!ignition.isBefore(from) && (to == null || !ignition.isAfter(to)))) {
      "PeriodEvent: ignition ($ignition) 落在 $from .. $to 之外"
    }
  }

  /** 是否仍在進行中（[to] 為 null）。素材出口應據此標示，否則 LLM 會把「沒有終點」讀成「已結束」。 */
  val ongoing: Boolean get() = to == null

  override fun yearMonth(): YearMonth = YearMonth.from(from)

  /**
   * 進行中（[ongoing]）者只由起點決定區間，**刻意不外推到「現在」** ——
   * 資料模型不該知道 today 是哪天。上界該由呼叫端以 viewDay 決定。
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

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AbstractEvent") {
    // 點事件（MinuteEvent / DayEvent / MonthEvent）：由 date 的字面格式決定是哪一種
    element<String>("date", isOptional = true)
    // 有延時的事件（PeriodEvent）：以 from 的存在作為判別式
    element<String>("from", isOptional = true)
    element<String>("to", isOptional = true)
    element<String>("ignition", isOptional = true)
    element("eventType", EventType.serializer().descriptor)
    element<String>("details")
    element("sentiment", EventSentiment.serializer().descriptor, isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: AbstractEvent) {
    when (value) {
      is MonthEvent  -> encoder.encodeSerializableValue(MonthEvent.serializer(), value)
      is DayEvent    -> encoder.encodeSerializableValue(DayEvent.serializer(), value)
      is MinuteEvent -> encoder.encodeSerializableValue(MinuteEvent.serializer(), value)
      is PeriodEvent -> encoder.encodeSerializableValue(PeriodEvent.serializer(), value)
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
      return Json.decodeFromJsonElement(PeriodEvent.serializer(), jsonObject)
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
 * 依 [AbstractEvent.yearMonthRange] 分群 —— 各自向外擴張 [extMonth] 個月後合併相鄰／重疊者。
 *
 * 以區間（而非單一 [AbstractEvent.yearMonth]）為依據，[PeriodEvent] 才能把自己跨到的月份
 * 一併拉進同一群。點事件退化成 `start == endInclusive`，行為與改動前完全相同。
 */
fun List<AbstractEvent>.groupAdjacentEvents(extMonth: Int = 1): List<List<AbstractEvent>> {
  if (this.size < 2) {
    return listOf(this)
  }

  val mergedRanges: List<YearMonthRange> = this.map { it.yearMonthRange() }.groupMergedRanges(extMonth)

  return mergedRanges.map { range: YearMonthRange ->
    this.filter { event -> event.yearMonthRange().overlaps(range) }
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

@Serializable
data class Past(
  val eventGroups: List<EventGroup>,
  val solarReturns: List<@Contextual IReturnDto>,
  val longTermTriggers: List<@Contextual ITimeLineEvent>,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay,
  val coverage: List<ScanCoverage> = emptyList()
)

@Serializable
data class Future(
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay,
  val astroEvents: List<@Contextual ITimeLineEvent>,
  val lunarReturns: List<ReturnCoverageDto>,
  val solarReturns: List<@Contextual IReturnDto>,
  val firdariaPeriods: List<Firdaria>,
  val profections: List<Profection>,
  val coverage: List<ScanCoverage> = emptyList()
)

@Serializable
data class MergedUserEventsModel(
  val natal: IPersonHoroscopeDto,
  val grain: BirthDataGrain,
  val summary: String,
  val past: Past,
  @Serializable(with = LocalDateSerializer::class)
  val today : LocalDate,
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
  val events: List<AbstractEvent>
) : IBirthDataNamePlace {

  override val time: ChronoLocalDateTime<*>
    get() = birthDay.let { birthDay ->
      if (hourMinute != null) {
        birthDay.atTime(hourMinute)
      } else {
        birthDay.atTime(12, 0)
      }
    }

  override val location: ILocation
    get() = Location(lat.toLat(), lng.toLng(), tzid)
}

/**
 * [ExtractedEvents] 去識別化的結果。
 *
 * 刻意與抽取步驟分離，理由有三：
 * 1. **盲測需要兩份**：評分得拿去識別版的論斷去對真實版的答案，抽取時就抹掉等於毀掉 ground truth。
 * 2. **去識別化的正確性只能靠對比驗證**：有了 [mapping] 才寫得出「redacted 版不得含有任何 mapping key」
 *    這種斷言（見 [survivingKeys]）。埋在抽取裡的黑箱布林值無從驗證，而 LLM 是非決定性的。
 * 3. **可稽核**：mapping 是人看得懂的替換對照表，可 diff、可 review、可累積成 regression fixture。
 *
 * @param redacted 去識別化後的事件，日期／[EventType]／[EventSentiment] 必須與原版逐筆一致
 * @param mapping  替換對照表，原文 → 替換後（例：`"李佩潔" → "伴侶"`、`"蔡阿嘎543" → "第六個影音頻道"`）
 */
@Serializable
data class Redaction(
  val redacted: ExtractedEvents,
  val mapping: Map<String, String>
) {
  /**
   * 驗證用：回傳仍殘留在 [redacted] 裡的 [mapping] key。
   * 空集合才代表去識別化完整；非空即為 LLM 漏抹。
   *
   * 注意這只能抓「已被 LLM 認定為敏感、卻沒抹乾淨」的字串。抹得掉的是姓名與專有名詞，
   * 抹不掉的是**事件內容本身的辨識度** —— 實測顯示，即使姓名全數抹除，
   * 「特定獎項 + 特定意外」這類事件組合仍足以讓 LLM 以 75–80% 信心指認當事人。
   * 真要盲掉得把事件降維成 (date, eventType, sentiment)，但那會同時毀掉校準價值。
   */
  fun survivingKeys(): Set<String> {
    val haystack = buildString {
      append(redacted.name).append('\n')
      append(redacted.place).append('\n')
      append(redacted.intro).append('\n')
      redacted.events.forEach { append(it.details).append('\n') }
    }
    return mapping.keys.filter { it.isNotBlank() && haystack.contains(it) }.toSet()
  }

  /**
   * 驗證用：日期／類型／情緒必須與原版逐筆一致 —— 去識別化只准改敘述，不准動訊號。
   *
   * 特別檢查**日期精度**：LLM 會傾向把 `2020-03-03` 這種 [DayEvent] 降級成 `2020-03` 的 [MonthEvent]
   * （大概是誤以為「日」也算個資）。那會靜默摧毀日級校準能力 ——
   * `getMergedUserEventsModel` 只對 [EventGrain.canDayLevelTransit] 為真的事件展開行運。
   * 因此這裡比對的是「[AbstractEvent.grain] + 完整日期」，不是 [AbstractEvent.yearMonth]。
   */
  fun signalMismatches(original: ExtractedEvents): List<String> {
    if (original.events.size != redacted.events.size) {
      return listOf("event count : ${original.events.size} -> ${redacted.events.size}")
    }
    fun AbstractEvent.signalKey(): String = "${grain()}:${dateLabel()}"
    return original.events.zip(redacted.events).mapNotNull { (a, b) ->
      when {
        a.signalKey() != b.signalKey() -> "date : ${a.signalKey()} -> ${b.signalKey()}"
        a.eventType != b.eventType     -> "eventType@${a.signalKey()} : ${a.eventType} -> ${b.eventType}"
        a.sentiment != b.sentiment     -> "sentiment@${a.signalKey()} : ${a.sentiment} -> ${b.sentiment}"
        else                           -> null
      }
    }
  }
}
