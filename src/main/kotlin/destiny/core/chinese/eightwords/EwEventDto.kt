package destiny.core.chinese.eightwords

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.electional.IEventDto
import destiny.core.electional.Impact
import destiny.core.electional.Span
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("EwEventDto")
data class EwEventDto(
  override val event: EwEvent,
  val outer: IEightWords,
  @Contextual
  override val begin: GmtJulDay,
  @Contextual
  override val end : GmtJulDay? = null,
  override val span: Span,
  override val impact: Impact
) : IEventDto
