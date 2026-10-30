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
import destiny.core.Graph
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
  /** 黃經速度（度／日，逆行為負）；軸點與虛點為 null */
  val speed: Double? = null,
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

/**
 * 定位星關係 —— [Graph] 的呈現層投影，星體一律是 [AstroPoint.nameKey]。只含傳統七星（[Planet.classicalList]）。
 *
 * 圖已在 server 算好（[IHoroscopeModel.getGraph]）；client 只需把相鄰兩顆連成箭頭，不必重跑圖演算法。
 * 每條鏈與每個環都依**定位方向**排列：前一顆由後一顆定位（落在後一顆守護的星座）。
 */
@Serializable
data class HoroscopeDispositorDto(
  /** 循環：每顆由下一顆定位，最後一顆由第一顆定位 */
  val circles: List<List<String>>,
  /** 最長的定位鏈；終點是 [terminals] 之一，或某個 [circles] 上的星 */
  val paths: List<List<String>>,
  /** 最終定位星：落在自己守護的星座，鏈條在此收斂 */
  val terminals: List<String>,
  /** 落在自己守護的星座、但沒有其他星由它定位 */
  val isolated: List<String>,
)

/** 格局的一條邊：兩端、相位與實際容許度（server 依兩星的黃經算好，renderer 不必自己算角距） */
@Serializable
data class HoroscopePatternLegDto(
  val from: String,
  val to: String,
  /** [Aspect] 名稱 */
  val aspect: String,
  val glyph: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
)

/**
 * 格局（[AstroPattern] 的呈現層投影）。判定已在 server 做完（格局的容許度比 [HoroscopeChartDto.aspects] 寬，
 * 所以邊不一定在 aspects 裡）；renderer 照 [legs] 畫線即可。恆星不參與（輪盤不畫恆星）。
 */
@Serializable
data class HoroscopePatternDto(
  /**
   * GRAND_TRINE、KITE、T_SQUARE、YOD、BOOMERANG、GOLDEN_YOD、GRAND_CROSS、DOUBLE_T、HEXAGON、
   * WEDGE、MYSTIC_RECTANGLE、PENTAGRAM、STELLIUM_SIGN、STELLIUM_HOUSE、CONFRONTATION
   */
  val type: String,
  /** 全部成員（[AstroPoint.nameKey]） */
  val points: List<String>,
  /** 頂點：T 三角的四分點、Yod／回力鏢的頂點、黃金 Yod 的指向點、風箏的頭、楔形的調停者 */
  val apex: String? = null,
  /** 大三角的元素（[Element]） */
  val element: String? = null,
  /** 大十字的三方性（[Quality]） */
  val quality: String? = null,
  /** 星群的星座／宮位 */
  val sign: String? = null,
  val house: Int? = null,
  val legs: List<HoroscopePatternLegDto>,
  /** 組合格局（雙 T、六芒星、回力鏢）的組成：[HoroscopeChartDto.patterns] 的索引 */
  val parts: List<Int> = emptyList(),
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val score: Double? = null,
)

/** 中點：[a]／[b] 的中點落在 [zDeg]，[focal] 就在那裡（容許度 [orb]） */
@Serializable
data class HoroscopeMidpointDto(
  val a: String,
  val b: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val zDeg: Double,
  val focal: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
)

/**
 * 光線傳遞（[destiny.core.astrology.classical.TranslationOfLight] 的呈現層投影）：
 * [by] 剛離開 [from] 的 [fromAspect]（出相位 [fromOrb]），下一個成形的是與 [to] 的 [toAspect]（入相位 [toOrb]）。
 * [between]：[from] 與 [to] 本身的相位，null ＝ 彼此不成相位
 */
@Serializable
data class HoroscopeTranslationDto(
  val by: String,
  val from: String,
  val fromAspect: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val fromOrb: Double,
  val to: String,
  val toAspect: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val toOrb: Double,
  val between: String? = null,
)

/**
 * 光線收集（[destiny.core.astrology.classical.CollectionOfLight] 的呈現層投影）：[a]、[b] 彼此不相見，都入相位到比較慢的 [by]。
 * [between]：[a] 與 [b] 本身的相位，null ＝ 不成相位；[aReception]／[bReception]：以哪一種 [destiny.core.astrology.classical.Dignity] 接納 [by]，null ＝ 不接納
 */
@Serializable
data class HoroscopeCollectionDto(
  val by: String,
  val a: String,
  val aAspect: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val aOrb: Double,
  val b: String,
  val bAspect: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val bOrb: Double,
  val between: String? = null,
  val aReception: String? = null,
  val bReception: String? = null,
)

/** 臨陣退縮軌跡的一個取樣點：盤面時刻後 [days] 日，退縮者 [byLng] 與對方 [otherLng] 的黃經 */
@Serializable
data class HoroscopeRefranationSampleDto(
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val days: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val byLng: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val otherLng: Double,
)

/**
 * 臨陣退縮（[destiny.core.astrology.classical.Refranation] 的呈現層投影）：[by] 與 [other] 正以 [aspect] 入相位（盤面容許度 [orb]），
 * 成形前 [by] 在 [stationDays] 日後停滯（[station]：[StationaryType]），[closestDays] 日後最接近、仍差 [closestOrb] 度。
 * [track]：到兩星再度拉開為止的黃經等距取樣
 */
@Serializable
data class HoroscopeRefranationDto(
  val by: String,
  val other: String,
  val aspect: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  val station: String,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val stationDays: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val closestDays: Double,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val closestOrb: Double,
  val track: List<HoroscopeRefranationSampleDto>,
)

@Serializable
data class HoroscopeChartDto(
  val meta: HoroscopeMetaDto,
  val points: List<HoroscopePointDto>,
  /** 12 宮頭。順序不保證，client 依 [HoroscopeHouseDto.id] 對位 */
  val houses: List<HoroscopeHouseDto>,
  /** 相位線。兩端點必定都在 [points] 內 */
  val aspects: List<HoroscopeAspectDto>,
  /** 定位星關係；來源盤沒有算、或有星不在 [points] 內時為 null（舊的凍結 JSON 也沒有） */
  val dispositors: HoroscopeDispositorDto? = null,
  /** 格局；成員都在 [points] 內 */
  val patterns: List<HoroscopePatternDto> = emptyList(),
  /** 有焦點星的中點；三顆都在 [points] 內 */
  val midpoints: List<HoroscopeMidpointDto> = emptyList(),
  /** 光線傳遞；三顆都在 [points] 內。由呼叫端以 [withTranslations] 附上 */
  val translations: List<HoroscopeTranslationDto> = emptyList(),
  /** 光線收集；三顆都在 [points] 內。由呼叫端以 [withCollections] 附上 */
  val collections: List<HoroscopeCollectionDto> = emptyList(),
  /** 臨陣退縮；兩顆都在 [points] 內。由呼叫端以 [withRefranations] 附上 */
  val refranations: List<HoroscopeRefranationDto> = emptyList(),
)

/** 附上臨陣退縮（[destiny.core.astrology.classical.RefranationImpl] 的結果）；有星不在盤上的略過 */
fun HoroscopeChartDto.withRefranations(list: List<destiny.core.astrology.classical.Refranation>): HoroscopeChartDto {
  val keys = points.mapTo(HashSet()) { it.point }
  return copy(refranations = list
    .filter { r -> r.refranator.nameKey in keys && r.other.nameKey in keys }
    .map { r ->
      HoroscopeRefranationDto(
        by = r.refranator.nameKey, other = r.other.nameKey,
        aspect = r.applying.aspect.name, orb = r.applying.orb,
        station = r.stationary.type.name, stationDays = r.stationDays,
        closestDays = r.closestDays, closestOrb = r.closestOrb,
        track = r.track.map { HoroscopeRefranationSampleDto(it.days, it.refranatorLng, it.otherLng) },
      )
    })
}

/** 附上光線收集（[destiny.core.astrology.classical.CollectionOfLightImpl] 的結果）；有星不在盤上的略過 */
fun HoroscopeChartDto.withCollections(list: List<destiny.core.astrology.classical.CollectionOfLight>): HoroscopeChartDto {
  val keys = points.mapTo(HashSet()) { it.point }
  return copy(collections = list
    .filter { c -> (c.planets + c.collector).all { it.nameKey in keys } }
    .map { c ->
      val (pa, pb) = c.planets
      HoroscopeCollectionDto(
        by = c.collector.nameKey,
        a = pa.nameKey, aAspect = c.first.aspect.name, aOrb = c.first.orb,
        b = pb.nameKey, bAspect = c.second.aspect.name, bOrb = c.second.orb,
        between = c.between?.aspect?.name,
        aReception = c.firstReception?.name,
        bReception = c.secondReception?.name,
      )
    })
}

/** 附上光線傳遞（[destiny.core.astrology.classical.TranslationOfLightImpl] 的結果）；有星不在盤上的略過 */
fun HoroscopeChartDto.withTranslations(list: List<destiny.core.astrology.classical.TranslationOfLight>): HoroscopeChartDto {
  val keys = points.mapTo(HashSet()) { it.point }
  return copy(translations = list
    .filter { t -> listOf(t.translator, t.from, t.to).all { it.nameKey in keys } }
    .map { t ->
      HoroscopeTranslationDto(
        by = t.translator.nameKey,
        from = t.from.nameKey, fromAspect = t.separating.aspect.name, fromOrb = t.separating.orb,
        to = t.to.nameKey, toAspect = t.applying.aspect.name, toOrb = t.applying.orb,
        between = t.between?.aspect?.name,
      )
    })
}

/** 格局可能用到的相位；組合格局（雙 T、六芒星、回力鏢）另外併入組成部分的邊 */
private val AstroPattern.legAspects: Set<Aspect>
  get() = when (this) {
    is AstroPattern.GrandTrine      -> setOf(Aspect.TRINE)
    is AstroPattern.Kite            -> setOf(Aspect.TRINE, Aspect.OPPOSITION, Aspect.SEXTILE)
    is AstroPattern.TSquared        -> setOf(Aspect.OPPOSITION, Aspect.SQUARE)
    is AstroPattern.Yod             -> setOf(Aspect.SEXTILE, Aspect.QUINCUNX)
    is AstroPattern.Boomerang       -> setOf(Aspect.OPPOSITION, Aspect.SEMISEXTILE)
    is AstroPattern.GoldenYod       -> setOf(Aspect.QUINTILE, Aspect.BIQUINTILE)
    is AstroPattern.GrandCross      -> setOf(Aspect.OPPOSITION, Aspect.SQUARE)
    is AstroPattern.DoubleT         -> emptySet()
    is AstroPattern.Hexagon         -> setOf(Aspect.SEXTILE, Aspect.OPPOSITION)
    is AstroPattern.Wedge           -> setOf(Aspect.OPPOSITION, Aspect.TRINE, Aspect.SEXTILE)
    is AstroPattern.MysticRectangle -> setOf(Aspect.OPPOSITION, Aspect.TRINE, Aspect.SEXTILE)
    is AstroPattern.Pentagram       -> setOf(Aspect.QUINTILE, Aspect.BIQUINTILE)
    is AstroPattern.StelliumSign,
    is AstroPattern.StelliumHouse   -> emptySet()
    is AstroPattern.Confrontation   -> setOf(Aspect.OPPOSITION)
  }

private val AstroPattern.typeKey: String
  get() = when (this) {
    is AstroPattern.GrandTrine      -> "GRAND_TRINE"
    is AstroPattern.Kite            -> "KITE"
    is AstroPattern.TSquared        -> "T_SQUARE"
    is AstroPattern.Yod             -> "YOD"
    is AstroPattern.Boomerang       -> "BOOMERANG"
    is AstroPattern.GoldenYod       -> "GOLDEN_YOD"
    is AstroPattern.GrandCross      -> "GRAND_CROSS"
    is AstroPattern.DoubleT         -> "DOUBLE_T"
    is AstroPattern.Hexagon         -> "HEXAGON"
    is AstroPattern.Wedge           -> "WEDGE"
    is AstroPattern.MysticRectangle -> "MYSTIC_RECTANGLE"
    is AstroPattern.Pentagram       -> "PENTAGRAM"
    is AstroPattern.StelliumSign    -> "STELLIUM_SIGN"
    is AstroPattern.StelliumHouse   -> "STELLIUM_HOUSE"
    is AstroPattern.Confrontation   -> "CONFRONTATION"
  }

private val AstroPattern.apexPoint: AstroPoint?
  get() = when (this) {
    is AstroPattern.TSquared  -> squared.point
    is AstroPattern.Yod       -> apex.point
    is AstroPattern.Boomerang -> oppoPoint.point
    is AstroPattern.GoldenYod -> pointer.point
    is AstroPattern.Kite      -> head.point
    is AstroPattern.Wedge     -> mediator.point
    else                      -> null
  }

private val AstroPattern.partPatterns: List<AstroPattern>
  get() = when (this) {
    is AstroPattern.DoubleT   -> tSquares.toList()
    is AstroPattern.Hexagon   -> grandTrines.toList()
    is AstroPattern.Boomerang -> listOf(yod)
    else                      -> emptyList()
  }

/** 格局的邊判到哪一個相位時，允許實際角距偏離多少（格局的容許度上限之外再留餘裕） */
private const val LEG_TOLERANCE = 12.0

/** [patterns] → 呈現層投影；有成員不在盤上（或是恆星）的格局，連同用到它的組合格局一起略過 */
private fun List<AstroPattern>.toPatternDtos(zDegOf: (AstroPoint) -> Double?, visibleKeys: Set<String>): List<HoroscopePatternDto> {
  val out = mutableListOf<HoroscopePatternDto>()
  val indexOf = mutableMapOf<AstroPattern, Int?>()

  fun legsAmong(points: List<AstroPoint>, aspects: Set<Aspect>): List<HoroscopePatternLegDto> =
    points.flatMapIndexed { i, a -> points.drop(i + 1).map { b -> a to b } }.mapNotNull { (a, b) ->
      val za = zDegOf(a) ?: return@mapNotNull null
      val zb = zDegOf(b) ?: return@mapNotNull null
      val d = ((za - zb) % 360 + 360) % 360
      val sep = minOf(d, 360 - d)
      aspects.map { it to kotlin.math.abs(sep - it.degree) }
        .filter { (_, orb) -> orb <= LEG_TOLERANCE }
        .minByOrNull { (_, orb) -> orb }
        ?.let { (aspect, orb) -> HoroscopePatternLegDto(a.nameKey, b.nameKey, aspect.name, aspect.glyph, orb) }
    }

  fun add(pattern: AstroPattern): Int? = indexOf.getOrPut(pattern) {
    val members = pattern.points.toList()
    if (members.any { it is FixedStar || it.nameKey !in visibleKeys }) return@getOrPut null
    val parts = pattern.partPatterns.map { add(it) ?: return@getOrPut null }
    val partLegs = parts.flatMap { out[it].legs }
    val ownLegs = legsAmong(members, pattern.legAspects)
      .filter { leg -> partLegs.none { setOf(it.from, it.to) == setOf(leg.from, leg.to) } }
    out += HoroscopePatternDto(
      type = pattern.typeKey,
      points = members.map { it.nameKey },
      apex = pattern.apexPoint?.nameKey,
      element = (pattern as? AstroPattern.GrandTrine)?.element?.name,
      quality = (pattern as? AstroPattern.GrandCross)?.quality?.name,
      sign = (pattern as? AstroPattern.StelliumSign)?.sign?.name,
      house = (pattern as? AstroPattern.StelliumHouse)?.house,
      legs = partLegs + ownLegs,
      parts = parts,
      score = pattern.score?.value,
    )
    out.size - 1
  }

  forEach { add(it) }
  return out
}


/** [Graph] → 呈現層投影；圖是空的（沒有算）或有星不在盤面上時回 null */
private fun Graph<Planet>.toDispositorDto(visibleKeys: Set<String>): HoroscopeDispositorDto? {
  val planets = circles.flatMap { it.toList() } + paths.flatten() + terminals + isolated
  if (planets.isEmpty() || planets.any { it.nameKey !in visibleKeys }) return null
  return HoroscopeDispositorDto(
    circles = circles.map { c -> c.toList().map { it.nameKey } },
    paths = paths.map { p -> p.map { it.nameKey } },
    terminals = terminals.map { it.nameKey },
    isolated = isolated.map { it.nameKey },
  )
}

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
        speed = info.speed,
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
    dispositors = graphPatterns.toDispositorDto(visibleKeys),
    patterns = astroPatterns.toPatternDtos({ stars[it]?.signDegree?.zDeg }, visibleKeys),
    midpoints = midPoints
      .filter { m -> (m.points + m.focal).all { it !is FixedStar && it.nameKey in visibleKeys } }
      .map { m -> HoroscopeMidpointDto(m.p1.nameKey, m.p2.nameKey, m.degree.zDeg, m.focal.nameKey, m.orb) },
  )
}

/**
 * 只留 [keep] 的點位，並維持各欄位「成員都在 [HoroscopeChartDto.points] 內」的契約：
 * 相位、中點只留端點都在的；格局有成員被拿掉就略過（用到它的組合格局一起略過，索引重排）；
 * 定位星關係只要有一顆被拿掉就整個拿掉（星座不可靠，整張圖都不可靠）。
 */
fun HoroscopeChartDto.keepingPoints(keep: (HoroscopePointDto) -> Boolean): HoroscopeChartDto {
  val kept = points.filter(keep)
  val keys = kept.mapTo(HashSet()) { it.point }
  val newIndex = mutableMapOf<Int, Int>()
  val keptPatterns = mutableListOf<HoroscopePatternDto>()
  patterns.forEachIndexed { i, p ->
    if (p.points.all { it in keys } && p.parts.all { it in newIndex }) {
      newIndex[i] = keptPatterns.size
      keptPatterns += p.copy(parts = p.parts.map { newIndex.getValue(it) })
    }
  }
  return copy(
    points = kept,
    aspects = aspects.filter { it.from in keys && it.to in keys },
    dispositors = dispositors?.takeIf { d -> (d.circles.flatten() + d.paths.flatten() + d.terminals + d.isolated).all { it in keys } },
    patterns = keptPatterns,
    midpoints = midpoints.filter { it.a in keys && it.b in keys && it.focal in keys },
    translations = translations.filter { it.by in keys && it.from in keys && it.to in keys },
    collections = collections.filter { it.by in keys && it.a in keys && it.b in keys },
    refranations = refranations.filter { it.by in keys && it.other in keys },
  )
}

/**
 * 依出生時刻精度拿掉算不準的部分，判準同 [BirthDataGrain.allowsNatalTarget]：
 * - 沒有分鐘（[BirthDataGrain.includeAxis] 為 false）⇒ 軸點、宮位、各點所在的宮位。
 *   上升／天頂度數歸零 —— 0° 是 renderer 對「沒有上升」的退路，而不是一個捏造的上升。
 * - 沒有時辰 ⇒ 再拿掉月亮。
 *
 * 相位、格局、中點、定位星關係的連帶處理見 [keepingPoints]。
 */
fun HoroscopeChartDto.gatedBy(grain: BirthDataGrain): HoroscopeChartDto {
  val gated: Set<String> = grain.gatedNatalTargets().map { it.nameKey }.toSet()
  val kept = keepingPoints { it.point !in gated }
  return if (grain.includeAxis) kept
  else kept.copy(
    meta = meta.copy(ascDeg = 0.0, mcDeg = 0.0),
    points = kept.points.map { it.copy(house = null) },
    houses = emptyList(),
    // 星群的宮位與宮位星群本身都不可得
    patterns = kept.patterns.map { it.copy(house = null) },
  )
}
