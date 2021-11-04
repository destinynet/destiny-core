/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.core.astrology

import kotlin.reflect.KClass


interface IPoint<T:Point> {

  val type : KClass<out Point>

  val values: Array<T>

  fun fromString(value: String): T?
}
