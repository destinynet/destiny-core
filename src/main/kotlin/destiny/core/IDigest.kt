/**
 * Created by smallufo on 2023-01-06.
 */
package destiny.core

import destiny.tools.ai.IChatCompletion
import java.io.Serializable
import java.util.*

/**
 * digest of Model [M] , output to type of [T]
 */
interface IDigest<M, T> : Serializable {

  fun digest(model: M, locale: Locale = Locale.getDefault()): T?

  /**
   * after got reply from [IChatCompletion], what to do with the reply ? Validation , transformation or trimming ?
   */
  fun postProcess(model: T) : T? {
    return model
  }
}
