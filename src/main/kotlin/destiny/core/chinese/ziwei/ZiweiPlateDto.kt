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
 * ⚠️ 命名規則：本檔案內**不得出現名為 `name` 的欄位**，人可讀字串一律叫 `label`。
 * 這讓「DTO 不含身分欄位」可以用欄位名精確比對來自動驗證（見對應測試）——
 * 若星名也叫 `name`，驗證就得改成路徑判斷，複雜且易錯。
 */
package destiny.core.chinese.ziwei

import destiny.core.getAbbreviation
import destiny.tools.Lang
import kotlinx.serialization.Serializable
import destiny.core.toString as pointToString

/** 星名的查表語系 —— 紫微固定繁中，不對外開放為參數 */
private val ZH = Lang.ZH_TW

/** 呈現密度 —— 對應 [ZStar] 的 sealed 子型別分組，而非 [ZStar.Type]（後者是「由什麼決定的星」，對顯示無意義） */
enum class ZiweiDensity {
  /** 主星（14）：多張盤並排概覽 */
  COMPACT,

  /** ＋六吉六煞＋乙級雜曜：單張放大 */
  FULL,

  /** ＋博士12／長生12／將前12／歲前12：`FULL` 裡的「顯示雜曜」開關 */
  ALL
}

@Serializable
data class ZiweiStarDto(
  /** 星名 */
  val label: String,
  /** 簡稱（格子擠不下時用） */
  val abbr: String,
  /** MAIN | LUCKY | UNLUCKY | MINOR | DOCTOR | LONGEVITY | GENERAL_FRONT | YEAR_FRONT */
  val tier: String,
  /** 廟旺弱陷，無資料為 null */
  val strength: Int?,
  /** 本命四化（祿/權/科/忌）；大限、流年四化不列入 */
  val transFours: List<String>,
)

@Serializable
data class ZiweiHouseDto(
  /** 地支 —— client 的擺位鍵 */
  val branch: String,
  /** 宮干支 */
  val stemBranch: String,
  /** 宮名 */
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
private fun ZStar.visibleIn(density: ZiweiDensity): Boolean = when (density) {
  ZiweiDensity.COMPACT -> this is StarMain
  ZiweiDensity.FULL    -> this is StarMain || this is StarLucky || this is StarUnlucky || this is StarMinor
  ZiweiDensity.ALL     -> true
}

fun IPlate.toZiweiPlateDto(density: ZiweiDensity): ZiweiPlateDto {
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
      mingBranch = houseDataSet.firstOrNull { it.house == House.命宮 }?.stemBranch?.branch?.name ?: "",
      bodyBranch = bodyHouse.branch.name,
    ),
    houses = houseDataSet.map { houseData ->
      ZiweiHouseDto(
        branch = houseData.stemBranch.branch.name,
        stemBranch = houseData.stemBranch.toString(),
        houseLabel = houseData.house.value,
        isMing = houseData.house == House.命宮,
        isBody = houseData.stemBranch == bodyHouse,
        // 主星有無決定空宮，不受 density 過濾影響
        isEmpty = houseData.stars.none { it is StarMain },
        fortuneFromAge = houseData.rangeFromAge,
        fortuneToAge = houseData.rangeToAge,
        stars = houseData.stars.filter { it.visibleIn(density) }.map { star ->
          ZiweiStarDto(
            label = star.pointToString(ZH),
            abbr = star.getAbbreviation(ZH),
            tier = star.tierLabel,
            strength = starStrengthMap[star],
            transFours = transFours[star]?.get(FlowType.MAIN)?.let { listOf(it.name) } ?: emptyList(),
          )
        }
      )
    }
  )
}
