package destiny.tools.ai

import destiny.tools.KotlinLogging
import destiny.tools.ai.model.Domain
import java.util.*

interface IPostProcessorsMap {

  fun getPostProcessors(domain: Domain): List<IPostProcessor>

  fun process(domain: Domain, content: String, locale: Locale?): String {
    return getPostProcessors(domain).fold(content) { acc, postProcessor ->
      val (processed, modified) = postProcessor.process(acc, locale)
      if (modified)
        logger.debug { "modified from $acc to $processed" }
      processed
    }
  }

  companion object {
    private val logger = KotlinLogging.logger {}
  }
}
