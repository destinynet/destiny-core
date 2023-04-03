/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * maybe Major Arcana
 */
@Serializable
@SerialName("SpreadThreeCards")
data class SpreadThreeCards(val card1: CardOrientation,
                            val card2: CardOrientation,
                            val card3: CardOrientation) : ISpread {

  override fun getTitle(locale: Locale): String {
    return when (locale.language) {
      "en" -> { "Three Card Spread" }
      "ja" -> { "三枚カード" }
      else -> { "三牌法" }
    }
  }
}
