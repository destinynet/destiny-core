/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.tools

import destiny.core.Descriptive
import destiny.core.IPattern
import destiny.core.astrology.*
import destiny.core.astrology.classical.Dignity
import destiny.core.astrology.classical.toString
import destiny.core.toString
import java.io.Serializable
import java.text.MessageFormat
import java.util.*

/**
 * 利用 properties 檔案，將 [IPattern] 轉換成 [Descriptive]
 * 利用 [IPattern]::class.java.simpleName 作為 nameKey
 * 而 另外帶入 commentKey (常用 "comment") 當作 commentKey
 *
 */
abstract class AbstractPropertyBasedPatternDescriptor(val pattern: IPattern,
                                                      private val commentKey: String,
                                                      private val parameters: List<Any>,
                                                      private val doubleFormat : String? = "%.1f") : Descriptive , Serializable {

  private val nameKey = pattern::class.java.simpleName

  abstract val resource: String

  override fun getTitle(locale: Locale): String {
    return try {
      logger.trace("try to get nameKey = {} of locale = {} , resource = {}", nameKey, locale, resource)
      ResourceBundle.getBundle(resource, locale).getString(nameKey)
    } catch (e: Exception) {
      logger.info("cannot get from nameKey = {}", nameKey)
      pattern::class.simpleName!!
    }
  }

  override fun getDescription(locale: Locale): String {
    logger.trace("commentKey = {} , parameters = {}", commentKey, parameters)
    val pattern: String = ResourceBundle.getBundle(resource, locale).getString("$nameKey.$commentKey")
    return MessageFormat.format(pattern, *getCommentParameters(locale, parameters))
  }

  /** 設定註解參數 , 檢查參數是否是 [ILocaleString] , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters
   *
   * score (0~1 的 double 值) 改用百分比呈現
   * */
  private fun getCommentParameters(locale: Locale, commentParameters: List<Any>): Array<Any> {

    fun objectToString(it : Any) : String {
      return when(it) {
        is Score -> (it.value * 100).toInt().toString()
        is ILocaleString -> it.getTitle(locale)
        is Double -> String.format( doubleFormat?:"%.1f", it)
        is AstroPoint -> it.toString(locale)
        is Element -> it.getTitle(locale)
        is Quality -> it.getTitle(locale)
        is Aspect -> it.getTitle(locale)
        is Dignity -> it.toString(locale)
        is ZodiacSign -> it.getTitle(locale)
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
