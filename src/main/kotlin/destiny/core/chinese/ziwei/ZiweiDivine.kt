/**
 * Created by smallufo on 2024-08-31.
 */
package destiny.core.chinese.ziwei

import destiny.core.IBirthDataNamePlace

data class ZiweiDivine(override val divineBdnp: IBirthDataNamePlace,
                       override val question: String) : IBdnpDivine
