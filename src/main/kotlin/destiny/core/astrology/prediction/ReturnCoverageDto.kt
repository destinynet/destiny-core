/**
 * Created by smallufo on 2026-02-16.
 *
 * Lightweight Return chart summary with range coverage info,
 * for embedding in [destiny.core.astrology.ITimeLineEventsModel].
 *
 * Much smaller than [IReturnDto] which embeds full [destiny.core.astrology.IHoroscopeDto]
 * (harmonics, midpoints, graphPatterns, classicalPatterns, etc.)
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.*
import destiny.core.calendar.GmtJulDay
import destiny.tools.serializers.IZodiacDegreeSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


// ── Shared extraction utilities ─────────────────────────────────────────────

/**
 * Filter aspects by score threshold and return sorted by score descending.
 */
fun Synastry.keyAspects(scoreThreshold: Double = 0.95): List<SynastryAspect> =
  aspects
    .filter { (it.score?.value ?: 0.0) >= scoreThreshold }
    .sortedByDescending { it.score }

/**
 * 以 **orb** 為界過濾，orb 升冪。
 *
 * ⚠️ 與 [keyAspects]（以 score 為界）**刻意分開，不是重複**：
 * 計數工具的查詢參數是 `maxOrb`，母體若以 score 篩就是「用 score 篩、用 orb 問」的單位錯配 ——
 * 模型下 `maxOrb=3` 會拿到一個被靜默截斷的分母，而它看不見那個截斷。
 */
fun Synastry.aspectsWithin(maxOrb: Double): List<SynastryAspect> =
  aspects
    .filter { it.orb <= maxOrb }
    .sortedBy { it.orb }

/**
 * Convert [Synastry.houseOverlayMap] to a simplified Map of natal-house → list of return [Planet].
 * Filters out non-Planet points and empty entries.
 */
fun Synastry.simplifiedHouseOverlay(): Map<Int, List<Planet>> =
  houseOverlayMap
    .mapValues { (_, overlays) -> overlays.map { it.outerPoint }.filterIsInstance<Planet>() }
    .filter { (_, planets) -> planets.isNotEmpty() }


// ── ReturnCoverageDto ───────────────────────────────────────────────────────

@Serializable
data class ReturnCoverageDto(
  val returnType: ReturnType,
  @Contextual
  val validFrom: GmtJulDay,
  @Contextual
  val validTo: GmtJulDay,
  /** Coverage percentage within the search range (0–100) */
  val coveragePercent: Int,

  /**
   * 返照盤自身的上升／天頂 —— **nullable，且帶完整精度**。
   *
   * 2026-08-24 之前是 `ascSign: ZodiacSign + ascDegree: Int` 四個非空欄位：
   * (a) `Int` 度數是假精度（判讀端拿 0.1° 級的 orb 對不上整數度數）；
   * (b) 非空型別在無出生時刻（grain 閘門擋掉軸點）時被迫 fallback 成
   *     「牡羊 0°」—— 憑空捏造，`GrainSanitizer` 的 key 黑名單就是為此而生。
   *
   * 改成 `IZodiacDegree?` 之後：無軸點就是 null，**型別層即擋掉捏造**，
   * 序列化清洗不再需要；有軸點時 [IZodiacDegreeSerializer] 給 {sign, degree}
   * 兩欄、度數兩位小數。
   */
  @Serializable(with = IZodiacDegreeSerializer::class)
  val asc: IZodiacDegree? = null,
  @Serializable(with = IZodiacDegreeSerializer::class)
  val mc: IZodiacDegree? = null,

  /** Key planet positions (all 10 planets) */
  val planets: Map<Planet, StarSummary>,

  /** Top aspects between return chart and natal chart (pre-filtered by score) */
  val keyAspectsToNatal: List<SynastryAspect>,

  /** Which return planets fall into which natal house */
  val houseOverlay: Map<Int, List<Planet>>
) {
  companion object {
    /**
     * Convert from [IReturnDto] to [ReturnCoverageDto].
     *
     * @param dto             Original return DTO (heavy, with full IHoroscopeDto)
     * @param fromGmt         Search range start
     * @param toGmt           Search range end
     * @param scoreThreshold  Only include aspects with score >= threshold (default 0.90)
     */
    fun from(
      dto: IReturnDto,
      fromGmt: GmtJulDay,
      toGmt: GmtJulDay,
      scoreThreshold: Double = 0.90
    ): ReturnCoverageDto {
      val chart = dto.returnChart
      val asc = chart.stars[Axis.RISING]
      val mc = chart.stars[Axis.MERIDIAN]

      // Coverage percentage
      val overlapStart = maxOf(dto.validFrom, fromGmt)
      val overlapEnd = minOf(dto.validTo, toGmt)
      val rangeDays = toGmt.value - fromGmt.value
      val coveragePercent = if (rangeDays > 0) ((overlapEnd.value - overlapStart.value) / rangeDays * 100).toInt() else 0

      return ReturnCoverageDto(
        returnType = dto.returnType,
        validFrom = dto.validFrom,
        validTo = dto.validTo,
        coveragePercent = coveragePercent,
        // 無軸點（grain 閘門）就是 null —— 不 fallback。舊版捏造「牡羊 0°」的教訓見欄位 KDoc
        asc = asc?.signDegree,
        mc = mc?.signDegree,
        planets = Planet.values.map { planet ->
          planet to chart.stars[planet]
        }.filter { (_, v) -> v != null }.associate { (k, v) ->
          k to StarSummary.from(v!!)
        },
        keyAspectsToNatal = dto.synastry.keyAspects(scoreThreshold),
        houseOverlay = dto.synastry.simplifiedHouseOverlay()
      )
    }
  }
}
