/**
 * Created by smallufo on 2019-03-20.
 */
package destiny.iching

import java.util.*

interface IHexagramProvider<T : IHexagram> {

  fun getHexagram(hex: IHexagram, locale: Locale=Locale.getDefault()): T
}