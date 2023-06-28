/**
 * @author smallufo
 * Created on 2009/3/1 at 上午 4:41:12
 */
package destiny.tools

/** 取得短網址  */
interface UrlShorter {

  /**
   * 傳入 longUrl , 如果發生網路問題，丟出 IOException , 如果無法 parse 或是其他問題，傳回 null
   */
  suspend fun getShortUrl(longUrl: String): String?
}

