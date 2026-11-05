/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.tools

import destiny.core.Descriptive
import destiny.core.IPattern
import destiny.core.astrology.*
import java.util.Locale

/**
 * 利用 properties 檔案，將 [IPattern] 轉換成 [Descriptive]
 * 利用 [IPattern]::class.java.simpleName 作為 nameKey
 * 而 另外帶入 commentKey (常用 "comment") 當作 commentKey
 *
 */
abstract class AbstractPropertyBasedPatternDescriptor(val pattern: IPattern,
                                                      private val commentKey: String,
                                                      private val parameters: List<Any>,
                                                      private val doubleFormat : String? = "%.1f") : Descriptive , JSerializable {

  private val nameKey = pattern::class.java.simpleName

  abstract val resource: String

  override fun getTitle(lang: Lang): String {
    logger.trace("try to get nameKey = {} of lang = {} , resource = {}", nameKey, lang, resource)
    return I18nBundles.string(resource, lang, nameKey)
      ?: pattern::class.simpleName!!.also { logger.info("cannot get from nameKey = {}", nameKey) }
  }

  /** 註解缺漏時退回 [getTitle]，與 `Enum<T>.getDescription` 的慣例一致（原本會拋例外） */
  override fun getDescription(lang: Lang): String {
    logger.trace("commentKey = {} , parameters = {}", commentKey, parameters)
    val pattern: String = I18nBundles.string(resource, lang, "$nameKey.$commentKey")
      ?: return getTitle(lang)
    return formatPattern(pattern, getCommentParameters(lang, parameters).toList())
  }

  /** 設定註解參數 , 檢查參數是否是 [ILocaleString] , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters
   *
   * score (0~1 的 double 值) 改用百分比呈現
   * */
  private fun getCommentParameters(lang: Lang, commentParameters: List<Any>): Array<Any> {

    fun objectToString(it : Any) : String {
      return when(it) {
        is Score -> (it.value * 100).toInt().toString()
        // AstroPoint、Dignity 都是 ILocaleString，走這一條
        is ILocaleString -> it.getTitle(lang)
        // 小數點固定用「.」：不帶 Locale 的話跟著 JVM 預設語系走（fr／es 會變成逗號）
        is Double -> String.format(Locale.ROOT, doubleFormat ?: "%.1f", it)
        is Element -> it.getTitle(lang)
        is Quality -> it.getTitle(lang)
        is Aspect -> it.getTitle(lang)
        is ZodiacSign -> it.getTitle(lang)
        is Collection<*> -> it.joinToString(",") { item -> objectToString(item?:"")  }
        else -> it.toString()
      }
    }

    return commentParameters.map { objectToString(it) }.toTypedArray()
  }


  companion object {
    val logger = KotlinLogging.logger { }
  }

}
