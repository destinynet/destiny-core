/**
 * Created by smallufo on 2019-10-13.
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.Point
import destiny.tools.serializers.astrology.LotSerializer
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.reflect.KClass

/**
 * Lot（希臘化占星的 klēros，中世紀以後俗稱「阿拉伯點」Arabic parts）：由上升與兩顆星算出來的點，公式見 [operands]／[lngOf]。
 *
 * nameKey 是 `Lot.Fortune` 這種形式；2026-11-13 以前叫 `Arabic`（nameKey `Arabic.Fortune`），舊的字串仍解得回來（[valueOf]）
 */
@Serializable(with = LotSerializer::class)
sealed class Lot(nameKey: String, abbrKey: String, unicode: Char? = null) : Star(nameKey, abbrKey, Star::class.java.name, unicode),
                                                                            Comparable<Lot> {

  /** 福點 (幸運點) Lot of Fortune */
  object Fortune : Lot("Lot.Fortune", "Lot.Fortune_ABBR", '⊗')

  /** 精神點 Lot of Spirit */
  object Spirit : Lot("Lot.Spirit", "Lot.Spirit_ABBR")

  /** 愛情點 Lot of Eros */
  object Eros : Lot("Lot.Eros", "Lot.Eros_ABBR")

  /** 勝利點 Lot of Victory */
  object Victory : Lot("Lot.Victory", "Lot.Victory_ABBR")

  /** 必要點 Lot of Necessity */
  object Necessity : Lot("Lot.Necessity", "Lot.Necessity_ABBR")

  /** 勇氣點 Lot of Courage */
  object Courage : Lot("Lot.Courage", "Lot.Courage_ABBR")

  /** 復仇點 Lot of Nemesis */
  object Nemesis : Lot("Lot.Nemesis", "Lot.Nemesis_ABBR")


  override fun compareTo(other: Lot): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }

  companion object : IPoints<Lot> {

    /**
     * 改名前的 nameKey 前綴（`Arabic.Fortune`）。`data_natal.horoscope.body` 的 `arabicLots` 存的是舊字串，
     * 讀得回來的相容只在這裡；DB 換成新字串之後拿掉（root `docs/plans/2026-11-13-hellenistic-lots-design.md` §10）
     */
    const val LEGACY_PREFIX = "Arabic."

    override val type: KClass<out Point> = Lot::class

    override val values by lazy {
      arrayOf(Fortune, Spirit, Eros, Victory, Necessity, Courage, Nemesis)
    }
    val list by lazy { listOf(*values) }

    override fun fromString(value: String, locale: Locale): Lot? {
      return values.firstOrNull {
        it.getTitle(locale).equals(value, ignoreCase = true)
      }
    }

    /** 新的 nameKey，或改名前的 `Arabic.Fortune` */
    override fun valueOf(nameKey: String): Lot? =
      super.valueOf(nameKey) ?: if (nameKey.startsWith(LEGACY_PREFIX)) super.valueOf("Lot." + nameKey.removePrefix(LEGACY_PREFIX)) else null
  }
}
