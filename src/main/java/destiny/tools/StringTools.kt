/**
 * Created by smallufo on 2019-04-02.
 */
package destiny.tools

object StringTools {

//  private const val char160 = 160.toChar()
//  private const val char005 = 5.toChar()


  // https://stackoverflow.com/a/11020944/298430
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
    return out.toString().replace("\\s".toRegex(), " ")
  }


  // not working
  fun localTrim(s: String): String {
    return s.trim()
      .replace("\r\n", "\n")
      .replace("\r", "\n")
      .replace("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "")
      .replace("[^\\x00-\\x7F]", "")
      .replace("\\P{Print}", "")
      .replace("\\p{XDigit}{4}" ,"")
  }
}