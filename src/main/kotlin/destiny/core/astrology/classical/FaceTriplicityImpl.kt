package destiny.core.astrology.classical

import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacSign

/**
 * Face , +1
 * 三分法十度區（Triplicity Decans）：每個星座的三個十度區，依序由「本星座、順數第五個、順數第九個星座」
 * （同元素的三個星座）的廟主掌管。
 *
 * 例：牡羊 0–10° 火星（牡羊）、10–20° 太陽（獅子）、20–30° 木星（射手）；
 * 　　金牛 0–10° 金星（金牛）、10–20° 水星（處女）、20–30° 土星（摩羯）。
 *
 * 廟主採傳統七星（[RulerPtolemyImpl]），結果即印度占星的 Drekkana（Parashara 版）。
 * 現代西洋占星的寫法若改用三王星為天蠍／雙魚／水瓶之主，天蠍、雙魚、水瓶所在的十度區會不同；此實作不涵蓋。
 *
 * 與 [FaceChaldeanImpl]（迦勒底星序，自牡羊 0° 起 火→日→金→水→月→土→木 循環）相比，
 * 三十六區中只有 9 區的主星相同，兩者不可混用於同一套尊貴計分。
 */
object FaceTriplicityImpl : IFace {

  override fun getPoint(degree: ZodiacDegree): Planet {
    val index = (degree.value / 10).toInt().coerceIn(0, 35)
    return faceStarList[index]
  }

  /** 36 個十度區的主星，自牡羊 0° 起 */
  private val faceStarList: List<Planet> = (0 until 12).flatMap { signIndex ->
    val sign = ZodiacSign[signIndex]
    (0 until 3).map { decan ->
      with(RulerPtolemyImpl) { sign.next(decan * 4).getRulerPoint() }!!
    }
  }
}
