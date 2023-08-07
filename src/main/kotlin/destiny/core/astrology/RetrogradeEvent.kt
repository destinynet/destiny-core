package destiny.core.astrology

data class RetrogradeEvent(val span: RetrogradeSpan) : IStarLocalEventSpan, IStarEventSpan by span {
  override val title: String
    get() {
      return buildString {
        append(span.star)
        append(" ")
        val name = when(span.phase) {
          RetrogradePhase.PREPARING    -> "準備逆行"
          RetrogradePhase.RETROGRADING -> "逆行"
          RetrogradePhase.LEAVING      -> "結束逆行"
        }
        append(name)
      }
    }
  override val description: String = " 為期 ${span.duration.inWholeDays}天"
}
