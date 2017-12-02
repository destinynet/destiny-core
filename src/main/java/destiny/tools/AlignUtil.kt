/**
 * @author smallufo
 * Created on 2008/2/20 at 上午 12:35:35
 */
package destiny.tools

import org.apache.commons.lang3.StringUtils

object AlignUtil {

  /**
   * 將 double 轉成 String , 前面塞入適當的 fill 字元，使其寬度變為 w
   * 如果 double 比 w 長，則從最「後面」摘掉字元 (因為這是小數點)
   *
   * Apache 的 [StringUtils] 尚無法提供此功能
   */
  fun alignRight(value: Double, width: Int, fill: Char): String {
    val sb = StringBuilder()

    sb.append(Math.abs(value).toString())
    val valueLength: Int
    if (value < 0)
      sb.insert(0, "-")
    valueLength = sb.length

    if (valueLength == width)
      return sb.toString()
    else if (valueLength < width) {
      val whiteSpaces = width - valueLength

      for (i in 0 until whiteSpaces) {
        sb.insert(0, fill)
      }
      return sb.toString()
    } else {
      //sb.length() > w
      return sb.substring(0, width)
    }
  }
}
