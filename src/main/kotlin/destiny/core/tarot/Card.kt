/**
 * Created by smallufo on 2023-04-01.
 */
package destiny.core.tarot

import destiny.core.astrology.Element
import destiny.core.astrology.Element.*
import destiny.core.tarot.Suit.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Suit(val element: Element?) {
  MAJOR(null),
  WANDS(FIRE),
  CUPS(WATER),
  SWORDS(AIR),
  PENTACLES(EARTH)
}

@Serializable
enum class Card(val suit: Suit) {
  FOOL(MAJOR),
  MAGICIAN(MAJOR),
  HIGH_PRIESTESS(MAJOR),
  EMPRESS(MAJOR),
  EMPEROR(MAJOR),
  HIEROPHANT(MAJOR),
  LOVERS(MAJOR),
  CHARIOT(MAJOR),
  STRENGTH(MAJOR),
  HERMIT(MAJOR),
  WHEEL_OF_FORTUNE(MAJOR),
  JUSTICE(MAJOR),
  HANGED_MAN(MAJOR),
  DEATH(MAJOR),
  TEMPERANCE(MAJOR),
  DEVIL(MAJOR),
  TOWER(MAJOR),
  STAR(MAJOR),
  MOON(MAJOR),
  SUN(MAJOR),
  JUDGEMENT(MAJOR),
  WORLD(MAJOR),

  @SerialName("W1")
  WANDS_ACE(WANDS),
  @SerialName("W2")
  WANDS_2(WANDS),
  @SerialName("W3")
  WANDS_3(WANDS),
  @SerialName("W4")
  WANDS_4(WANDS),
  @SerialName("W5")
  WANDS_5(WANDS),
  @SerialName("W6")
  WANDS_6(WANDS),
  @SerialName("W7")
  WANDS_7(WANDS),
  @SerialName("W8")
  WANDS_8(WANDS),
  @SerialName("W9")
  WANDS_9(WANDS),
  @SerialName("WX")
  WANDS_10(WANDS),
  @SerialName("WP")
  WANDS_PAGE(WANDS),
  @SerialName("WN")
  WANDS_KNIGHT(WANDS),
  @SerialName("WQ")
  WANDS_QUEEN(WANDS),
  @SerialName("WK")
  WANDS_KING(WANDS),

  @SerialName("C1")
  CUPS_ACE(CUPS),
  @SerialName("C2")
  CUPS_2(CUPS),
  @SerialName("C3")
  CUPS_3(CUPS),
  @SerialName("C4")
  CUPS_4(CUPS),
  @SerialName("C5")
  CUPS_5(CUPS),
  @SerialName("C6")
  CUPS_6(CUPS),
  @SerialName("C7")
  CUPS_7(CUPS),
  @SerialName("C8")
  CUPS_8(CUPS),
  @SerialName("C9")
  CUPS_9(CUPS),
  @SerialName("CX")
  CUPS_10(CUPS),
  @SerialName("CP")
  CUPS_PAGE(CUPS),
  @SerialName("CN")
  CUPS_KNIGHT(CUPS),
  @SerialName("CQ")
  CUPS_QUEEN(CUPS),
  @SerialName("CK")
  CUPS_KING(CUPS),

  @SerialName("S1")
  SWORDS_ACE(SWORDS),
  @SerialName("S2")
  SWORDS_2(SWORDS),
  @SerialName("S3")
  SWORDS_3(SWORDS),
  @SerialName("S4")
  SWORDS_4(SWORDS),
  @SerialName("S5")
  SWORDS_5(SWORDS),
  @SerialName("S6")
  SWORDS_6(SWORDS),
  @SerialName("S7")
  SWORDS_7(SWORDS),
  @SerialName("S8")
  SWORDS_8(SWORDS),
  @SerialName("S9")
  SWORDS_9(SWORDS),
  @SerialName("SX")
  SWORDS_10(SWORDS),
  @SerialName("SP")
  SWORDS_PAGE(SWORDS),
  @SerialName("SN")
  SWORDS_KNIGHT(SWORDS),
  @SerialName("SQ")
  SWORDS_QUEEN(SWORDS),
  @SerialName("SK")
  SWORDS_KING(SWORDS),


  @SerialName("P1")
  PENTACLES_ACE(PENTACLES),
  @SerialName("P2")
  PENTACLES_2(PENTACLES),
  @SerialName("P3")
  PENTACLES_3(PENTACLES),
  @SerialName("P4")
  PENTACLES_4(PENTACLES),
  @SerialName("P5")
  PENTACLES_5(PENTACLES),
  @SerialName("P6")
  PENTACLES_6(PENTACLES),
  @SerialName("P7")
  PENTACLES_7(PENTACLES),
  @SerialName("P8")
  PENTACLES_8(PENTACLES),
  @SerialName("P9")
  PENTACLES_9(PENTACLES),
  @SerialName("PX")
  PENTACLES_10(PENTACLES),
  @SerialName("PP")
  PENTACLES_PAGE(PENTACLES),
  @SerialName("PN")
  PENTACLES_KNIGHT(PENTACLES),
  @SerialName("PQ")
  PENTACLES_QUEEN(PENTACLES),
  @SerialName("PK")
  PENTACLES_KING(PENTACLES);

  companion object {
    fun list(suit : Suit): List<Card> {
      return entries.filter { c -> c.suit == suit }.toList()
    }

    fun list(element: Element) : List<Card> {
      return entries.filter { c -> c.suit.element == element }.toList()
    }
  }
}
