package destiny.core.astrology

import destiny.core.astrology.prediction.PredictiveTechnique
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure


sealed interface ITimeLineEvent {
  val astro: AstroEvent
  val description: String
  val convergentTime: GmtJulDay
  val divergentTime: GmtJulDay
  val source: PredictiveTechnique
}

class IProgressionEventSerializer(private val gmtJulDayTimeSerializer: KSerializer<GmtJulDay>,
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
  override val source: PredictiveTechnique,
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


@Serializable
data class TimeLineEventsModel(
  val natal: IPersonHoroscopeDto,
  val grain: BirthDataGrain,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay,
  val events: List<@Contextual ITimeLineEvent>,
)
