/**
 * Created by smallufo on 2021-09-21.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import java.util.*

enum class Tianyi {
  Authorized,
  LiuBowen,
  LiurenPithy,
  Ocean,
  ZiweiBook
}

fun Tianyi.asDescriptive() = object : Descriptive {

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(Tianyi::class.java.name, locale).getString("${name}.title")
  }

  override fun getDescription(locale: Locale): String {
    return ResourceBundle.getBundle(Tianyi::class.java.name, locale).getString("${name}.description")
  }
}

fun Tianyi.toString(locale: Locale): String {
  return this.asDescriptive().toString(locale)
}

fun Tianyi.getDescription(locale: Locale): String {
  return this.asDescriptive().getDescription(locale)
}
