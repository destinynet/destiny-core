/**
 * Created by smallufo on 2019-04-02.
 */
package destiny.tools

object StringTools {

  /**
   * 清除看不到的字元
   * 以及移除前後空白
   * https://stackoverflow.com/a/11020944/298430
   */
  fun String.clean(): String {
    val out = StringBuilder()
    var current: Char
    if ("" == this) {
      return ""
    }
    for (element in this) {
      current = element
      if (current.code == 0x9
        || current.code == 0xA
        || current.code == 0xD
        || current.code in 0x20..0xD7FF
        || current.code in 0xE000..0xFFFD
        || current.code in 0x10000..0x10FFFF
      ) {
        out.append(current)
      }

    }
    return out.toString().replace("\\s".toRegex(), " ").trim()
  }

  fun String.takeAndEllipsis(limit: Int): String {
    return if (this.length <= limit) {
      this
    } else {
      this.take(limit)+"…"
    }
  }
}
