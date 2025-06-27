package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.electional.Astro
import destiny.core.electional.AstroEventDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure


sealed interface IProgressionEvent {
  val astro: Astro
  val description: String
  val convergentTime: GmtJulDay
  val divergentTime: GmtJulDay
}

class IProgressionEventSerializer(private val gmtJulDaySerializer: KSerializer<GmtJulDay>) : KSerializer<IProgressionEvent> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("IProgressionEvent") {
    element<String>("description")
    element("convergentTime", gmtJulDaySerializer.descriptor)
    element("divergentTime", gmtJulDaySerializer.descriptor)
  }

  override fun serialize(encoder: Encoder, value: IProgressionEvent) {
    encoder.encodeStructure(descriptor) {
      encodeStringElement(descriptor, 0, value.description)
      encodeSerializableElement(descriptor, 1, gmtJulDaySerializer, value.convergentTime)
      encodeSerializableElement(descriptor, 2, gmtJulDaySerializer, value.divergentTime)
    }
  }

  override fun deserialize(decoder: Decoder): IProgressionEvent {
    throw UnsupportedOperationException("Deserialization not supported")
  }
}

data class ProgressionEvent(
  val astroEvent: AstroEventDto,
  override val divergentTime: GmtJulDay
) : IProgressionEvent {
  override val astro: Astro
    get() = astroEvent.event

  override val description: String
    get() = astroEvent.event.description

  override val convergentTime: GmtJulDay
    get() = astroEvent.begin
}


@Serializable
data class ProgressionAstroEventsModel(
  val natal: IPersonHoroscopeDto,
  val grain: BirthDataGrain,
  @Contextual
  val fromTime: GmtJulDay,
  @Contextual
  val toTime: GmtJulDay,
  val secondaryProgressionEvents: List<@Contextual IProgressionEvent>,
  val tertiaryProgressionEvents: List<@Contextual IProgressionEvent>
)
