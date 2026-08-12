package destiny.core

import destiny.core.astrology.*
import destiny.tools.I18nBundles
import destiny.tools.ILocaleString
import destiny.tools.JSerializable
import destiny.tools.toLang
import java.util.*
import destiny.tools.defaultLocale
import destiny.tools.Lang

/**
 * 抽象class , 代表星盤上的一「點」，可能是實星（行星 [Planet], 小行星 [Asteroid], 恆星 [FixedStar]），
 * 虛星 (漢堡星 [Hamburger]) , 也可能只是交點 (例如：黃白交點 [LunarPoint])
 * 目前繼承圖如下：
 *
 *                                                               Point
 *                                                                 |
 *                                            +--------------------+--------------------+
 *                                            |                                         |
 *                                        AstroPoint                                  [ZStar] (紫微)
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
  val nameKey: String,
  val resource: String,
  /** 縮寫key , 為了輸出美觀所用 , 限定兩個 bytes , 例如 : 日(SU) , 月(MO) , 冥(PL) , 升(No) , 強(So) , 穀 , 灶 ... */
  val abbrKey: String? = null) : JSerializable
{

  init {
    @Suppress("LeakingThis")
    register(this)
  }

  open fun readResolve(): Any {
    return getByNameKey(nameKey) ?: throw IllegalArgumentException("$nameKey does not exist")
  }

  /** 名稱  */
  private val name: String by lazy {
    I18nBundles.string(resource, defaultLocale.toLang(), nameKey) ?: nameKey
  }

  /** toString 直接取名稱  */
  override fun toString(): String {
    return name
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Point) return false

    return nameKey == other.nameKey
  }

  override fun hashCode(): Int {
    return nameKey.hashCode()
  }

  companion object {
    private val instances = mutableMapOf<String, Point>()

    fun register(point : Point) {
      instances[point.nameKey] = point
    }

    fun getByNameKey(name : String) : Point? = instances[name]
  }
}

fun Point.asLocaleString() = object : ILocaleString {
  override fun getTitle(lang: Lang): String {
    return I18nBundles.string(resource, lang, nameKey) ?: nameKey
  }
}

fun Point.toString(lang: Lang): String {
  return this.asLocaleString().getTitle(lang)
}

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun Point.toString(locale: Locale): String = toString(locale.toLang())

/** 取得縮寫 , 如果沒有傳入縮寫，則把 name 取前兩個 bytes  */
fun Point.getAbbreviation(lang: Lang): String {
  /** 處理縮寫 */
  fun getAbbr(lang: Lang, value: String): String {
    return if (lang.language == "zh") {
      value.substring(0, 1)
//      val byteArray: ByteArray
//      val arr = ByteArray(2)
//      try {
//        byteArray = String(value.toCharArray()).toByteArray(charset("Big5"))
//        System.arraycopy(byteArray, 0, arr, 0, 2)
//      } catch (ignored: UnsupportedEncodingException) {
//      }
//      String(arr)
    } else {
      value.substring(0, 2)
    }
  }

  return if (abbrKey != null) {
    I18nBundles.string(resource, lang, abbrKey) ?: abbrKey
  } else {
    val name = I18nBundles.string(resource, lang, nameKey) ?: nameKey
    getAbbr(lang, name)
  }
}

/** 橋接，說明見 [destiny.tools.ILocaleString] */
fun Point.getAbbreviation(locale: Locale): String = getAbbreviation(locale.toLang())
