/**
 * Created by smallufo on 2024-11-17.
 */
package destiny.tools.location

import destiny.core.calendar.ILatLng


interface ICountryPoi {
  val name: String
  val regex: Regex?
  val code: String
  val pois: List<Country.Poi>
  val latLng: ILatLng?
  /**
   * IANA 時區。資料檔只寫在最高一層「轄下全部同一個時區」的節點，子節點繼承（解析時就填好，這裡是生效值）；
   * 轄下跨時區又沒有往下寫的節點為 null
   */
  val tzid: String?
  /** 只給自由文字比對用的細地名（鄉鎮、區縣、市町村）：不出現在表單、不算進 [Country.levels] */
  val aliases: List<Country.Alias>
}

data class Country(
  override val name: String,
  override val regex: Regex? = null,
  override val latLng: ILatLng? = null,
  override val code: String,
  override val pois: List<Poi> = emptyList(),
  override val tzid: String? = null,
  override val aliases: List<Alias> = emptyList(),
) : ICountryPoi {

  /**
   * 表單有幾層下拉：葉節點的深度，國家本身算第 1 層（香港 1、台灣 2、美國 3）。
   * 同一國的葉節點必須一樣深 —— 表單的層數是整個國家固定的
   */
  val levels: Int = leafDepths(pois, 1).distinct().let { depths ->
    require(depths.size == 1) { "'$name' 的地點深度不一致：$depths" }
    depths.single().also { require(it in 1..3) { "'$name' 的層數 $it 不在 1..3" } }
  }

  private fun leafDepths(pois: List<Poi>, depth: Int): List<Int> =
    if (pois.isEmpty()) listOf(depth) else pois.flatMap { leafDepths(it.pois, depth + 1) }

  data class Poi(
    override val name: String,
    override val regex: Regex? = null,
    override val latLng: ILatLng? = null,
    override val code: String,
    override val pois: List<Poi> = emptyList(),
    override val tzid: String? = null,
    override val aliases: List<Alias> = emptyList(),
  ) : ICountryPoi {
    init {
      if (pois.isEmpty()) {
        requireNotNull(latLng) {
          "Poi '$name' with no children must have a valid latLng."
        }
      }
    }
  }

  /**
   * 細地名：掛在所屬的 [Poi]（或一層國家本身、或另一個 [Alias]）底下，自己有座標，code 由祖先決定。
   * 例：新北（`TW-NWT`）底下的板橋、新莊；橫濱市底下的泉區。
   *
   * @param tzid 生效值（繼承自祖先）
   */
  data class Alias(
    val name: String,
    val regex: Regex? = null,
    val latLng: ILatLng,
    val tzid: String? = null,
    val aliases: List<Alias> = emptyList(),
  )
}
