package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.electional.Astro
import destiny.core.electional.AstroEventDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


interface IProgressionEvent {
  val astro: Astro
  val description : String
  val convergentTime : GmtJulDay
  val divergentTime : GmtJulDay
}

@Serializable
data class ProgressionEvent(
  val astroEventDto: AstroEventDto,

  @Contextual
  override val divergentTime: GmtJulDay
) : IProgressionEvent {
  override val astro: Astro
    get() = astroEventDto.event

  override val description: String
    get() = astroEventDto.event.description

  override val convergentTime: GmtJulDay
    get() = astroEventDto.begin
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
