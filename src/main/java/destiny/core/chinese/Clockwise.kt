/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese

import destiny.core.Descriptive
import java.util.*

enum class Clockwise {
  CLOCKWISE, // 順行
  COUNTER    // 逆行
}

fun Clockwise.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      Clockwise.CLOCKWISE -> "順時針"
      Clockwise.COUNTER   -> "逆時針"
    }
  }
}
