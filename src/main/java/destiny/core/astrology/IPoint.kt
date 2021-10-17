/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.core.astrology


interface IPoint<T> {

  val values: Array<T>

  fun fromString(value: String): T?
}
