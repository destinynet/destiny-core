/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import kotlinx.serialization.Serializable


enum class Orientation {
  UPRIGHT,
  REVERSED
}

@Serializable
data class CardOrientation(val card: Card, val orientation: Orientation = Orientation.UPRIGHT)
