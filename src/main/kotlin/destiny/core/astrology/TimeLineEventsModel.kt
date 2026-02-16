package destiny.core.astrology

import destiny.core.EventType
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.prediction.EventSource
import destiny.core.astrology.prediction.Firdaria
import destiny.core.astrology.prediction.IReturnDto
import destiny.core.astrology.prediction.Profection
import destiny.core.astrology.prediction.ReturnCoverageDto
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng.Companion.toLng
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.YearMonthRange
import destiny.core.calendar.chinese.groupMergedRanges
import destiny.tools.ai.model.FormatSpec
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
  abstract fun yearMonth() : YearMonth
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
    element<String>("date")
    element("eventType", EventType.serializer().descriptor)
    element<String>("details")
    element("sentiment", EventSentiment.serializer().descriptor, isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: AbstractEvent) {
    // 序列化邏輯保持不變
    when (value) {
      is MonthEvent -> encoder.encodeSerializableValue(MonthEvent.serializer(), value)
      is DayEvent   -> encoder.encodeSerializableValue(DayEvent.serializer(), value)
      is MinuteEvent -> encoder.encodeSerializableValue(MinuteEvent.serializer(), value)
    }
  }

  override fun deserialize(decoder: Decoder): AbstractEvent {
    val jsonDecoder = decoder as? JsonDecoder
      ?: throw IllegalStateException("This serializer can only be used with JSON format.")

    // 將整個 JSON 物件讀取為 JsonElement
    val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

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

fun List<AbstractEvent>.groupAdjacentEvents(extMonth: Int = 1): List<List<AbstractEvent>> {
  if (this.size < 2) {
    return listOf(this)
  }

  val yearMonths = this.map { it.yearMonth() }

  val mergedRanges: List<YearMonthRange> = yearMonths.groupMergedRanges(extMonth)

  return mergedRanges.map { range: YearMonthRange ->
    this.filter { event ->
      !event.yearMonth().isBefore(range.start) && !event.yearMonth().isAfter(range.endInclusive)
    }
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

@Serializable
data class Past(
  val eventGroups: List<EventGroup>,
  val solarReturns: List<@Contextual IReturnDto>,
  val longTermTriggers: List<@Contextual ITimeLineEvent>,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay
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
  val profections: List<Profection>
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

  companion object {
    val formatSpec = FormatSpec.of<ExtractedEvents>("celebrity_intro", "Celebrity's profile and important life events")
  }
}
