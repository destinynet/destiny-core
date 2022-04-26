/**
 * Created by smallufo on 2019-10-13.
 */
package destiny.core.astrology

import destiny.core.IPoints
import destiny.core.toString
import java.util.*
import kotlin.reflect.KClass

sealed class Arabic(nameKey: String, abbrKey: String, unicode: Char? = null) : Star(nameKey, abbrKey, Star::class.java.name, unicode),
                                                                               Comparable<Arabic> {

  /** 福點 (幸運點) Part of Fortune */
  object Fortune : Arabic("Arabic.Fortune", "Arabic.Fortune_ABBR", '⊗')

  /** 精神點 Part of Spirit */
  object Spirit : Arabic("Arabic.Spirit", "Arabic.Spirit_ABBR")

  /** 愛情點 Lot of Eros */
  object Eros : Arabic("Arabic.Eros", "Arabic.Eros_ABBR")

  /** 勝利點 Lot of Victory */
  object Victory : Arabic("Arabic.Victory", "Arabic.Victory_ABBR")

  /** 必要點 Lot of Necessity */
  object Necessity : Arabic("Arabic.Necessity", "Arabic.Necessity_ABBR")

  /** 勇氣點 Lot of Courage */
  object Courage : Arabic("Arabic.Courage", "Arabic.Courage_ABBR")

  /** 復仇點 Lot of Nemesis */
  object Nemesis : Arabic("Arabic.Nemesis", "Arabic.Nemesis_ABBR")


  override fun compareTo(other: Arabic): Int {
    if (this == other)
      return 0

    return values.indexOf(this) - values.indexOf(other)
  }

  companion object : IPoints<Arabic> {

    override val type: KClass<out AstroPoint> = Arabic::class

    override val values by lazy {
      arrayOf(Fortune, Spirit, Eros, Victory, Necessity, Courage, Nemesis)
    }
    val list by lazy { listOf(*values) }

    override fun fromString(value: String, locale: Locale): Arabic? {
      return values.firstOrNull {
        it.toString(locale).equals(value, ignoreCase = true)
      }
    }
  }
}
