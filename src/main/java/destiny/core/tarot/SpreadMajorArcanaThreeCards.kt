/**
 * Created by smallufo on 2023-04-02.
 */
package destiny.core.tarot

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SpreadMajorArcanaThreeCards(val card1: CardOrientation,
                                       val card2: CardOrientation,
                                       val card3: CardOrientation) : ISpread {

  override fun getTitle(locale: Locale): String {
    return when (locale.language) {
      "en" -> { "Three Card Major Arcana Spread" }
      "ja" -> { "3枚の大アルカナスプレッド" }
      else -> { "大阿爾克那三牌法" }
    }
  }
}
