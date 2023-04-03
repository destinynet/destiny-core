/**
 * Created by smallufo on 2023-04-03.
 */
package destiny.core.tarot

import destiny.core.IDigest
import kotlin.reflect.KClass


interface ISpreadDigest<T : ISpread> : IDigest<T , String> {

  val spreadClass : KClass<out ISpread>
}
