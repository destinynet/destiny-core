/**
 * @author smallufo
 * Created on 2007/8/29 at 下午 3:10:18
 */
package destiny.core.astrology

import destiny.core.ILoop
import destiny.core.astrology.Element.*
import destiny.core.astrology.Quality.*
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.IYinYang
import destiny.tools.ArrayTools
import destiny.tools.CircleTools
import destiny.tools.ILocaleString
import java.util.*


fun ZodiacSign.asLocaleString() = object : ILocaleString {
  private val resource = ZodiacSign::class.qualifiedName!!
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(this@asLocaleString.nameKey)
  }
}

fun ZodiacSign.toString(locale: Locale): String {
  return this.asLocaleString().toString(locale)
}


/** 黃道十二宮  */
enum class ZodiacSign(val nameKey: String,
                      private val abbrKey: String,
                      /** 四正 (火/土/風/水)  */
                      val element: Element,
                      /** 三方 (基本/固定/變動)  */
                      val quality: Quality,
                      /** 陰陽  */
                      private val yinYang: Boolean,
                      /** 黃道起始度數  */
                      val degree: Int,
                      val unicode: Char) : IYinYang, ILoop<ZodiacSign> {
  /** Aries 戌/牡羊  */
  ARIES("ZodiacSign.ARIES", "ZodiacSign.ARIES_ABBR", FIRE, CARDINAL, true, 0, '♈'),
  /** Taurus 酉/金牛  */
  TAURUS("ZodiacSign.TAURUS", "ZodiacSign.TAURUS_ABBR", EARTH, FIXED, false, 30, '♉'),
  /** Gemini 申/雙子  */
  GEMINI("ZodiacSign.GEMINI", "ZodiacSign.GEMINI_ABBR", AIR, MUTABLE, true, 60, '♊'),
  /** Cancer 未/巨蟹  */
  CANCER("ZodiacSign.CANCER", "ZodiacSign.CANCER_ABBR", WATER, CARDINAL, false, 90, '♋'),
  /** Leo 午/獅子  */
  LEO("ZodiacSign.LEO", "ZodiacSign.LEO_ABBR", FIRE, FIXED, true, 120, '♌'),
  /** Virgo 巳/處女  */
  VIRGO("ZodiacSign.VIRGO", "ZodiacSign.VIRGO_ABBR", EARTH, MUTABLE, false, 150, '♍'),
  /** Libra 辰/天秤  */
  LIBRA("ZodiacSign.LIBRA", "ZodiacSign.LIBRA_ABBR", AIR, CARDINAL, true, 180, '♎'),
  /** Scorpio 卯/天蠍  */
  SCORPIO("ZodiacSign.SCORPIO", "ZodiacSign.SCORPIO_ABBR", WATER, FIXED, false, 210, '♏'),
  /** Sagittarius 寅/射手  */
  SAGITTARIUS("ZodiacSign.SAGITTARIUS", "ZodiacSign.SAGITTARIUS_ABBR", FIRE, MUTABLE, true, 240, '♐'),
  /** Capricorn 丑/摩羯  */
  CAPRICORN("ZodiacSign.CAPRICORN", "ZodiacSign.CAPRICORN_ABBR", EARTH, CARDINAL, false, 270, '♑'),
  /** Aquarius 子/水瓶  */
  AQUARIUS("ZodiacSign.AQUARIUS", "ZodiacSign.AQUARIUS_ABBR", AIR, FIXED, true, 300, '♒'),
  /** Pisces 亥/雙魚  */
  PISCES("ZodiacSign.PISCES", "ZodiacSign.PISCES_ABBR", WATER, MUTABLE, false, 330, '♓');

  /** 縮寫  */
  val abbreviation: String
    get() = ResourceBundle.getBundle(resource, Locale.getDefault()).getString(abbrKey)

  /** 取得對沖的星座  */
  val oppositeSign: ZodiacSign
    get() = values()[normalize(this.index + 6)]

  /** 取得星座的 index , 為 0-based , 牡羊座為 0 , 金牛座為 1 , ... , 雙魚座為 11  */
  val index: Int by lazy {
    values().indexOf(this)
  }

  /**
   * @return 取得星座的地支名稱 (牡羊 : 戌)
   */
  val branch: Branch by lazy {
    map.getValue(this)
  }

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  fun getAbbreviation(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(abbrKey)
  }

  override fun next(n: Int): ZodiacSign {
    return get(index + n)
  }

  override val booleanValue: Boolean
    get() = yinYang

  companion object {

    private val resource = ZodiacSign::class.qualifiedName!!

    private val map = mapOf(
      ARIES to 戌,
      TAURUS to 酉,
      GEMINI to 申,
      CANCER to 未,
      LEO to 午,
      VIRGO to 巳,
      LIBRA to 辰,
      SCORPIO to 卯,
      SAGITTARIUS to 寅,
      CAPRICORN to 丑,
      AQUARIUS to 子,
      PISCES to 亥
    )

    /**
     * 抓取地支的 index , 為 0-based
     * 0 : 牡羊
     * 1 : 金牛
     * ...
     * 11 : 雙魚
     */
    operator fun get(index: Int): ZodiacSign {
      return ArrayTools[values(), index]
    }

    /** 取得黃道帶上的某度，屬於哪個星座  */
    fun of(degree: Double): ZodiacSign {
      val index = (CircleTools.getNormalizeDegree(degree) / 30).toInt()
      return values()[index]
    }

    /** 此黃道帶的度數，等於什麼星座幾度  */
    fun getSignAndDegree(degree: Double): Pair<ZodiacSign, Double> {
      val index = (CircleTools.getNormalizeDegree(degree) / 30).toInt()
      val sign = values()[index]
      val deg = degree % 30
      return Pair(sign, deg)
    }

    private fun normalize(value: Int): Int {
      return when {
        value > 11 -> normalize(value - 12)
        value < 0 -> normalize(value + 12)
        else -> value
      }
    }

    /**
     * 從地支，找星座
     */
    fun of(branch: Branch): ZodiacSign {
      return map.map { (k, v) -> v to k }.toMap().getValue(branch)
    }

    /**
     * 找尋 某種 [Element] 星座
     */
    fun of(element: Element): Set<ZodiacSign> {
      return values().filter { it.element == element }.toSet()

    }


  }



}
