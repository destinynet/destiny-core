/**
 * @author smallufo
 * Created on 2006/5/5 at 上午 04:20:08
 */
package destiny.core

import java.util.*

interface Descriptive {

  /** 取得名稱  */
  fun getTitle(locale: Locale): String

  /** 詳細描述  */
  fun getDescription(locale: Locale): String
}
