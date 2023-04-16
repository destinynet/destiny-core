/**
 * Created by smallufo on 2021-12-22.
 */
package destiny.core.oracles.dizang

import destiny.core.Gender
import destiny.core.oracles.IOracleViewModel


data class DizangViewModel(override val matcher: Int,
                           val bgUrl: String,
                           override val question: String? = null,
                           override val gender: Gender? = null) : IOracleViewModel<Dizang , Int>
