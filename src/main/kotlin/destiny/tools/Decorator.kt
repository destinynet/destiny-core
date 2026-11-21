/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:20:43
 */
package destiny.tools


/** 依 [lang] 挑最接近的 decorator（語言＋地區＋變體 → 語言＋地區 → 語言 → 第一個）輸出 */
@JvmName("getOutputStringByLang")
fun <T> Map<Lang, Decorator<T>>.getOutputString(value: T, lang: Lang): String {
  return this.getValue(LangTools.getBestMatchingLangOrFirst(lang, this.keys)).getOutputString(value)
}

interface Decorator<in T> {

  fun getOutputString(value: T): String
}
