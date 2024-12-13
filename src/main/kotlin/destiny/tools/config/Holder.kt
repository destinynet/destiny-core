/**
 * Created by smallufo on 2024-12-14.
 */
package destiny.tools.config

import destiny.tools.KotlinLogging
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


class Holder<T : Any>(initialConfig: T) {

  private val configLock = ReentrantReadWriteLock()
  private var currentConfig: T = initialConfig

  fun getConfig(): T = configLock.read { currentConfig }

  fun updateConfig(newConfig: T) = configLock.write {
    if (currentConfig != newConfig) {
      logger.info { "updating config..." }
      currentConfig = newConfig
    } else {
      logger.trace { "config unchanged, skip updating" }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger {}
  }
}
