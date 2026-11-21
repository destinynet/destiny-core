/**
 * Created by smallufo on 2023-04-01.
 */
package destiny.core.tarot

import destiny.core.Descriptive
import destiny.tools.JSerializable
import destiny.tools.Lang
import destiny.tools.LangTools
import destiny.tools.toLang
import kotlinx.serialization.Serializable
import java.util.Locale


@Serializable
sealed interface ISpread : Descriptive, JSerializable {

  val cards : List<CardOrientation>
    get() = getLangPosMap().map { it.first }.toList()

  /** 每張牌的位置名稱（依語系） */
  fun getLangPosMap(): List<Pair<CardOrientation, Map<Lang, String>>>

  fun getCardPositions(lang: Lang): List<Pair<CardOrientation, String>> {
    return getLangPosMap().map { (co: CardOrientation, posMap: Map<Lang, String>) ->
      co to posMap.getValue(LangTools.getBestMatchingLangOrFirst(lang, posMap.keys))
    }.toList()
  }

  /** `Locale` 橋接（給 JVM 端既有的呼叫端），勿覆寫。轉 KMP 時移到 jvmMain */
  fun getCardPositions(locale: Locale): List<Pair<CardOrientation, String>> = getCardPositions(locale.toLang())
}
