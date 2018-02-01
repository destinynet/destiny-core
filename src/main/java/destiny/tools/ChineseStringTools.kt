/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.tools

object ChineseStringTools {

  const val NULL_CHAR = "　" //空白字元，使用全形的空白, 在 toString() 時使用

  fun digitToChinese(digit: Int): String {
    when (digit) {
      1 -> return "一"
      2 -> return "二"
      3 -> return "三"
      4 -> return "四"
      5 -> return "五"
      6 -> return "六"
      7 -> return "七"
      8 -> return "八"
      9 -> return "九"
    }
    throw IllegalArgumentException("digitToChinese : " + digit)
  }
}
