/**
 * @author smallufo
 * Created on 2007/7/25 at 上午 12:11:33
 */
package destiny.core.calendar

import destiny.tools.JSerializable
import kotlin.math.abs
import kotlin.math.roundToLong

class GoogleMapsUrlBuilder : LocationUrlBuilder, JSerializable {

  // https://www.google.com/maps?&z=10&q=25.039059+121.517675&ll=25.039059+121.517675
  override fun getUrl(lat: Double, lng: Double): String {
    val latStr = lat.fixed6()
    val lngStr = lng.fixed6()
    return "https://www.google.com/maps?&z=10&q=$latStr+$lngStr&ll=$latStr+$lngStr&z=14"
  }

  companion object {
    /**
     * 固定 6 位小數，等價於 `String.format(Locale.ROOT, "%f", this)`。
     *
     * 不用 [String.format] 的兩個理由：
     *  1. 它是 JVM-only，擋住此類進入 KMP commonMain；
     *  2. 無 Locale 參數的 `String.format` 會套用 [java.util.Locale.getDefault]，
     *     在小數點為逗號的語系（de / fr …）會產出 `25,039059`，是壞掉的 URL。
     *
     * 等價性由 `GoogleMapsUrlBuilderTest` 以邊界值 + 20 萬筆隨機取樣驗證。
     *
     * 注意必須用 [roundToLong]（ties → +∞，同 `Math.round`）而非 [kotlin.math.round]
     * —— 後者是 `rint` 語意（ties → 偶數），會讓 0.0000005 進位成 0.000000 而與 `%f` 不符。
     */
    internal fun Double.fixed6(): String {
      val neg = this < 0 || (this == 0.0 && 1.0 / this < 0)   // 含 -0.0
      val scaled = (abs(this) * 1_000_000.0).roundToLong()    // ties 進位方向同 %f 的 HALF_UP
      val frac = (scaled % 1_000_000L).toString().padStart(6, '0')
      return buildString {
        if (neg) append('-')
        append(scaled / 1_000_000L).append('.').append(frac)
      }
    }
  }
}
