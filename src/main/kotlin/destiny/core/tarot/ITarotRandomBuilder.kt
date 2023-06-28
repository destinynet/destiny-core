/**
 * Created by smallufo on 2023-04-05.
 */
package destiny.core.tarot


interface ITarotRandomBuilder<T : ISpread> {

  fun random(): T
}
