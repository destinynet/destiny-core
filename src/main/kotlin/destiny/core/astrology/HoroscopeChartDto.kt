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
import destiny.core.Gender
import destiny.core.getAbbreviation
import destiny.tools.Lang
import destiny.tools.serializers.DoubleTwoDecimalSerializer
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
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val zDeg: Double,
  /** 星座（[ZodiacSign] 名稱） */
  val sign: String,
  /** 星座內度數 0–30 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val signDegree: Double,
  /** 宮位 1–12，無法判定為 null */
  val house: Int?,
  val retrograde: Boolean,
  /** PLANET | NODE | ASTEROID | FIXED_STAR | LOT | AXIS | OTHER */
  val kind: String,
  /** 縮寫（繁中，如 日、月、昇）—— 小空間的標籤用它，[label] 是全名 */
  val abbr: String = "",
  /** 英文縮寫（如 Su、Mo、AC；取自英文 bundle） */
  val abbrEn: String = "",
  /** 赤緯（度）；算不出時為 null */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val declination: Double? = null,
  /** 逆行階段（停滯前、逆行中、停滯後）；順行為 null。比 [retrograde] 細 */
  val retrogradePhase: RetrogradePhase? = null,
)

@Serializable
data class HoroscopeHouseDto(
  /** 1–12 */
  val id: Int,
  /** 宮頭的絕對黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
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
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val angle: Double,
  /** 誤差（度） */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  /** 主要相位（[Aspect.Importance.HIGH]）—— renderer 據此決定線寬 */
  val major: Boolean,
  /** true=入相位、false=出相位、null=未知 */
  val applying: Boolean?,
  /** 相位符號（[Aspect.glyph]：☌ ☍ □ △ ⚹…，次要相位為縮寫） */
  val glyph: String = "",
  /** 相位強度（0–1，容許度內約 0.6 起跳；越緊越接近 1）；未知為 null */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val score: Double? = null,
)

@Serializable
data class HoroscopeMetaDto(
  /** M | F */
  val gender: String,
  /** 上升點的絕對黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val ascDeg: Double,
  /** 天頂的絕對黃經 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
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

/** 有性別的盤（[Natal]／[NatalV2] 皆可） */
fun IPersonHoroscopeDto.toHoroscopeChartDto(density: ChartDensity): HoroscopeChartDto =
  toHoroscopeChartDto(density, gender)

/**
 * 只讀 [stars]、[houses]、[tightestAspects] —— V1 的 [IHoroscopeDto] 就夠，不必先轉 V2。
 * 盤本身不帶性別時（非 person 的 [IHoroscopeDto]），由呼叫端給。
 */
fun IHoroscopeDto.toHoroscopeChartDto(density: ChartDensity, gender: Gender): HoroscopeChartDto {
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
        abbr = point.getAbbreviation(ZH),
        abbrEn = point.getAbbreviation(Lang.EN),
        declination = info.declination,
        retrogradePhase = info.retrogradePhase,
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
        glyph = aspect.glyph,
        angle = aspect.degree,
        orb = pattern.orb,
        major = major,
        applying = pattern.aspectType?.let { it == IPointAspectPattern.AspectType.APPLYING },
        score = pattern.score?.value,
      )
    },
  )
}

/**
 * 依出生時刻精度拿掉算不準的部分，判準同 [BirthDataGrain.allowsNatalTarget]：
 * - 沒有分鐘（[BirthDataGrain.includeAxis] 為 false）⇒ 軸點、宮位、各點所在的宮位。
 *   上升／天頂度數歸零 —— 0° 是 renderer 對「沒有上升」的退路，而不是一個捏造的上升。
 * - 沒有時辰 ⇒ 再拿掉月亮。
 *
 * 相位只留兩端都還在的。
 */
fun HoroscopeChartDto.gatedBy(grain: BirthDataGrain): HoroscopeChartDto {
  val gated: Set<String> = grain.gatedNatalTargets().map { it.nameKey }.toSet()
  val kept = points.filter { it.point !in gated }
  val keys = kept.map { it.point }.toSet()
  val keptAspects = aspects.filter { it.from in keys && it.to in keys }
  return if (grain.includeAxis) copy(points = kept, aspects = keptAspects)
  else copy(
    meta = meta.copy(ascDeg = 0.0, mcDeg = 0.0),
    points = kept.map { it.copy(house = null) },
    houses = emptyList(),
    aspects = keptAspects,
  )
}
