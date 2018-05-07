/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology.eclipse

import java.io.Serializable

/**
 * 在一個地點觀測到的日、月食資料，開始、結束、各個接觸點 (contact) 各為何時
 */
data class EclipseSpan(
  /** 經度  */
  val lng: Double,
  /** 緯度  */
  val lat: Double,
  /** 高度 (米)  */
  val alt: Double,
  /** 哪一種食，其 起訖 資訊為何  */
  val eclipse: IEclipse) : Serializable