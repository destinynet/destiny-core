/**
 * Created by smallufo on 2020-11-24.
 */
package destiny.core.oracles.storm

import destiny.core.Gender
import destiny.core.oracles.IOracleViewModel


data class StormViewModel(override val matcher: Int,
                          /** 主籤詩 的背景 */
                          val bgUrl: String,
                          /** 東坡解、碧仙註 的背景 */
                          val bgUrl2: String,
                          override val question: String? = null,
                          override val gender: Gender? = null) : IOracleViewModel<Storm, Int>
