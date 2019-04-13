/**
 * Created by smallufo on 2017-04-12.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Stem
import java.io.Serializable
import java.util.*

/**
 * 參考文件
 * http://numerologys.net/index.php/紫微斗數:四化異同 , or 短網址 : http://bit.ly/2sC9B4q
 */
abstract class TransFourAbstractImpl : ITransFour, Serializable {

  protected abstract val table: Collection<Triple<Stem, ITransFour.Value, ZStar>>

  override fun getStarOf(stem: Stem, value: ITransFour.Value): ZStar {
    return table
      .filter { it.first == stem && it.second == value}
      .map { it.third }
      .first()
  }

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(TransFourAbstractImpl::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }
}
