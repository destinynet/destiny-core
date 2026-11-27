/**
 * Created by smallufo on 2024-04-21.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng
import destiny.core.calendar.IPlace
import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import destiny.tools.JSerializable
import java.util.*
import destiny.tools.defaultLocale

interface IPoi : ILatLng, IPlace, JSerializable {
  val name: String
  val placeId: String
  val userRatingsTotal: Int?

  /** 供應商標的主要類型（例：`market`、`rest_stop`、`observation_deck`）；沒有就 null */
  val primaryType: String?

  /** 供應商標的全部類型；查詢時用的 type 會在其中 */
  val types: List<String>
}

data class GMapPoi(
  override val name: String,
  override val lat: Lat,
  override val lng: Lng,
  override val placeId: String,
  override val userRatingsTotal: Int?,
  override val primaryType: String? = null,
  override val types: List<String> = emptyList(),
) : IPoi {
  override val place: String = name
}

interface INearByPoi : IPoi {
  val meters: Int
}

data class NearByPoi(val poi: IPoi, override val meters: Int) : INearByPoi, IPoi by poi

/** 結果的排序（也決定超過 [NearByQuery.maxResults] 時留下哪些） */
enum class NearByRank {
  /** 熱門程度 */
  POPULARITY,
  /** 離圓心由近到遠 */
  DISTANCE
}

/**
 * 一次搜尋。
 *
 * 圓心（[lat]、[lng]）不必是使用者所在：要查某個方向那一側有什麼，可以把圓心沿該方向移出去，
 * 圓內的結果才會集中在那一側，而不是被四周最熱門的那幾個占滿。
 *
 * @param types 一次請求涵蓋的類型（任一符合即可）
 * @param keyword 只有支援關鍵字的實作會送出；不支援的實作忽略它
 */
data class NearByQuery(
  val lat: Lat,
  val lng: Lng,
  val radiusMeters: Int,
  val types: List<String>,
  val rank: NearByRank = NearByRank.POPULARITY,
  val maxResults: Int = 20,
  val keyword: String? = null,
) : JSerializable

interface INearBy {

  /**
   * 送出**一次**請求，回傳供應商給的原始結果（不套任何依 type 的過濾規則）。
   * 實作負責快取；[NearByQuery] 的每個欄位都是快取 key 的一部分。
   */
  suspend fun search(query: NearByQuery, locale: Locale = defaultLocale): List<IPoi>

  suspend fun getNearBy(lat: Lat, lng: Lng, type: String, keyword: String?, radiusMeters: Int, locale: Locale = defaultLocale): List<IPoi>

  suspend fun getNearBys(lat: Lat, lng: Lng, types: List<String>, radiusMeters: Int, locale: Locale = defaultLocale): List<INearByPoi>
}

/**
 * [INearBy.search] 單一次查詢的完整識別 —— 快取以此為 key。
 *
 * ## 規則：凡是會改變結果的參數，都必須在這裡
 *
 * 這個 class 存在的唯一理由，是把「什麼會影響結果」寫死成型別，而不是留給每個 call site
 * 自己拼一個 key。少一個欄位就是靜默地拿到別次查詢的結果 —— 回傳的東西格式完全正確，
 * 只是內容不對，呼叫端不會收到任何錯誤。
 *
 * | 欄位 | 少了它會怎樣 |
 * |---|---|
 * | [provider] | 不同實作打的是不同後端，同一個查詢的結果並不相同 |
 * | [query] | 圓心、半徑、types、排序、筆數、keyword 任何一項不同，結果都不同 |
 * | [locale] | 回傳的地點名稱跟著語系走 |
 *
 * 依 type 覆寫半徑的實作（`getNearBy`），[query] 存的是**覆寫之後的有效半徑** ——
 * 兩個不同的請求半徑若被覆寫成同一個值，它們本來就該共用同一筆。
 *
 * ## 快取掛在哪一層
 *
 * 掛在 [INearBy.search]（一次請求），不要掛在 [INearBy.getNearBys]（一整份 type 清單、多次請求）。
 * 掛在上層的話 key 就得涵蓋整份清單，於是兩份不同的清單要嘛互相頂替、要嘛完全不共用；
 * 掛在一次請求這層，重疊的那些 type 才是真的共用。
 */
data class NearByCacheKey(
  val provider: String,
  val query: NearByQuery,
  val locale: Locale,
) : JSerializable
