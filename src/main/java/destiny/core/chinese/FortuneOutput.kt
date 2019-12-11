/**
 * Created by smallufo on 2017-04-24.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import java.util.*

/**
 * 輸出大運的模式
 */
enum class FortuneOutput : Descriptive {
  虛歲,
  西元,
  民國,
  實歲;

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(FortuneOutput::class.java.name, locale).getString(name)
  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }
}
