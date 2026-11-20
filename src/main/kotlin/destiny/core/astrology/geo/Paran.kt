/**
 * Created by smallufo on 2026-11-21.
 */
package destiny.core.astrology.geo

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Aspect
import kotlinx.serialization.Serializable

/**
 * paran（paranatellonta，「一起到角」）的查詢條件。
 *
 * 出生那天地球自轉一圈的過程中，[anchors] 裡的某顆星到某個角（[angles]）的同一刻，
 * [partners] 裡的某顆星與它成世俗相位（[aspects]）。這個條件只跟緯度有關，所以結果是一條緯線。
 *
 * **相位的意思＝世俗相位（in mundo，Placidus 半弧比例）**：把每一象限依星的半日弧／半夜弧等分成 90°，
 * MC＝0、DSC＝90、IC＝180、ASC＝270。anchor 在角上時世俗位置剛好是這四個值之一，所以
 * 合＝partner 在同一個角、衝＝對面的角、刑＝另一軸的角 —— [Aspect.PARAN] 就是傳統的 paran。
 * 其他相位（六合、三合…）是世俗相位 paran，此時 partner 不在角上（[Paran.partnerAngle] 為 null）。
 *
 * 為什麼一定要有一顆在角上：兩顆星的世俗距離一天之中會繞完一整圈，不指定錨點的話，任何緯度、任何相位都會發生。
 *
 * @param basis 星體座標的基準：[AstroGeoBasis.IN_MUNDO] 用星體實際的赤緯（實際的升落），
 *              [AstroGeoBasis.ZODIACAL] 用黃道度數投影（黃緯視為 0）
 */
data class ParanQuery(
  val anchors: Set<AstroPoint>,
  val partners: Set<AstroPoint>,
  val aspects: Set<Aspect> = Aspect.PARAN,
  val angles: Set<GeoAngle> = GeoAngle.entries.toSet(),
  val basis: AstroGeoBasis = AstroGeoBasis.IN_MUNDO,
)

/**
 * 一條 paran 緯線：[anchor] 在 [anchorAngle] 的同一刻，[partner] 與它成世俗 [aspect]。
 *
 * @param partnerAngle 合、衝、刑時 partner 也在角上（哪一個角）；其他相位為 null
 * @param lat 緯度（度，北緯為正）
 */
@Serializable
data class Paran(
  val anchor: AstroPoint,
  val anchorAngle: GeoAngle,
  val partner: AstroPoint,
  val aspect: Aspect,
  val partnerAngle: GeoAngle?,
  val lat: Double,
)

interface IParanCalculator {

  /**
   * 合、衝、刑對調兩星的角色結果相同：同一對星（anchor、partner 兩組都含有時）只以先出現在 [ParanQuery.anchors] 的那顆當 anchor 算一次。
   * 其他相位不對稱（A 在角、B 成六合，與 B 在角、A 成六合是兩條不同的緯線），兩個方向都算。
   * 同一顆星不與自己配對；context 裡沒有的星略過。
   */
  fun parans(ctx: AstroGeoContext, query: ParanQuery): List<Paran>
}
