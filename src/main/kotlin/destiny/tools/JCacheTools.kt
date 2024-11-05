package destiny.tools

import org.apache.commons.lang3.RandomStringUtils
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.configuration.Factory
import javax.cache.configuration.MutableConfiguration
import javax.cache.expiry.ExpiryPolicy

object JCacheTools {

  private val logger = KotlinLogging.logger { }

  fun <K, V> createDefaultCache(
    cacheManager: CacheManager, cacheName: String?,
    keyType: Class<K>,
    valueType: Class<V>,
    expiryPolicyFactory: Factory<out ExpiryPolicy>
  ): Cache<K, V> {
    val configuration = MutableConfiguration<K, V>()
      .setTypes(keyType, valueType)
      .setExpiryPolicyFactory(expiryPolicyFactory)

    val name = cacheName ?: RandomStringUtils.randomAlphabetic(10)

    cacheManager.createCache(name, configuration)
    return cacheManager.getCache<K, V>(name).also {
      logger.warn("Returning dynamic generated cache : {} . keyType = {} , valueType = {}", it, keyType, valueType)
    }
  }
}
