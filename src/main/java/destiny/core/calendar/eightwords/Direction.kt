/**
 * Created by smallufo on 2014-12-01.
 */
package destiny.core.calendar.eightwords

import destiny.core.Descriptive
import java.util.*

/** 排列方向：右到左，還是左到右  */
enum class Direction : Descriptive {
  R2L, L2R;

  override fun getTitle(locale: Locale): String {
    return when (this) {
      L2R -> "左至右"
      R2L -> "右至左"
    }
  }
}
