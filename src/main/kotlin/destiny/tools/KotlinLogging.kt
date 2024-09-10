package destiny.tools

import mu.KLogger
import mu.KotlinLogging as MuKotlinLogging

object KotlinLogging {
  fun logger(func: () -> Unit): KLogger {
    return MuKotlinLogging.logger(func)
  }

  fun logger(name: String): KLogger {
    return MuKotlinLogging.logger(name)
  }
}
typealias DestinyKLogger = KLogger

