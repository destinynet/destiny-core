package destiny.core.astrology

import destiny.core.astrology.classical.rules.Misc

data class VocEvent(val voc: Misc.VoidCourse) : IStarLocalEventSpan, IStarEventSpan by voc {
  override val title: String = "${voc.star} 空亡"
  override val description: String = " 為期 ${voc.duration.inWholeHours}小時"
}
