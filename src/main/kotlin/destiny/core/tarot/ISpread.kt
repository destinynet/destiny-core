/**
 * Created by smallufo on 2023-04-01.
 */
package destiny.core.tarot

import destiny.core.Descriptive
import destiny.tools.LocaleTools
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
sealed interface ISpread : Descriptive, java.io.Serializable {

  val cards : List<CardOrientation>

  fun getLocalePosMap(): List<Pair<CardOrientation, Map<Locale, String>>>

  fun getCardPositions(locale: Locale): List<Pair<CardOrientation, String>> {
    return getLocalePosMap().map { (co: CardOrientation, posMap: Map<Locale, String>) ->
      val loc = LocaleTools.getBestMatchingLocaleOrFirst(locale, posMap.keys)
      co to posMap[loc]!!
    }.toList()
  }
}
