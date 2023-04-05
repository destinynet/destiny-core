/**
 * Created by smallufo on 2023-04-05.
 */
package destiny.core.tarot

import destiny.tools.random.RandomService


interface ITarotRandomBuilder<T : ISpread> {

  fun random(randomService: RandomService): T
}
