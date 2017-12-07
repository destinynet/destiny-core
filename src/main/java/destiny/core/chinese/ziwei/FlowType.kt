/**
 * Created by smallufo on 2017-04-17.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import java.util.*

/** 「流運」的類型  */
enum class FlowType : Descriptive {
  本命, 大限, 流年, 流月, 流日, 流時;

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(FlowType::class.java.name, locale).getString(name)
    } catch (e: MissingResourceException) {
      name
    }

  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}
