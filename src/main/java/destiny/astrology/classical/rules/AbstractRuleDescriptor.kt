package destiny.astrology.classical.rules

import destiny.core.Descriptive
import destiny.tools.ILocaleString
import java.text.MessageFormat
import java.util.*

/**
 * 新版 [Rule]
 */
abstract class AbstractRuleDescriptor<out T : IPlanetPattern>(val rule: T) : Descriptive {

  val nameKey = rule.javaClass.simpleName!!

  val resource = with(StringBuilder()) {
    append("destiny.astrology.classical.Classical")
//      append(rule.javaClass.`package`.name)
//      append('.')
//      append(rule.javaClass.simpleName)
    }.toString()

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(resource, locale).getString(nameKey)
    } catch (e: Exception) {
      rule.javaClass.simpleName
    }
  }

  override fun getDescription(locale: Locale): String {
    return getCommentParameters(locale).let { pair ->
      val commentKey = pair.first
      val commentParameters = pair.second
      val pattern = ResourceBundle.getBundle(resource, locale).getString("$nameKey.$commentKey")
      MessageFormat.format(pattern, *getCommentParameters(locale, commentParameters))
    }
  }

  /** 設定註解參數 , 檢查參數是否是 [ILocaleString] , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters  */
  private fun getCommentParameters(locale: Locale, commentParameters: List<Any>): Array<Any> {
    return commentParameters.map {
      when (it) {
        is ILocaleString -> it.toString(locale)
        is Double -> String.format("%.${1}f" , it)
        else -> it
      }
    }.toTypedArray()
  }

  abstract fun getCommentParameters(locale: Locale): Pair<String , List<Any>>
}