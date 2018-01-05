/**
 * @author smallufo
 * Created on 2008/2/20 at 上午 12:35:35
 */
package destiny.tools

import org.apache.commons.lang3.StringUtils
import kotlin.math.absoluteValue

object AlignTools {

  fun leftPad(value: String, size: Int, padChar: Char = ' '): String {
    return if (value.length >= size)
      value.substring(0, size)
    else {
      return value.padStart(size, padChar)
    }
  }

  /**
   * 將 double 轉成 String , 前面塞入適當的 fill 字元，使其寬度變為 w
   * 如果 double 比 w 長，則從最「後面」摘掉字元 (因為這是小數點)
   *
   * Apache 的 [StringUtils] 尚無法提供此功能
   */
  fun alignRight(value: Double, width: Int, padChar: Char = ' '): String {

    val s = value.absoluteValue.toString().let {
      if (value < 0)
        '-' + it
      else
        it
    }

    val valueLength = s.length

    return when {
      valueLength == width -> s
      valueLength < width -> {
        val whiteSpaces = width - valueLength
        padChar.toString().repeat(whiteSpaces) + s
      }
      else -> //sb.length() > w
        s.substring(0, width)
    }
  }


  /**
   * 將 int 轉成 String , 前後 塞入適當的空白字元，使其寬度變為 w
   * 如果 int 比 w 長，則從最前面摘掉字元
   */
  fun alignCenter(value: Int, width: Int): String {
    val sb = StringBuilder()
    if (value < 0)
      sb.append("前")

    sb.append(Math.abs(value).toString())
    val valueLength: Int
    valueLength = if (value > 0)
      sb.length
    else
      sb.length + 1 // 加上一個「前」 的 2-bytes

    when {
      valueLength == width -> return sb.toString()
      valueLength < width -> {
        val leftSpaces: Int //左邊的空格
        val rightSpaces: Int //右邊的空格
        if (valueLength % 2 == 0) {
          //長度是偶數
          if (width % 2 == 0) {
            //寬度是偶數
            //左右空格同寬
            leftSpaces = (width - valueLength) / 2
            rightSpaces = leftSpaces
          } else {
            //寬度是奇數
            leftSpaces = (width - valueLength) / 2 + 1
            rightSpaces = leftSpaces - 1
          }
        } else {
          //長度是奇數
          if (width % 2 == 0) {
            //寬度是偶數
            leftSpaces = (width - valueLength) / 2
            rightSpaces = leftSpaces + 1
          } else {
            //寬度是奇數
            //左右空格同寬
            leftSpaces = (width - valueLength) / 2
            rightSpaces = leftSpaces
          }
        }

        sb.insert(0, generateDoubleSpace(leftSpaces))
        sb.append(generateDoubleSpace(rightSpaces))

        return sb.toString()
      }
      else -> //sb.length() > w
        return sb.substring(valueLength - width)
    }
  } //alignCenter

  /**
   * 產生全形空白
   */
  private fun generateDoubleSpace(length: Int): String {
    val sb = StringBuilder()
    val doubleByteSpaces = length / 2
    for (i in 0 until doubleByteSpaces) {
      sb.insert(0, "　")
    }
    if (length % 2 == 1)
      sb.insert(doubleByteSpaces, ' ')

    return sb.toString()
  }
}
