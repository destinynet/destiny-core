/**
 * Created by smallufo on 2024-12-14.
 */
package destiny.tools.config

import jakarta.inject.Named


@Named
class Provider<T:Any>(private val holder: Holder<T>) {
  fun getConfig(): T = holder.getConfig()
}
