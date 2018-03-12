/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.tools

object ChineseStringTools {

  const val NULL_CHAR = "　" //空白字元，使用全形的空白, 在 toString() 時使用

  fun digitToChinese(digit: Int): String {
    return when (digit) {
      1 -> "一"
      2 -> "二"
      3 -> "三"
      4 -> "四"
      5 -> "五"
      6 -> "六"
      7 -> "七"
      8 -> "八"
      9 -> "九"
      else -> NULL_CHAR
    }
  }

  fun toBiggerDigit(digit: Int): String {
    return when (digit) {
      1 -> "１"
      2 -> "２"
      3 -> "３"
      4 -> "４"
      5 -> "５"
      6 -> "６"
      7 -> "７"
      8 -> "８"
      9 -> "９"
      else -> ChineseStringTools.NULL_CHAR
    }
  }
}
