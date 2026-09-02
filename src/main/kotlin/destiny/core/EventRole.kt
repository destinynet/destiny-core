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
 * ## ⛔ 這裡**刻意不提供** `toAgency()` 之類的轉換函式
 *
 * 上面那段對應關係是**文件**義務，寫在 KDoc 就已經履行完畢；可執行的轉換函式對它零貢獻。
 * 反過來，它會讓 `event.role?.toAgency()` 這種寫法只花一個 token
 * 就把剛分開的進料側與判讀側在管線裡重新混回去 —— 而且**會編譯、會看起來像有意為之**，
 * 正是本次重構要消滅的那一族缺陷。
 *
 * ⚠️ 真的出現呼叫端時再加回來。等有了呼叫端就拔不掉了 —— 所以順序不能反過來。
 *
 * ⚠️ 命名不叫 `Role`：`destiny.tools.ai.Role`（對話角色）已存在，而抽取端
 * （`destiny.tools.model.CelebrityService`、`MergedUserEventsCoordinator`）是用
 * **萬用 import**（`import destiny.tools.ai.*`）把它引進來的。
 * ⇒ 取名 `Role` 的後果不是撞名，是**靜默遮蔽**：一行明確的 `import destiny.core.Role`
 * 會安靜蓋掉萬用那個，**沒有編譯錯誤**，只有人拿錯 enum。
 */
@Serializable
enum class EventRole {
  /** 他是發起的一方：他選擇、他推進、他取得 */
  INITIATOR,

  /** 他是承受的一方：由他人或外部條件決定，他回應 */
  RECIPIENT,
}
