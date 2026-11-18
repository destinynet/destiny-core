/**
 * @author smallufo
 * Created on 2007/11/26 at 上午 5:40:57
 */
package destiny.core.astrology.classical

import destiny.tools.ILocaleString
import destiny.tools.I18nBundles
import destiny.tools.Lang

/**
 * 行星落入星座的 , 廟 旺 陷 落
 */
/**
 * [score]：Lilly《Christian Astrology》的本質尊貴分數（廟 +5、旺 +4、三分 +3、界 +2、面 +1、落 -4、陷 -5）
 */
enum class Dignity(val nameKey: String, val score: Int) : Comparator<Dignity>, ILocaleString {

  /** 廟 (+5) , 守護  */
  RULER("Dignity.RULER", 5),

  /** 旺 (+4) , 躍升  */
  EXALTATION("Dignity.EXALTATION", 4),

  /** 三分性 (+3) , 三分主星 */
  TRIPLICITY("Dignity.TRIPLICITY", 3),

  /** 界 (+2) , 六度守護 */
  TERM("Dignity.TERM", 2),

  /** 面 (+1) , 十度守護 */
  FACE("Dignity.FACE", 1),

  /** 落 (-4) , 失利 , 某派認為此情形比 [DETRIMENT] 更糟 */
  FALL("Dignity.FALL", -4),

  /** 陷 (-5) , 自己守護星座的對立面 */
  DETRIMENT("Dignity.DETRIMENT", -5);

  override fun getTitle(lang: Lang): String {
    return I18nBundles.string("destiny.core.astrology.classical.Classical", lang, nameKey) ?: nameKey
  }

  override fun compare(o1: Dignity?, o2: Dignity?): Int {
    return entries.toTypedArray().let { array ->
      array.indexOf(o1) - array.indexOf(o2)
    }
  }
}
