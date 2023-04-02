/**
 * Created by smallufo on 2023-04-01.
 */
package destiny.core.tarot

import destiny.core.Descriptive
import kotlinx.serialization.Serializable


@Serializable
sealed interface ISpread : Descriptive, java.io.Serializable {

  val key: String
    get() = javaClass.simpleName

}
