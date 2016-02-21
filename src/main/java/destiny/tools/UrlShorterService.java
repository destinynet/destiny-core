/**
 * @author smallufo
 * Created on 2011/2/28 at 下午4:44:12
 */
package destiny.tools;

public interface UrlShorterService {

  /** 提供 UrlShorter 的單一進入點，處理 Exceptions */
  String getShortUrl(String longUrl);

  void resetUrlShorters();
}
