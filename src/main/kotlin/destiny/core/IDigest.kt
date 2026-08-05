/**
 * Created by smallufo on 2023-01-06.
 */
package destiny.core

import destiny.tools.JSerializable
import java.util.*
import destiny.tools.defaultLocale

/**
 * digest of Model [M] , output to type of [T]
 */
interface IDigest<M, T> : JSerializable {

  fun digest(model: M, locale: Locale = defaultLocale): T?
}
