/**
 * Created by smallufo on 2023-04-16.
 */
package destiny.core.oracles

import destiny.core.Gender


interface IOracleQuestion<T, M> {

  val matcher: M

  val question: String?

  val gender: Gender?

}
