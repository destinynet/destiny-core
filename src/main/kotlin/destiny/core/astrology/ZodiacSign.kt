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
import destiny.tools.CircleTools.normalize
import destiny.tools.getTitle
import kotlinx.serialization.Serializable
import java.util.*
import destiny.tools.I18nBundles
import destiny.tools.toLang
import destiny.tools.defaultLocale


fun ZodiacSign.getAbbreviation(locale: Locale): String {
  val resource = ZodiacSign::class.qualifiedName!!
  return I18nBundles.string(resource, locale.toLang(), this.abbrKey) ?: this.abbrKey
}


/** 黃道十二宮  */
@Serializable
enum class ZodiacSign(val abbrKey: String,
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
  ARIES("ZodiacSign.ARIES_ABBR", FIRE, CARDINAL, true, 0, '♈'),
  /** Taurus 酉/金牛  */
  TAURUS("ZodiacSign.TAURUS_ABBR", EARTH, FIXED, false, 30, '♉'),
  /** Gemini 申/雙子  */
  GEMINI("ZodiacSign.GEMINI_ABBR", AIR, MUTABLE, true, 60, '♊'),
  /** Cancer 未/巨蟹  */
  CANCER("ZodiacSign.CANCER_ABBR", WATER, CARDINAL, false, 90, '♋'),
  /** Leo 午/獅子  */
  LEO("ZodiacSign.LEO_ABBR", FIRE, FIXED, true, 120, '♌'),
  /** Virgo 巳/處女  */
  VIRGO("ZodiacSign.VIRGO_ABBR", EARTH, MUTABLE, false, 150, '♍'),
  /** Libra 辰/天秤  */
  LIBRA("ZodiacSign.LIBRA_ABBR", AIR, CARDINAL, true, 180, '♎'),
  /** Scorpio 卯/天蠍  */
  SCORPIO("ZodiacSign.SCORPIO_ABBR", WATER, FIXED, false, 210, '♏'),
  /** Sagittarius 寅/射手  */
  SAGITTARIUS("ZodiacSign.SAGITTARIUS_ABBR", FIRE, MUTABLE, true, 240, '♐'),
  /** Capricorn 丑/摩羯  */
  CAPRICORN("ZodiacSign.CAPRICORN_ABBR", EARTH, CARDINAL, false, 270, '♑'),
  /** Aquarius 子/水瓶  */
  AQUARIUS("ZodiacSign.AQUARIUS_ABBR", AIR, FIXED, true, 300, '♒'),
  /** Pisces 亥/雙魚  */
  PISCES("ZodiacSign.PISCES_ABBR", WATER, MUTABLE, false, 330, '♓');

  /** 縮寫  */
  val abbreviation: String
    get() = I18nBundles.string(resource, defaultLocale.toLang(), abbrKey) ?: abbrKey

  /** 取得對沖的星座  */
  val oppositeSign: ZodiacSign
    get() = get(ordinal + 6)


  /** 取得星座的 index , 為 0-based , 牡羊座為 0 , 金牛座為 1 , ... , 雙魚座為 11  */
  val index: Int = ordinal

  /**
   * @return 取得星座的地支名稱 (牡羊 : 戌)
   */
  val branch: Branch by lazy {
    map.getValue(this)
  }

  /**
   * ⚠️ 回傳**預設語系的顯示名稱**（例如「獅子」），不是識別字。
   * 要識別字用 [name]（kotlinx／Jackson／JPA 也都走 [name]），要顯示名稱用 [getTitle] 並明確帶語系。
   */
  override fun toString(): String {
    return this.getTitle(defaultLocale)
  }

//  fun getAbbreviation(locale: Locale): String {
//    return ResourceBundle.getBundle(resource, locale).getString(abbrKey)
//  }

  override fun next(n: Int): ZodiacSign {
    return get(ordinal + n)
  }

  /**
   * 以 [from] 為第 1 個星座，順黃道數到本星座是第幾個（1–12）—— 整宮制的「從某星座起算第幾宮」。
   * 居上（右方第 9／10／11 個）、不相見（第 2／6／8／12 個）、對沖（第 7 個）、黃道釋放的角宮都用它
   */
  fun placeFrom(from: ZodiacSign): Int = (index - from.index).mod(12) + 1

  /**
   * 落在本星座 [degInSign] 度（0–30）的點，位置有 ±[error] 度的誤差時，可能其實落在哪個相鄰的星座（壓線）：
   * 離星座尾端不到 [error] 度回下一個星座，否則離開頭不到 [error] 度回上一個；兩邊都夠不到回 null。「不到」不含剛好等於。
   * 只問會不會變、手上有 [ZodiacDegree] 時，用 [ZodiacDegree.signChangesWithin]
   */
  fun neighborWithin(degInSign: Double, error: Double): ZodiacSign? = when {
    30 - degInSign < error -> next(1)
    degInSign < error      -> next(-1)
    else                   -> null
  }

  /** 與 [other] 相隔幾個星座，不分方向（0–6）：同星座 0、對面 6 */
  fun stepsTo(other: ZodiacSign): Int = (other.index - index).mod(12).let { minOf(it, 12 - it) }

  /**
   * **以星座計**，本星座與 [other] 成什麼相位（托勒密五相位）：同星座＝合、隔 2＝六合、隔 3＝刑、隔 4＝拱、對面＝沖；
   * 隔 1 或 5 個星座（從對方起算第 2／6／8／12 個）彼此不相見（aversion），回 null。不分方向。
   *
   * 「以星座計的相位」只有這一個定義 —— 野生、居上、不相見、受剋的對沖、依星座也成立的入相位都由它推，不要各自再列一張星座位置表
   */
  fun aspectTo(other: ZodiacSign): Aspect? = when (stepsTo(other)) {
    0    -> Aspect.CONJUNCTION
    2    -> Aspect.SEXTILE
    3    -> Aspect.SQUARE
    4    -> Aspect.TRINE
    6    -> Aspect.OPPOSITION
    else -> null
  }

  /** 映點星座：以巨蟹／摩羯軸對稱（雙子↔巨蟹、金牛↔獅子…），古典說這兩個星座彼此「看得見」（beholding）；度數版見 [ZodiacDegree.antiscion] */
  val antiscionSign: ZodiacSign
    get() = get(5 - index)

  /** 反映點星座：以牡羊／天秤軸對稱（牡羊↔雙魚、金牛↔水瓶…），古典說這兩個星座「命令與服從」（commanding／obeying）；度數版見 [ZodiacDegree.contraAntiscion] */
  val contraAntiscionSign: ZodiacSign
    get() = get(11 - index)

  override val booleanValue: Boolean
    get() = yinYang

  companion object {

    private val resource = ZodiacSign::class.qualifiedName!!

    private val branchToZodiacMap: Map<Branch, ZodiacSign> by lazy {
      entries.associateBy { it.branch }
    }

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
      return entries[index.mod(entries.size)]
    }

    /** 取得黃道帶上的某度，屬於哪個星座  */
    fun of(degree: Double): ZodiacSign {
      val index = (degree.normalize() / 30).toInt()
      return entries[index]
    }

    /** 此黃道帶的度數，等於什麼星座幾度  */
    fun getSignAndDegree(degree: Double): Pair<ZodiacSign, Double> {
      return of(degree) to degree % 30
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
      return branchToZodiacMap.getValue(branch)
    }

    /**
     * 找尋 某種 [Element] 星座
     */
    fun of(element: Element): Set<ZodiacSign> {
      return entries.filter { it.element == element }.toSet()

    }


  }



}
