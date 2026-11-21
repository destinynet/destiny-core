/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.tools

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * 類別內部自用的小型記憶快取（純函式的結果、有上限、可選到期）。
 *
 * 這是一個**接縫**：目前以 Caffeine 實作，是 destiny-core 裡唯一直接碰 Caffeine 的地方。
 * 日後進 KMP 時改成 `expect`／`actual`，呼叫端不必改。
 *
 * @see KvCache
 */
object MemoCaches {

  /**
   * @param maxSize           最多幾筆，超過依 LRU 淘汰
   * @param expireAfterAccess 多久沒被讀取就丟掉；`null` 不設
   * @param expireAfterWrite  放進去多久之後丟掉；`null` 不設
   */
  fun <K : Any, V : Any> lru(
    maxSize: Long,
    expireAfterAccess: Duration? = null,
    expireAfterWrite: Duration? = null,
  ): KvCache<K, V> {
    val builder = Caffeine.newBuilder().maximumSize(maxSize)
    expireAfterAccess?.also { builder.expireAfterAccess(it.toJavaDuration()) }
    expireAfterWrite?.also { builder.expireAfterWrite(it.toJavaDuration()) }
    return CaffeineKvCache(builder.build())
  }

  private class CaffeineKvCache<K : Any, V : Any>(private val cache: Cache<K, V>) : KvCache<K, V> {
    override fun get(key: K): V? = cache.getIfPresent(key)
    override fun put(key: K, value: V) = cache.put(key, value)
    override fun remove(key: K) = cache.invalidate(key)
    override fun clear() = cache.invalidateAll()

    /** Caffeine 的 `get(key, mappingFunction)` 是原子的：同一個 key 並行時只算一次 */
    override fun getOrPut(key: K, compute: () -> V): V = cache.get(key) { compute() }
  }
}
