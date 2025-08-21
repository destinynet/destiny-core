/**
 * Created by smallufo on 2025-06-28.
 */
package destiny.core.astrology.prediction


enum class EventSource {
  TRANSIT,
  SECONDARY,
  TERTIARY,
  MINOR,
  SOLAR_ARC,
}

data class EventSourceConfig(val source: EventSource, val pastExtDays: Int = 0, val futureExtDays: Int = 0)
