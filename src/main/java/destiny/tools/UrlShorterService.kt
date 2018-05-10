/**
 * @author smallufo
 * Created on 2011/2/28 at 下午4:44:12
 */
package destiny.tools

interface UrlShorterService {

  /** 提供 UrlShorter 的單一進入點，處理 Exceptions  */
  fun getShortUrl(longUrl: String): String

  fun resetUrlShorters()
}
