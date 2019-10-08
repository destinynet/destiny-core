/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.astrology

import destiny.core.Descriptive
import destiny.core.IPattern
import destiny.core.IPatternDescriptor
import destiny.tools.ILocaleString
import mu.KotlinLogging
import java.text.MessageFormat
import java.util.*

/**
 * maybe merged with [destiny.astrology.classical.rules.AbstractPlanetPatternDescriptor]
 */
class AstroPatternDescriptor(private val pattern : IPattern,
                             private val commentKey: String,
                             private val parameters: List<Any>) : Descriptive {

  private val nameKey = pattern::class.java.simpleName

  val resource = "destiny.astrology.AstroPatterns"


  override fun getTitle(locale: Locale): String {
    return try {
      logger.trace("try to get nameKey = {} of locale = {} , resource = {}", nameKey, locale, resource)
      ResourceBundle.getBundle(resource, locale).getString(nameKey)
    } catch (e: Exception) {
      logger.warn("cannot get from nameKey = {} , from resource = {}", nameKey , resource)
      pattern::class.java.simpleName
    }
  }

  override fun getDescription(locale: Locale): String {
    logger.trace("commentKey = {} , parameters = {}", commentKey, parameters)
    val pattern = ResourceBundle.getBundle(resource, locale).getString("$nameKey.$commentKey")
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


object astroPatternTranslator : IPatternDescriptor<AstroPattern> {

  override fun getDescriptor(pattern: AstroPattern): Descriptive {
    return when (pattern) {
      is AstroPattern.GrandTrine -> AstroPatternDescriptor(pattern , "comment" , listOf("test"))
      is AstroPattern.Kite -> TODO()
      is AstroPattern.TSquared -> TODO()
      is AstroPattern.Yod -> TODO()
      is AstroPattern.Boomerang -> TODO()
      is AstroPattern.GoldenYod -> TODO()
      is AstroPattern.GrandCross -> TODO()
      is AstroPattern.DoubleT -> TODO()
      is AstroPattern.Hexagon -> TODO()
      is AstroPattern.Wedge -> TODO()
      is AstroPattern.MysticRectangle -> TODO()
      is AstroPattern.Pentagram -> TODO()
      is AstroPattern.StelliumSign -> TODO()
      is AstroPattern.StelliumHouse -> TODO()
      is AstroPattern.Confrontation -> TODO()
    }
  }

}