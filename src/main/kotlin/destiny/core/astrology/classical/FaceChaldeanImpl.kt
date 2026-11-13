package destiny.core.astrology.classical

import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree

/**
 * Face , +1
 * 迦勒底十度區（Chaldean Decans / Faces）：自牡羊 0° 起，每 10° 一區，依迦勒底星序
 * 土→木→火→日→金→水→月 循環分配（牡羊第一區由火星起），36 區恰好轉完五圈多一顆，
 * 所以雙魚第三區（火）與牡羊第一區（火）首尾相接。
 *
 * 這是 Lilly 等古典占星沿用的 Face 表；印度占星與現代西洋則另用三分法，見 [FaceTriplicityImpl]。
 *
 * 註：Ptolemy 在 Tetrabiblos 所說的 Face（行星與日月的距離，等於其廟宮與日月廟宮的距離）
 * 是另一個概念，並不是這張十度區表。
 */
object FaceChaldeanImpl : IFace {

  /** 迦勒底星序（由慢到快） */
  private val chaldeanOrder = listOf(SATURN, JUPITER, MARS, SUN, VENUS, MERCURY, MOON)

  /** 36 個十度區的主星，自牡羊 0° 起；牡羊第一區為火星（星序索引 2） */
  private val faceStarList: List<Planet> = List(36) { i -> chaldeanOrder[(i + 2) % chaldeanOrder.size] }

  override fun getPoint(degree: ZodiacDegree): Planet {
    return faceStarList[(degree.value / 10).toInt().coerceIn(0, 35)]
  }
}
