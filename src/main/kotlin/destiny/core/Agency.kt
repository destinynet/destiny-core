package destiny.core

import kotlinx.serialization.Serializable


/**
 * 主詞 —— 這件事**是他做的，還是發生在他身上的**。
 *
 * 幾乎每一個 [EventType] 都有一個**共用同一組幾何的鏡像**：
 * 「他提分手」↔「他被提分手」、「他賣出資產」↔「他被迫變現」。
 * 兩邊在星象上往往分不出，在生活裡卻是完全相反的兩件事 ——
 * **一個沒有主詞的預測在實務上不可用。**
 *
 * ## 判準是「動作是誰做的」，不是「結果由誰決定」
 *
 * 這條界線在兩處會被誤讀，故寫死：
 *
 * - `COMPETITION_LOSS`（競逐失利）是 [ACTIVE] —— 參賽是他做的，輸贏由裁判定；
 *   輸贏屬「好壞」那一軸，交給 `EventSentiment`，不要拿它來翻轉主詞。
 * - `PROMOTION_APPOINTMENT`（升遷／獲得職位）是 [PASSIVE] —— 任命這個動作是別人做的，
 *   即使他為此努力多年。
 *
 * 照這條判準，主詞多半已經寫在型別的敘述裡（「遭攻擊」vs「加害他人」），
 * 這正是 [EventType.fixedAgency] 存在的理由。
 *
 * [INDETERMINATE] 是合法且受歡迎的答案，但必須說明要分出來需要什麼資料。
 */
@Serializable
enum class Agency {
  /** 他是發起的一方：他選擇、他推進、他取得 */
  ACTIVE,

  /** 他是承受的一方：由他人或外部條件決定，他回應 */
  PASSIVE,

  /** 在現有的資料精度下分不出主客 */
  INDETERMINATE,
}
