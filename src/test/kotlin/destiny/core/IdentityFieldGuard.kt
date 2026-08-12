/**
 * Created by smallufo on 2026-08-12.
 *
 * 「呈現層 DTO 不得攜帶當事人身分資料」的自動驗證。
 *
 * 用途：命盤 DTO（紫微／占星／八字）可以公開分享盤面，但不得洩漏當事人是誰，
 * 也不得洩漏可反推精確生日、地點的欄位。這種保證若只靠「輸出時記得過濾」，
 * 日後有人往 DTO 加一個欄位就破功；改由測試遞迴掃描 serializer descriptor，
 * 一有身分欄位就紅燈。
 *
 * 掃描是**遞迴**的 —— 只檢查 top-level 欄位擋不住嵌套洩漏（例如某個 nested
 * DTO 自己帶了 `localDateTime`）。
 */
package destiny.core

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

/**
 * 身分／可反推生日地點的欄位名（精確比對）。
 *
 * 呈現層 DTO 的人可讀字串一律命名為 `label`，故 `name` 可以無歧義地列為禁字。
 */
val IDENTITY_FIELD_NAMES: Set<String> = setOf(
  // 身分
  "name", "names", "displayName", "subjectId", "url", "adbUrl", "photo", "photoUrl",
  "bio", "bioExcerpt", "notes", "summaries",
  // 精確時間
  "time", "utc", "localDateTime", "datetime", "chineseDate", "gmtJulDay", "age", "birthData", "birthDataText",
  // 地點
  "place", "location", "lat", "lng", "tzid",
)

/**
 * 遞迴列出 [descriptor] 樹中所有身分欄位，格式為 `serialName.fieldName`；乾淨則回傳空 list。
 */
fun identityFieldsIn(descriptor: SerialDescriptor, forbidden: Set<String> = IDENTITY_FIELD_NAMES): List<String> {
  val found = mutableListOf<String>()
  val visited = mutableSetOf<String>()

  fun walk(d: SerialDescriptor) {
    // 同一個型別只走一次，避免遞迴型別無限展開
    if (!visited.add(d.serialName)) return

    for (i in 0 until d.elementsCount) {
      val elementName = d.getElementName(i)
      // List/Map 的元素名是 "0" / "key" / "value"，不是真的欄位名，不比對
      if (d.kind is StructureKind.CLASS || d.kind is StructureKind.OBJECT) {
        if (elementName in forbidden) found += "${d.serialName}.$elementName"
      }
      walk(d.getElementDescriptor(i))
    }
  }

  walk(descriptor)
  return found
}
