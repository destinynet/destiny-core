/**
 * Created by smallufo on 2026-08-13.
 *
 * 占星圓盤的**呈現層 DTO** —— 專供 client 端 renderer 使用，與 [NatalV2] 刻意分離。
 *
 * 存在的理由與 [destiny.core.chinese.ziwei.ZiweiPlateDto] 相同：
 *
 * 1. **去識別化的型別保證**：[NatalV2] 帶有 `name` / `time` / `utc` / `location` / `place` / `age`。
 *    本 DTO **結構上就沒有宣告這些欄位**，因此「盤面可公開、當事人身分不外流」由編譯器把關，
 *    不倚賴輸出白名單的維護紀律。需要顯示身分時，由呼叫端另外組一個並列的身分物件。
 * 2. **座標攤平**：圓盤只需要「絕對黃經」。[NatalV2] 的位置藏在 `IZodiacDegree` 裡，
 *    且星體以 [AstroPoint] 當 map key（序列化為 nameKey），client 拿不到字形與分類。
 *    mapper 明確把 `zDeg`、`glyph`、`kind` 帶出來，renderer 才不必自維護對照表。
 *
 * ⚠️ 命名規則：本檔案內**不得出現名為 `name` / `time` / `place` / `location` 的欄位**，
 * 人可讀字串一律叫 `label`。這讓「DTO 不含身分欄位」可以用欄位名精確比對來自動驗證（見對應測試）。
 *
 * 另外刻意**不含** `mundanePositions`（alt/az/時角可反推出生時間與地點）。
 */
package destiny.core.astrology

import destiny.core.ChartDensity
import destiny.tools.Lang
import kotlinx.serialization.Serializable
import destiny.core.toString as pointToString

/** 星名的查表語系 —— 固定繁中，不對外開放為參數 */
private val ZH = Lang.ZH_TW

@Serializable
data class HoroscopePointDto(
  /** 點位識別鍵（[AstroPoint.nameKey]，如 `Planet.SUN`） */
  val point: String,
  /** 顯示名（繁中） */
  val label: String,
  /** unicode 字形（如 ☉），無則 null —— renderer 退回顯示 [label] */
  val glyph: String?,
  /** **絕對黃經 0–360**，圓盤唯一需要的座標 */
  val zDeg: Double,
  /** 星座（[ZodiacSign] 名稱） */
  val sign: String,
  /** 星座內度數 0–30 */
  val signDegree: Double,
  /** 宮位 1–12，無法判定為 null */
  val house: Int?,
  val retrograde: Boolean,
  /** PLANET | NODE | ASTEROID | FIXED_STAR | LOT | AXIS | OTHER */
  val kind: String,
)

@Serializable
data class HoroscopeHouseDto(
  /** 1–12 */
  val id: Int,
  /** 宮頭的絕對黃經 */
  val cuspDeg: Double,
  /** 宮頭所在星座 */
  val cuspSign: String,
  /** 宮主星 */
  val ruler: String,
  val rulerLabel: String,
)

@Serializable
data class HoroscopeAspectDto(
  /** 兩端點的 [AstroPoint.nameKey] */
  val from: String,
  val to: String,
  /** [Aspect] 名稱，如 `CONJUNCTION` */
  val aspect: String,
  /** 該相位的**標準**角度（0/60/90/120/180…），非實測角 */
  val angle: Double,
  /** 誤差（度） */
  val orb: Double,
  /** 主要相位（[Aspect.Importance.HIGH]）—— renderer 據此決定線寬 */
  val major: Boolean,
  /** true=入相位、false=出相位、null=未知 */
  val applying: Boolean?,
)

@Serializable
data class HoroscopeMetaDto(
  /** M | F */
  val gender: String,
  /** 上升點的絕對黃經 */
  val ascDeg: Double,
  /** 天頂的絕對黃經 */
  val mcDeg: Double,
)

@Serializable
data class HoroscopeChartDto(
  val meta: HoroscopeMetaDto,
  val points: List<HoroscopePointDto>,
  /** 12 宮頭。順序不保證，client 依 [HoroscopeHouseDto.id] 對位 */
  val houses: List<HoroscopeHouseDto>,
  /** 相位線。兩端點必定都在 [points] 內 */
  val aspects: List<HoroscopeAspectDto>,
)

/** [AstroPoint] 的型別 → 分類標籤（供 renderer 分層與樣式） */
private val AstroPoint.kindLabel: String
  get() = when (this) {
    is Planet     -> "PLANET"
    is LunarNode  -> "NODE"
    is Asteroid   -> "ASTEROID"
    is FixedStar  -> "FIXED_STAR"
    is Arabic     -> "LOT"
    is Axis       -> "AXIS"
    else          -> "OTHER"
  }

/** 該點位是否在此密度下呈現 */
private fun AstroPoint.visibleIn(density: ChartDensity): Boolean = when (density) {
  ChartDensity.COMPACT      -> this is Planet
  ChartDensity.FULL,
  ChartDensity.ALL          -> true
}

fun IPersonHoroscopeDtoV2.toHoroscopeChartDto(density: ChartDensity): HoroscopeChartDto {
  val houseDtos = houses.map { h ->
    HoroscopeHouseDto(
      id = h.id,
      cuspDeg = h.cusp.zDeg,
      cuspSign = h.cusp.sign.name,
      ruler = h.ruler.nameKey,
      rulerLabel = h.ruler.pointToString(ZH),
    )
  }

  /**
   * 上升／天頂優先取 [Axis] 的實際位置，僅在缺漏時退回宮頭。
   * 不能一律用宮頭 —— 整宮制（Whole Sign）的第一宮頭是上升星座的 0°，不等於上升度數。
   */
  fun axisDeg(axis: Axis, fallbackHouseId: Int): Double =
    stars[axis]?.signDegree?.zDeg
      ?: houseDtos.firstOrNull { it.id == fallbackHouseId }?.cuspDeg
      ?: 0.0

  val pointDtos = stars.entries
    .filter { (point, _) -> point.visibleIn(density) }
    .map { (point, info) ->
      HoroscopePointDto(
        point = point.nameKey,
        label = point.pointToString(ZH),
        glyph = point.unicode?.toString(),
        zDeg = info.signDegree.zDeg,
        sign = info.signDegree.sign.name,
        signDegree = info.signDegree.signDegree.second,
        house = info.house,
        retrograde = info.isRetrograde,
        kind = point.kindLabel,
      )
    }

  val majorAspects = Aspect.getAspects(Aspect.Importance.HIGH).toSet()
  val visibleKeys = pointDtos.map { it.point }.toSet()

  return HoroscopeChartDto(
    meta = HoroscopeMetaDto(
      gender = gender.name,
      ascDeg = axisDeg(Axis.RISING, 1),
      mcDeg = axisDeg(Axis.MERIDIAN, 10),
    ),
    points = pointDtos,
    houses = houseDtos,
    aspects = tightestAspects.mapNotNull { pattern ->
      val ends = pattern.points.take(2)
      if (ends.size < 2) return@mapNotNull null
      // 兩端點都必須在盤面上，否則會畫出連到空氣的線
      if (ends.any { it.nameKey !in visibleKeys }) return@mapNotNull null
      val aspect = Aspect.getAspect(pattern.angle) ?: return@mapNotNull null
      val major = aspect in majorAspects
      // 次要相位只在 ALL 出現 —— 全開會把圓盤糊成一團
      if (!major && density != ChartDensity.ALL) return@mapNotNull null

      HoroscopeAspectDto(
        from = ends[0].nameKey,
        to = ends[1].nameKey,
        aspect = aspect.name,
        angle = aspect.degree,
        orb = pattern.orb,
        major = major,
        applying = pattern.aspectType?.let { it == IPointAspectPattern.AspectType.APPLYING },
      )
    },
  )
}
