/**
 * @author smallufo
 * Created on 2013/5/4 at 下午4:49:25
 */
package destiny.tools

import kotlinx.coroutines.runBlocking

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
  suspend fun upload(data:ByteArray , format:String) : String

  @Throws(HostingException::class)
  fun blockingUpload(data:ByteArray , format:String) : String {
    return runBlocking {
      upload(data, format)
    }
  }


  /**
   * 把 link 包以 IMG tag
   */
  @Throws(HostingException::class)
  fun uploadAndWrapImgCode(data: ByteArray, format: String): String {
    val link = blockingUpload(data , format)
    return "[IMG]$link[/IMG]"
  }
}
