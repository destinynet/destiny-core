package destiny.tools

import javax.cache.configuration.Configuration

interface CacheConfigType<K, V>

interface CacheConfigProvider<C : CacheConfigType<*, *>> {
  val cacheMap: Map<String, C>

  fun <K, V> transformConfig(config: C): Configuration<K, V>
}
