/**
 * @author smallufo
 * Created on 2010/10/19 at 下午12:22:23
 */
package destiny.tools

import java.util.*

interface SearchableI18n<T> : Searchable<T> {
  /** 搜尋關鍵字，若找不到，則傳回 長度為零的 list 。 start 為 0-based  */
  fun search(keyword: String, start: Int, count: Int, vararg locales: Locale): List<T>

  /** 搜尋關鍵字，會傳回幾筆資料  */
  fun getSearchResultSize(keyword: String, vararg locales: Locale): Int
}
