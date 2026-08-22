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
}

data class GMapPoi(override val name: String, override val lat: Lat, override val lng: Lng, override val placeId: String, override val userRatingsTotal: Int?) : IPoi {
  override val place: String = name
}

interface INearByPoi : IPoi {
  val meters: Int
}

data class NearByPoi(val poi: IPoi, override val meters: Int) : INearByPoi, IPoi by poi

interface INearBy {

  suspend fun getNearBy(lat: Lat, lng: Lng, type: String, keyword: String?, radiusMeters: Int, locale: Locale = defaultLocale): List<IPoi>

  suspend fun getNearBys(lat: Lat, lng: Lng, types: List<String>, radiusMeters: Int, locale: Locale = defaultLocale): List<INearByPoi>
}

/**
 * [INearBy.getNearBy] 單一次查詢的完整識別 —— 快取以此為 key。
 *
 * ## 規則：凡是會改變結果的參數，都必須在這裡
 *
 * 這個 class 存在的唯一理由，是把「什麼會影響結果」寫死成型別，而不是留給每個 call site
 * 自己拼一個 key。少一個欄位就是靜默地拿到別次查詢的結果 —— 回傳的東西格式完全正確，
 * 只是內容不對，呼叫端不會收到任何錯誤。
 *
 * | 欄位 | 少了它會怎樣 |
 * |---|---|
 * | [provider] | 不同實作打的是不同後端，同座標同 [type] 的結果並不相同 |
 * | [type] | 不同 type 的結果互相頂替 |
 * | [keyword] | 有些實作會把它送進查詢條件 |
 * | [radiusMeters] | 半徑決定命中範圍 |
 * | [locale] | 回傳的地點名稱跟著語系走 |
 *
 * [radiusMeters] 存的是**實作套用自身覆寫規則之後的有效半徑**，不是呼叫端傳進來的那個 ——
 * 兩個不同的請求半徑若被同一個 [type] 覆寫成同一個值，它們本來就該共用同一筆。
 *
 * ## 快取掛在哪一層
 *
 * 掛在 [INearBy.getNearBy]（單一 type），不要掛在 [INearBy.getNearBys]（一整份 type 清單）。
 * 掛在上層的話 key 就得涵蓋整份清單，於是兩份不同的清單要嘛互相頂替、要嘛完全不共用；
 * 往下沉一層，重疊的那些 type 才是真的共用。
 */
data class NearByCacheKey(
  val provider: String,
  val lat: Lat,
  val lng: Lng,
  val type: String,
  val keyword: String?,
  val radiusMeters: Int,
  val locale: Locale,
) : JSerializable
