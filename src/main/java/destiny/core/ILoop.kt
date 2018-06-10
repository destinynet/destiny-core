/**
 * Created by smallufo on 2018-06-11.
 */
package destiny.core

interface ILoop<T> {

  fun next(n: Int): T

  fun prev(n: Int): T {
    return next(0 - n)
  }

  val next: T
    get() = next(1)

  val previous: T
    get() = prev(1)
}