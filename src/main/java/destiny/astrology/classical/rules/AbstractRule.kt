/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:21:29
 */
package destiny.astrology.classical.rules

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.tools.ILocaleString
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.text.MessageFormat
import java.util.*


abstract class AbstractRule protected constructor(
  private val resource: String,
  override val type: RuleType) : IRule,

  Serializable, ILocaleString {

  val logger = LoggerFactory.getLogger(javaClass)!!

  private var locale = Locale.getDefault()


  /** 名稱key  */
  private val nameKey: String = javaClass.simpleName

  override fun isApplicable(planet: Planet, h: IHoroscopeModel): Boolean {
    logger.debug("'{}' : isApplicable({})", javaClass.simpleName, planet)
    return (getResult(planet, h) != null)
  }

  abstract fun getResult(planet: Planet, h: IHoroscopeModel): Pair<String, Array<Any>>?


  /** 名稱  */
  override val name: String by lazy {
    ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
  }

  /** 名稱  */
  override fun getName(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  /** 設定註解參數 , 檢查參數是否是 [ILocaleString] , 如果是的話 , 就轉為適當的 locale
   * ex : {0} 位於第 {1} 宮 , 就要處理 {0} {1} , 填入 commentParameters  */
  private fun getCommentParameters(locale: Locale, commentParameters: Array<Any>): Array<Any> {

    return commentParameters.map {
      when (it) {
        is ILocaleString -> it.toString(locale)
        is Double -> it.toString().substring(0, 5) //避免 double 輸出太長
        else -> it
      }
    }.toTypedArray()
  }


  /** 取得某 Locale 之下的註解  */
  override fun getComment(planet: Planet, h: IHoroscopeModel, locale: Locale): String? {
    return getResult(planet, h)?.let { pair ->
      val commentKey = pair.first
      val commentParameters = pair.second
      val pattern = ResourceBundle.getBundle(resource, locale).getString("$nameKey.$commentKey")
      MessageFormat.format(pattern, *getCommentParameters(locale, commentParameters))
    }
  }

  override fun toString(): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }

  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(resource, locale).getString(nameKey)
  }


  fun setLocale(locale: Locale) {
    this.locale = locale
  }


}
