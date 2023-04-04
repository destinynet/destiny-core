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
@SerialName("3cards")
data class SpreadThreeCards(@SerialName("c1") val card1: CardOrientation,
                            @SerialName("c2") val card2: CardOrientation,
                            @SerialName("c3") val card3: CardOrientation) : ISpread {

  override fun getTitle(locale: Locale): String {
    return when (locale.language) {
      "en" -> { "Three Card Spread" }
      "ja" -> { "三枚カード" }
      else -> { "三牌法" }
    }
  }

  companion object {
    fun of(list : List<CardOrientation>) : SpreadThreeCards {
      require(list.size == 3)
      return SpreadThreeCards(list[0] , list[1] , list[2])
    }
  }
}
