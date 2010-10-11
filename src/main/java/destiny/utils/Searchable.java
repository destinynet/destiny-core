/**
 * @author smallufo
 * Created on 2010/10/10 at 上午10:09:04
 */
package destiny.utils;

import java.util.List;

public interface Searchable<T>
{
  public void reIndex();
  
  /** 搜尋關鍵字，若找不到，則傳回 長度為零的 list */
  public List<T> search(String keyword);
}
