/**
 * Created by smallufo on 2019-04-02.
 */
package destiny.tools

import java.net.URLDecoder

object StringTools {

  /**
   * 清除看不到的字元
   * 以及移除前後空白
   * https://stackoverflow.com/a/11020944/298430
   */
  fun clean(s: String): String {
    val out = StringBuilder()
    var current: Char
    if ("" == s) {
      return ""
    }
    for (element in s) {
      current = element
      if (current.code == 0x9
        || current.code == 0xA
        || current.code == 0xD
        || current.code in 0x20..0xD7FF
        || current.code in 0xE000..0xFFFD
        || current.code in 0x10000..0x10FFFF) {
        out.append(current)
      }

    }
    return out.toString().replace("\\s".toRegex(), " ").trim()
  }


  /**
   * 不斷 url decode 到不能 decode 為止
   * 必須把 空白字元 與 "+" 視為相等
   */
  fun String.decodeToFinalUrl() : String {
    val s = URLDecoder.decode(this, "UTF-8").replace(" ", "+")
    return if (s == this)
      s
    else
      s.decodeToFinalUrl()
  }
}
