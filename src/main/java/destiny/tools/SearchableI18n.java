/**
 * @author smallufo
 * Created on 2010/10/19 at 下午12:22:23
 */
package destiny.tools;

import java.util.List;
import java.util.Locale;

public interface SearchableI18n<T> extends Searchable<T>
{
  /** 搜尋關鍵字，若找不到，則傳回 長度為零的 list 。 start 為 0-based */
  List<T> search(String keyword , int start , int count , Locale... locales);
  
  /** 搜尋關鍵字，會傳回幾筆資料 */
  int getSearchResultSize(String keyword , Locale... locales);
}
