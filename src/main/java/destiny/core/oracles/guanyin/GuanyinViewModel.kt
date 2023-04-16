/**
 * Created by smallufo on 2020-11-16.
 */
package destiny.core.oracles.guanyin

import destiny.core.Gender
import destiny.core.oracles.IOracleViewModel

data class GuanyinViewModel(override val matcher: Int,
                            val bgUrl: String,
                            override val question: String? = null,
                            override val gender: Gender? = null) : IOracleViewModel<Guanyin, Int>
