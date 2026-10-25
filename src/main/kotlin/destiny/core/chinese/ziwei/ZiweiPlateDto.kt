/**
 * Created by smallufo on 2026-08-12.
 *
 * 紫微盤的**呈現層 DTO** —— 專供 client 端 renderer 使用，與 [Plate] 刻意分離。
 *
 * 存在的兩個理由：
 *
 * 1. **去識別化的型別保證**：[Plate] 帶有 `name` / `chineseDate`（農曆日期等同精確生日）/
 *    `localDateTime` / `location` / `place`。本 DTO **結構上就沒有宣告這些欄位**
 *    ——不是序列化後被過濾掉，而是壓根沒有可填之處。因此「盤面可公開、當事人身分不外流」
 *    這件事由編譯器把關，不倚賴輸出白名單的維護紀律。需要顯示身分時，由呼叫端另外組一個
 *    並列的身分物件，而非塞進盤面。
 * 2. **[ZStar] 序列化會掉分類**：`ZStarSerializer` 只吐星名字串，client 分不出
 *    14 主星與長生十二神。分級顯示所需的 [ZiweiStarDto.tier] 必須由 mapper 明確帶上。
 *
 * 星名與宮名一律為**繁體中文**（紫微斗數不做多語系）；簡稱取自既有的 `ZStar.properties`。
 *
 * 大限起訖的西元年（[ZiweiHouseDto.fortuneFromYear]）只在呼叫端傳 `yearsBy` 時出現 ——
 * 年份配上虛歲可推出確切出生年，盤面要公開時不帶。
 *
 * ⚠️ 命名規則：本檔案內**不得出現名為 `name` 的欄位**，人可讀字串一律叫 `label`。
 * 這讓「DTO 不含身分欄位」可以用欄位名精確比對來自動驗證（見對應測試）——
 * 若星名也叫 `name`，驗證就得改成路徑判斷，複雜且易錯。
 */
package destiny.core.chinese.ziwei

import destiny.core.ChartDensity
import destiny.core.IntAgeNote
import destiny.core.chinese.AgeType
import destiny.core.getAbbreviation
import destiny.tools.Lang
import kotlinx.serialization.Serializable
import destiny.core.toString as pointToString

/** 星名的查表語系 —— 紫微固定繁中，不對外開放為參數 */
private val ZH = Lang.ZH_TW

@Serializable
data class ZiweiStarDto(
  /** 星名 */
  val label: String,
  /** 簡稱（格子擠不下時用） */
  val abbr: String,
  /** MAIN | LUCKY | UNLUCKY | MINOR | DOCTOR | LONGEVITY | GENERAL_FRONT | YEAR_FRONT */
  val tier: String,
  /** 廟旺弱陷的級數（1=廟 … 7=陷），無資料為 null。供樣式分級用 */
  val strength: Int?,
  /** 廟旺弱陷的文字（廟/旺/地/利/平/閒/陷），無資料為 null */
  val strengthLabel: String?,
  /** 本命四化（祿/權/科/忌）；流運四化見 [flowTransFours] */
  val transFours: List<T4Value>,
  /** 流運四化（只含 [ZiweiPlateDto.flows] 裡有的層），例：{SECTION:權, YEAR:科} */
  val flowTransFours: Map<FlowType, T4Value> = emptyMap(),
)

/** 宮干四化的一筆：此宮宮干化 [value] 的是 [starLabel]，那顆星在 [toBranch] */
@Serializable
data class ZiweiFlyDto(val value: T4Value, val starLabel: String, val toBranch: String)

@Serializable
data class ZiweiHouseDto(
  /** 地支 —— client 的擺位鍵 */
  val branch: String,
  /** 宮干支 */
  val stemBranch: String,
  /** 本命宮名（疊加流運時亦同，流運宮名見 [flowHouseLabels]） */
  val houseLabel: String,
  val isMing: Boolean,
  /** 身宮 */
  val isBody: Boolean,
  /** 空宮（無主星）—— 與當前 density 無關 */
  val isEmpty: Boolean,
  /** 大限起（inclusive） */
  val fortuneFromAge: Int,
  /** 大限訖（inclusive） */
  val fortuneToAge: Int,
  val stars: List<ZiweiStarDto>,
  /** 此宮在各流運層叫什麼宮，例：{SECTION:"田宅", YEAR:"命宮"}；本命盤為空 */
  val flowHouseLabels: Map<FlowType, String> = emptyMap(),
  /** 同上的傳統簡稱（[House.abbr]），例：{SECTION:"田", YEAR:"偶"} —— 夫妻是「偶」、交友是「奴」，不是取首字 */
  val flowHouseAbbrs: Map<FlowType, String> = emptyMap(),
  /** 宮干四化（通常 4 筆）；自化 ＝ toBranch 是本宮、化入對宮 ＝ toBranch 是對宮 */
  val flies: List<ZiweiFlyDto> = emptyList(),
  /** 小限歲數 */
  val smallRanges: List<Int> = emptyList(),
  /** 大限起的西元年（呼叫端要求才給，見 [toZiweiPlateDto] 的 yearsBy） */
  val fortuneFromYear: Int? = null,
  /** 大限訖的西元年（同上） */
  val fortuneToYear: Int? = null,
  /** [fortuneFromYear] 的各種年份註記（[IntAgeNote.noteOf]；不適用者不列，例：民國元年前沒有 Minguo） */
  val fortuneFromNotes: Map<IntAgeNote, String> = emptyMap(),
  /** [fortuneToYear] 的各種年份註記（同上） */
  val fortuneToNotes: Map<IntAgeNote, String> = emptyMap(),
)

/** 疊加的一層流運 */
@Serializable
data class ZiweiFlowDto(
  /** 流運層別：SECTION（大限）、YEAR（流年）；盤上若疊了流月／流日／流時，也會照列 */
  val type: FlowType,
  val stemBranch: String,
  /** 這一層的命宮落在哪個地支 */
  val mingBranch: String,
)

/**
 * 盤面層級的事實。五行局刻意只給 [fiveElement] 與 [state] 兩個零件，
 * 「水二局」這種字串由 renderer 自行組合（中文數字或阿拉伯數字屬呈現決定）。
 */
@Serializable
data class ZiweiMetaDto(
  /** M | F */
  val gender: String,
  /** 出生年干支（可能是節氣年，也可能是陰曆年，依起盤設定） */
  val yearStemBranch: String,
  /** 時辰 */
  val hourBranch: String,
  /** DAY | NIGHT */
  val dayNight: String,
  /** 五行 */
  val fiveElement: String,
  /** 第幾局 */
  val state: Int,
  /** 命主 */
  val mainStarLabel: String,
  /** 身主 */
  val bodyStarLabel: String,
  /** 命宮地支 */
  val mingBranch: String,
  /** 身宮地支 */
  val bodyBranch: String,
)

@Serializable
data class ZiweiPlateDto(
  val meta: ZiweiMetaDto,
  /** 12 宮。順序不保證，client 依 [ZiweiHouseDto.branch] 擺位 */
  val houses: List<ZiweiHouseDto>,
  /** 疊加的流運層，由外而內（大限、流年）；本命盤為空 */
  val flows: List<ZiweiFlowDto> = emptyList(),
)

/** [ZStar] 的 sealed 子型別 → 顯示分級標籤 */
private val ZStar.tierLabel: String
  get() = when (this) {
    is StarMain         -> "MAIN"
    is StarLucky        -> "LUCKY"
    is StarUnlucky      -> "UNLUCKY"
    is StarMinor        -> "MINOR"
    is StarDoctor       -> "DOCTOR"
    is StarLongevity    -> "LONGEVITY"
    is StarGeneralFront -> "GENERAL_FRONT"
    is StarYearFront    -> "YEAR_FRONT"
  }

/** 該星是否在此密度下呈現 */
private fun ZStar.visibleIn(density: ChartDensity): Boolean = when (density) {
  ChartDensity.COMPACT -> this is StarMain
  ChartDensity.FULL    -> this is StarMain || this is StarLucky || this is StarUnlucky || this is StarMinor
  ChartDensity.ALL     -> true
}

/**
 * @param yearsBy 要輸出大限的西元年時，給大限歲數的算法（虛歲／實歲）；null ＝不輸出年份。
 *   呼叫端須傳入起盤時所用的同一個 [IZiweiConfig.sectionAgeType]，否則年份會差一年
 */
fun IPlate.toZiweiPlateDto(density: ChartDensity, yearsBy: AgeType? = null): ZiweiPlateDto {
  // 疊加盤的 HouseData.house 是最後一層流運的宮名，本命一律取 flowHouseMap[MAIN]
  fun HouseData.natal(): House = getHouse(FlowType.MAIN)
  val layers: List<FlowType> = flowBranchMap.keys.filter { it != FlowType.MAIN }.sorted()
  val birthWestYear = chineseDate.westYear
  // 歲數 → 年份，與 IZiweiFeature.getYearsOfFlowSection 同規則：虛歲減一、實歲不減
  fun westYearOfAge(age: Int): Int? = when (yearsBy) {
    AgeType.VIRTUAL -> birthWestYear + age - 1
    AgeType.REAL    -> birthWestYear + age
    null            -> null
  }
  fun notesOf(westYear: Int?): Map<IntAgeNote, String> =
    westYear?.let { y -> IntAgeNote.entries.mapNotNull { n -> n.noteOf(y)?.let { n to it } }.toMap() } ?: emptyMap()

  return ZiweiPlateDto(
    meta = ZiweiMetaDto(
      gender = gender.name,
      yearStemBranch = year.toString(),
      hourBranch = hour.name,
      dayNight = dayNight.name,
      fiveElement = fiveElement.toString(),
      state = state,
      mainStarLabel = mainStar.pointToString(ZH),
      bodyStarLabel = bodyStar.pointToString(ZH),
      mingBranch = houseDataSet.firstOrNull { it.natal() == House.命宮 }?.stemBranch?.branch?.name ?: "",
      bodyBranch = bodyHouse.branch.name,
    ),
    houses = houseDataSet.map { houseData ->
      val natal = houseData.natal()
      ZiweiHouseDto(
        branch = houseData.stemBranch.branch.name,
        stemBranch = houseData.stemBranch.toString(),
        houseLabel = natal.value,
        isMing = natal == House.命宮,
        isBody = houseData.stemBranch == bodyHouse,
        // 主星有無決定空宮，不受 density 過濾影響
        isEmpty = houseData.stars.none { it is StarMain },
        fortuneFromAge = houseData.rangeFromAge,
        fortuneToAge = houseData.rangeToAge,
        stars = houseData.stars.filter { it.visibleIn(density) }.map { star ->
          val strength = starStrengthMap[star]
          val t4 = transFours[star].orEmpty()
          ZiweiStarDto(
            label = star.pointToString(ZH),
            abbr = star.getAbbreviation(ZH),
            tier = star.tierLabel,
            strength = strength,
            strengthLabel = strength?.let { IStrength.getString(it, ZH) },
            transFours = t4[FlowType.MAIN]?.let { listOf(it) } ?: emptyList(),
            flowTransFours = t4.filterKeys { it in layers },
          )
        },
        flowHouseLabels = layers.mapNotNull { type -> houseData.flowHouseMap[type]?.let { type to it.value } }.toMap(),
        flowHouseAbbrs = layers.mapNotNull { type -> houseData.flowHouseMap[type]?.let { type to it.abbr.toString() } }.toMap(),
        flies = houseData.flies.map { (v, star, branch) -> ZiweiFlyDto(v, star.pointToString(ZH), branch.name) }
          .sortedBy { it.value.ordinal },
        smallRanges = houseData.smallRanges,
        fortuneFromYear = westYearOfAge(houseData.rangeFromAge),
        fortuneToYear = westYearOfAge(houseData.rangeToAge),
        fortuneFromNotes = notesOf(westYearOfAge(houseData.rangeFromAge)),
        fortuneToNotes = notesOf(westYearOfAge(houseData.rangeToAge)),
      )
    },
    flows = layers.map { type ->
      ZiweiFlowDto(
        type = type,
        stemBranch = flowBranchMap.getValue(type).toString(),
        mingBranch = houseDataSet.firstOrNull { it.flowHouseMap[type] == House.命宮 }?.stemBranch?.branch?.name ?: "",
      )
    },
  )
}
