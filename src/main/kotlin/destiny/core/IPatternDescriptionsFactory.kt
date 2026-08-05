/**
 * Created by smallufo on 2022-04-26.
 */
package destiny.core

import destiny.tools.JSerializable
import java.util.*
import destiny.tools.defaultLocale


interface IPatternDescriptionsFactory<T, P : IPattern, D : IPatternParasDescription<P>> : JSerializable {

  fun getPatternDescriptions(model: T, locale: Locale = defaultLocale): List<D>

  fun getDescriptions(pattern: P, locale: Locale = defaultLocale): List<D>
}
