/**
 * Created by smallufo on 2025-01-14.
 */
package destiny.tools.ai.model

import destiny.tools.ai.Provider
import destiny.tools.ai.Reply


class LlmException(val provider: Provider, val model: String, val e: Reply.Error) : Exception()
