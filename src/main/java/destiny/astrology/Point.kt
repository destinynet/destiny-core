/**
 * @author smallufo
 * Created on 2007/7/24 at 下午 1:34:43
 */
package destiny.astrology

import destiny.tools.ILocaleString

import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * 抽象class , 代表星盤上的一「點」，可能是實星（行星 [Planet], 小行星 [Asteroid], 恆星 [FixedStar]），
 * 虛星 (漢堡星 [Hamburger]) , 也可能只是交點 (例如：黃白交點 [LunarPoint])
 * 目前繼承圖如下：
 *
 *                                          Point
 *                                            |
 *                       +--------------------+--------------------+
 *                       |                                         |
 *                     Star                                       Axis
 *                       |
 *   +---------+---------+--------+-----------------+
 *   |         |         |        |                 |
 * Planet  Asteroid  FixedStar LunarPoint(A)    Hamburger
 *  行星    小行星     恆星     日月交點         漢堡虛星
 *                                |
 *                                |
 *                       +--------+--------+
 *                       |                 |
 *                   LunarNode         LunarApsis
 *                   [TRUE/MEAN]       [MEAN/OSCU]
 *                   North/South   PERIGEE (近)/APOGEE (遠)
 *
 */
abstract class Point(
  /** 名稱key , nameKey 相等，則此 Point 視為 equals!  */
  private val nameKey: String,
  private val resource: String,
  /** 縮寫key , 為了輸出美觀所用 , 限定兩個 bytes , 例如 : 日(SU) , 月(MO) , 冥(PL) , 升(No) , 強(So) , 穀 , 灶 ... */
  private val abbrKey: String? = null) : Serializable, ILocaleString {

  /** 名稱  */
  private val name: String by lazy {
    ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  /** 取得縮寫 , 如果沒有傳入縮寫，則把 name 取前兩個 bytes  */
  val abbreviation: String by lazy {
    if (abbrKey != null) {
      ResourceBundle.getBundle(resource, Locale.getDefault()).getString(abbrKey)
    } else {
      val name = ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
      getAbbr(Locale.getDefault(), name)
    }
  }


  /** 名稱  */
  fun getName(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  /** toString 直接取名稱  */
  override fun toString(): String {
    return name
  }

  /** toString 直接取名稱 (name)  */
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  /** 取得縮寫 , 如果沒有傳入縮寫，則把 name 取前兩個 bytes  */
  fun getAbbreviation(locale: Locale): String {
    return if (abbrKey != null) {
      ResourceBundle.getBundle(resource, locale).getString(abbrKey)
    } else {
      val name = ResourceBundle.getBundle(resource, locale).getString(nameKey)
      getAbbr(locale, name)
    }
  }

  /**
   * 處理縮寫
   */
  private fun getAbbr(locale: Locale?, value: String): String {
    return if (locale != null && locale.language == "zh" && locale.country == "TW") {
      val byteArray: ByteArray
      val arr = ByteArray(2)
      try {
        byteArray = String(value.toCharArray()).toByteArray(charset("Big5"))

        System.arraycopy(byteArray, 0, arr, 0, 2)
      } catch (ignored: UnsupportedEncodingException) {
      }

      String(arr)
    } else {
      value.substring(0, 2)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Point) return false

    if (nameKey != other.nameKey) return false

    return true
  }

  override fun hashCode(): Int {
    return nameKey.hashCode()
  }


}
