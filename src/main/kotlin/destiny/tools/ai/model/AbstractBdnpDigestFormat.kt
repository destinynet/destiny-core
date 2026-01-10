/**
 * Created by smallufo on 2026-01-10.
 */
package destiny.tools.ai.model

import destiny.core.IBirthDataNamePlace
import java.util.Locale


abstract class AbstractBdnpDigestFormat<CFG : Any , OUT : Any>(formatSpec: FormatSpec<OUT>) : AbstractDigestFormat<Pair<IBirthDataNamePlace, CFG>, OUT>(formatSpec) {

  abstract fun mainBodyPrompt(bdnp: IBirthDataNamePlace, config: CFG, locale: Locale): String
}
