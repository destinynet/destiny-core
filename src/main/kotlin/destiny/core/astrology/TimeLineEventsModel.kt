package destiny.core.astrology

import destiny.core.astrology.prediction.EventSource
import destiny.core.astrology.prediction.IReturnDto
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.LocalDateSerializer
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
import java.time.YearMonth


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
  val returnCharts : List<IReturnDto>
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
  override val returnCharts: List<IReturnDto> = emptyList()
) : ITimeLineEventsModel


@Serializable
data class YearMonthEvent(
  @Serializable(with = YearMonthSerializer::class)
  val yearMonth: YearMonth,
  val event: String
)

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
