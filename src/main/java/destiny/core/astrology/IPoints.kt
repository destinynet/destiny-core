/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.core.astrology

import java.util.*
import kotlin.reflect.KClass


interface IPoints<T:Point> {

  val type : KClass<out Point>

  val values: Array<T>

  fun fromString(value: String, locale: Locale = Locale.ENGLISH): T?
}
