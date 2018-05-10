/**
 * @author smallufo
 * Created on 2010/10/10 at 上午10:09:04
 */
package destiny.tools

interface Searchable<T> {
  fun reIndex()

  /** 搜尋關鍵字，若找不到，則傳回 長度為零的 list 。 start 為 0-based  */
  fun search(keyword: String, start: Int, count: Int): List<T>

  /** 搜尋關鍵字，會傳回幾筆資料  */
  fun getSearchResultSize(keyword: String): Int
}
