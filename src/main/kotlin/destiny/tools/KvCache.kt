/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.tools

/**
 * 平台中立的 key-value 快取。
 *
 * destiny-core 只需要「查得到就用、查不到就算完放進去」，所以介面只有這幾個動作；
 * 容量、到期、要不要寫到磁碟，全由實作決定。
 *
 * 實作的來源：
 *  - 由外部注入的（例如 [AbstractCachedFeature.gmtCache]）：由使用端決定要接哪一種快取，也可以不給（`null` ＝ 不快取）
 *  - 類別內部自用的小型記憶：[MemoCaches.lru]
 *
 * 刻意是**介面**而不是快取註解：註解式快取要靠 AOP 代理攔截方法呼叫，不是每個平台都有。
 */
interface KvCache<K, V> {

  /** 沒有這個 key（或已到期）時回 `null` */
  operator fun get(key: K): V?

  fun put(key: K, value: V)

  fun remove(key: K)

  fun clear()

  /**
   * 有就回傳，沒有就以 [compute] 算出、放進快取再回傳。
   *
   * 預設實作**不保證原子性**：並行時同一個 key 可能被算不只一次（結果相同，只是多算）。
   * 支援原子計算的實作會覆寫它。
   */
  fun getOrPut(key: K, compute: () -> V): V =
    get(key) ?: compute().also { put(key, it) }
}
