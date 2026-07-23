/**
 * Created by smallufo on 2025-06-30.
 */
package destiny.core.astrology

import destiny.core.astrology.prediction.*
import destiny.tools.CircleTools.normalize
import destiny.tools.KotlinLogging
import jakarta.inject.Named

/**
 * [IPrimaryDirection] 的實作。
 *
 * 目前僅實作 **黃道-赤經法** ([PrimaryDirectionMethod.Zodiacal] + [PdFrame.RIGHT_ASCENSION])：
 * 在天球赤道上以赤經 (RA) 量測「把某相位點推進到另一點」所需的弧角，弧角再經 [ITimeKey] 換算為年數。
 *
 * 純運算（黃赤交角委派 [IObliquityCalculator]，黃道→赤經用 [Astronomical] 純數學），與 swisseph 無關，故置於 destiny-core。
 *
 * ### 方向 (維度D，[forward])
 * - **順推 (forward=true)**：promissor 的相位點被周日運動帶往 significator。`arc = RA(promissor±aspect) − RA(significator)`
 * - **逆推 (forward=false)**：significator 被帶往 promissor，即上式反向。`arc = RA(significator) − RA(promissor±aspect)`
 *
 * 對稱性：同一組 (sig, pro, aspect) 的順推弧與逆推弧互為 `360 − x`，因此壽命範圍內通常只有一個成立。
 *
 * ### 尚未實作
 * - [PdFrame.PLACIDUS] / [PdFrame.REGIOMONTANUS] / [PdFrame.CAMPANUS]
 * - [PrimaryDirectionMethod.Mundane] (世俗主限 / rapt parallels)
 */
@Named
class PrimaryDirectionImpl(
  private val obliquityCalculator: IObliquityCalculator
) : IPrimaryDirection {

  override fun getDirectionEvents(
    natalChart: IHoroscopeModel,
    significators: Set<AstroPoint>,
    promissors: Set<AstroPoint>,
    aspects: Set<Aspect>,
    method: PrimaryDirectionMethod,
    forward: Boolean,
    timeKey: ITimeKey,
  ): List<DirectionEvent> {

    // --- 方法分派：目前僅支援 黃道 + 赤經 ---
    when (method) {
      is PrimaryDirectionMethod.Zodiacal -> when (method.frame) {
        PdFrame.RIGHT_ASCENSION -> Unit // 繼續往下
        else                    -> TODO("PdFrame ${method.frame} 尚未實作")
      }

      is PrimaryDirectionMethod.Mundane  -> TODO("Mundane (世俗) 主限尚未實作")
    }

    val eps = obliquityCalculator.getObliquity(natalChart.gmtJulDay)

    val result = mutableListOf<DirectionEvent>()

    for (sig in significators) {
      val sigPos = natalChart.getPosition(sig) ?: continue
      val sigLat = if (method.withLatitude) sigPos.lat else 0.0
      val raSig = toRA(sigPos.lng, sigLat, eps)

      for (pro in promissors) {
        if (sig == pro) continue
        val proPos = natalChart.getPosition(pro) ?: continue

        for (aspect in aspects) {
          // 該相位對應的黃道相位點：合相/衝相只有一點，其餘有 ±degree 兩點
          for (offset in aspectOffsets(aspect.degree)) {
            val targetLng = (proPos.lng + offset).normalize()
            // 黃緯 (維度C) 僅適用於星體本體 (合相點)；非合相的相位點是黃道面上的幾何點 (緯度 0)
            val proLat = if (method.withLatitude && offset == 0.0) proPos.lat else 0.0
            val raTarget = toRA(targetLng, proLat, eps)

            val arc = if (forward) {
              (raTarget - raSig).normalize()  // 順推：promissor 相位點 → significator
            } else {
              (raSig - raTarget).normalize()  // 逆推：significator → promissor 相位點
            }

            val years = timeKey.getYears(arc)
            if (years <= 0.0 || years > MAX_YEARS) continue

            val eventGmt = natalChart.gmtJulDay.plus(years * Constants.TROPICAL_YEAR_DAYS)
            result.add(DirectionEvent(sig, pro, aspect, arc, years, eventGmt, forward))
          }
        }
      }
    }

    return result.sortedBy { it.years }
  }

  /** 黃道座標 → 赤經 (Right Ascension)，純數學 (Meeus 13.3)，與 swisseph swe_cotrans 等價 (誤差 < 1e-12°) */
  private fun toRA(zodiacLng: Double, zodiacLat: Double, eps: Double): Double {
    return Astronomical.eclipticToEquatorial(zodiacLng, zodiacLat, eps).rightAscension
  }

  companion object {
    private val logger = KotlinLogging.logger { }

    /** 壽命上限 (年)。用年數過濾而非 raw arc，才能通用於不同 [ITimeKey]。 */
    private const val MAX_YEARS = 120.0

    /** 某相位角對應的黃道偏移量集合 (去重合相/衝相的重複點) */
    private fun aspectOffsets(degree: Double): List<Double> = when (degree) {
      0.0, 180.0 -> listOf(degree)
      else       -> listOf(degree, -degree)
    }
  }
}
