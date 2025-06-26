package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.electional.Astro
import destiny.core.electional.AstroEventDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


interface IProgressionEvent {
  val astro: Astro
  val description : String
  val convergentTime : GmtJulDay
  val divergentTime : GmtJulDay
}

@Serializable
@SerialName("ProgressionEvent")
data class ProgressionEvent(
  val astroEvent: AstroEventDto,

  @Contextual
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
  val secondaryProgressionEvents : List<IProgressionEvent>,
  val tertiaryProgressionEvents : List<IProgressionEvent>
)
