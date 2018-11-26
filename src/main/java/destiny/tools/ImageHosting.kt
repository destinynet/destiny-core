/**
 * @author smallufo
 * Created on 2013/5/4 at 下午4:49:25
 */
package destiny.tools

interface ImageHosting {

  class HostingException : Exception {

    constructor(e: Exception) : super(e)

    constructor(reason: String) : super(reason)
  }

  /**
   * @param data   byte array
   * @param format , 圖檔格式 , "png" / "gif" / "jpg" ...
   */
  @Throws(HostingException::class)
  fun upload(data:ByteArray , format:String) : String


  /**
   * 把 link 包以 IMG tag
   */
  @Throws(HostingException::class)
  fun uploadAndWrapImgCode(data: ByteArray, format: String): String {
    val link = upload(data , format)
    return "[IMG]$link[/IMG]"
  }
}
