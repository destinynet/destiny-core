package destiny.core.astrology

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.prediction.EventSource
import destiny.core.astrology.prediction.IReturnDto
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.Lat.Companion.toLat
import destiny.core.calendar.Lng.Companion.toLng
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.YearMonthRange
import destiny.core.calendar.chinese.groupMergedRanges
import destiny.tools.ai.model.FormatSpec
import destiny.tools.serializers.GenderSerializer
import destiny.tools.serializers.LocalDateSerializer
import destiny.tools.serializers.LocalTimeSerializer
import destiny.tools.serializers.YearMonthSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.chrono.ChronoLocalDateTime


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
  val lunarReturns : List<IReturnDto>
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
  override val lunarReturns: List<IReturnDto> = emptyList()
) : ITimeLineEventsModel


@Serializable
data class YearMonthEvent(
  @Serializable(with = YearMonthSerializer::class)
  val yearMonth: YearMonth,
  val event: String
)

fun List<YearMonthEvent>.groupAdjacentEvents(extMonth: Int = 1) : List<List<YearMonthEvent>> {
  if (this.size < 2) {
    return listOf(this)
  }

  val yearMonths = this.map { it.yearMonth }

  val mergedRanges: List<YearMonthRange> = yearMonths.groupMergedRanges(extMonth)

  return mergedRanges.map { range: YearMonthRange ->
    this.filter { event ->
      !event.yearMonth.isBefore(range.start) && !event.yearMonth.isAfter(range.endInclusive)
    }
  }
}

interface ITimeLineWithUserEventsModel : ITimeLineEventsModel {
  val today: LocalDate
  val summary: String
  val userEvents : List<YearMonthEvent>
}

@Serializable
data class TimeLineWithUserEventsModel(
  private val timeLineEventsModel : ITimeLineEventsModel,
  @Serializable(with = LocalDateSerializer::class)
  override val today: LocalDate,
  override val summary: String,
  override val userEvents: List<YearMonthEvent>
) : ITimeLineWithUserEventsModel , ITimeLineEventsModel by timeLineEventsModel

@Serializable
data class EventGroup(
  @Contextual
  val fromTime : GmtJulDay,
  @Contextual
  val toTime : GmtJulDay,
  val userEvents : List<YearMonthEvent>,
  val astroEvents : List<@Contextual ITimeLineEvent>,
  val lunarReturns : List<@Contextual IReturnDto>
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
  val lunarReturns: List<@Contextual IReturnDto>,
  val solarReturns: List<@Contextual IReturnDto>
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
  val events: List<YearMonthEvent>
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
