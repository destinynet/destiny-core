/*
 * 彩色的一個字 (byte)
 * 包含 字體、Color、URL 等資訊
 * @author smallufo
 * @date 2004/8/13
 * @time 下午 06:08:03
 */
package destiny.tools.canvas

import java.awt.Font
import java.io.Serializable


internal class ColorByte(val byte: Byte, val font: Font?, val url: String?, val title: String?) : Serializable {


  var foreColor: String? = null //前景色

  private var backColor: String? = null //背景色

  constructor(bgChar: Char) : this(bgChar.toByte(), null, null, null, null, null)

  constructor(b: Byte, foreColor: String?, backColor: String?, font: Font?, url: String?, title: String?) : this(
    b,
    font,
    url,
    title
  ) {
    this.foreColor = validateColor(foreColor)
    this.backColor = validateColor(backColor)
  }

  /**
   * @return Returns the backColor.
   */
  fun getBackColor(): String? {
    return backColor
  }

  fun setBackColor(color: String?) {
    this.backColor = this.validateColor(color)
  }


  /**
   * @param color 檢查 color 字串，將其轉成正確的表示語法
   * 檢查 color 的每個 byte 是否介於 0~9 , A~F , a~f 之間
   * 0 ~ 9 = (ASCII) 48 ~ 57
   * A ~ F = (ASCII) 65 ~ 70
   * a ~ f = (ASCII) 97 ~ 102
   * 如果檢查正確的話，代表使用者所填的 color 是 "000000" 到 "FFFFFF" 之間（或是 3-byte 的表示法 000 到 FFF ） ,
   * 但是沒有加上 "#" , 所以會在前面加上 "#"
   */
  private fun validateColor(color: String?): String? {
    var finalColor = color
    if (color != null &&
      (
        color.toByteArray().size == 3
          || color.toByteArray().size == 6
          || color.startsWith("#") && (color.toByteArray().size == 4
          || color.toByteArray().size == 7)
        )
    ) {
      val colorBytes = color.toByteArray()
      val start = if (color.startsWith("#")) 1 else 0
      val isRGB = (start until colorBytes.size).all {
        (colorBytes[it] in 48..57
          || colorBytes[it] in 65..70
          || colorBytes[it] in 97..102)
      }
      //loop 6 bytes
      if (isRGB) {
        finalColor = color.toUpperCase()
        if (start == 0)
          finalColor = "#" + finalColor
      } else
        finalColor = color
    }

    return finalColor
  }//validateColor


  /**
   * 檢查這個 ColorByte 是否與另一個 ColorByte 除了 byte 不同外，其他都相同
   * @param cb
   * @return
   */
  fun isSameProperties(cb: ColorByte): Boolean {
    return (this.foreColor == null && cb.foreColor == null || this.foreColor != null && this.foreColor == cb.foreColor)
      && (this.backColor == null && cb.backColor == null || this.backColor != null && this.backColor == cb.backColor)
      && (this.font == null && cb.font == null || this.font != null && this.font == cb.font)
      && this.url == cb.url
      && (this.title == null && cb.title == null || this.title != null && this.title == cb.title)
  }

  override fun toString(): String {
    return "ColorByte [b=$byte]"
  }

}//Class
