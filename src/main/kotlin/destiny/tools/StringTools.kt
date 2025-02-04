/**
 * Created by smallufo on 2019-04-02.
 */
package destiny.tools

object StringTools {

  /**
   * simple Meyer’s Diff Algorithm
   */
  fun markDifferences(original: String, modified: String): String {
    val diffResult = StringBuilder()
    var i = 0
    var j = 0

    while (i < original.length && j < modified.length) {
      if (original[i] == modified[j]) {
        diffResult.append(original[i])
        i++
        j++
      } else {
        diffResult.append("[${modified[j]}]")  // 用[]標記變更
        j++
        i++
      }
    }

    // 若 modified 有額外字符
    while (j < modified.length) {
      diffResult.append("[${modified[j]}]")
      j++
    }

    return diffResult.toString()
  }

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
