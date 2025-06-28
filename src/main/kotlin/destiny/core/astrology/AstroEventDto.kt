package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.electional.IEventDto
import destiny.core.electional.Impact
import destiny.core.electional.Span
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AstroEventDto")
data class AstroEventDto(
  override val event: AstroEvent,
  @Contextual
  override val begin: GmtJulDay,
  @Contextual
  override val end : GmtJulDay? = null,
  override val span: Span,
  override val impact: Impact
) : IEventDto
