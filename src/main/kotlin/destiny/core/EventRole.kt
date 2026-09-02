package destiny.core

import kotlinx.serialization.Serializable

/**
 * 主詞 —— 這件事的**動作**是誰做的。
 *
 * ## 判準：「動作是誰做的」，不是「結果由誰決定」
 *
 * ⚠️ 但這條判準單獨用會**循環**：「動作是什麼」取決於名稱怎麼取，而名稱是我們自己取的
 * （「任命」是別人做的、「進入高等教育」是他做的 —— 同一種結構卻判成相反）。
 * 故 `Situation.roles` 的填法另有一條不依賴名稱的規則，見該欄位的 KDoc。
 *
 * ## 與 [Agency] 的關係：**語意相同，值域不同**
 *
 * [INITIATOR] ≡ [Agency.ACTIVE]、[RECIPIENT] ≡ [Agency.PASSIVE]，同一根軸。
 * 分成兩個型別只有一個理由：**本 enum 沒有 `INDETERMINATE`**。
 *
 * 「分不出來」是判讀端在不確定下的答案，屬 [Agency]；
 * 進料端的「分不出來」是 `AbstractEvent.role == null`（未答），屬事件層。
 * 型別層則根本沒有這個概念 —— `Situation.roles` 只列舉**可能的主詞**。
 *
 * ⇒ 因為 `Set<EventRole>` 裡不存在 `INDETERMINATE`，
 * 「型別層不得使用 INDETERMINATE」這條不變式**在型別上就寫不出來**，不必再靠測試把關。
 *
 * ⚠️ 命名不叫 `Role`：`destiny.tools.ai.Role`（對話角色）已佔用該名，
 * 且抽取端（`CelebrityService`）會同時用到兩者。
 */
@Serializable
enum class EventRole {
  /** 他是發起的一方：他選擇、他推進、他取得 */
  INITIATOR,

  /** 他是承受的一方：由他人或外部條件決定，他回應 */
  RECIPIENT,
  ;

  /** 對應的判讀側值。⚠️ 反向沒有全函數 —— [Agency.INDETERMINATE] 無對應。 */
  fun toAgency(): Agency = when (this) {
    INITIATOR -> Agency.ACTIVE
    RECIPIENT -> Agency.PASSIVE
  }
}
