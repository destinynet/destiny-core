/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class Orientation {
  @SerialName("U")
  UPRIGHT,
  @SerialName("R")
  REVERSED
}

@Serializable
@SerialName("CO")
data class CardOrientation(@SerialName("c") val card: Card,
                           @SerialName("o") val orientation: Orientation = Orientation.UPRIGHT)
