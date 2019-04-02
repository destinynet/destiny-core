/**
 * Created by smallufo on 2019-04-02.
 */
package destiny.tools

object StringTools {

  // https://stackoverflow.com/a/11020944/298430
  /**
   * 清除看不到的字元
   * 以及移除前後空白
   */
  fun clean(s: String): String {
    val out = StringBuilder()
    var current: Char
    if ("" == s) {
      return ""
    }
    for (i in 0 until s.length) {
      current = s[i]
      if (current.toInt() == 0x9
        || current.toInt() == 0xA
        || current.toInt() == 0xD
        || current.toInt() in 0x20..0xD7FF
        || current.toInt() in 0xE000..0xFFFD
        || current.toInt() in 0x10000..0x10FFFF) {
        out.append(current)
      }

    }
    return out.toString().replace("\\s".toRegex(), " ").trim()
  }

}