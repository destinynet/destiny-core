package destiny.astrology.classical.rules

import destiny.core.Descriptive
import destiny.tools.ILocaleString
import mu.KotlinLogging
import java.text.MessageFormat
import java.util.*

/**
 * 將 [IPlanetPattern] 轉譯成 [Descriptive] , 以利終端顯示
 *
 * maybe merged with [destiny.astrology.AstroPatternDescriptor]
 */
abstract class AbstractPlanetPatternDescriptor<out T : IPlanetPattern>(
  private val pattern: IPlanetPattern, val key: String, private val parameters: List<Any>) : Descriptive {

  private val nameKey = pattern.javaClass.simpleName!!

  abstract val resource: String

  override fun getTitle(locale: Locale): String {
    return try {
      logger.trace("try to get nameKey = {} of locale = {} , resource = {}", nameKey, locale, resource)
      ResourceBundle.getBundle(resource, locale).getString(nameKey)
    } catch (e: Exception) {
      logger.trace("cannot get from nameKey = {}", nameKey)
      pattern.javaClass.simpleName
    }
  }

  override fun getDescription(locale: Locale): String {
    logger.trace("key = {} , parameters = {}", key, parameters)
    val pattern = ResourceBundle.getBundle(resource, locale).getString("$nameKey.$key")
    return MessageFormat.format(pattern, *getCommentParameters(locale, parameters))
  }

  /** 設定註解參數 , 檢查參數是否是 [ILocaleString] , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters  */
  private fun getCommentParameters(locale: Locale, commentParameters: List<Any>): Array<Any> {
    return commentParameters.map {
      when (it) {
        is ILocaleString -> it.toString(locale)
        is Double -> String.format("%.${1}f", it)
        else -> it
      }
    }.toTypedArray()
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }

}
