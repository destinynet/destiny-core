/**
 * @author smallufo
 * Created on 2010/10/10 at 上午10:09:04
 */
package destiny.tools;

import java.util.List;

public interface Searchable<T>
{
  void reIndex();

  /** 搜尋關鍵字，若找不到，則傳回 長度為零的 list 。 start 為 0-based */
  List<T> search(String keyword , int start , int count);
  
  /** 搜尋關鍵字，會傳回幾筆資料 */
  int getSearchResultSize(String keyword);
}
