/**
 * Created by smallufo on 2017-04-08.
 */
package destiny.tools

object ChineseStringTools {

  const val NULL_CHAR = "　" //空白字元，使用全形的空白, 在 toString() 時使用

  /**
   * 轉換成中文數字
   */
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
      0 -> "０"
      else -> NULL_CHAR
    }
  }

  fun Int.toChineseDigit(): String {
    return digitToChinese(this)
  }

  /**
   * 轉換成全形數字
   */
  fun digitToFull(digit: Int): String {
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
      0 -> "０"
      else -> NULL_CHAR
    }
  }

  fun Int.toFullDigit(): String {
    return digitToFull(this)
  }


  fun toBiggerDigits(value: Int): String {
    return value.toString().toList().joinToString("") { char ->
      digitToFull(char.toString().toInt())
    }
  }

  /** 搜尋字串中的數字，並且替換成全型 */
  fun replaceToBiggerDigits(value : String) : String {
    return value.toCharArray().joinToString("") { c : Char ->
      if (c.isDigit()){
        digitToFull(c.toString().toInt())
      }
      else
        c.toString()
    }
  }

  /**
   * @param value 確保每個字都是全型中文字
   */
  fun alignRight(value: String, width: Int): String {
    val valueLength = value.length*2
    return if (valueLength == width)
      value
    else {
      val doubleByteSpaces = (width - valueLength)/2
      NULL_CHAR.repeat(doubleByteSpaces)+ value
    }
  }
}
