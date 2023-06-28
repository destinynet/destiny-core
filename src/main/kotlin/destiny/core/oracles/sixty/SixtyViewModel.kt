/**
 * Created by smallufo on 2020-11-12.
 */
package destiny.core.oracles.sixty

import destiny.core.Gender
import destiny.core.oracles.IOracleViewModel


data class SixtyViewModel(override val matcher: SixtyMatcher,
                          val bgUrl: String,
                          override val question: String? = null,
                          override val gender: Gender? = null) : IOracleViewModel<Sixty, SixtyMatcher>

