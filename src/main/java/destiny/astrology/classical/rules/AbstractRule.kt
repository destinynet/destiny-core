/**
 * @author smallufo
 * Created on 2007/12/29 at 上午 4:21:29
 */
package destiny.astrology.classical.rules

import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.tools.ILocaleString
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple2
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.text.MessageFormat
import java.util.*

abstract class AbstractRule protected constructor(private val resource: String) : RuleIF, Serializable, ILocaleString {

  fun Pair<String, Array<Any>>?.toOld() : Optional<Tuple2<String, Array<Any>>> {
    return this?.let { p -> Tuple.tuple(p.first , p.second) }.let { Optional.ofNullable(it) }
  }

  val logger = LoggerFactory.getLogger(javaClass)

  private var locale = Locale.getDefault()


  /** 名稱key  */
  private val nameKey: String = javaClass.simpleName

  override fun isApplicable(planet: Planet, h: Horoscope): Boolean {
    logger.debug("'{}' : isApplicable({})", javaClass.simpleName, planet)
    return getResult(planet, h).isPresent
  }

  /**
   * Tuple<String , Object[]> 參數：
   * String 為 ResourceBundle 取得的 key , 前面要 prepend '[rule_name].'
   * Object[] 為 MessageFormat.format(pattern , Object[]) 後方的參數
  </String> */
  protected abstract fun getResult(planet: Planet, h: Horoscope): Optional<Tuple2<String, Array<Any>>>

  open fun getResult2(planet: Planet, h: Horoscope): Pair<String, Array<Any>>? {
    return getResult(planet, h).map { t -> Pair(t.v1, t.v2) }.orElse(null)
  }

  /** 名稱  */
  override fun getName(): String {
    return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey)
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
        is Double -> it.toString().substring(0,5) //避免 double 輸出太長
        else -> it
      }
    }.toTypedArray()
  }


  /** 取得某 Locale 之下的註解  */
  override fun getComment(planet: Planet, h: Horoscope, locale: Locale): Optional<String> {
    return getResult(planet, h).map { tuple ->
      val commentKey = tuple.v1()
      val commentParameters = tuple.v2()
      val pattern = ResourceBundle.getBundle(resource, locale).getString(nameKey + "." + commentKey)
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
